package com.champlain.enrollmentsservice.dataaccesslayer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment {

    @Id
    private String id;
    private String enrollmentId;
    private Integer enrollmentYear;
    private Semester semester;
    private String studentId;
    private String studentFirstName;
    private String studentLastName;
    private String courseId;
    private String courseNumber;
    private String courseName;
}
