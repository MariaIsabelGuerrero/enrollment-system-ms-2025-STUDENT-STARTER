package com.champlain.courseservice.presentationlayer;

public record CourseRequestModel(String courseNumber,
                                 String courseName,
                                 Integer numHours,
                                 Double numCredits,
                                 String department) {
}
