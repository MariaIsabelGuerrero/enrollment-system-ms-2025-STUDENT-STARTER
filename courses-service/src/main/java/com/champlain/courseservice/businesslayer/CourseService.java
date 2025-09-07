package com.champlain.courseservice.businesslayer;


import com.champlain.courseservice.presentationlayer.CourseRequestModel;
import com.champlain.courseservice.presentationlayer.CourseResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CourseService {
    Flux<CourseResponseModel> getAllCourses();
    Mono<CourseResponseModel> getCourseByCourseId(String id);
    Mono<CourseResponseModel> addCourse(Mono<CourseRequestModel> courseRequestModel);
    Mono<CourseResponseModel> updateCourseByCourseId(Mono<CourseRequestModel> courseRequestModel, String courseId);
    Mono<CourseResponseModel> deleteCourseByCourseId(String courseId);
}
