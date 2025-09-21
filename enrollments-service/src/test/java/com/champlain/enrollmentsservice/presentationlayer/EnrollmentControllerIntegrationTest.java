package com.champlain.enrollmentsservice.presentationlayer;

import com.champlain.enrollmentsservice.TestData;
import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.domainclientlayer.courses.CourseResponseModel;
import com.champlain.enrollmentsservice.domainclientlayer.students.StudentResponseModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnrollmentControllerIntegrationTest extends AbstractIntegrationClass {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    protected MockServerClient mockServerClient;

    @Autowired
    protected WebTestClient webTestClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private void mockGetCourseByCourseIdSuccess(CourseResponseModel model) throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(model);

        mockServerClient
                .when(
                        HttpRequest.request()
                                .withPath("/api/v1/courses/" + model.courseId())
                )
                .respond(
                        HttpResponse.response(jsonBody)
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                );
    }

    private void mockGetCourseByCourseIdException(String courseId, int responseCode) {
        mockServerClient
                .when(
                        HttpRequest.request()
                                .withPath("/api/v1/courses/" + courseId)
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(responseCode)
                                .withContentType(MediaType.APPLICATION_JSON) // from mockserver model lib
                );
    }

    private void mockGetStudentByStudentIdSuccess(StudentResponseModel model) throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(model); // must be a String

        mockServerClient
                .when(
                        HttpRequest.request()
                                .withPath("/api/v1/students/" + model.studentId())
                )
                .respond(
                        HttpResponse.response(jsonBody)
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                );
    }

    private void mockGetStudentByStudentIdException(String studentId, int responseCode) {
        mockServerClient
                .when(
                        HttpRequest.request()
                                .withPath("/api/v1/students/" + studentId)
                )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(responseCode)
                                .withContentType(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    @Order(1)
    public void whenGetAllEnrollments_ReturnEventStream() {
        webTestClient.get()
                .uri("/api/v1/enrollments")
                .accept(org.springframework.http.MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(EnrollmentResponseModel.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .expectNextCount(testData.dbSize)
                .verifyComplete();
    }

    @Test
    @Order(2)
    public void whenAddEnrollment_withNonExistingCourseId_thenThrowNotFoundException() {
        //arrange - NEGATIVE TEST
        try {
            mockGetStudentByStudentIdSuccess(testData.student1ResponseModel);
            mockGetCourseByCourseIdException(TestData.NON_EXISTING_COURSEID, 404);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // Act
        webTestClient.post()
                .uri("/api/v1/enrollments")
                .contentType(APPLICATION_JSON)
                .body(Mono.just(testData.enrollment_withNonExistingCourseId_RequestModel), EnrollmentRequestModel.class)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Course with id=" + TestData.NON_EXISTING_COURSEID + " is not found");
        // Assert
        StepVerifier
                .create(enrollmentRepository.count())
                .expectNext(testData.dbSize)
                .verifyComplete();
    }

    @Test
    @Order(3)
    public void whenAddValidEnrollmentRequest_thenReturnEnrollmentResponseModel() throws JsonProcessingException {
        //arrange - POSITIVE TEST
        try {
            mockGetStudentByStudentIdSuccess(testData.student1ResponseModel);
            mockGetCourseByCourseIdSuccess(testData.course1ResponseModel);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        webTestClient.post()
                .uri("/api/v1/enrollments")
                .contentType(APPLICATION_JSON)
                .body(Mono.just(testData.enrollment1RequestModel), EnrollmentRequestModel.class)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .value(enrollmentResponseModel -> {
                    assertNotNull(enrollmentResponseModel);
                    assertNotNull(enrollmentResponseModel.enrollmentId());
                    assertEquals(testData.enrollment1RequestModel.enrollmentYear(), enrollmentResponseModel.enrollmentYear());
                    assertEquals(testData.enrollment1RequestModel.semester(), enrollmentResponseModel.semester());
                    assertEquals(testData.enrollment1RequestModel.studentId(), enrollmentResponseModel.studentId());
                    assertEquals(testData.student1ResponseModel.firstName(), enrollmentResponseModel.studentFirstName());
                    assertEquals(testData.student1ResponseModel.lastName(), enrollmentResponseModel.studentLastName());
                    assertEquals(testData.enrollment1RequestModel.courseId(), enrollmentResponseModel.courseId());
                    assertEquals(testData.course1ResponseModel.courseName(), enrollmentResponseModel.courseName());
                    assertEquals(testData.course1ResponseModel.courseNumber(), enrollmentResponseModel.courseNumber());
                });

        StepVerifier
                .create(enrollmentRepository.count())
                .expectNext(testData.dbSize + 1)
                .verifyComplete();
    }
    @Test
    @Order(4)
    void whenGetEnrollmentWithExistingEnrollmentId_thenReturnEnrollmentResponseModel() {
        webTestClient.get()
                .uri("/api/v1/enrollments/" + testData.enrollment1.getEnrollmentId())
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .value(enrollmentResponseModel -> {
                    assertNotNull(enrollmentResponseModel);
                    assertEquals(testData.enrollment1.getEnrollmentId(), enrollmentResponseModel.enrollmentId());
                });
    }

    @Test
    @Order(5)
    void whenAddEnrollment_withNonExistingStudentId_thenThrowNotFoundException() {
        try {
            mockGetStudentByStudentIdException(TestData.NON_EXISTING_STUDENTID, 404);
            mockGetCourseByCourseIdSuccess(testData.course1ResponseModel);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        webTestClient.post()
                .uri("/api/v1/enrollments")
                .contentType(APPLICATION_JSON)
                .body(Mono.just(testData.enrollment_withNonExistingStudentId_RequestModel), EnrollmentRequestModel.class)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Student with id=" + TestData.NON_EXISTING_STUDENTID + " is not found");
    }
    @Test
    @Order(6)
    void whenAddEnrollment_withInvalidStudentId_thenThrowUnprocessableEntityException() {
        //act
        webTestClient.post()
                .uri("/api/v1/enrollments")
                .contentType(APPLICATION_JSON)
                .body(Mono.just(testData.enrollment_withInvalidStudentId_RequestModel), EnrollmentRequestModel.class)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid student ID: must be 36 characters");

    }
    @Test
    @Order(7)
    void whenAddEnrollment_withInvalidCourseId_thenThrowUnprocessableEntityException() {
        webTestClient.post()
                .uri("/api/v1/enrollments")
                .contentType(APPLICATION_JSON)
                .body(Mono.just(testData.enrollment_withInvalidCourseId_RequestModel), EnrollmentRequestModel.class)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid course ID: must be 36 characters");
    }
    @Test
    @Order(8)
    void whenGetEnrollmentByEnrollmentId_withExistingEnrollmentId_thenReturnEnrollmentResponseModel() {
        webTestClient.get()
                .uri("/api/v1/enrollments/"+ testData.enrollment1.getEnrollmentId())
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .value(enrollmentResponseModel -> {
                    assertNotNull(enrollmentResponseModel);
                    assertEquals(testData.enrollment1.getEnrollmentId(), enrollmentResponseModel.enrollmentId());
                    assertEquals(testData.enrollment1.getEnrollmentYear(), enrollmentResponseModel.enrollmentYear());
                    assertEquals(testData.enrollment1.getSemester(), enrollmentResponseModel.semester());
                });
    }

    @Test
    @Order(9)
    void whenGetEnrollmentByEnrollmentId_withNonExistingEnrollmentId_thenThrowNotFoundException() {
        webTestClient.get()
                .uri("/api/v1/enrollments/{id}", TestData.NON_EXISTING_ENROLLMENTID)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Enrollment with id=" + TestData.NON_EXISTING_ENROLLMENTID + " is not found");
    }

    @Test
    @Order(10)
    void whenGetEnrollmentByEnrollmentId_withInvalidEnrollment_thenThrowUnprocessableEntityException() {
        webTestClient.get()
                .uri("/api/v1/enrollments/{id}", TestData.INVALID_ENROLLMENTID)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Provided enrollment id is invalid: " + TestData.INVALID_ENROLLMENTID);
    }
    @Test
    @Order(11)
    void whenUpdateEnrollment_withExistingEnrollmentId_thenReturnEnrollmentResponseModel()  {
        // Arrange
        try {
            mockGetStudentByStudentIdSuccess(testData.student2ResponseModel);
            mockGetCourseByCourseIdSuccess(testData.course2ResponseModel);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        webTestClient.put()
                .uri("/api/v1/enrollments/" + testData.enrollment1.getEnrollmentId())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(Mono.just(testData.enrollment2RequestModel), EnrollmentRequestModel.class)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .value(enrollmentResponseModel -> {
                    assertNotNull(enrollmentResponseModel);
                    assertEquals(testData.enrollment1.getEnrollmentId(), enrollmentResponseModel.enrollmentId());
                    assertEquals(testData.enrollment2RequestModel.enrollmentYear(), enrollmentResponseModel.enrollmentYear());
                    assertEquals(testData.enrollment2RequestModel.semester(), enrollmentResponseModel.semester());
                });
    }

    @Test
    @Order(12)
    void whenUpdateEnrollment_withNonExisting_thenThrowNotFoundException()  {
        try {
            mockGetStudentByStudentIdSuccess(testData.student2ResponseModel);
            mockGetCourseByCourseIdSuccess(testData.course2ResponseModel);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //act
        webTestClient.put()
                .uri("/api/v1/enrollments/" + TestData.NON_EXISTING_ENROLLMENTID)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(Mono.just(testData.enrollment2RequestModel), EnrollmentRequestModel.class)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Enrollment with id=" + TestData.NON_EXISTING_ENROLLMENTID + " is not found");
    }

    @Test
    @Order(13)
    void whenUpdateEnrollment_withInvalidEnrollmentId_thenThrowUnprocessableEntityException() {
        webTestClient.put()
                .uri("/api/v1/enrollments/" + TestData.INVALID_ENROLLMENTID)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(Mono.just(testData.enrollment2RequestModel), EnrollmentRequestModel.class)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Provided enrollment id is invalid: " + TestData.INVALID_ENROLLMENTID);
    }

    @Test
    @Order(14)
    public void whenGetAllEnrollments_thenReturnFluxOfTwoEnrollmentResponseModels() {
        Long expectedCount = enrollmentRepository.count().block();

        webTestClient.get()
                .uri("/api/v1/enrollments")
                .accept(org.springframework.http.MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(EnrollmentResponseModel.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .expectNextCount(expectedCount)
                .verifyComplete();
    }

    @Test
    @Order(15)
    public void whenDeleteWithNonExistingEnrollmentId_thenReturnNotFound() {
        webTestClient.delete()
                .uri("/api/v1/enrollments/" + TestData.NON_EXISTING_ENROLLMENTID)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Enrollment with id=" + TestData.NON_EXISTING_ENROLLMENTID + " is not found");
    }

    @Test
    @Order(16)
    public void whenDeleteWithInvalidEnrollmentId_thenReturnUnprocessableEntity() {
        webTestClient.delete()
                .uri("/api/v1/enrollments/" + TestData.INVALID_ENROLLMENTID)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Provided enrollment id is invalid: " + TestData.INVALID_ENROLLMENTID);
    }

    @Test
    @Order(17)
    public void whenDeleteWithExistingEnrollmentId_thenReturnEnrollmentResponseModel() {
        enrollmentRepository.deleteAll().block();


        enrollmentRepository.save(testData.enrollment1).block();

        webTestClient.delete()
                .uri("/api/v1/enrollments/" + testData.enrollment1.getEnrollmentId())
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .value(enrollmentResponseModel -> {
                    assertNotNull(enrollmentResponseModel);
                    assertEquals(testData.enrollment1.getEnrollmentId(), enrollmentResponseModel.enrollmentId());
                });

        // Verify deletion
        StepVerifier
                .create(enrollmentRepository.findEnrollmentByEnrollmentId(testData.enrollment1.getEnrollmentId()))
                .expectComplete()
                .verify();
    }
    @Test
    @Order(18)
    void whenAddEnrollment_withMissingSemester_thenThrowUnprocessableEntityException() {
        var req = new com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel(
                2023,
                null, // <- missing Semester
                testData.student1ResponseModel.studentId(),
                testData.course1ResponseModel.courseId()
        );

        webTestClient.post()
                .uri("/api/v1/enrollments")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(reactor.core.publisher.Mono.just(req),
                        com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel.class)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Semester is required");
    }

    @Test
    @Order(19)
    void whenAddEnrollment_withInvalidEnrollmentYear_thenThrowUnprocessableEntityException() {
        var req = new com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel(
                1999, // <- below 2000
                com.champlain.enrollmentsservice.dataaccesslayer.Semester.FALL,
                testData.student1ResponseModel.studentId(),
                testData.course1ResponseModel.courseId()
        );

        webTestClient.post()
                .uri("/api/v1/enrollments")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(reactor.core.publisher.Mono.just(req),
                        com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel.class)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectHeader().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Enrollment year must be between 2000 and this year + 1");
    }
}







