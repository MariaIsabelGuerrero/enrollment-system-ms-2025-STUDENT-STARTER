package com.champlain.enrollmentsservice.businesslayer;

import com.champlain.enrollmentsservice.TestData;
import com.champlain.enrollmentsservice.businesslayer.enrollments.EnrollmentServiceImpl;
import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.domainclientlayer.courses.CourseResponseModel;
import com.champlain.enrollmentsservice.domainclientlayer.courses.CourseServiceClient;
import com.champlain.enrollmentsservice.domainclientlayer.students.StudentResponseModel;
import com.champlain.enrollmentsservice.domainclientlayer.students.StudentServiceClientAsynchronous;
import com.champlain.enrollmentsservice.exceptionhandling.exceptions.EnrollmentNotFoundException;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceUnitTest {

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentServiceClientAsynchronous studentClient;

    @Mock
    private CourseServiceClient courseClient;

    protected final TestData testData = new TestData();

    @Test
    void whenGetAllEnrollments_thenReturnAllEnrollments() {
        //arrange
        when(enrollmentRepository.findAll())
                .thenReturn(Flux.just(testData.enrollment1, testData.enrollment2));

        //act
        Flux<EnrollmentResponseModel> result = enrollmentService.getEnrollments();

        //assert
        StepVerifier.create(result)
                .expectNextMatches(enrollmentResponseModel -> {
                    assertNotNull(enrollmentResponseModel);
                    assertEquals(enrollmentResponseModel.enrollmentId(), testData.enrollment1.getEnrollmentId());
                    assertEquals(enrollmentResponseModel.enrollmentYear(), testData.enrollment1.getEnrollmentYear());
                    assertEquals(enrollmentResponseModel.semester(), testData.enrollment1.getSemester());
                    assertEquals(enrollmentResponseModel.courseId(), testData.enrollment1.getCourseId());
                    assertEquals(enrollmentResponseModel.courseName(), testData.enrollment1.getCourseName());
                    assertEquals(enrollmentResponseModel.courseNumber(), testData.enrollment1.getCourseNumber());
                    assertEquals(enrollmentResponseModel.studentId(), testData.enrollment1.getStudentId());
                    assertEquals(enrollmentResponseModel.studentFirstName(), testData.enrollment1.getStudentFirstName());
                    assertEquals(enrollmentResponseModel.studentLastName(), testData.enrollment1.getStudentLastName());
                    return true;
                })
                .expectNextMatches(enrollmentResponseModel -> {
                    assertNotNull(enrollmentResponseModel);
                    assertEquals(enrollmentResponseModel.enrollmentId(), testData.enrollment2.getEnrollmentId());
                    assertEquals(enrollmentResponseModel.enrollmentYear(), testData.enrollment2.getEnrollmentYear());
                    assertEquals(enrollmentResponseModel.semester(), testData.enrollment2.getSemester());
                    assertEquals(enrollmentResponseModel.courseId(), testData.enrollment2.getCourseId());
                    assertEquals(enrollmentResponseModel.courseName(), testData.enrollment2.getCourseName());
                    assertEquals(enrollmentResponseModel.courseNumber(), testData.enrollment2.getCourseNumber());
                    assertEquals(enrollmentResponseModel.studentId(), testData.enrollment2.getStudentId());
                    assertEquals(enrollmentResponseModel.studentFirstName(), testData.enrollment2.getStudentFirstName());
                    assertEquals(enrollmentResponseModel.studentLastName(), testData.enrollment2.getStudentLastName());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void whenAddEnrollment_withValidRequestBody_ReturnEnrollmentResponseModel() {
        //arrange
        StudentResponseModel studentResponseModel = new StudentResponseModel(
                testData.student1ResponseModel.studentId(),
                testData.student1ResponseModel.firstName(),
                testData.student1ResponseModel.lastName(),
                testData.student1ResponseModel.program(),
                testData.student1ResponseModel.stuff()
        );

        CourseResponseModel courseResponseModel = new CourseResponseModel(
                testData.course1ResponseModel.courseId(),
                testData.course1ResponseModel.courseNumber(),
                testData.course1ResponseModel.courseName(),
                testData.course1ResponseModel.numHours(),
                testData.course1ResponseModel.numCredits(),
                testData.course1ResponseModel.department()
        );

        Enrollment savedEnrollment = Enrollment.builder()
                .enrollmentId("generated-uuid")
                .enrollmentYear(testData.enrollment1RequestModel.enrollmentYear())
                .semester(testData.enrollment1RequestModel.semester())
                .studentId(studentResponseModel.studentId())
                .studentFirstName(studentResponseModel.firstName())
                .studentLastName(studentResponseModel.lastName())
                .courseId(courseResponseModel.courseId())
                .courseName(courseResponseModel.courseName())
                .courseNumber(courseResponseModel.courseNumber())
                .build();

        when(studentClient.getStudentByStudentId(testData.enrollment1RequestModel.studentId()))
                .thenReturn(Mono.just(studentResponseModel));
        when(courseClient.getCourseByCourseId(testData.enrollment1RequestModel.courseId()))
                .thenReturn(Mono.just(courseResponseModel));
        when(enrollmentRepository.save(any(Enrollment.class)))
                .thenReturn(Mono.just(savedEnrollment));

        //act
        Mono<EnrollmentResponseModel> result = enrollmentService.addEnrollment(Mono.just(testData.enrollment1RequestModel));

        //assert
        StepVerifier.create(result)
                .expectNextMatches(enrollmentResponseModel -> {
                    assertNotNull(enrollmentResponseModel);
                    assertEquals(testData.enrollment1RequestModel.enrollmentYear(), enrollmentResponseModel.enrollmentYear());
                    assertEquals(testData.enrollment1RequestModel.semester(), enrollmentResponseModel.semester());
                    assertEquals(testData.enrollment1RequestModel.studentId(), enrollmentResponseModel.studentId());
                    assertEquals(testData.enrollment1RequestModel.courseId(), enrollmentResponseModel.courseId());
                    assertEquals(studentResponseModel.firstName(), enrollmentResponseModel.studentFirstName());
                    assertEquals(studentResponseModel.lastName(), enrollmentResponseModel.studentLastName());
                    assertEquals(courseResponseModel.courseName(), enrollmentResponseModel.courseName());
                    assertEquals(courseResponseModel.courseNumber(), enrollmentResponseModel.courseNumber());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void whenDeleteEnrollmentById_thenDeleteEnrollmentAndReturnEnrollmentResponseModel() {
        //arrange
        String enrollmentId = testData.enrollment1.getEnrollmentId();

        when(enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId))
                .thenReturn(Mono.just(testData.enrollment1));
        when(enrollmentRepository.delete(testData.enrollment1))
                .thenReturn(Mono.empty());

        //act
        Mono<EnrollmentResponseModel> result = enrollmentService.deleteEnrollmentByEnrollmentId(enrollmentId);

        //assert
        StepVerifier.create(result)
                .expectNextMatches(enrollmentResponseModel -> {
                    assertNotNull(enrollmentResponseModel);
                    assertEquals(testData.enrollment1.getEnrollmentId(), enrollmentResponseModel.enrollmentId());
                    assertEquals(testData.enrollment1.getEnrollmentYear(), enrollmentResponseModel.enrollmentYear());
                    assertEquals(testData.enrollment1.getSemester(), enrollmentResponseModel.semester());
                    assertEquals(testData.enrollment1.getStudentId(), enrollmentResponseModel.studentId());
                    assertEquals(testData.enrollment1.getStudentFirstName(), enrollmentResponseModel.studentFirstName());
                    assertEquals(testData.enrollment1.getStudentLastName(), enrollmentResponseModel.studentLastName());
                    assertEquals(testData.enrollment1.getCourseId(), enrollmentResponseModel.courseId());
                    assertEquals(testData.enrollment1.getCourseName(), enrollmentResponseModel.courseName());
                    assertEquals(testData.enrollment1.getCourseNumber(), enrollmentResponseModel.courseNumber());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void whenGetEnrollmentByEnrollmentId_withExistingId_thenReturnEnrollmentResponseModel() {
        //arrange - POSITIVE TEST
        String enrollmentId = testData.enrollment1.getEnrollmentId();

        when(enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId))
                .thenReturn(Mono.just(testData.enrollment1));

        Mono<EnrollmentResponseModel> result = enrollmentService.getEnrollmentByEnrollmentId(enrollmentId);

        StepVerifier.create(result)
                .expectNextMatches(enrollmentResponseModel -> {
                    assertNotNull(enrollmentResponseModel);
                    assertEquals(testData.enrollment1.getEnrollmentId(), enrollmentResponseModel.enrollmentId());
                    assertEquals(testData.enrollment1.getEnrollmentYear(), enrollmentResponseModel.enrollmentYear());
                    assertEquals(testData.enrollment1.getSemester(), enrollmentResponseModel.semester());
                    assertEquals(testData.enrollment1.getStudentId(), enrollmentResponseModel.studentId());
                    assertEquals(testData.enrollment1.getStudentFirstName(), enrollmentResponseModel.studentFirstName());
                    assertEquals(testData.enrollment1.getStudentLastName(), enrollmentResponseModel.studentLastName());
                    assertEquals(testData.enrollment1.getCourseId(), enrollmentResponseModel.courseId());
                    assertEquals(testData.enrollment1.getCourseName(), enrollmentResponseModel.courseName());
                    assertEquals(testData.enrollment1.getCourseNumber(), enrollmentResponseModel.courseNumber());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void whenGetEnrollmentByEnrollmentId_withNonExistingId_thenThrowEnrollmentNotFoundException() {
        //arrange - - NEGATIVE TEST
        String nonExistingEnrollmentId = UUID.randomUUID().toString();

        when(enrollmentRepository.findEnrollmentByEnrollmentId(nonExistingEnrollmentId))
                .thenReturn(Mono.empty());

        //act
        Mono<EnrollmentResponseModel> result = enrollmentService.getEnrollmentByEnrollmentId(nonExistingEnrollmentId);

        //assert
        StepVerifier
                .create(result)
                .expectErrorMatches(e -> e instanceof EnrollmentNotFoundException &&
                        e.getMessage().equals("Enrollment with id=" + nonExistingEnrollmentId + " is not found"))
                .verify();
    }


}