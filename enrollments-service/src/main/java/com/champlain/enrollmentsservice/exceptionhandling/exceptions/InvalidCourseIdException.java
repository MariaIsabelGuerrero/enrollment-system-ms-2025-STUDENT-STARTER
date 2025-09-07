package com.champlain.enrollmentsservice.exceptionhandling.exceptions;

public class InvalidCourseIdException extends RuntimeException {

    private static final String MESSAGE = "Course id=%s is invalid";

    public InvalidCourseIdException() {}

    public InvalidCourseIdException(String courseId) { super(MESSAGE.formatted(courseId)); }

    public InvalidCourseIdException(Throwable cause) { super(cause); }

    public InvalidCourseIdException(String courseId, Throwable cause) { super(MESSAGE.formatted(courseId), cause); }

}
