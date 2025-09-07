package com.champlain.enrollmentsservice.exceptionhandling;

import com.champlain.enrollmentsservice.exceptionhandling.exceptions.*;
import reactor.core.publisher.Mono;

public class ApplicationExceptions {

    public static <T> Mono<T> enrollmentNotFound(String enrollmentId) {
        return Mono.error(new EnrollmentNotFoundException(enrollmentId));
    }

    public static <T> Mono<T> courseNotFound(String courseId) {
        return Mono.error(new CourseNotFoundException(courseId));
    }

    public static <T> Mono<T> studentNotFound(String studentId) {
        return Mono.error(new StudentNotFoundException(studentId));
    }

    public static <T> Mono<T> invalidCourseId(String courseId) {
        return Mono.error(new InvalidCourseIdException(courseId));
    }

    public static <T> Mono<T> invalidStudentId(String studentId) {
        return Mono.error(new InvalidStudentIdException(studentId));
    }

    public static <T> Mono<T> invalidEnrollmentId(String enrollmentId) {
        return Mono.error(new InvalidEnrollmentIdException(enrollmentId));
    }

    public static <T> Mono<T> invalidEnrollmentYear() {
        return Mono.error(new InvalidEnrollmentYearException("Enrollment year must be between 2000 and this year + 1"));
    }

    public static <T> Mono<T> missingStudentId() {
        return Mono.error(new InvalidEnrollmentRequestException("Student Id is required"));
    }

    public static <T> Mono<T> missingCourseId() {
        return Mono.error(new InvalidEnrollmentRequestException("Course Id is required"));
    }

    public static <T> Mono<T> missingSemester() {
        return Mono.error(new InvalidEnrollmentRequestException("Semester is required"));
    }

}
