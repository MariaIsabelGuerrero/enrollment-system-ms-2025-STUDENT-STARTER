package com.champlain.studentsservice.datamapperlayer;

import com.champlain.studentsservice.dataaccesslayer.Student;
import com.champlain.studentsservice.presentationlayer.StudentResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentResponseMapper {

    @Mapping(expression = "java(student.getStudentIdentifier().getStudentId())", target = "studentId")
    StudentResponseModel entityToResponseModel(Student student);

    List<StudentResponseModel> entityListToResponseModelList(List<Student> students);
}
