package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.dataaccesslayer.Semester;


public record EnrollmentResponseModel  (String enrollmentId, Integer enrollmentYear,
    Semester semester,
    String studentId,
    String studentFirstName,
    String studentLastName,
    String courseId,
    String courseNumber,
    String courseName){
}

