package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.dataaccesslayer.Semester;


public record EnrollmentRequestModel (
    Integer enrollmentYear,
    Semester semester,
    String studentId,
    String courseId){
}

