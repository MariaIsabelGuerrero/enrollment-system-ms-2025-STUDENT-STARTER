package com.champlain.courseservice.presentationlayer;

public record CourseResponseModel(String courseId,
                                  String courseNumber,
                                  String courseName,
                                  Integer numHours,
                                  Double numCredits,
                                  String department) {
}
