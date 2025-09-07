package com.champlain.enrollmentsservice.businesslayer.enrollments;

import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.domainclientlayer.courses.CourseServiceClient;
import com.champlain.enrollmentsservice.domainclientlayer.students.StudentServiceClientAsynchronous;
import com.champlain.enrollmentsservice.exceptionhandling.exceptions.EnrollmentNotFoundException;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import com.champlain.enrollmentsservice.utils.EntityModelUnity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    final private StudentServiceClientAsynchronous studentClient;
    final private CourseServiceClient courseClient;
    final private EnrollmentRepository enrollmentRepository;


    public EnrollmentServiceImpl(StudentServiceClientAsynchronous studentClient,CourseServiceClient courseClient,EnrollmentRepository enrollmentRepository ) {
        this.studentClient = studentClient;
        this.courseClient = courseClient;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public Flux<EnrollmentResponseModel> getEnrollments() {
        return enrollmentRepository.findAll()
                .map(EntityModelUnity::toModel);
    }

    @Override
    public Mono<EnrollmentResponseModel> getEnrollmentByEnrollmentId(String enrollmentId) {
        return enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId)
                .switchIfEmpty(Mono.error(new EnrollmentNotFoundException("Enrollment id not found: " + enrollmentId)))
                .map(EntityModelUnity::toModel);
    }

    @Override
    public Mono<EnrollmentResponseModel> addEnrollment(Mono<EnrollmentRequestModel> enrollmentRequestModel) {
        return enrollmentRequestModel
                .map(RequestContext::new)
                .flatMap(this::studentRequestResponse)
                .flatMap(this::courseRequestResponse)
                .map(EntityModelUnity::toEntity)
                .flatMap(enrollmentRepository::save)
                .map(EntityModelUnity::toModel);

    }

    @Override
    public Mono<EnrollmentResponseModel> updateEnrollmentByEnrollmentId(Mono<EnrollmentRequestModel> enrollmentRequestModel, String enrollmentId) {
        return enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new EnrollmentNotFoundException("Enrollment id not found: " + enrollmentId))))
                .flatMap(existingEnrollment -> enrollmentRequestModel
                        .map(RequestContext::new)
                        .flatMap(this::studentRequestResponse)
                        .flatMap(this::courseRequestResponse)
                        .map(EntityModelUnity::toEntity)
                        .doOnNext(e -> e.setEnrollmentId(existingEnrollment.getEnrollmentId()))
                        .doOnNext(e -> e.setId(existingEnrollment.getId())))
                .flatMap(enrollmentRepository::save)
                .map(EntityModelUnity::toModel);
    }

    @Override
    public Mono<EnrollmentResponseModel> deleteEnrollmentByEnrollmentId(String enrollmentId) {
        return enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new EnrollmentNotFoundException("Enrollment id not found: " + enrollmentId))))
                .flatMap(existingEnrollment -> enrollmentRepository.delete(existingEnrollment)
                        .then(Mono.just(existingEnrollment)))
                .map(EntityModelUnity::toModel);
    }


    private Mono<RequestContext> studentRequestResponse(RequestContext rc) {
        return this.studentClient
                .getStudentByStudentId(rc.getEnrollmentRequestModel().studentId())
                .doOnNext(rc::setStudentResponseModel)
                .thenReturn(rc);
    }

    private Mono<RequestContext> courseRequestResponse(RequestContext rc) {
        return this.courseClient.getCourseByCourseId(rc.getEnrollmentRequestModel().courseId())
                        .doOnNext(rc::setCourseResponseModel)
                        .thenReturn(rc);
    }




}
