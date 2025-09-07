package com.champlain.courseservice.businesslayer;

import com.champlain.courseservice.dataaccesslayer.Course;
import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.exceptionhandling.exceptions.NotFoundException;
import com.champlain.courseservice.mapper.EntityModelMapper;
import com.champlain.courseservice.presentationlayer.CourseRequestModel;
import com.champlain.courseservice.presentationlayer.CourseResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public Flux<CourseResponseModel> getAllCourses() {
        return courseRepository.findAll()
                .map(EntityModelMapper::toModel);
    }

    @Override
    public Mono<CourseResponseModel> getCourseByCourseId(String courseId) {
        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Course not found with id: " + courseId)))
                .map(EntityModelMapper::toModel);
    }

    @Override
    public Mono<CourseResponseModel> addCourse(Mono<CourseRequestModel> courseRequestModel) {
        return courseRequestModel
                .map(EntityModelMapper::toEntity)
                .flatMap(courseRepository::save)
                .map(EntityModelMapper::toModel);
    }

    @Override
    public Mono<CourseResponseModel> updateCourseByCourseId(Mono<CourseRequestModel> courseRequestModel, String courseId) {
        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Course not found with id: " + courseId)))
                .flatMap(existingCourse ->
                        courseRequestModel
                                .map(requestModel -> {
                                    Course updatedCourse = EntityModelMapper.toEntity(requestModel);
                                    updatedCourse.setId(existingCourse.getId());
                                    updatedCourse.setCourseId(existingCourse.getCourseId());
                                    return updatedCourse;
                                })
                .flatMap(courseRepository::save)
                .map(EntityModelMapper::toModel)
                );
    }

    @Override
    public Mono<CourseResponseModel> deleteCourseByCourseId(String courseId) {
        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Course not found with id: " + courseId)))
                .flatMap(existingCourse -> courseRepository.delete(existingCourse)
                        .then(Mono.just(existingCourse)))
                        .map(EntityModelMapper::toModel);
    }
}