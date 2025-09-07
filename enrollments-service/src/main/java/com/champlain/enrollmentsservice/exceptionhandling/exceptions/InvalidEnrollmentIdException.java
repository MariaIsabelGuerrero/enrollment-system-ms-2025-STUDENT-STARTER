package com.champlain.enrollmentsservice.exceptionhandling.exceptions;

public class InvalidEnrollmentIdException extends RuntimeException {

    private static final String MESSAGE = "Enrollment id=%s is invalid";

    public InvalidEnrollmentIdException() {}

    public InvalidEnrollmentIdException(String courseId) { super(MESSAGE.formatted(courseId)); }

    public InvalidEnrollmentIdException(Throwable cause) { super(cause); }

    public InvalidEnrollmentIdException(String courseId, Throwable cause) { super(MESSAGE.formatted(courseId), cause); }

}
