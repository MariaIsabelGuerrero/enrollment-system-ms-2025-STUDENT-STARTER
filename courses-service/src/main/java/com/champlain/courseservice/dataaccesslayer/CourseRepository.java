package com.champlain.courseservice.dataaccesslayer;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CourseRepository extends ReactiveCrudRepository<Course,Integer> {

    Mono<Course> findCourseByCourseId(String courseId);
}
