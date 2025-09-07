package com.champlain.enrollmentsservice.config;

import com.champlain.enrollmentsservice.domainclientlayer.courses.CourseServiceClient;
import com.champlain.enrollmentsservice.domainclientlayer.students.StudentServiceClientAsynchronous;
import com.champlain.enrollmentsservice.domainclientlayer.students.StudentServiceClientSynchronous;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class ServiceClientConfig {

    @Bean
    public CourseServiceClient courseServiceClient(@Value("${app.courses-service.host}") String courseServiceHost,
                                                   @Value("${app.courses-service.port}") String courseServicePort) {

        var baseUrl = "http://" + courseServiceHost + ":" + courseServicePort + "/api/v1/courses";
        log.debug("CourseServiceClient baseUrl: {}", baseUrl);
        return new CourseServiceClient(createWebClient(baseUrl));
    }

    @Bean
    public StudentServiceClientAsynchronous studentServiceClientAsynchronous(@Value("${app.students-service.host}") String studentServiceHost,
                                                                 @Value("${app.students-service.port}") String studentServicePort) {

        var baseUrl = "http://" + studentServiceHost + ":" + studentServicePort + "/api/v1/students";
        log.debug("StudentServiceClientAsynchronous baseUrl: {}", baseUrl);
        return new StudentServiceClientAsynchronous(createWebClient(baseUrl));
    }

    @Bean
    RestTemplate restTemplate()
    {
        return new RestTemplate();
    }

    @Bean
    public StudentServiceClientSynchronous studentServiceClientSynchronous(@Value("${app.students-service.host}") String studentServiceHost,
                                                                           @Value("${app.students-service.port}") String studentServicePort,
                                                                           RestTemplate restTemplate,
                                                                           ObjectMapper mapper
    ) {

        var baseUrl = "http://" + studentServiceHost + ":" + studentServicePort + "/api/v1/students";
        log.debug("StudentServiceClientSynchronous baseUrl: {}", baseUrl);
        return new StudentServiceClientSynchronous(baseUrl, restTemplate, mapper);
    }


    private WebClient createWebClient(String baseUrl) {
        log.info("base url: {}", baseUrl);
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

}
