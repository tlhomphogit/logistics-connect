# LogisticsConnect Project Roadmap

## Goal
Build a microservices-based logistics tracking application fulfilling the Systems Integration requirements: RESTful APIs, JSON serialization, and Java Messaging Service (JMS). 

## 🛠 Methodology
* **Test-Driven Development (TDD):** Unit tests written using JUnit 5 before feature implementation. Guard clauses and domain logic must be tested first.
* **Integration Testing:** Ensuring all isolated services, databases, and message brokers communicate correctly using Testcontainers and full server environments.
* **Continuous Integration (CI/CD):** Automated pipelines (GitHub Actions) configured to run build and test stages on every commit, utilizing GitHub Secrets.
* **Security First:** No hardcoded credentials. All secrets are managed via `.env` files and excluded from version control.

---

### Phase 1: Repository Setup & Secure Infrastructure (Done)
- [x] Set up the multi-module Spring Boot project structure in IntelliJ IDEA.
- [x] Initialize Git repository and `ROADMAP.md`.
- [x] Create `.gitignore` to exclude IDE files, compiled code, and `.env` files.
- [x] Create a `.env` file for secure local environment variables (`POSTGRES_USER`, `ACTIVEMQ_PASSWORD`, etc.).
- [x] Create `compose.yaml` to set up the PostgreSQL 18 Database and ActiveMQ Artemis broker.
- [x] Implement a root `Makefile` to orchestrate Docker containers and Maven build commands.
- [x] Configure the CI/CD pipeline file to spin up infrastructure and run automated tests securely.

### Phase 2: Synchronous REST Services (Shipment Service) (Current)
- [x] **TDD:** Write full server integration test asserting `HTTP 404` for non-existent endpoints using `TestRestTemplate`.
- [ ] Configure Spring Data JPA and Hibernate (`ddl-auto`) to manage the PostgreSQL schema automatically.
- [ ] Define the `Shipment` domain model (Java Entity) and verify guard clauses.
- [ ] Implement `POST /api/v1/shipments` to create shipping orders (JSON serialization).
- [ ] Implement `GET /api/v1/shipments/{id}` to retrieve order status.
- [ ] **Integration Test:** Verify database persistence and API JSON responses.

### Phase 3: Asynchronous JMS Messaging (Tracking & Notification)
- [ ] Configure ActiveMQ Artemis Producer in the **Tracking Service**.
- [ ] Implement REST endpoint to accept high-frequency delivery truck GPS telemetry.
- [ ] Configure ActiveMQ Artemis Consumer in the **Notification Service**.
- [ ] Implement background worker to evaluate delays and save alerts to the Notification Database.
- [ ] **Integration Test:** Verify message queuing and asynchronous consumption.

### Phase 4: Finalization & Demonstration
- [ ] Perform a full end-to-end system test.
- [ ] Polish `README.md` and API documentation.
- [ ] Record the 5-10 minute YouTube demo video.
- [ ] Submit proof of work before the deadline.