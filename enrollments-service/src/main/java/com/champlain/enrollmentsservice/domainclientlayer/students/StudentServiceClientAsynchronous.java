package com.champlain.enrollmentsservice.domainclientlayer.students;

import com.champlain.enrollmentsservice.exceptionhandling.ApplicationExceptions;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;
import org.springframework.web.reactive.function.client.WebClientResponseException.UnprocessableEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Thread.currentThread;

@Slf4j
public class StudentServiceClientAsynchronous {

    private final WebClient webClient;

    private final List<Integer> range = IntStream.rangeClosed(1, 1000)
            .boxed()
            .toList();

    public StudentServiceClientAsynchronous(WebClient webClient) {
        this.webClient = webClient;
    }
    @Generated
    public Mono<StudentResponseModel> getStudentByStudentId(final String studentId) {
        return this.webClient.get()
                .uri("/{studentId}", studentId)
                .retrieve()
                .bodyToMono(StudentResponseModel.class)
                .onErrorResume(NotFound.class, ex -> ApplicationExceptions.studentNotFound(studentId))
                .onErrorResume(UnprocessableEntity.class, ex -> ApplicationExceptions.invalidStudentId(studentId));
    }

        /*
       The number of default threads is dependent on the core count of the host system.
       Remember, Webflux will try to keep said threads as busy as possible, so how many
       threads you have assigned doesn't really matter, as long as they consume the full power of the CPU.

       More threads will just have to wait for their turn to use the CPU.

       !! I have 10 cores (12 logical), but I only see 4 being used.
       !! Note my wsl is set to use 6 virtual processors.
     */

@Generated
    public Flux<StudentResponseModel> getAllStudents() {
        return this.webClient
                .get()
                .retrieve()
                .bodyToFlux(StudentResponseModel.class);
    }
@Generated
    //Gets students by databaseRowId
    public Mono<StudentResponseModel> getStudent(int id) {
        //log.debug(String.format("Calling getStudentAsync(%d)", id));


        return webClient.get()
                .uri("/row/{id}", id)
                .retrieve()
                .bodyToMono(StudentResponseModel.class);

    }
    @Generated
    public Flux<StudentResponseModel> get1000StudentsAsync() {

        /* We invoke flatMap to run the getUser method we created previously.
        This reactive operator has a concurrency level of 256 (on one thread) by default,
        meaning it executes at most 256 getUser calls simultaneously.
        This number is configurable via method parameter using an overloaded version of flatMap.
         */

        /* Snippet of results from running this method: all are on same io thread

enrollments-service  | 2024-08-13T19:31:55.520Z  INFO 1 --- [enrollments-service] [or-http-epoll-2] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#35,reactor-http-epoll-2,5,main]
enrollments-service  | 2024-08-13T19:31:55.520Z  INFO 1 --- [enrollments-service] [or-http-epoll-2] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#35,reactor-http-epoll-2,5,main]
enrollments-service  | 2024-08-13T19:31:55.520Z  INFO 1 --- [enrollments-service] [or-http-epoll-2] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#35,reactor-http-epoll-2,5,main]
enrollments-service  | 2024-08-13T19:31:55.520Z  INFO 1 --- [enrollments-service] [or-http-epoll-2] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#35,reactor-http-epoll-2,5,main]
enrollments-service  | 2024-08-13T19:31:55.520Z  INFO 1 --- [enrollments-service] [or-http-epoll-2] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#35,reactor-http-epoll-2,5,main]

         */

        return Flux.fromIterable(range)
                .flatMap(this::getStudent)
                .doOnNext(s -> log.info("Current thread running " + currentThread()));
    }
@Generated
    public Flux<StudentResponseModel> get1000StudentsAsyncParallel() {

        /* Snippet of results from running this method: up to max cores of io threads and multiple parallel threads (system and load dependant)

enrollments-service              | 2024-08-20T16:44:49.442Z  INFO 1 --- [enrollments-service] [or-http-epoll-3] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#46,reactor-http-epoll-3,5,main]
enrollments-service              | 2024-08-20T16:44:49.460Z  INFO 1 --- [enrollments-service] [     parallel-1] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#56,parallel-1,5,main]
enrollments-service              | 2024-08-20T16:44:49.466Z  INFO 1 --- [enrollments-service] [     parallel-1] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#56,parallel-1,5,main]
enrollments-service              | 2024-08-20T16:44:49.469Z  INFO 1 --- [enrollments-service] [     parallel-1] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#56,parallel-1,5,main]
enrollments-service              | 2024-08-20T16:44:49.473Z  INFO 1 --- [enrollments-service] [     parallel-1] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#56,parallel-1,5,main]
enrollments-service              | 2024-08-20T16:44:49.475Z  INFO 1 --- [enrollments-service] [     parallel-1] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#56,parallel-1,5,main]
enrollments-service              | 2024-08-20T16:44:49.476Z  INFO 1 --- [enrollments-service] [     parallel-1] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#56,parallel-1,5,main]
enrollments-service              | 2024-08-20T16:44:49.479Z  INFO 1 --- [enrollments-service] [     parallel-1] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#56,parallel-1,5,main]
enrollments-service              | 2024-08-20T16:44:49.483Z  INFO 1 --- [enrollments-service] [or-http-epoll-5] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#54,reactor-http-epoll-5,5,main]
enrollments-service              | 2024-08-20T16:44:49.497Z  INFO 1 --- [enrollments-service] [or-http-epoll-5] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#54,reactor-http-epoll-5,5,main]
enrollments-service              | 2024-08-20T16:44:49.503Z  INFO 1 --- [enrollments-service] [or-http-epoll-5] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#54,reactor-http-epoll-5,5,main]

         */

        return Flux.fromIterable(range)
                .flatMap(this::getStudent)
                .doOnNext(s -> log.info("Current thread running " + currentThread()))
                .subscribeOn(Schedulers.parallel());
    }

    public Flux<StudentResponseModel> get1000StudentsAsyncBounded() {

        /* Snippet of results from running this method: 1 bounded elastic thread, up to max cores of io threads

enrollments-service  | 2024-08-13T19:27:24.605Z  INFO 1 --- [enrollments-service] [oundedElastic-1] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#37,boundedElastic-1,5,main]
enrollments-service  | 2024-08-13T19:27:24.605Z  INFO 1 --- [enrollments-service] [oundedElastic-1] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#37,boundedElastic-1,5,main]
enrollments-service  | 2024-08-13T19:27:24.606Z  INFO 1 --- [enrollments-service] [oundedElastic-1] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#37,boundedElastic-1,5,main]
enrollments-service  | 2024-08-13T19:27:24.606Z  INFO 1 --- [enrollments-service] [oundedElastic-1] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#37,boundedElastic-1,5,main]
enrollments-service  | 2024-08-13T19:27:24.606Z  INFO 1 --- [enrollments-service] [oundedElastic-1] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#37,boundedElastic-1,5,main]
enrollments-service  | 2024-08-13T19:27:24.609Z  INFO 1 --- [enrollments-service] [or-http-epoll-6] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#40,reactor-http-epoll-6,5,main]
enrollments-service  | 2024-08-13T19:27:24.609Z  INFO 1 --- [enrollments-service] [or-http-epoll-6] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#40,reactor-http-epoll-6,5,main]
enrollments-service  | 2024-08-13T19:27:24.610Z  INFO 1 --- [enrollments-service] [or-http-epoll-6] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#40,reactor-http-epoll-6,5,main]
enrollments-service  | 2024-08-13T19:27:24.611Z  INFO 1 --- [enrollments-service] [or-http-epoll-6] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#40,reactor-http-epoll-6,5,main]
enrollments-service  | 2024-08-13T19:27:24.612Z  INFO 1 --- [enrollments-service] [or-http-epoll-6] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#40,reactor-http-epoll-6,5,main]
enrollments-service  | 2024-08-13T19:27:24.617Z  INFO 1 --- [enrollments-service] [or-http-epoll-6] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#40,reactor-http-epoll-6,5,main]
enrollments-service  | 2024-08-13T19:27:24.619Z  INFO 1 --- [enrollments-service] [or-http-epoll-2] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#35,reactor-http-epoll-2,5,main]
enrollments-service  | 2024-08-13T19:27:24.621Z  INFO 1 --- [enrollments-service] [or-http-epoll-4] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#38,reactor-http-epoll-4,5,main]
enrollments-service  | 2024-08-13T19:27:24.622Z  INFO 1 --- [enrollments-service] [or-http-epoll-4] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#38,reactor-http-epoll-4,5,main]
enrollments-service  | 2024-08-13T19:27:24.622Z  INFO 1 --- [enrollments-service] [or-http-epoll-4] c.c.e.d.StudentClientAsynchronous        : Current thread running Thread[#38,reactor-http-epoll-4,5,main]

         */


        return Flux.fromIterable(range)
                .flatMap(this::getStudent)
                .doOnNext(s -> log.info("Current thread running " + currentThread()))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
