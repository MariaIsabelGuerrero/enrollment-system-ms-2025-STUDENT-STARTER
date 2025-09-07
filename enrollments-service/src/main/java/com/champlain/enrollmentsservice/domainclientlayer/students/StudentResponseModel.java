package com.champlain.enrollmentsservice.domainclientlayer.students;

public record StudentResponseModel(String studentId,
                                   String firstName,
                                   String lastName,
                                   String program,
                                   String stuff) {
}
