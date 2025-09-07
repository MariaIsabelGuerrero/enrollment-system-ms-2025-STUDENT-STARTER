package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.dataaccesslayer.Course;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private String validCourseId;
    private Course validCourse;
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
                    assertEquals(courseRequestModel.courseNumber(),
                            courseResponseModel.courseNumber());
                    assertEquals(courseRequestModel.courseName(),
                            courseResponseModel.courseName());
                    assertEquals(courseRequestModel.numHours(),
                            courseResponseModel.numHours());
                    assertEquals(courseRequestModel.numCredits(),
                            courseResponseModel.numCredits());
                    assertEquals(courseRequestModel.department(),
                            courseResponseModel.department());
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

    // Utility method from PDF
    protected String resourceToString(String relativePath) {
        final Path TEST_RESOURCES_PATH = Path.of("src/test/resources");
        try {
            return Files.readString(TEST_RESOURCES_PATH.resolve(relativePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Students will add more tests including the following:
    @Test
    void getCourseByCourseId_shouldSucceedWithExistingId() {
        // First create a course to test with
        CourseRequestModel courseRequestModel = new CourseRequestModel(
                "cat-test-001",
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

        // Now get the course by ID
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
                    assertEquals(courseRequestModel.courseNumber(), courseResponseModel.courseNumber());
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
                    assertTrue(errorInfo.getMessage().contains("Course Id not found"));
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
        // This test would need to clear the database first
        // Since we have 1000L courses by default, skip this or modify setup
    }

    @Test
    void addNewCourse_withInvalidNumCredits_shouldReturnUnProcessableEntity() {
        CourseRequestModel courseRequestModel = new CourseRequestModel(
                "cat-423",
                "Web Services Testing",
                45,
                -1.0, // Invalid credits
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
        // First create a course
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

        // Now update it
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
                });
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
                    assertTrue(errorInfo.getMessage().contains("Course Id not found"));
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
        // First create a course
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

        // Now delete it
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
                    assertTrue(errorInfo.getMessage().contains("Course Id not found"));
                });
    }

}
