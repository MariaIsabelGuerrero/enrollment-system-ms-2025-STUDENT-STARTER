#!/usr/bin/env bash

spring init \
--boot-version=3.5.4 \
--build=gradle \
--type=gradle-project \
--java-version=21 \
--packaging=jar \
--name=enrollments-service \
--package-name=com.champlain.enrollmentsservice \
--groupId=com.champlain.enrollmentsservice \
--dependencies=webflux \
--version=1.0.0-SNAPSHOT \
enrollments-service

spring init \
--boot-version=3.5.4 \
--build=gradle \
--type=gradle-project \
--java-version=21 \
--packaging=jar \
--name=students-service \
--package-name=com.champlain.studentsservice \
--groupId=com.champlain.studentsservice \
--dependencies=web,webflux \
--version=1.0.0-SNAPSHOT \
students-service

spring init \
--boot-version=3.5.4 \
--build=gradle \
--type=gradle-project \
--java-version=21 \
--packaging=jar \
--name=courses-service \
--package-name=com.champlain.courseservice \
--groupId=com.champlain.coursesservice \
--dependencies=webflux \
--version=1.0.0-SNAPSHOT \
courses-service


