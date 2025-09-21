Enrollment System – Backend Services

A comprehensive backend for managing students, courses, and enrollments, clean REST APIs, predictable error handling, and straightforward local setup.
Features

Student Management

• Add and manage students with validated fields (first name, last name, email)

• Edit student details with field level validation

• Delete students with safe checks and meaningful responses

Course Catalog

• Create and manage courses (course number, title, capacity, department)

• Update capacity and metadata with guard rails

• Archive/delete courses with referential integrity checks

Enrollment Management

• Enroll and withdraw students from courses

• Capacity enforcement with clear error messages

• Basic statuses (ENROLLED, WITHDRAWN)

• Simple seat availability checks and conflicts prevention

Tech Stack

• Java 17 (LTS)

• Spring Boot 3 (Web, Validation, Data)

• Gradle 8 (Kotlin or Groovy DSL)

• MapStruct (DTO ↔ entity mapping)

• JUnit 5, Mockito, WebTestClient/MockMvc

• H2 (dev) or PostgreSQL (Docker)

Architecture & Libraries

• Layered architecture: Controller → Service → Mapper → Repository

• Spring Web (or WebFlux per module) for REST endpoints

• Spring Data (JPA or R2DBC by module)

• MapStruct to keep controllers slim and models clean

• Repository pattern with domain focused methods

Build System

• Gradle with version catalogs

• JaCoCo for coverage reports

• Optional Docker Compose for PostgreSQL and local wiring
