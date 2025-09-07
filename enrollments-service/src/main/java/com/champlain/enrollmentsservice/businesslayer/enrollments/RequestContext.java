package com.champlain.enrollmentsservice.businesslayer.enrollments;

import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import com.champlain.enrollmentsservice.domainclientlayer.courses.CourseResponseModel;
import com.champlain.enrollmentsservice.domainclientlayer.students.StudentResponseModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestContext {
    private EnrollmentRequestModel enrollmentRequestModel;
    private Enrollment enrollment;
    private StudentResponseModel studentResponseModel;
    private CourseResponseModel courseResponseModel;

    public RequestContext(EnrollmentRequestModel enrollmentRequestModel) {
        this.enrollmentRequestModel = enrollmentRequestModel;
    }

}
