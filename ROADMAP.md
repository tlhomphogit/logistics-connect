# LogisticsConnect Project Roadmap

## Goal
Build a microservices-based logistics tracking application fulfilling the Systems Integration requirements: RESTful APIs, JSON serialization, and Java Messaging Service (JMS). 

## 🛠 Methodology
* **Test-Driven Development (TDD):** Unit tests written using JUnit 5 before feature implementation. Guard clauses and domain logic must be tested first.
* **Integration Testing:** Ensuring all isolated services, databases, and message brokers communicate correctly using Testcontainers.
* **Continuous Integration (CI/CD):** Automated pipelines (GitHub Actions) configured to run build and test stages on every commit.
* **Security First:** No hardcoded credentials. All secrets are managed via `.env` files and excluded from version control.

---

### Phase 1: Repository Setup & Secure Infrastructure (Current)
- [x] Set up the multi-module Spring Boot project structure in IntelliJ IDEA.
- [x] Initialize Git repository and `ROADMAP.md`.
- [x] Create `.gitignore` to exclude IDE files, compiled code, and `.env` files.
- [ ] Create a `.env` file for secure local environment variables (e.g., `MYSQL_ROOT_PASSWORD`, `ACTIVEMQ_PASSWORD`).
- [ ] Create `docker-compose.yml` to set up the MySQL Database and ActiveMQ Artemis broker, injecting variables from `.env`.
- [ ] Configure the CI/CD pipeline file to run automated tests on push.

### Phase 2: Synchronous REST Services (Shipment Service)
- [ ] **TDD:** Write unit tests for Shipment domain models and guard clauses.
- [ ] Configure Spring Data JPA to connect to the MySQL database securely using environment variables.
- [ ] Implement `POST /api/v1/shipments` to create shipping orders (JSON serialization).
- [ ] Implement `GET /api/v1/shipments/{id}` to retrieve order status.
- [ ] **Integration Test:** Verify database persistence and API JSON responses using Spring Boot test slices.

### Phase 3: Asynchronous JMS Messaging (Tracking & Notification)
- [ ] Configure ActiveMQ Artemis Producer in the **Tracking Service**.
- [ ] Implement REST endpoint to accept high-frequency delivery truck GPS telemetry.
- [ ] Configure ActiveMQ Artemis Consumer in the **Notification Service**.
- [ ] Implement background worker to evaluate delays and save alerts to the Notification MySQL Database.
- [ ] **Integration Test:** Spin up embedded ActiveMQ (or Testcontainers) to verify message queuing and asynchronous consumption.

### Phase 4: Finalization & Demonstration
- [ ] Perform a full end-to-end system test.
- [ ] Polish `README.md` and API documentation.
- [ ] Record the 5-10 minute YouTube demo video.
- [ ] Submit proof of work before the deadline.
