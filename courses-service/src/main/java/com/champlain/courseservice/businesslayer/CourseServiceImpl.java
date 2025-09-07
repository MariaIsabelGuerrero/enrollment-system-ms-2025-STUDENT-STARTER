package com.champlain.courseservice.businesslayer;

import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.exceptionhandling.exceptions.CourseNotFoundException;
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
                .switchIfEmpty(Mono.error(new CourseNotFoundException("Course id not found: " + courseId)))
                .doOnNext(i -> log.debug("The course entity is: "+ i.toString()))
                .map(EntityModelMapper::toModel)
                .log();
    }

    @Override
    public Mono<CourseResponseModel> addCourse(Mono<CourseRequestModel> courseRequestModel) {
        return courseRequestModel
                .map(EntityModelMapper::toEntity)
                .doOnNext(e -> e.setCourseId(EntityModelMapper.generateUUIDString()))
                .flatMap(courseRepository::save)
                .map(EntityModelMapper::toModel);
    }

    @Override
    public Mono<CourseResponseModel> updateCourseByCourseId(Mono<CourseRequestModel> courseRequestModel, String courseId) {
        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CourseNotFoundException("Course id not found: " + courseId))))
                .flatMap( s -> courseRequestModel
                        .map(EntityModelMapper::toEntity)
                        .doOnNext(e -> e.setCourseId(s.getCourseId()))
                .doOnNext(e -> e.setId(s.getId())))
                .flatMap(courseRepository::save)
                .map(EntityModelMapper::toModel);
    }

    @Override
    public Mono<CourseResponseModel> deleteCourseByCourseId(String courseId) {
        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CourseNotFoundException("Course id not found: " + courseId))))
                .flatMap(existingCourse -> courseRepository.delete(existingCourse)
                        .then(Mono.just(existingCourse)))
                        .map(EntityModelMapper::toModel);
    }
}