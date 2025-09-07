package com.champlain.enrollmentsservice.dataaccesslayer;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EnrollmentRepository extends ReactiveMongoRepository<Enrollment, String> {

    Mono<Enrollment> findEnrollmentByEnrollmentId(String enrollmentId);
}
