package com.champlain.enrollmentsservice.domainclientlayer.students;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Thread.currentThread;

@Slf4j
public class StudentServiceClientSynchronous {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final List<Integer> range = IntStream.rangeClosed(1, 1000)
            .boxed()
            .toList();

    public StudentServiceClientSynchronous(String baseUrl, RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
        this.mapper = mapper;
    }

    public StudentResponseModel getStudentSync(int id) {

        //gets student by the database row id
        return restTemplate.getForObject("/row/" + id, StudentResponseModel.class);
    }


    public List<StudentResponseModel> get1000StudentsLoop() {

    /* Snippet of results from this method: all on one thread

enrollments-service  | 2024-08-13T19:43:57.078Z  INFO 1 --- [enrollments-service] [or-http-epoll-3] c.c.e.d.StudentClientSynchronous         : Current thread running Thread[#36,reactor-http-epoll-3,5,main]
enrollments-service  | 2024-08-13T19:43:57.081Z  INFO 1 --- [enrollments-service] [or-http-epoll-3] c.c.e.d.StudentClientSynchronous         : Current thread running Thread[#36,reactor-http-epoll-3,5,main]
enrollments-service  | 2024-08-13T19:43:57.083Z  INFO 1 --- [enrollments-service] [or-http-epoll-3] c.c.e.d.StudentClientSynchronous         : Current thread running Thread[#36,reactor-http-epoll-3,5,main]
enrollments-service  | 2024-08-13T19:43:57.085Z  INFO 1 --- [enrollments-service] [or-http-epoll-3] c.c.e.d.StudentClientSynchronous         : Current thread running Thread[#36,reactor-http-epoll-3,5,main]

       */

        List<StudentResponseModel> studentResponseModels = new ArrayList<>();

        for (int i = 1; i < 1001; i++) {

            log.info("Current thread running " + currentThread());
            studentResponseModels.add(getStudentSync(i));

        }

        return studentResponseModels;
    }

    public List<StudentResponseModel> get1000StudentsSyncParallel() {

        /* Snippet of results from this method: one http-epoll (io) thread but up to 6 commonPool-worker threads

enrollments-service  | 2024-08-13T19:46:23.548Z  INFO 1 --- [enrollments-service] [or-http-epoll-2] c.c.e.d.StudentClientSynchronous         : Current thread running Thread[#35,reactor-http-epoll-2,5,main]
enrollments-service  | 2024-08-13T19:46:23.553Z  INFO 1 --- [enrollments-service] [onPool-worker-2] c.c.e.d.StudentClientSynchronous         : Current thread running Thread[#38,ForkJoinPool.commonPool-worker-2,5,main]
enrollments-service  | 2024-08-13T19:46:23.555Z  INFO 1 --- [enrollments-service] [onPool-worker-4] c.c.e.d.StudentClientSynchronous         : Current thread running Thread[#40,ForkJoinPool.commonPool-worker-4,5,main]
enrollments-service  | 2024-08-13T19:46:23.555Z  INFO 1 --- [enrollments-service] [onPool-worker-5] c.c.e.d.StudentClientSynchronous         : Current thread running Thread[#41,ForkJoinPool.commonPool-worker-5,5,main]
enrollments-service  | 2024-08-13T19:46:23.555Z  INFO 1 --- [enrollments-service] [or-http-epoll-2] c.c.e.d.StudentClientSynchronous         : Current thread running Thread[#35,reactor-http-epoll-2,5,main]
enrollments-service  | 2024-08-13T19:46:23.561Z  INFO 1 --- [enrollments-service] [onPool-worker-2] c.c.e.d.StudentClientSynchronous         : Current thread running Thread[#38,ForkJoinPool.commonPool-worker-2,5,main]
enrollments-service  | 2024-08-13T19:46:23.562Z  INFO 1 --- [enrollments-service] [onPool-worker-4] c.c.e.d.StudentClientSynchronous         : Current thread running Thread[#40,ForkJoinPool.commonPool-worker-4,5,main]

/*
         */
        return range.parallelStream()
                .map(i -> {
                    log.info("Current thread running " + currentThread());
                    return getStudentSync(i);
                })
                .toList();
    }
}
