package com.champlain.enrollmentsservice.exceptionhandling.exceptions;

public class InvalidEnrollmentRequestException extends RuntimeException {


    public InvalidEnrollmentRequestException() {}

    public InvalidEnrollmentRequestException(String message) { super(message); }

    public InvalidEnrollmentRequestException(Throwable cause) { super(cause); }

    public InvalidEnrollmentRequestException(String message, Throwable cause) { super(message, cause); }

}
