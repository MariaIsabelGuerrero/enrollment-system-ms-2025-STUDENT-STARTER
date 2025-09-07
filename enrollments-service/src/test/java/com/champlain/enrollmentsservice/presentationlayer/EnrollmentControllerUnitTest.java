package com.champlain.enrollmentsservice.presentationlayer;

import com.champlain.enrollmentsservice.TestData;
import com.champlain.enrollmentsservice.businesslayer.enrollments.EnrollmentService;
import com.champlain.enrollmentsservice.dataaccesslayer.Semester;
import com.champlain.enrollmentsservice.exceptionhandling.exceptions.EnrollmentNotFoundException;
import com.champlain.enrollmentsservice.exceptionhandling.exceptions.InvalidInputException;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentController;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentControllerUnitTest {

    @InjectMocks
    private EnrollmentController enrollmentController;
    @Mock
    private EnrollmentService enrollmentService;

    private final TestData testData = new TestData();



    @Test
    void whenGetEnrollmentByEnrollmentId_nonExisting_thenEnrollmentNotFoundException() {
        when(enrollmentService.getEnrollmentByEnrollmentId(TestData.NON_EXISTING_ENROLLMENTID))
                .thenReturn(Mono.error(new EnrollmentNotFoundException(TestData.NON_EXISTING_ENROLLMENTID)));

        Mono<ResponseEntity<EnrollmentResponseModel>> result = enrollmentController
                .getEnrollmentByEnrollmentId(TestData.NON_EXISTING_ENROLLMENTID);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof EnrollmentNotFoundException &&
                        e.getMessage().equals("Enrollment with id=" + TestData.NON_EXISTING_ENROLLMENTID + " is not found"))
                .verify();
    }

    @Test
    void getEnrollmentByEnrollmentId_validEnrollmentId_enrollmentReturned() {
        //arrange
        EnrollmentResponseModel enrollmentResponseModel = new EnrollmentResponseModel(
                "06a7d573-bcab-4db3-956f-773324b92a80",
                2021,
                Semester.FALL,
                "c3540a89-cb47-4c96-888e-ff96708db4d8",
                "Christine",
                "Gerard",
                "9a29fff7-564a-4cc9-8fe1-36f6ca9bc223",
                "trs-075",
                "Web Services"
        );

        when(enrollmentService.getEnrollmentByEnrollmentId("06a7d573-bcab-4db3-956f-773324b92a80"))
                .thenReturn(Mono.just(enrollmentResponseModel));

        //act
        Mono<ResponseEntity<EnrollmentResponseModel>> result = enrollmentController
                .getEnrollmentByEnrollmentId("06a7d573-bcab-4db3-956f-773324b92a80");

        //assert
        StepVerifier.create(result)
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode().is2xxSuccessful() &&
                                responseEntity.getBody() != null &&
                                responseEntity.getBody().enrollmentId().equals("06a7d573-bcab-4db3-956f-773324b92a80"))
                .verifyComplete();
    }

    @Test
    void addEnrollment_validEnrollment_enrollmentAdded() {
        //arrange
        EnrollmentResponseModel enrollmentResponseModel = new EnrollmentResponseModel(
                "06a7d573-bcab-4db3-956f-773324b92a80",
                2021,
                Semester.FALL,
                "c3540a89-cb47-4c96-888e-ff96708db4d8",
                "Christine",
                "Gerard",
                "9a29fff7-564a-4cc9-8fe1-36f6ca9bc223",
                "trs-075",
                "Web Services"
        );

        when(enrollmentService.addEnrollment(any(Mono.class)))
                .thenReturn(Mono.just(enrollmentResponseModel));

        //act
        Mono<ResponseEntity<EnrollmentResponseModel>> result = enrollmentController
                .addEnrollment(Mono.just(testData.enrollment1RequestModel));

        //assert
        StepVerifier.create(result)
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode().is2xxSuccessful() &&
                                responseEntity.getBody() != null &&
                                responseEntity.getBody().enrollmentYear().equals(2021))
                .verifyComplete();
    }

    @Test
    public void getEnrollmentByEnrollmentId_withInvalidEnrollmentId_throwsNotFoundException() {

        //act
        Mono<ResponseEntity<EnrollmentResponseModel>> result = enrollmentController
                .getEnrollmentByEnrollmentId(TestData.INVALID_ENROLLMENTID);

        //assert
        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof InvalidInputException &&
                        e.getMessage().equals("Provided enrollment id is invalid: " + TestData.INVALID_ENROLLMENTID))
                .verify();
    }

    @Test
    public void getAllEnrollments_validEnrollments_enrollmentsReturned() {
        //arrange
        EnrollmentResponseModel enrollment1 = new EnrollmentResponseModel(
                "06a7d573-bcab-4db3-956f-773324b92a80",
                2021,
                Semester.FALL,
                "c3540a89-cb47-4c96-888e-ff96708db4d8",
                "Christine",
                "Gerard",
                "9a29fff7-564a-4cc9-8fe1-36f6ca9bc223",
                "trs-075",
                "Web Services"
        );

        EnrollmentResponseModel enrollment2 = new EnrollmentResponseModel(
                "98f7b33a-d62a-420a-a84a-05a27c85fc91",
                2021,
                Semester.FALL,
                "c3540a89-cb47-4c96-888e-ff96708db4d8",
                "Christine",
                "Gerard",
                "d819e4f4-25af-4d33-91e9-2c45f0071606",
                "ygo-675",
                "Shakespeare's Greatest Works"
        );

        when(enrollmentService.getEnrollments())
                .thenReturn(Flux.just(enrollment1, enrollment2));

        //act
        Flux<EnrollmentResponseModel> result = enrollmentController.getEnrollments();

        //assert
        StepVerifier.create(result)
                .expectNext(enrollment1)
                .expectNext(enrollment2)
                .verifyComplete();
    }

    @Test
    public void updateEnrollment_validEnrollment_enrollmentUpdated() {
        //arrange
        EnrollmentResponseModel updatedEnrollment = new EnrollmentResponseModel(
                "06a7d573-bcab-4db3-956f-773324b92a80",
                2023,
                Semester.FALL,
                "1f538db7-320a-4415-bad4-e1d44518b1ff",
                "Willis",
                "Faraday",
                "8d764f78-8468-4769-b643-10cde392fbde",
                "xud-857",
                "Waves"
        );

        when(enrollmentService.updateEnrollmentByEnrollmentId(
                any(Mono.class),
                eq("06a7d573-bcab-4db3-956f-773324b92a80")))
                .thenReturn(Mono.just(updatedEnrollment));

        //act
        Mono<ResponseEntity<EnrollmentResponseModel>> result = enrollmentController
                .updateEnrollmentByEnrollmentId("06a7d573-bcab-4db3-956f-773324b92a80", Mono.just(testData.enrollment2RequestModel));

        //assert
        StepVerifier.create(result)
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode().is2xxSuccessful() &&
                                responseEntity.getBody() != null &&
                                responseEntity.getBody().enrollmentYear().equals(2023))
                .verifyComplete();
    }

    @Test
    public void updateEnrollment_withInvalidEnrollmentId_throwsInvalidInputException() {
        //act
        Mono<ResponseEntity<EnrollmentResponseModel>> result = enrollmentController
                .updateEnrollmentByEnrollmentId(TestData.INVALID_ENROLLMENTID, Mono.just(testData.enrollment2RequestModel));

        //assert
        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof InvalidInputException &&
                        e.getMessage().equals("Provided enrollment id is invalid: " + TestData.INVALID_ENROLLMENTID))
                .verify();
    }

    @Test
    public void deleteEnrollment_validEnrollmentId_enrollmentDeleted() {
        //arrange
        EnrollmentResponseModel deletedEnrollment = new EnrollmentResponseModel(
                "06a7d573-bcab-4db3-956f-773324b92a80",
                2021,
                Semester.FALL,
                "c3540a89-cb47-4c96-888e-ff96708db4d8",
                "Christine",
                "Gerard",
                "9a29fff7-564a-4cc9-8fe1-36f6ca9bc223",
                "trs-075",
                "Web Services"
        );

        when(enrollmentService.deleteEnrollmentByEnrollmentId("06a7d573-bcab-4db3-956f-773324b92a80"))
                .thenReturn(Mono.just(deletedEnrollment));

        //act
        Mono<ResponseEntity<EnrollmentResponseModel>> result = enrollmentController
                .deleteEnrollmentByEnrollmentId("06a7d573-bcab-4db3-956f-773324b92a80");

        //assert
        StepVerifier.create(result)
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode().is2xxSuccessful() &&
                                responseEntity.getBody() != null &&
                                responseEntity.getBody().enrollmentId().equals("06a7d573-bcab-4db3-956f-773324b92a80"))
                .verifyComplete();
    }

    @Test
    public void deleteEnrollment_withInvalidEnrollmentId_throwsInvalidInputException() {

        //act
        Mono<ResponseEntity<EnrollmentResponseModel>> result = enrollmentController
                .deleteEnrollmentByEnrollmentId(TestData.INVALID_ENROLLMENTID);

        //assert
        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof InvalidInputException &&
                        e.getMessage().equals("Provided enrollment id is invalid: " + TestData.INVALID_ENROLLMENTID))
                .verify();
    }

}