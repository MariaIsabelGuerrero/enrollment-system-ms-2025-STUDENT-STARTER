package com.champlain.enrollmentsservice.validation;

import com.champlain.enrollmentsservice.exceptionhandling.ApplicationExceptions;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import reactor.core.publisher.Mono;

import java.time.Year;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class RequestValidator {

    public static UnaryOperator<Mono<EnrollmentRequestModel>> validateBody() {
        return enrollmentRequestModel -> enrollmentRequestModel
                .filter(hasStudentId())
                .switchIfEmpty(ApplicationExceptions.missingStudentId())
                .filter(hasValidStudentIdFormat())
                .switchIfEmpty(ApplicationExceptions.invalidStudentId("Invalid student ID format must be 36 characters"))
                .filter(hasCourseId())
                .switchIfEmpty(ApplicationExceptions.missingCourseId())
                .filter(hasValidCourseIdFormat())
                .switchIfEmpty(ApplicationExceptions.invalidCourseId("Invalid course ID format must be 36 characters"))
                .filter(hasSemester())
                .switchIfEmpty(ApplicationExceptions.missingSemester())
                .filter(hasValidEnrollmentYear())
                .switchIfEmpty(ApplicationExceptions.invalidEnrollmentYear());
    }

    private static Predicate<EnrollmentRequestModel> hasStudentId() {
        return enrollmentRequestModel -> Objects.nonNull(enrollmentRequestModel.studentId())
                && !enrollmentRequestModel.studentId().isEmpty();
    }

    private static Predicate<EnrollmentRequestModel> hasValidStudentIdFormat() {
        return enrollmentRequestModel -> enrollmentRequestModel.studentId().length() == 36;
    }

    private static Predicate<EnrollmentRequestModel> hasCourseId() {
        return enrollmentRequestModel -> Objects.nonNull(enrollmentRequestModel.courseId())
                && !enrollmentRequestModel.courseId().isEmpty();
    }

    private static Predicate<EnrollmentRequestModel> hasValidCourseIdFormat() {
        return enrollmentRequestModel -> enrollmentRequestModel.courseId().length() == 36;
    }

    private static Predicate<EnrollmentRequestModel> hasSemester() {
        return enrollmentRequestModel -> Objects.nonNull(enrollmentRequestModel.semester());
    }

    private static Predicate<EnrollmentRequestModel> hasValidEnrollmentYear() {
        return enrollmentRequestModel -> {
            if (Objects.isNull(enrollmentRequestModel.enrollmentYear())) {
                return false;
            }
            int currentYear = Year.now().getValue();
            return enrollmentRequestModel.enrollmentYear() >= 2000
                    && enrollmentRequestModel.enrollmentYear() <= currentYear + 1;
        };
    }
}