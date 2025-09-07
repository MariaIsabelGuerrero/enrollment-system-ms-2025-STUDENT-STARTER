package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.businesslayer.enrollments.EnrollmentService;
import com.champlain.enrollmentsservice.exceptionhandling.exceptions.InvalidInputException;
import com.champlain.enrollmentsservice.validation.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/enrollments")
@Slf4j
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EnrollmentResponseModel> getEnrollments() {
        return enrollmentService.getEnrollments();
    }

    @GetMapping(value = "/{enrollmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EnrollmentResponseModel>> getEnrollmentByEnrollmentId(@PathVariable String enrollmentId) {
        return Mono.just(enrollmentId)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(Mono.error(new InvalidInputException("Provided enrollment id is invalid: " + enrollmentId)))
                .flatMap(enrollmentService::getEnrollmentByEnrollmentId)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EnrollmentResponseModel>> addEnrollment(@RequestBody Mono<EnrollmentRequestModel> enrollmentRequestModel) {
        return enrollmentRequestModel
                .transform(RequestValidator.validateBody())
                .as(enrollmentService::addEnrollment)
                .map(e -> ResponseEntity.status(HttpStatus.CREATED).body(e))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping(value = "/{enrollmentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EnrollmentResponseModel>> updateEnrollmentByEnrollmentId(@PathVariable String enrollmentId, @RequestBody Mono<EnrollmentRequestModel> enrollmentRequestModel) {
        return Mono.just(enrollmentId)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(Mono.error(new InvalidInputException("Provided enrollment id is invalid: " + enrollmentId)))
                .flatMap(id -> enrollmentRequestModel
                        .transform(RequestValidator.validateBody())
                        .as(request -> enrollmentService.updateEnrollmentByEnrollmentId(request, id)))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping(value = "/{enrollmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EnrollmentResponseModel>> deleteEnrollmentByEnrollmentId(@PathVariable String enrollmentId) {
        return Mono.just(enrollmentId)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(Mono.error(new InvalidInputException("Provided enrollment id is invalid: " + enrollmentId)))
                .flatMap(enrollmentService::deleteEnrollmentByEnrollmentId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }


}
