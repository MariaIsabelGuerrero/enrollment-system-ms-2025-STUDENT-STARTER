package com.champlain.enrollmentsservice.exceptionhandling.exceptions;

public class EnrollmentNotFoundException extends RuntimeException{

    private static final String MESSAGE = "Enrollment with id=%s is not found";

    public EnrollmentNotFoundException() {}

    public EnrollmentNotFoundException(String courseId) { super(MESSAGE.formatted(courseId)); }

    public EnrollmentNotFoundException(Throwable cause) { super(cause); }

    public EnrollmentNotFoundException(String courseId, Throwable cause) { super(MESSAGE.formatted(courseId), cause); }
}
