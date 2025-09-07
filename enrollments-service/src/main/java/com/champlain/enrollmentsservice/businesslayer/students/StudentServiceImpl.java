package com.champlain.enrollmentsservice.businesslayer.students;

import com.champlain.enrollmentsservice.domainclientlayer.students.StudentResponseModel;
import com.champlain.enrollmentsservice.domainclientlayer.students.StudentServiceClientAsynchronous;
import com.champlain.enrollmentsservice.domainclientlayer.students.StudentServiceClientSynchronous;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentServiceClientSynchronous studentServiceClientSynchronous;
    private final StudentServiceClientAsynchronous studentServiceClientAsynchronous;

    public StudentServiceImpl(StudentServiceClientSynchronous studentServiceClientSynchronous, StudentServiceClientAsynchronous studentServiceClientAsynchronous) {
        this.studentServiceClientSynchronous = studentServiceClientSynchronous;
        this.studentServiceClientAsynchronous = studentServiceClientAsynchronous;
    }

    @Override
    public Flux<StudentResponseModel> getAllStudents() {
        return studentServiceClientAsynchronous.getAllStudents();
    }

    //synchronous
    @Override
    public List<StudentResponseModel> get1000StudentsSyncLoop() {
        return studentServiceClientSynchronous.get1000StudentsLoop();
    }

    @Override
    public List<StudentResponseModel> get1000StudentsSyncParallel() {
        return studentServiceClientSynchronous.get1000StudentsSyncParallel();
    }

    //asynchronous
    @Override
    public Flux<StudentResponseModel> get1000StudentsAsync() {
        return studentServiceClientAsynchronous.get1000StudentsAsync();
    }

    @Override
    public Flux<StudentResponseModel> get1000StudentsAsyncParallel() {
        return studentServiceClientAsynchronous.get1000StudentsAsyncParallel();
    }

    @Override
    public Flux<StudentResponseModel> get1000StudentsAsyncBounded() {
        return studentServiceClientAsynchronous.get1000StudentsAsyncBounded();
    }

    @Override
    public Mono<StudentResponseModel> getStudentByStudentId(String studentId) {
        return studentServiceClientAsynchronous.getStudentByStudentId(studentId);
    }
}
