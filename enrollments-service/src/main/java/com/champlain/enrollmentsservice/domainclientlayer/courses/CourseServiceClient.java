package com.champlain.enrollmentsservice.domainclientlayer.courses;

import com.champlain.enrollmentsservice.exceptionhandling.ApplicationExceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
public class CourseServiceClient {

    private final WebClient webClient;

    public CourseServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<CourseResponseModel> getCourseByCourseId(final String courseId) {
        return webClient.get()
                .uri("/{courseId}", courseId)
                .retrieve()
                .bodyToMono(CourseResponseModel.class)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> ApplicationExceptions.courseNotFound(courseId))
                .onErrorResume(WebClientResponseException.UnprocessableEntity.class, ex -> ApplicationExceptions.invalidCourseId(courseId));
    }

}
