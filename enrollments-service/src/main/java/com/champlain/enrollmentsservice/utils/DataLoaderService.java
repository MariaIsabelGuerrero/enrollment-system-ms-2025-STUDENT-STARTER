package com.champlain.enrollmentsservice.utils;

import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.dataaccesslayer.Semester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DataLoaderService implements CommandLineRunner {

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Override
    public void run(String... args) throws Exception {

        Enrollment enrollment1 = Enrollment.builder()
                .enrollmentId("06a7d573-bcab-4db3-956f-773324b92a80")
                .enrollmentYear(2021)
                .semester(Semester.FALL)
                .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .studentFirstName("Christine")
                .studentLastName("Gerard")
                .courseId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                .courseNumber("trs-075")
                .courseName("Web Services")
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .enrollmentId("98f7b33a-d62a-420a-a84a-05a27c85fc91")
                .enrollmentYear(2021)
                .semester(Semester.FALL)
                .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .studentFirstName("Christine")
                .studentLastName("Gerard")
                .courseId("d819e4f4-25af-4d33-91e9-2c45f0071606")
                .courseNumber("ygo-675")
                .courseName("Shakespeare's Greatest Works")
                .build();

        Flux.just(enrollment1, enrollment2)
                .flatMap(s -> enrollmentRepository.insert(Mono.just(s))
                        .log(s.toString()))
                .subscribe();  //if you don't subscribe, nothing happens
    }
}

