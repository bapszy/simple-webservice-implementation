# Library Management System

A RESTful web service built with Java 21 and Spring Boot for managing books, borrowers, and library operations with built-in OpenTelemetry observability.

## Tech Stack & Prerequisites

* Java 21
* Spring Boot 3.3.x
* Spring Data JPA & H2 In-Memory Database
* Gradle
* OpenTelemetry & Micrometer Tracing
* JUnit 5 & Mockito

---

## How to Build and Run

1. Build the application:
   ./gradlew build

2. Run unit and integration tests:
   ./gradlew test

3. Run the application:
   ./gradlew bootRun

The server will start at http://localhost:8080.

---

## API Endpoints & Usage

### Borrower Management
* POST /borrowers — Create a new borrower
* GET /borrowers/{id} — Get borrower details
* GET /borrowers/{id}/books — Get all books currently borrowed by this user

### Book Management
* GET /books — List all books
* POST /books — Add a new book
* POST /books/{id}/borrow?borrowerId={borrowerId} — Borrow a book

---

## Database Console & Observability

* H2 In-Memory Console: http://localhost:8080/h2-console
  * JDBC URL: jdbc:h2:mem:librarydb
  * Username: sa
  * Password: (empty)

* OpenTelemetry Custom Metrics:
  * View custom borrow count metric: http://localhost:8080/actuator/metrics/library.books.borrowed.total
