package com.champlain.studentsservice.dataaccesslayer;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class StudentIdentifier {
    private String studentId;

    public StudentIdentifier() {
        this.studentId = UUID.randomUUID().toString();
    }

}
