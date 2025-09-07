USE `students-db`;

create table if not exists students (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(36) UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    program VARCHAR(50),
    stuff TEXT
);
