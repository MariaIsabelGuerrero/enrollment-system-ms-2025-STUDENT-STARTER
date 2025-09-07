DROP TABLE IF EXISTS courses;

CREATE TABLE IF NOT EXISTS courses (
    id SERIAL,
    course_id VARCHAR(36),
    course_number VARCHAR(7),
    course_name VARCHAR(100),
    num_hours SMALLINT,
    num_credits DECIMAL(19,2),
    department VARCHAR(50),
    PRIMARY KEY (id)
);