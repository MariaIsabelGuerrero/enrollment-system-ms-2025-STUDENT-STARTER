package com.champlain.studentsservice.businesslayer;

import com.champlain.studentsservice.presentationlayer.StudentResponseModel;

import java.util.List;

public interface StudentService {

    List<StudentResponseModel> getStudents();
    StudentResponseModel getStudentByRowId(Integer id);
    StudentResponseModel getStudentByStudentId(String studentId);
}
