package com.champlain.enrollmentsservice;

import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import com.champlain.enrollmentsservice.dataaccesslayer.Semester;
import com.champlain.enrollmentsservice.domainclientlayer.courses.CourseResponseModel;
import com.champlain.enrollmentsservice.domainclientlayer.students.StudentResponseModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;

public class TestData {

    public final Long dbSize = 2L;

    public final static String NON_EXISTING_ENROLLMENTID = "169de995-ef94-4dd1-9ea9-8f6a571fb0dd";
    public final static String INVALID_ENROLLMENTID = "Enrollment123";

    public final static String NON_EXISTING_STUDENTID = "5651e83d-ef7e-470a-a02b-685b1f445d3b";
    public final static String INVALID_STUDENTID = "Student123";

    public final static String NON_EXISTING_COURSEID = "d0846f4e-c3a7-4f58-b9d8-5521f45657c2";
    public final static String INVALID_COURSEID = "Course123";

    public StudentResponseModel student1ResponseModel = new StudentResponseModel(
            "c3540a89-cb47-4c96-888e-ff96708db4d8",
            "Donna",
            "Hornsby",
            "History",
            "stuff"
    );

    //for update request
    public StudentResponseModel student2ResponseModel = new StudentResponseModel(
            "1f538db7-320a-4415-bad4-e1d44518b1ff",
            "Willis",
            "Faraday",
            "Pure and Applied Sciences",
            "stuff"
    );

    //for invalid request
    public StudentResponseModel student3ResponseModel = new StudentResponseModel(
            "c3540a89-cb47-4c96-888e-ff96708db4d",
            "Donna",
            "Hornsby",
            "History",
            "stuff"
    );

    public CourseResponseModel course1ResponseModel = new CourseResponseModel(
            "9a29fff7-564a-4cc9-8fe1-36f6ca9bc223",
            "Web Services",
            "N45-LA",
            60,
            2.0,
            "Computer Science"
    );

    //for update request
    public CourseResponseModel course2ResponseModel = new CourseResponseModel(
            "8d764f78-8468-4769-b643-10cde392fbde",
            "xud-857",
            "Waves",
            60,
            2.5,
            "Physics"
    );

    public EnrollmentRequestModel enrollment1RequestModel = new EnrollmentRequestModel(
            2021,
            Semester.FALL,
            student1ResponseModel.studentId(),
            course1ResponseModel.courseId()
    );

    //for update request
    public EnrollmentRequestModel enrollment2RequestModel = new EnrollmentRequestModel(
            2023,
            Semester.FALL,
            student2ResponseModel.studentId(),
            course2ResponseModel.courseId()
    );

    //for non-existing studentId
    public EnrollmentRequestModel enrollment_withNonExistingStudentId_RequestModel = new EnrollmentRequestModel(
            2023,
            Semester.FALL,
            NON_EXISTING_STUDENTID,
            course2ResponseModel.courseId()
    );

    //for invalid studentId
    public EnrollmentRequestModel enrollment_withInvalidStudentId_RequestModel = new EnrollmentRequestModel(
            2023,
            Semester.FALL,
            INVALID_STUDENTID,
            course2ResponseModel.courseId()
    );


    //for non-existing courseId
    public EnrollmentRequestModel enrollment_withNonExistingCourseId_RequestModel = new EnrollmentRequestModel(
            2023,
            Semester.FALL,
            student1ResponseModel.studentId(),
            NON_EXISTING_COURSEID
    );

    //for invalid courseId
    public EnrollmentRequestModel enrollment_withInvalidCourseId_RequestModel = new EnrollmentRequestModel(
            2023,
            Semester.FALL,
            student1ResponseModel.studentId(),
            INVALID_COURSEID
    );

    //for database initialization
    public Enrollment enrollment1 = Enrollment.builder()
            .enrollmentId("06a7d573-bcab-4db3-956f-773324b92a80")
            .enrollmentYear(2021)
            .semester(Semester.FALL)
            .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
            .studentFirstName("Christine")
            .studentLastName("Gerard")
            .courseId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
            .courseNumber("trs-075")
            .courseName("Web Services")
            .build();

    public Enrollment enrollment2 = Enrollment.builder()
            .enrollmentId("98f7b33a-d62a-420a-a84a-05a27c85fc91")
            .enrollmentYear(2021)
            .semester(Semester.FALL)
            .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
            .studentFirstName("Christine")
            .studentLastName("Gerard")
            .courseId("d819e4f4-25af-4d33-91e9-2c45f0071606")
            .courseNumber("ygo-675")
            .courseName("Shakespeare's Greatest Works")
            .build();

}
