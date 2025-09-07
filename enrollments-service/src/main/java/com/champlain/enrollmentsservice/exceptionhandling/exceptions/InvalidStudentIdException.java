package com.champlain.enrollmentsservice.exceptionhandling.exceptions;

public class InvalidStudentIdException extends RuntimeException {

    private static final String MESSAGE = "Student id=%s is invalid";

    public InvalidStudentIdException() {}

    public InvalidStudentIdException(String courseId) { super(MESSAGE.formatted(courseId)); }

    public InvalidStudentIdException(Throwable cause) { super(cause); }

    public InvalidStudentIdException(String courseId, Throwable cause) { super(MESSAGE.formatted(courseId), cause); }

}
