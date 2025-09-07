package com.champlain.enrollmentsservice.exceptionhandling.exceptions;

public class StudentNotFoundException extends RuntimeException{

    private static final String MESSAGE = "Student with id=%s is not found";

    public StudentNotFoundException() {}

    public StudentNotFoundException(String courseId) { super(MESSAGE.formatted(courseId)); }

    public StudentNotFoundException(Throwable cause) { super(cause); }

    public StudentNotFoundException(String courseId, Throwable cause) { super(MESSAGE.formatted(courseId), cause); }
}
