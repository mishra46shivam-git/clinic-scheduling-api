# 🏥 Clinical Scheduling REST API

A high-concurrency clinical appointment scheduling engine built with **Spring Boot 3**, **Hibernate / JPA**, and **MySQL**. Engineered to prevent double-booking anomalies using database-level optimistic concurrency control, normalize temporal data boundaries across time zones, and provide self-documenting OpenAPI specifications.

---

## 🚀 Key Architectural Highlights

* **Double-Booking Prevention via Optimistic Concurrency Control**: Uses JPA `@Version` on `AppointmentSlot` entities. Prevents race conditions during simultaneous slot bookings without expensive, deadlock-prone pessimistic table locks.
* **Temporal Normalization**: Decouples API input DTOs into human-readable `LocalDate` and `LocalTime`, assembling them into strict UTC `ZonedDateTime` at domain boundaries to prevent cross-timezone skew.
* **Automated Multi-Threaded Stress Testing**: Built-in JUnit 5 integration suite utilizing `ExecutorService` and `CountDownLatch` to simulate synchronized race conditions and assert slot integrity under concurrent load.
* **Defensive Domain Queries**: JPQL overlap validation queries guard against overlapping doctor availability before slot persistence.
* **OpenAPI 3 / Swagger Documentation**: Centralized contract schemas with contextual HTTP response definitions (`200 OK`, `201 Created`, `400 Bad Request`, `404 Not Found`, `409 Conflict`).

---

## 🛠️ Tech Stack

* **Language**: Java 17+ / Java 21
* **Framework**: Spring Boot 3
* **Persistence**: Spring Data JPA / Hibernate
* **Database**: MySQL 8.0
* **API Documentation**: SpringDoc OpenAPI / Swagger UI
* **Testing**: JUnit 5, AssertJ, Java Concurrency Utilities (`CountDownLatch`, `ExecutorService`)
* **Build Tool**: Maven

---

## ⚡ Concurrency Model & Validation

When $N$ patients attempt to reserve the same appointment slot simultaneously:

[ Thread 1 ] ──┐
[ Thread 2 ] ──┼──> [ POST /api/v1/appointments ] ──> [ Optimistic Lock Check (@Version) ]
[ Thread N ] ──┘                                            │
├─► Winner (Version N -> N+1): 201 Created
└─► Losers (Stale Version):    409 Conflict


The test `AppointmentBookingConcurrencyTest` verifies this behavior programmatically:
1. Spawns 5 threads synchronized via `readyLatch`.
2. Trips `startLatch` to execute booking requests at the same instant.
3. Asserts that `successfulBookings == 1`, `failedBookings == 4`, and slot status transitions to `BOOKED`.

---

## 📖 API Endpoints

### 1. Slots (`/api/v1/slots`)
* `POST /api/v1/slots` — Create an available slot for a doctor (validates against schedule overlap).
* `GET /api/v1/slots` — Query available slots filtered by doctor and temporal range.

### 2. Appointments (`/api/v1/appointments`)
* `POST /api/v1/appointments` — Book an available slot (optimistic locking enforced).
* `GET /api/v1/appointments/{id}` — Retrieve appointment record details.

---

## 📑 Interactive API Documentation (Swagger / OpenAPI)

The application provides self-documenting, interactive OpenAPI 3 contracts using SpringDoc Swagger UI. Developers and API consumers can test live requests, review request/response schemas, and verify validation rules directly from the browser.

### Accessing Swagger UI
1. Run the Spring Boot application locally.
2. Open your browser and navigate to:
   http://localhost:8080/swagger-ui/index.html

3. To view the raw OpenAPI 3.0 specification in JSON format:
   http://localhost:8080/v3/api-docs


### Documented Schemas & Error Models
* **Centralized Schemas**: Includes detailed request/response models for `AppointmentSlot`, `Appointment`, and `Doctor`.
* **Standardized Status Codes**:
* `200 OK` / `201 Created` — Successful slot retrieval and appointment booking.
* `400 Bad Request` — Field validation failures (e.g., malformed ISO time formats).
* `404 Not Found` — Requested entity (slot, doctor, or patient) does not exist.
* `409 Conflict` — Race condition detected via optimistic locking (`@Version`) or slot overlap conflict.

## 💻 Getting Started

### Prerequisites
* JDK 17+ or JDK 21 installed
* MySQL running on `localhost:3306`

### Configuration
Update `src/main/resources/application.properties` with your database credentials or pass them via environment variables:
```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/clinic_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:your_password}
spring.jpa.hibernate.ddl-auto=update
Run the Application
Bash
# Clone the repository
git clone https://github.com/mishra46shivam-git/clinic-scheduling-api.git
cd clinic-scheduling-api

# Run tests
./mvnw clean test

# Start the application
./mvnw spring-boot:run
Once running, access the interactive Swagger documentation at:

http://localhost:8080/swagger-ui/index.html

---

### How to paste it in IntelliJ:
1. Double-click to open your **`README.md`** file in IntelliJ.
2. Press **Ctrl + A** to select everything currently inside.
3. Press **Ctrl + V** to replace it with the code block above.
4. Press **Ctrl + S** to save.
