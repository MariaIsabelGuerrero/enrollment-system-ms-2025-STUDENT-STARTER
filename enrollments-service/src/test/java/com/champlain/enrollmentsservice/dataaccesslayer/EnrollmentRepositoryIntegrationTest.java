package com.champlain.enrollmentsservice.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@ActiveProfiles("test")
public class EnrollmentRepositoryIntegrationTest {
    @Autowired
    private EnrollmentRepository enrollmentRepository;


    @BeforeEach
    void setUpDB() {
        StepVerifier.create(enrollmentRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    void findEnrollmentByEnrollmentId_withExistingId_shouldReturnEnrollment() {
        // Arrange - POSITIVE TEST
        String enrollmentId = UUID.randomUUID().toString();
        Enrollment enrollment = Enrollment.builder()
                .enrollmentId(enrollmentId)
                .studentId(UUID.randomUUID().toString())
                .courseId(UUID.randomUUID().toString())
                .semester(Semester.FALL)
                .enrollmentYear(2025)
                .build();

        // Act
        StepVerifier.create(enrollmentRepository.save(enrollment))
                .consumeNextWith(savedEnrollment -> {
                    assertNotNull(savedEnrollment);
                    assertEquals(enrollment.getEnrollmentId(), savedEnrollment.getEnrollmentId());
                })
                .verifyComplete();

        StepVerifier.create(enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId))
                .consumeNextWith(foundEnrollment -> {
                    assertNotNull(foundEnrollment);
                    assertEquals(enrollmentId, foundEnrollment.getEnrollmentId());
                    assertEquals(enrollment.getStudentId(), foundEnrollment.getStudentId());
                    assertEquals(enrollment.getCourseId(), foundEnrollment.getCourseId());
                    assertEquals(enrollment.getSemester(), foundEnrollment.getSemester());
                    assertEquals(enrollment.getEnrollmentYear(), foundEnrollment.getEnrollmentYear());
                })
                .verifyComplete();
    }

    @Test
    void findEnrollmentByEnrollmentId_withNonExistingId_shouldReturnEmptyMono() {
        // Arrange - NEGATIVE TEST
        String nonExistingId = UUID.randomUUID().toString();

        // Act & Assert
        StepVerifier
                .create(enrollmentRepository.findEnrollmentByEnrollmentId(nonExistingId))
                .expectNextCount(0)
                .verifyComplete();
    }


}
