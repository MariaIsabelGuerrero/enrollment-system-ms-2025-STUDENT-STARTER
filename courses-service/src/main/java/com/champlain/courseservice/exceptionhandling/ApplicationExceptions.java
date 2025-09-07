package com.champlain.courseservice.exceptionhandling;

import com.champlain.courseservice.exceptionhandling.exceptions.CourseNotFoundException;
import com.champlain.courseservice.exceptionhandling.exceptions.InvalidCourseIdException;
import com.champlain.courseservice.exceptionhandling.exceptions.InvalidInputException;
import reactor.core.publisher.Mono;

public class ApplicationExceptions {

    public static <T> Mono<T> courseNotFound(String courseId) {
        return Mono.error(new CourseNotFoundException(courseId));
    }

    public static <T> Mono<T> missingCourseNumber() {
        return Mono.error(new InvalidInputException("Course number is required"));
    }

    public static <T> Mono<T> missingCourseName() {
        return Mono.error(new InvalidInputException("Course name is required"));
    }

    // ADD THIS METHOD
    public static <T> Mono<T> missingNumCredits() {
        return Mono.error(new InvalidInputException("Course credits is required"));
    }

    public static <T> Mono<T> invalidCourseCredits() {
        return Mono.error(new InvalidInputException("Course credits must be greater than 0"));
    }

    // ADD THIS METHOD
    public static <T> Mono<T> missingNumHours() {
        return Mono.error(new InvalidInputException("Course hours is required"));
    }

    public static <T> Mono<T> invalidCourseHours() {
        return Mono.error(new InvalidInputException("Course hours must be greater than 0"));
    }

    public static <T> Mono<T> invalidCourseId(String courseId) {
        return Mono.error(new InvalidCourseIdException(courseId));
    }
}