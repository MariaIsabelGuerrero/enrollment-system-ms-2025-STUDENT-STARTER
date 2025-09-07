package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.businesslayer.CourseService;
import com.champlain.courseservice.exceptionhandling.exceptions.InvalidInputException;
import com.champlain.courseservice.validation.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("api/v1/courses")
public class CourseController {
    private final CourseService courseService;
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CourseResponseModel> getAllCourses(){
        return courseService.getAllCourses();
    }


    @GetMapping(value = "/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CourseResponseModel>> getCourseByCourseId(@PathVariable String courseId) {
        return Mono.just(courseId)
                .filter(id  -> id.length() == 36)
                .switchIfEmpty(Mono.error(new InvalidInputException("Invalid courseId: " + courseId)))
                .flatMap(courseService::getCourseByCourseId)
                .map(ResponseEntity::ok);

    }

    @PostMapping()
    public Mono<ResponseEntity<CourseResponseModel>> addCourse(@RequestBody Mono<CourseRequestModel> courseRequestModel) {
        return courseRequestModel.transform(RequestValidator.validateBody())
                .flatMap(validReq -> this.courseService.addCourse(Mono.just(validReq)))
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c));
    }

    @PutMapping(value="/{courseId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CourseResponseModel>> updateCourseByCourseId(@RequestBody Mono<CourseRequestModel> courseRequestModel, @PathVariable String courseId) {
        return Mono.just(courseId)
                .filter(id -> id.length()==36)
                .switchIfEmpty(Mono.error(new InvalidInputException("Invalid courseId: " + courseId)))
                .flatMap(id -> courseRequestModel
                        .transform(RequestValidator.validateBody())
                        .as(request -> courseService.updateCourseByCourseId(request, id)))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping(value="/{courseId}", produces= MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CourseResponseModel>> deleteCourseByCourseId(@PathVariable String courseId) {
        return Mono.just(courseId)
                .filter(id -> id.length()==36)
                .switchIfEmpty(Mono.error(new InvalidInputException("Invalid courseId: " + courseId)))
                .flatMap(courseService::deleteCourseByCourseId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}