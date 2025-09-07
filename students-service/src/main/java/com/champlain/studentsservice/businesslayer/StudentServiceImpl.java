package com.champlain.studentsservice.businesslayer;

import com.champlain.studentsservice.dataaccesslayer.Student;
import com.champlain.studentsservice.dataaccesslayer.StudentRepository;
import com.champlain.studentsservice.datamapperlayer.StudentResponseMapper;
import com.champlain.studentsservice.presentationlayer.StudentResponseModel;
import com.champlain.studentsservice.utils.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService{

    private final StudentResponseMapper studentResponseMapper;
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentResponseMapper studentResponseMapper, StudentRepository studentRepository) {
        this.studentResponseMapper = studentResponseMapper;
        this.studentRepository = studentRepository;
    }

    @Override
    public List<StudentResponseModel> getStudents() {
        return studentResponseMapper.entityListToResponseModelList(studentRepository.findAll());
    }

    @Override
    public StudentResponseModel getStudentByRowId(Integer id) {
        Student foundStudent = studentRepository.findById(id).orElse(null);

        if (foundStudent == null) {
            throw new NotFoundException("No student at rowId: " + id);
        }

        return studentResponseMapper.entityToResponseModel(foundStudent);
    }

    @Override
    public StudentResponseModel getStudentByStudentId(String studentId) {
        Student foundStudent = studentRepository.findStudentByStudentIdentifier_StudentId(studentId);

        if (foundStudent == null) {
            throw new NotFoundException("Unknown studentId: " + studentId);
        }

        return studentResponseMapper.entityToResponseModel(foundStudent);
    }
}
