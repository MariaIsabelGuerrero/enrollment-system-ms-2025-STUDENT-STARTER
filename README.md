# 🎓 Enrollment System — Microservices Project (2025)

A **Spring Boot microservices-based enrollment management system**, designed for educational institutions to manage students, courses, and enrollments efficiently.  
Built with **Java 17**, **Spring Boot 3**, **Gradle**, and **Docker Compose**, this system demonstrates distributed architecture, inter-service communication, and containerized deployment.

---

## 🌟 Overview

The **Enrollment System** provides a modular and scalable backend where each microservice manages a specific business domain:

- 🧑‍🎓 **Students Service** — Manages student profiles and personal data  
- 📘 **Courses Service** — Handles course catalog, descriptions, and credits  
- 🧾 **Enrollments Service** — Manages course registration and linking between students and courses  

The services communicate through RESTful APIs and can be run independently or together via Docker Compose.

---

## 🧩 Project Structure

```
enrollment-system-ms-2025-STUDENT-STARTER/
├── students-service/        # Handles student-related operations
├── courses-service/         # Handles course-related operations
├── enrollments-service/     # Manages course enrollment logic
├── data/                    # Data initialization and utility scripts
├── docker-compose.yml       # Docker configuration for all microservices
├── gradlew / gradlew.bat    # Gradle wrapper scripts
└── create-projects.bash     # Script to set up submodules
```

---

## ⚙️ Tech Stack

| Layer | Technology |
|-------|-------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.x |
| **Build Tool** | Gradle (Kotlin DSL) |
| **Containerization** | Docker & Docker Compose |
| **Database** | H2 / PostgreSQL (depending on service config) |
| **Architecture** | Microservices (REST-based) |
| **Testing** | JUnit 5 & Mockito |
| **Configuration** | application.yml / environment variables |

---

## 🐳 Running the Project with Docker

1. **Ensure Docker is installed and running** on your machine.

2. **Open a terminal** in the project root folder (`enrollment-system-ms-2025-STUDENT-STARTER`).

3. **Build and start all microservices**:
   ```bash
   docker-compose up --build
   ```

4. Wait until all services start successfully. Each service will run on a separate port (e.g.):
   - Students Service → `http://localhost:7001`
   - Courses Service → `http://localhost:7002`
   - Enrollments Service → `http://localhost:7003`

5. To stop all containers:
   ```bash
   docker-compose down
   ```

---

## 🧠 Manual Setup (Without Docker)

If you prefer to run the project manually:

1. **Open the root project** in **IntelliJ IDEA** or another IDE.  
2. **Build all submodules** using Gradle:
   ```bash
   ./gradlew clean build
   ```
3. **Run each service individually** using:
   ```bash
   ./gradlew bootRun
   ```
4. Verify the services are available at:
   - `localhost:7001` → Students
   - `localhost:7002` → Courses
   - `localhost:7003` → Enrollments

---

## 🔗 Example API Endpoints

| Service | Method | Endpoint | Description |
|----------|---------|-----------|--------------|
| Students | `GET` | `/api/v1/students` | Get all students |
| Students | `POST` | `/api/v1/students` | Add a new student |
| Courses | `GET` | `/api/v1/courses` | Get all courses |
| Enrollments | `POST` | `/api/v1/enrollments` | Enroll a student in a course |
| Enrollments | `GET` | `/api/v1/enrollments/{studentId}` | Get student’s courses |

---

## 🧭 Architecture Overview

Each microservice follows the **3-Layer Architecture Pattern**:

```
┌────────────────────────────┐
│       Presentation         │  →  REST Controllers
├────────────────────────────┤
│         Service            │  →  Business Logic Layer
├────────────────────────────┤
│        Data Access         │  →  Repositories / Databases
└────────────────────────────┘
```

The services communicate via **REST APIs**, and data consistency is ensured through service-level isolation and DTO mapping.

---

## 🧑‍💻 Development Setup

### Requirements

- **JDK 17+**
- **Gradle 8+**
- **Docker Desktop (latest)**
- **Postman** (optional, for API testing)

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/MariaIsabelGuerrero/enrollment-system-ms-2025.git
   ```
2. Open in IntelliJ IDEA or VS Code.  
3. Run Gradle sync to install dependencies.  
4. Start each microservice via Gradle or Docker Compose.  

---

## 🧾 Environment Variables

Each service can define its environment variables in `application.yml` or via Docker Compose:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/studentsdb
    username: user
    password: pass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
server:
  port: 7001
```

---

## 🧪 Testing

Run all tests using Gradle:
```bash
./gradlew test
```

Test reports will be available in `build/reports/tests/test/index.html`.

---

## 🤝 Contributing

Contributions are welcome! Feel free to fork this repo, create a feature branch, and submit a pull request.



