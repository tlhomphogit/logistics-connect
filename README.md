# LogisticsConnect

A multi-module microservices application for logistics tracking: synchronous order management over REST and asynchronous telemetry processing over JMS.

## Project Status


| Service                                          | Status                                                             |
| -------------------------------------------------- | -------------------------------------------------------------------- |
| Shipment Service (REST + PostgreSQL)             | Implemented: create and fetch shipments.                           |
| Tracking Service (REST + JMS producer)           | Implemented: accepts telemetry and publishes to Artemis.           |
| Notification Service (JMS consumer + PostgreSQL) | Implemented: saves delay alerts and exposes them for verification. |

See [ROADMAP.md](ROADMAP.md) for the plan, [ARCHITECTURE.md](ARCHITECTURE.md) for the structure, and [KNOWN_ISSUES.md](KNOWN_ISSUES.md) for open problems.

## Tech Stack

* **Language:** Java 26
* **Framework:** Spring Boot 4.1.1
* **Build Tool:** Maven (Wrapper included)
* **Database:** PostgreSQL 18
* **Message Broker:** Apache ActiveMQ Artemis
* **Infrastructure:** Docker, Docker Compose & Make
* **Testing:** JUnit 5, Spring Boot Test (`RestTestClient`), Testcontainers

## Architecture Overview

LogisticsConnect follows a Test-Driven Development (TDD) approach and is divided into three microservices:

1. **Shipment Service (REST):** Standard operations for shipping orders, stored in `shipment_db` when running under Compose.
2. **Tracking Service (REST & JMS Producer):** Ingests telemetry and publishes it to the `telemetry.queue` Artemis queue. It trusts the tracking number in the payload to keep services decoupled.
3. **Notification Service (JMS Consumer):** Consumes telemetry, saves an alert when `recordedAt` is more than 15 minutes old, and exposes saved alerts at `GET /api/v1/alerts`.

## Prerequisites

* JDK 26
* Docker Desktop (running)
* `make` (on Windows, use Git Bash/WSL, or run the underlying commands from the Makefile)

## Local Development Setup

```bash
# 0. Create your local environment file (never commit it)
cp .env.example .env        # then edit the values

# 1. Build service JARs used by the Dockerfiles
./mvnw -DskipTests package

# 2. Build and start all services and infrastructure
docker compose up --build -d

# 3. Check status and logs
docker compose ps
docker compose logs -f shipment-service tracking-service notification-service

# 4. Run the global test suite (uses Testcontainers)
./mvnw clean test

# 5. Stop containers (keep data) / remove containers and volumes
docker compose down
# docker compose down -v  # destructive: permanently removes database data
```

Compose loads database and broker credentials from `.env`. The shipment Compose service enables Hibernate schema updates for its isolated `shipment_db`; notification uses `notification_db`. Integration tests use Testcontainers, not the Compose databases.

Service and infrastructure ports: shipment `8080`, tracking `8081`, notification `8082`, PostgreSQL `5433`, Artemis JMS `61616`, Artemis console `http://localhost:8161`.

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
* Invalid input (blank fields, weight <= 0) returns `400` with a JSON error body.

**Get a shipment**

```bash
curl http://localhost:8080/api/v1/shipments/1
```

* `200 OK`: returns the shipment.
* `404 Not Found`: no shipment with that id.

## Postman end-to-end system test

Start the complete Compose stack using the setup steps above. Create a shipment with `POST http://localhost:8080/api/v1/shipments` and this JSON body:

```json
{
  "trackingNumber":"TRK-E2E-001",
  "origin":"Benoni",
  "destination":"Cape Town",
  "weight":450.5
}
```

Then submit telemetry with `POST http://localhost:8081/api/telemetry`, `Content-Type: application/json`, and a timestamp at least 15 minutes old to trigger a delay alert. Use a unique tracking number for each run:

```json
{
  "truckId":"TRK-100",
  "trackingNumber":"TRK-E2E-001",
  "latitude":-26.1887,
  "longitude":28.3207,
  "speedKmh":0,
  "recordedAt":"2020-01-01T00:00:00Z"
}
```

Expect `202 Accepted`. Allow the JMS consumer a moment to process, then request `GET http://localhost:8082/api/v1/alerts`. The response should include the matching `truckId` and `trackingNumber`. A `recordedAt` within the last 15 minutes is accepted but does not create a delay alert. Tracking trusts the supplied tracking number; it does not currently verify that a shipment exists.

## Continuous Integration

GitHub Actions (`.github/workflows/ci.yml`) builds and tests on every push and pull request to `main`. Database credentials are supplied through GitHub Secrets (`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`).
