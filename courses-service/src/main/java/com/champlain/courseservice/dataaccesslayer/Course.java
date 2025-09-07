package com.champlain.courseservice.dataaccesslayer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "courses")
@AllArgsConstructor
@NoArgsConstructor
public class Course {

    @Id
    private Integer id;

    @Column("course_id")
    private String courseId;

    @Column("course_number")
    private String courseNumber;

    @Column("course_name")
    private String courseName;

    @Column("num_hours")
    private Integer numHours;

    @Column("num_credits")
    private Double numCredits;

    @Column("department")
    private String department;

}

