package com.champlain.studentsservice.presentationlayer;

import com.champlain.studentsservice.businesslayer.StudentService;
import com.champlain.studentsservice.utils.exceptions.InvalidInputException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping(value = "",
    produces = "application/json")
    public ResponseEntity<List<StudentResponseModel>> getStudents() {
        return ResponseEntity.ok().body(studentService.getStudents());
    }

    //get by database rowId
    @GetMapping(value = "/row/{id}", produces = "application/json")
    public ResponseEntity<StudentResponseModel> getStudentByRowId(@PathVariable Integer id) {
        return ResponseEntity.ok().body(studentService.getStudentByRowId(id));
    }

    //get by studentId
    @GetMapping(value = "/{studentId}", produces = "application/json")
    public ResponseEntity<StudentResponseModel> getStudentByStudentId(@PathVariable String studentId) {
        if (studentId.length() != 36) {
            throw new InvalidInputException("Invalid studentId: " + studentId);
        }
        return ResponseEntity.ok().body(studentService.getStudentByStudentId(studentId));
    }
    
}
