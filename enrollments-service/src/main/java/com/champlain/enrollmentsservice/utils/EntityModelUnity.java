package com.champlain.enrollmentsservice.utils;

import com.champlain.enrollmentsservice.businesslayer.enrollments.RequestContext;
import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;

import java.util.UUID;

public class EntityModelUnity {
    public static Enrollment toEntity(RequestContext rc) {
        return Enrollment.builder()
                .enrollmentId(generateUUIDString())
                .enrollmentYear(rc.getEnrollmentRequestModel().enrollmentYear())
                .semester(rc.getEnrollmentRequestModel().semester())
                .studentId(rc.getStudentResponseModel().studentId())
                .studentFirstName(rc.getStudentResponseModel().firstName())
                .studentLastName(rc.getStudentResponseModel().lastName())
                .courseId(rc.getCourseResponseModel().courseId())
                .courseName(rc.getCourseResponseModel().courseName())
                .courseNumber(rc.getCourseResponseModel().courseNumber())
                .build();
    }
    public static EnrollmentResponseModel toModel(Enrollment enrollment) {
        return new EnrollmentResponseModel(
                enrollment.getEnrollmentId(),
                enrollment.getEnrollmentYear(),
                enrollment.getSemester(),
                enrollment.getStudentId(),
                enrollment.getStudentFirstName(),
                enrollment.getStudentLastName(),
                enrollment.getCourseId(),
                enrollment.getCourseNumber(),
                enrollment.getCourseName()
        );
    }
    public static String generateUUIDString() {
        return UUID.randomUUID().toString();
    }
}
