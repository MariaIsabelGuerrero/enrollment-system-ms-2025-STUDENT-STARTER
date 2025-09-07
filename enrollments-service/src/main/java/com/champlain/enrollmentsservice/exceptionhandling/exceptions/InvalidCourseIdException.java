package com.champlain.enrollmentsservice.exceptionhandling.exceptions;

public class InvalidCourseIdException extends RuntimeException {

    private static final String MESSAGE = "Invalid course ID: must be 36 characters";

    public InvalidCourseIdException() {}

    public InvalidCourseIdException(String courseId) { super(MESSAGE); }

    public InvalidCourseIdException(Throwable cause) { super(cause); }

    public InvalidCourseIdException(String courseId, Throwable cause) { super(MESSAGE,cause); }

}
