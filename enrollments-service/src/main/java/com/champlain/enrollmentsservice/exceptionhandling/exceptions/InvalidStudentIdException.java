package com.champlain.enrollmentsservice.exceptionhandling.exceptions;

public class InvalidStudentIdException extends RuntimeException {

    private static final String MESSAGE = "Invalid student ID: must be 36 characters";

    public InvalidStudentIdException() {}

    public InvalidStudentIdException(String courseId) { super(MESSAGE); }

    public InvalidStudentIdException(Throwable cause) { super(cause); }

    public InvalidStudentIdException(String courseId, Throwable cause) { super(MESSAGE,cause); }

}
