package com.champlain.courseservice.presentationlayer;


import com.champlain.courseservice.businesslayer.CourseService;
import com.champlain.courseservice.exceptionhandling.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourseControllerUnitTest {
    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseService courseService;

    private final String FOUND_COURSE_ID = "bc89ba5d-4f42-44f3-ad1d-4723841d5402";
    private final String NON_EXISTING_COURSE_ID = "c3540a89-cb47-4c96-888e-ff96708db400";
    private final String INVALID_COURSE_ID = "c3540a89-cb47-4c96-888e-f";

    @Test
    public void whenAddCourse_withInvalidHours_thenThrowInvalidInputException() {
        //arrange
        CourseRequestModel courseRequestModel = new CourseRequestModel(
                "cat-423",
                "Web Services Testing",
                0,
                3.0,
                "Computer Science"
        );

        Mono<ResponseEntity<CourseResponseModel>> result =
                courseController.addCourse(Mono.just(courseRequestModel));

        //act and assert
        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof InvalidInputException &&
                        e.getMessage().equals("Course hours must be greater than 0"))
                .verify();
    }

    @Test
    public void whenAddCourse_withValidData_thenReturnCourseResponseModel() {
        //arrange
        CourseRequestModel courseRequestModel = new CourseRequestModel(
                "cat-423",
                "Web Services Testing",
                45,
                3.0,
                "Computer Science"
        );

        CourseResponseModel mockResponse = new CourseResponseModel(
                FOUND_COURSE_ID,
                "cat-423",
                "Web Services Testing",
                45,
                3.0,
                "Computer Science"
        );

        when(courseService.addCourse(any(Mono.class)))
                .thenReturn(Mono.just(mockResponse));

        //act
        Mono<ResponseEntity<CourseResponseModel>> result =
                courseController.addCourse(Mono.just(courseRequestModel));

        //assert
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    CourseResponseModel body = responseEntity.getBody();
                    assertNotNull(body);
                    assertEquals(mockResponse.courseId(), body.courseId());
                    assertEquals(mockResponse.courseNumber(), body.courseNumber());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void getCourseByCourseId_validCourseId_courseReturned() {
        //arrange
        CourseResponseModel mockResponse = new CourseResponseModel(
                FOUND_COURSE_ID,
                "cat-420",
                "Web Services",
                45,
                3.0,
                "Computer Science"
        );

        when(courseService.getCourseByCourseId(FOUND_COURSE_ID))
                .thenReturn(Mono.just(mockResponse));

        //act
        Mono<ResponseEntity<CourseResponseModel>> result =
                courseController.getCourseByCourseId(FOUND_COURSE_ID);

        //assert
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    CourseResponseModel body = responseEntity.getBody();
                    assertNotNull(body);
                    assertEquals(FOUND_COURSE_ID, body.courseId());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void whenGetCourseByCourseId_withInvalidCourseId_thenReturnInvalidInputException() {
        //act and assert
        StepVerifier.create(courseController.getCourseByCourseId(INVALID_COURSE_ID))
                .expectErrorMatches(e -> e instanceof InvalidInputException &&
                        e.getMessage().contains("Course Id is invalid"))
                .verify();
    }

    @Test
    public void whenUpdateCourse_thenReturnCourseResponseModel() {
        //arrange
        CourseRequestModel updateRequest = new CourseRequestModel(
                "cat-updated",
                "Updated Course",
                60,
                4.0,
                "Computer Science"
        );

        CourseResponseModel mockResponse = new CourseResponseModel(
                FOUND_COURSE_ID,
                "cat-updated",
                "Updated Course",
                60,
                4.0,
                "Computer Science"
        );

        when(courseService.updateCourseByCourseId(any(Mono.class), eq(FOUND_COURSE_ID)))
                .thenReturn(Mono.just(mockResponse));

        //act
        Mono<ResponseEntity<CourseResponseModel>> result =
                courseController.updateCourseByCourseId(Mono.just(updateRequest), FOUND_COURSE_ID);

        //assert
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    CourseResponseModel body = responseEntity.getBody();
                    assertNotNull(body);
                    assertEquals(mockResponse.courseNumber(), body.courseNumber());
                    assertEquals(mockResponse.courseName(), body.courseName());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void whenUpdateCourse_withInvalidCourseId_thenReturnInvalidInputException() {
        //arrange
        CourseRequestModel updateRequest = new CourseRequestModel(
                "cat-invalid", "Invalid Update", 45, 3.0, "Computer Science");

        //act and assert
        StepVerifier.create(courseController.updateCourseByCourseId(
                        Mono.just(updateRequest), INVALID_COURSE_ID))
                .expectErrorMatches(e -> e instanceof InvalidInputException &&
                        e.getMessage().contains("Course Id is invalid"))
                .verify();
    }

    @Test
    public void whenDeleteCourse_thenReturnCourseResponseModel() {
        //arrange
        CourseResponseModel mockResponse = new CourseResponseModel(
                FOUND_COURSE_ID,
                "cat-delete",
                "Course to Delete",
                45,
                3.0,
                "Computer Science"
        );

        when(courseService.deleteCourseByCourseId(FOUND_COURSE_ID))
                .thenReturn(Mono.just(mockResponse));

        //act
        Mono<ResponseEntity<CourseResponseModel>> result =
                courseController.deleteCourseByCourseId(FOUND_COURSE_ID);

        //assert
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    CourseResponseModel body = responseEntity.getBody();
                    assertNotNull(body);
                    assertEquals(FOUND_COURSE_ID, body.courseId());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void whenDeleteCourse_withInvalidCourseId_thenReturnInvalidInputException() {
        //act and assert
        StepVerifier.create(courseController.deleteCourseByCourseId(INVALID_COURSE_ID))
                .expectErrorMatches(e -> e instanceof InvalidInputException &&
                        e.getMessage().contains("Course Id is invalid"))
                .verify();
    }
}
