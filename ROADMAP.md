# LogisticsConnect Project Roadmap

## Goal

Build a microservices-based logistics tracking application fulfilling the Systems Integration requirements: RESTful APIs, JSON serialization, and Java Messaging Service (JMS).

## 🛠 Methodology

* **Test-Driven Development (TDD):** Unit tests written using JUnit 5 before feature implementation. Guard clauses and domain logic must be tested first.
* **Integration Testing:** Ensuring all isolated services, databases, and message brokers communicate correctly using Testcontainers and full server environments.
* **Continuous Integration (CI/CD):** Automated pipelines (GitHub Actions) configured to run build and test stages on every commit, utilizing GitHub Secrets.
* **Security First:** No hardcoded credentials. All secrets are managed via `.env` files and excluded from version control.
* **Single Database Engine:** PostgreSQL 18 is the only relational database in the project. (The original scaffold used MySQL; that decision was reversed. See ARCHITECTURE.md.)

**Legend:** `[X]` done · `[ ]` open · `LC-0xx` = tracked in [KNOWN_ISSUES.md](KNOWN_ISSUES.md)

---

### Phase 1: Repository Setup & Secure Infrastructure (Done)

- [X]  Set up the multi-module Spring Boot project structure in IntelliJ IDEA.
- [X]  Initialize Git repository and `ROADMAP.md`.
- [X]  Create `.gitignore` to exclude IDE files, compiled code, and `.env` files.
- [X]  Create a `.env` file for secure local environment variables (`POSTGRES_USER`, `POSTGRES_PASSWORD`, etc.).
- [X]  Create `compose.yaml` to set up the PostgreSQL 18 Database and ActiveMQ Artemis broker.
- [X]  Implement a root `Makefile` to orchestrate Docker containers and Maven build commands.
- [X]  Configure the CI/CD pipeline file to spin up infrastructure and run automated tests securely.

> Known gaps carried out of this phase (fixed in Phase 2.5): credentials are still duplicated in the Makefile, `application.properties` and `compose.yaml` (LC-006, LC-007), and `.env` was committed once before being untracked (LC-008).

### Phase 2: Synchronous REST Services (Shipment Service) (Functionally complete, hardening in Phase 2.5)

- [ ]  **TDD:** Write full server integration test asserting `HTTP 404` for a non-existent shipment. *(Previously ticked, but no 404 test exists in the codebase. Reopened, see LC-002. Note: tests use `RestTestClient`, not `TestRestTemplate` as originally written.)*
- [X]  Configure Spring Data JPA and Hibernate (`ddl-auto`) to manage the PostgreSQL schema automatically.
- [X]  Define the `Shipment` domain model (Java Entity) and verify guard clauses.
- [X]  Implement `POST /api/v1/shipments` to create shipping orders (JSON serialization).
- [X]  Implement `GET /api/v1/shipments/{id}` to retrieve order status.
- [X]  **Integration Test:** Verify database persistence and API JSON responses (happy paths for POST and GET).
- [X]  Handle duplicate tracking numbers with `HTTP 409` via `GlobalExceptionHandler` (implemented, but untested and over-broad, see LC-002 and LC-005).

### Phase 2.5: Alignment & Hardening (Gate: complete before starting Phase 3)

Goal: make the foundation trustworthy so Phase 3 builds on solid ground. Follow TDD: write the failing test first for every code item.

**A. Documentation alignment**

- [X]  Create `KNOWN_ISSUES.md` logging every issue found in the project audit.
- [X]  Update `ARCHITECTURE.md`: mark implemented vs planned files, fix the directory tree, document config/secrets flow, database decisions, and planned messaging.
- [X]  Update `README.md`: correct the stack (PostgreSQL only), add `.env` setup, API reference, current status, and test caveats.
- [X]  Add `.env.example` so the required variables are documented without committing secrets.
- [X]  Swap `mysql-connector-j` for the PostgreSQL driver in `notification-service/pom.xml` (LC-010).

**B. Shipment-service correctness (TDD)**

- [X]  LC-001: Test that invalid input (blank origin/destination/tracking number, weight <= 0, missing fields) returns `HTTP 400` with a JSON error body. Then handle `IllegalArgumentException` in `GlobalExceptionHandler` (decide: domain guard clauses only, or add Bean Validation on `CreateShipmentRequest`).
- [X]  LC-002: Add integration tests for `GET` non-existent id -> 404, and duplicate tracking number -> 409.
- [X]  LC-005: Narrow the 409 handler so only unique-constraint violations report "already exists" (e.g. check `existsByTrackingNumber` first, or inspect the constraint name).
- [X]  LC-003: Introduce `ShipmentService` and move creation/lookup logic out of the controller.
- [X]  LC-004: Replace the `String status` with a `ShipmentStatus` enum (`PENDING` first; add later states as features need them).

**C. Configuration, secrets & test isolation**

- [X]  LC-006: Make `.env` the single source of truth. Makefile loads it (`-include .env` + `export`), and `application.properties` reads credentials from environment variables without hardcoded password defaults.
- [X]  LC-009: Externalize datasource host/port/database name (currently hardcoded `localhost:5433/logistics_db`).
- [X]  LC-007: Drive Artemis credentials from `.env` (compose currently hardcodes `admin/admin`; the old roadmap referenced `ACTIVEMQ_PASSWORD` but nothing reads it).
- [X]  LC-008: Check whether the committed-then-removed `.env` ever held real secrets; if so, rotate them (and the GitHub Secrets).
- [X]  LC-016: Stop integration tests from running against the dev database (`deleteAll()` currently wipes local data). Use Testcontainers PostgreSQL (`@ServiceConnection`) or a dedicated test database/profile.
- [X]  LC-017: Split JPA settings by profile (`show-sql` and `ddl-auto=update` are dev-only; remove the redundant Hibernate dialect line).

**D. Build & CI**

- [X]  LC-011: Verify and standardize the web starter name across modules (`spring-boot-starter-web` in shipment vs `spring-boot-starter-webmvc` in tracking) against the Spring Boot 4.1 docs.
- [X]  LC-012: Move shared dependencies (test starters, Testcontainers) into the parent POM's `dependencyManagement`, and add `spring-boot-starter-test` to tracking and notification.
- [X]  LC-013: Update `ci.yml`: wait for both containers, publish test reports on failure, and drop the `make up` dependency once tests use Testcontainers.
- [X]  Confirm `./mvnw clean test` passes locally and in CI after the changes above.

**E. Phase 3 design decisions (write down before coding; proposed defaults in italics)**

- [X]  **Telemetry message contract (JSON):** *`truckId`, `trackingNumber`, `latitude`, `longitude`, `speedKmh`, `recordedAt` (ISO-8601 UTC).*
- [ ]  **Queue names:** *`telemetry.queue` for GPS updates; a dead-letter/retry policy for poison messages.*
- [X]  **"Delay" rule:** define what counts as a delay alert. *e.g. no update from a truck for N minutes, or the truck is outside an expected route/ETA window.*
- [X]  **Notification datastore:** *PostgreSQL, its own database (or schema) `notification_db`, created via compose init script.* Update `.env`/`compose.yaml` accordingly.
- [X]  **Tracking -> Shipment link:** decide whether tracking-service validates `trackingNumber` against shipment-service (REST call) or trusts the payload. *Proposed: trust the payload for now to keep services decoupled.*
- [X]  **Ports:** *shipment 8080, tracking 8081, notification 8082.*

### Phase 3: Asynchronous JMS Messaging (Tracking & Notification)

- [ ]  Add the missing Spring Boot application classes, `application.yml` files, and `src/test` trees for both services.
- [ ]  **TDD:** Write the failing test for the tracking REST endpoint (valid payload -> 202/201, invalid -> 400).
- [ ]  Configure ActiveMQ Artemis Producer in the **Tracking Service**.
- [ ]  Implement REST endpoint to accept high-frequency delivery truck GPS telemetry.
- [ ]  Configure ActiveMQ Artemis Consumer in the **Notification Service**.
- [ ]  Implement background worker to evaluate delays and save alerts to the Notification Database (PostgreSQL).
- [ ]  **Integration Test:** Verify message queuing and asynchronous consumption using Testcontainers (Artemis + PostgreSQL).
- [ ]  Add a `Dockerfile` for each service as it becomes runnable.

### Phase 4: Finalization & Demonstration

- [ ]  Perform a full end-to-end system test (shipment created -> telemetry ingested -> alert stored).
- [ ]  Optionally extend `compose.yaml` to run all three services, not just infrastructure.
- [ ]  Polish `README.md` and API documentation.
- [ ]  Refresh `ARCHITECTURE.md` and `microservice-architecture.html` to match the final system; close out `KNOWN_ISSUES.md`.
- [ ]  Record the 5-10 minute YouTube demo video.
- [ ]  Submit proof of work before the deadline.

---

## Backlog (not scheduled)

* `GET /api/v1/shipments` (list/paginate) and status-update endpoint.
* API documentation via OpenAPI/Swagger.
* Spring Actuator health endpoints for each service.
* Database migrations (Flyway) to replace `ddl-auto=update`.
