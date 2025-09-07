package com.champlain.studentsservice.dataaccesslayer;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="students")
@Data
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private StudentIdentifier studentIdentifier;

    private String firstName;
    private String lastName;
    private String program;
    private String stuff;
}
