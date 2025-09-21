package com.champlain.courseservice.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataR2dbcTest
@ActiveProfiles("test")
public class CourseRepositoryIntegrationTest {

    @Autowired
    private CourseRepository courseRepository;

    Course course1;
    Course course2;

    @Autowired
    private DatabaseClient databaseClient;
    @BeforeEach
    void setUpDB() {
        StepVerifier
                .create(courseRepository.deleteAll())
                .expectNextCount(0)
                .verifyComplete();
    }
    @Test
    void findCourseByCourseId_shouldSucceedWhenExistingId(){
        //positive
        String courseId = UUID.randomUUID().toString();
        Course course = Course.builder()
                .courseId(courseId)
                .courseNumber("cat-420")
                .courseName("Web Services")
                .numHours(45)
                .numCredits(3.0)
                .department("Computer Science")
                .build();
        StepVerifier.create(courseRepository.save(course))
                .consumeNextWith(insertedCourse -> {
                    assertNotNull(insertedCourse);
                    assertEquals(course.getCourseId(), insertedCourse.getCourseId());
                })
                .verifyComplete();
        StepVerifier.create(courseRepository
                .findCourseByCourseId(course.getCourseId()))
                .consumeNextWith(foundCourse -> {
                    assertNotNull(foundCourse);
                    assertEquals(course.getCourseId(), foundCourse.getCourseId());

                })
                .verifyComplete();
    }
    @Test
    //negative
    void findCourseByCourseId_shouldFailWhenNonExistingId() {
        //negative
        StepVerifier.create(courseRepository.findCourseByCourseId("non-existing-id"))
                .expectComplete()
                .verify();
    }



}
