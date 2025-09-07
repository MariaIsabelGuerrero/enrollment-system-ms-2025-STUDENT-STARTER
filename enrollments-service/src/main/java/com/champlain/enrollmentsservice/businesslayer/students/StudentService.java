package com.champlain.enrollmentsservice.businesslayer.students;

import com.champlain.enrollmentsservice.domainclientlayer.students.StudentResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StudentService {
    Flux<StudentResponseModel> getAllStudents();
    List<StudentResponseModel> get1000StudentsSyncLoop();
    List<StudentResponseModel> get1000StudentsSyncParallel();
    Flux<StudentResponseModel> get1000StudentsAsync();
    Flux<StudentResponseModel> get1000StudentsAsyncParallel();
    Flux<StudentResponseModel> get1000StudentsAsyncBounded();
    Mono<StudentResponseModel> getStudentByStudentId(String studentId);
}
