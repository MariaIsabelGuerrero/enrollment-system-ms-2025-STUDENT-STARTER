package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.exceptionhandling.HttpErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureWebTestClient
@Slf4j
public class CourseControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CourseRepository courseRepository;


    private final Long dbSize = 1000L;

    @BeforeEach
    public void dbSetup() {

        StepVerifier
                .create(courseRepository.count())
                .consumeNextWith(count -> {
                    assertEquals(dbSize, count);
                })
                .verifyComplete();
    }

    @Test
    public void getAllCoursesEventStream() {
        this.webTestClient.get()
                .uri("/api/v1/courses")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(CourseResponseModel.class)
                .getResponseBody()
                .doOnNext(course -> log.info("{}", course)) //only for debugging
                .as(StepVerifier::create)
                .expectNextCount(dbSize)
                .verifyComplete();
    }

    @Test
    void addNewCourse_withValidRequestBody_shouldSucceed() {
        //positive
        CourseRequestModel courseRequestModel = new CourseRequestModel(
                "cat-423",
                "Web Services Testing",
                45,
                3.0,
                "Computer Science"
        );
        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CourseResponseModel.class)
                .value(courseResponseModel -> {
                    assertNotNull(courseResponseModel);
                    assertNotNull(courseResponseModel.courseId());
                    assertEquals(courseRequestModel.courseNumber(), courseResponseModel.courseNumber());
                });
    }

    @Test
    void addNewCourse_withMissingCourseName_shouldReturnUnProcessableEntity() {
        var courseRequestModel = this.resourceToString("courseRequestModel-missing-courseName-422.json");

        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertEquals("Course name is required", errorInfo.getMessage());
                });
    }

    private String resourceToString(String relativePath) {
        java.nio.file.Path TEST_RESOURCES_PATH = java.nio.file.Path.of("src/test/resources");
        try {
            return java.nio.file.Files.readString(TEST_RESOURCES_PATH.resolve(relativePath));
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getCourseByCourseId_shouldSucceedWithExistingId() {
        CourseRequestModel courseRequestModel = new CourseRequestModel(
                "cat-001",
                "Test Course",
                45,
                3.0,
                "Computer Science"
        );

        String courseId = webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CourseResponseModel.class)
                .returnResult()
                .getResponseBody()
                .courseId();


        webTestClient
                .get()
                .uri("/api/v1/courses/{courseId}", courseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CourseResponseModel.class)
                .value(courseResponseModel -> {
                    assertNotNull(courseResponseModel);
                    assertEquals(courseId, courseResponseModel.courseId());
                    assertEquals("cat-001", courseResponseModel.courseNumber());
                });
    }

    @Test
    void getCourseByCourseId_shouldReturnNotFound_WithNonExistingId() {
        String nonExistingId = UUID.randomUUID().toString();

        webTestClient
                .get()
                .uri("/api/v1/courses/{courseId}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertTrue(errorInfo.getMessage().contains("not found"));
                });
    }

    @Test
    void getCourseByCourseId_shouldReturnUnProcessableEntity_WithInvalidId() {
        String invalidId = "invalid-id-format";

        webTestClient
                .get()
                .uri("/api/v1/courses/{courseId}", invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertTrue(errorInfo.getMessage().contains("Course Id is invalid"));
                });
    }

    @Test
    void getAllCourses_whenNoCourses_shouldReturnEmptyStream() {

        StepVerifier
                .create(courseRepository.deleteAll())
                .verifyComplete();


        StepVerifier
                .create(courseRepository.count())
                .expectNext(0L)
                .verifyComplete();


        webTestClient
                .get()
                .uri("/api/v1/courses")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(CourseResponseModel.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }


    @Test
    void addNewCourse_withInvalidNumCredits_shouldReturnUnProcessableEntity() {
        CourseRequestModel courseRequestModel = new CourseRequestModel(
                "cat-423",
                "Web Services Testing",
                45,
                -1.0,
                "Computer Science"
        );

        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertTrue(errorInfo.getMessage().contains("Course credits must be greater than 0"));
                });

        CourseRequestModel nullNameRequest = new CourseRequestModel(
                "cat-424", null, 45, 3.0, "Computer Science");
        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(nullNameRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertTrue(errorInfo.getMessage().contains("Course name is required"));
                });

        // Test null course number (this WILL trigger validation)
        CourseRequestModel nullNumberRequest = new CourseRequestModel(
                null, "Test Course", 45, 3.0, "Computer Science");
        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(nullNumberRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertTrue(errorInfo.getMessage().contains("Course number is required"));
                });

        // Test zero hours (this WILL trigger validation)
        CourseRequestModel zeroHoursRequest = new CourseRequestModel(
                "cat-425", "Test Course", 0, 3.0, "Computer Science");
        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(zeroHoursRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertTrue(errorInfo.getMessage().contains("Course hours must be greater than 0"));
                });

        // Test null credits (this WILL trigger validation)
        CourseRequestModel nullCreditsRequest = new CourseRequestModel(
                "cat-426", "Test Course", 45, null, "Computer Science");
        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(nullCreditsRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertTrue(errorInfo.getMessage().contains("Course credits"));
                });

        // Test null hours (this WILL trigger validation)
        CourseRequestModel nullHoursRequest = new CourseRequestModel(
                "cat-427", "Test Course", null, 3.0, "Computer Science");
        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(nullHoursRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertTrue(errorInfo.getMessage().contains("Course hours"));
                });
    }


    @Test
    void addNewCourse_withMissingNumHours_shouldReturnUnProcessableEntity() {
        var courseRequestModel = this.resourceToString("courseRequestModel-missing-numHours-422.json");

        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertEquals("Course hours is required", errorInfo.getMessage());
                });
    }

    @Test
    void addNewCourse_withMissingNumCredits_shouldReturnUnProcessableEntity() {
        var courseRequestModel = this.resourceToString("courseRequestModel-missing-numCredits-422.json");

        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertEquals("Course credits is required", errorInfo.getMessage());
                });
    }

    @Test
    void updateCourse_withValidRequestBody_ShouldSucceed() {
        CourseRequestModel originalCourse = new CourseRequestModel(
                "cat-original",
                "Original Course",
                45,
                3.0,
                "Computer Science"
        );

        String courseId = webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(originalCourse)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CourseResponseModel.class)
                .returnResult()
                .getResponseBody()
                .courseId();


        CourseRequestModel updateRequest = new CourseRequestModel(
                "cat-updated",
                "Updated Course",
                60,
                4.0,
                "Computer Science"
        );

        webTestClient
                .put()
                .uri("/api/v1/courses/{courseId}", courseId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CourseResponseModel.class)
                .value(courseResponseModel -> {
                    assertNotNull(courseResponseModel);
                    assertEquals(courseId, courseResponseModel.courseId());
                    assertEquals(updateRequest.courseNumber(), courseResponseModel.courseNumber());
                    assertEquals(updateRequest.courseName(), courseResponseModel.courseName());
                    assertEquals(updateRequest.numHours(), courseResponseModel.numHours());
                    assertEquals(updateRequest.numCredits(), courseResponseModel.numCredits());
                    assertEquals(updateRequest.department(), courseResponseModel.department());
                });

        String invalidCourseId = "invalid-format";
        webTestClient
                .get()
                .uri("/api/v1/courses/{courseId}", invalidCourseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        // Test non-existing course ID scenario
        String nonExistingId = UUID.randomUUID().toString();
        webTestClient
                .get()
                .uri("/api/v1/courses/{courseId}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateCourse_withNonExistingCourseId_ShouldReturnNotFound() {
        String nonExistingId = UUID.randomUUID().toString();
        CourseRequestModel updateRequest = new CourseRequestModel(
                "cat-404",
                "Non-existing Course",
                45,
                3.0,
                "Computer Science"
        );

        webTestClient
                .put()
                .uri("/api/v1/courses/{courseId}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertTrue(errorInfo.getMessage().contains("Course not found with id"));
                });
    }

    @Test
    void updateCourse_withInvalidCourseId_ShouldReturnUnProcessableEntity() {
        String invalidId = "invalid-id-format";
        CourseRequestModel updateRequest = new CourseRequestModel(
                "cat-422",
                "Invalid Update",
                45,
                3.0,
                "Computer Science"
        );

        webTestClient
                .put()
                .uri("/api/v1/courses/{courseId}", invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertTrue(errorInfo.getMessage().contains("Course Id is invalid"));
                });
    }

    @Test
    void deleteCourse_withValidCourseId_ShouldReturnDeletedCourse() {
        CourseRequestModel courseRequest = new CourseRequestModel(
                "cat-delete",
                "Course to Delete",
                45,
                3.0,
                "Computer Science"
        );

        String courseId = webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CourseResponseModel.class)
                .returnResult()
                .getResponseBody()
                .courseId();


        webTestClient
                .delete()
                .uri("/api/v1/courses/{courseId}", courseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CourseResponseModel.class)
                .value(courseResponseModel -> {
                    assertNotNull(courseResponseModel);
                    assertEquals(courseId, courseResponseModel.courseId());
                    assertEquals(courseRequest.courseNumber(), courseResponseModel.courseNumber());
                    assertEquals(courseRequest.courseName(), courseResponseModel.courseName());
                    assertEquals(courseRequest.numHours(),   courseResponseModel.numHours());
                    assertEquals(courseRequest.numCredits(), courseResponseModel.numCredits());
                    assertEquals(courseRequest.department(), courseResponseModel.department());

                });
    }

    @Test
    public void deleteCourse_withNonExistingCourseId_shouldReturnNotFound() {
        String nonExistingId = UUID.randomUUID().toString();

        webTestClient
                .delete()
                .uri("/api/v1/courses/{courseId}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(HttpErrorInfo.class)
                .value(errorInfo -> {
                    assertNotNull(errorInfo);
                    assertTrue(errorInfo.getMessage().contains("not found"));
                });

    }

}
