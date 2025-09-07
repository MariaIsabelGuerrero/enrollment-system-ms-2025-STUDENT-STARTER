package com.champlain.enrollmentsservice.presentationlayer.students;

import com.champlain.enrollmentsservice.businesslayer.students.StudentService;
import com.champlain.enrollmentsservice.domainclientlayer.students.StudentResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/v1/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    //@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StudentResponseModel> getAllStudents() {
        return studentService.getAllStudents();
    }


    @GetMapping(value = "/syncloop", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StudentResponseModel>> get1000StudentsSync() {
        LocalTime start = LocalTime.now();
        List<StudentResponseModel> students = studentService.get1000StudentsSyncLoop();
        log.debug("Time taken to get 1000 students with syncloop: \t{}", LocalTime.now().minusNanos(start.toNanoOfDay()).toNanoOfDay());
        return ResponseEntity.ok().body(students);
    }

    @GetMapping(value = "/syncparallel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StudentResponseModel>> get1000StudentsSyncParallel() {
        LocalTime start = LocalTime.now();
        List<StudentResponseModel> students = studentService.get1000StudentsSyncParallel();
        log.debug("Time taken to get 1000 students with syncparallel: \t{}", LocalTime.now().minusNanos(start.toNanoOfDay()).toNanoOfDay());
        return ResponseEntity.ok().body(students);
    }

    @GetMapping(value = "/async", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StudentResponseModel> get1000StudentsAsync() {
        LocalTime start = LocalTime.now();
        Flux<StudentResponseModel> students = studentService.get1000StudentsAsync();
        log.debug("Time taken to get 1000 students with async: \t\t{}", LocalTime.now().minusNanos(start.toNanoOfDay()).toNanoOfDay());
        return students;
    }

    @GetMapping(value = "/asyncparallel", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StudentResponseModel> get1000StudentsAsyncParallel() {
        LocalTime start = LocalTime.now();
        Flux<StudentResponseModel> students = studentService.get1000StudentsAsyncParallel();
        log.debug("Time taken to get 1000 students with asyncparallel: \t{}", LocalTime.now().minusNanos(start.toNanoOfDay()).toNanoOfDay());
        return students;
    }

    @GetMapping(value = "/asyncbounded", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StudentResponseModel> get1000StudentsAsyncBounded() {
        LocalTime start = LocalTime.now();
        Flux<StudentResponseModel> students = studentService.get1000StudentsAsyncBounded();
        log.debug("Time taken to get 1000 students with asyncbounded: \t{}", LocalTime.now().minusNanos(start.toNanoOfDay()).toNanoOfDay());
        return students;
    }

    @GetMapping(value = "/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<StudentResponseModel>> getStudentByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok().body(studentService.getStudentByStudentId(studentId));
    }
}
