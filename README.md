# LogisticsConnect

A multi-module microservices application for logistics tracking: synchronous order management over REST and asynchronous telemetry processing over JMS.

## Project Status

| Service | Status |
|---------|--------|
| Shipment Service (REST + PostgreSQL) | Implemented: create and fetch shipments. Hardening in progress. |
| Tracking Service (REST + JMS producer) | Not started (Phase 3) |
| Notification Service (JMS consumer + PostgreSQL) | Not started (Phase 3) |

See [ROADMAP.md](ROADMAP.md) for the plan, [ARCHITECTURE.md](ARCHITECTURE.md) for the structure, and [KNOWN_ISSUES.md](KNOWN_ISSUES.md) for open problems.

## Tech Stack
* **Language:** Java 26
* **Framework:** Spring Boot 4.1.1
* **Build Tool:** Maven (Wrapper included)
* **Database:** PostgreSQL 18
* **Message Broker:** Apache ActiveMQ Artemis
* **Infrastructure:** Docker, Docker Compose & Make
* **Testing:** JUnit 5, Spring Boot Test (`RestTestClient`), Testcontainers (planned)

## Architecture Overview
LogisticsConnect follows a Test-Driven Development (TDD) approach and is divided into three microservices:

1. **Shipment Service (REST):** Standard operations for shipping orders, stored in `logistics_db`.
2. **Tracking Service (REST & JMS Producer):** *(planned)* Ingests high-frequency GPS telemetry from delivery trucks and immediately offloads it to an ActiveMQ queue for high availability.
3. **Notification Service (JMS Consumer):** *(planned)* Background worker that consumes telemetry messages, evaluates delays and stores alerts in PostgreSQL.

## Prerequisites
* JDK 26
* Docker Desktop (running)
* `make` (on Windows, use Git Bash/WSL, or run the underlying commands from the Makefile)

## Local Development Setup

```bash
# 0. Create your local environment file (never commit it)
cp .env.example .env        # then edit the values

# 1. Start the infrastructure (PostgreSQL & ActiveMQ Artemis)
make up

# 2. Access the database interactively
make psql

# 3. Run the global test suite
make test

# 4. Run tests for a specific microservice
make test MODULE=shipment-service

# 5. Start a specific microservice locally
make run MODULE=shipment-service

# 6. Tear down infrastructure (keep data) / wipe everything
make down
make destroy
```

**Configuration notes (current behaviour):**
* Docker Compose reads `.env`, but the application and Makefile do not yet. The app falls back to the defaults in `shipment-service/src/main/resources/application.properties`, so keep your `.env` values in line with them (tracked as LC-006).
* Integration tests currently run against the local dev database and clear the `shipments` table (LC-016). Do not keep data you care about there while running `make test`.
* Infrastructure ports: PostgreSQL `localhost:5433`, Artemis JMS `61616`, Artemis console `http://localhost:8161`.

## Shipment Service API

Base path: `/api/v1/shipments` (default port 8080)

**Create a shipment**
```bash
curl -X POST http://localhost:8080/api/v1/shipments \
  -H "Content-Type: application/json" \
  -d '{"trackingNumber":"TRK-2026-X","origin":"Benoni","destination":"Cape Town","weight":450.5}'
```
* `201 Created`: returns the shipment, including generated `id` and `status: "PENDING"`.
* `409 Conflict`: a shipment with that tracking number already exists.
* Invalid input (blank fields, weight <= 0) currently returns `500`; a proper `400` response is scheduled (LC-001).

**Get a shipment**
```bash
curl http://localhost:8080/api/v1/shipments/1
```
* `200 OK`: returns the shipment.
* `404 Not Found`: no shipment with that id.

## Continuous Integration
GitHub Actions (`.github/workflows/ci.yml`) builds and tests on every push and pull request to `main`. Database credentials are supplied through GitHub Secrets (`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`).
