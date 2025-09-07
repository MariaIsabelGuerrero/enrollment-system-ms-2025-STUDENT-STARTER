package com.champlain.studentsservice.presentationlayer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseModel {

    private String studentId;
    private String firstName;
    private String lastName;
    private String program;
    private String stuff;
}
