package com.champlain.studentsservice.dataaccesslayer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    Student findStudentByStudentIdentifier_StudentId(String studentId);
}
