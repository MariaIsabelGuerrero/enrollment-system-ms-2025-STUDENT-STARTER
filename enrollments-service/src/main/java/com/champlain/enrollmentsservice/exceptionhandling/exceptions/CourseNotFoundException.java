package com.champlain.enrollmentsservice.exceptionhandling.exceptions;

public class CourseNotFoundException extends RuntimeException{

    private static final String MESSAGE = "Course with id=%s is not found";

    public CourseNotFoundException() {}

    public CourseNotFoundException(String courseId) { super(MESSAGE.formatted(courseId)); }

    public CourseNotFoundException(Throwable cause) { super(cause); }

    public CourseNotFoundException(String courseId, Throwable cause) { super(MESSAGE.formatted(courseId), cause); }
}
