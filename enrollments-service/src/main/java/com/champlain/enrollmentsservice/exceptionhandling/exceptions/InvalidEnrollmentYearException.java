package com.champlain.enrollmentsservice.exceptionhandling.exceptions;

public class InvalidEnrollmentYearException extends RuntimeException {

    public InvalidEnrollmentYearException() {}

    public InvalidEnrollmentYearException(String message) { super(message); }

    public InvalidEnrollmentYearException(Throwable cause) { super(cause); }

    public InvalidEnrollmentYearException(String message, Throwable cause) { super(message, cause); }

}
