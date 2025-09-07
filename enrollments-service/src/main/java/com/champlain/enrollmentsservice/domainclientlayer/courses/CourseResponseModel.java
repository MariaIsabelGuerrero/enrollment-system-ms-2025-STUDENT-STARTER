package com.champlain.enrollmentsservice.domainclientlayer.courses;

public record CourseResponseModel(String courseId,
                                  String courseNumber,
                                  String courseName,
                                  Integer numHours,
                                  Double numCredits,
                                  String department) {
}
