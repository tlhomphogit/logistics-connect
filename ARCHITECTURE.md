# LogisticsConnect Architecture & Directory Tree

## Microservices Workspace Overview
This project uses a Maven multi-module layout to manage three microservices in a single monorepo. The root directory acts as the parent POM and infrastructure host; business logic and tests live in the sub-modules.

**Legend:** ✅ implemented · 🔲 planned (scheduled in [ROADMAP.md](ROADMAP.md)). Open problems are tracked in [KNOWN_ISSUES.md](KNOWN_ISSUES.md).

## Module Status

| Module | Role | Data store | Messaging | Status |
|--------|------|------------|-----------|--------|
| `shipment-service` | Synchronous REST CRUD for shipping orders | PostgreSQL (`logistics_db`) | none | ✅ POST/GET implemented, hardening in Phase 2.5 |
| `tracking-service` | REST ingestion of GPS telemetry, JMS producer | none | Artemis producer | 🔲 `pom.xml` only (Phase 3) |
| `notification-service` | JMS consumer, delay evaluation, alert storage | PostgreSQL (database/schema TBD, see below) | Artemis consumer | 🔲 `pom.xml` only (Phase 3) |

## Key Decisions
* **PostgreSQL 18 is the only relational database.** The initial scaffold used MySQL; the project migrated to PostgreSQL. shipment-service was migrated first; `notification-service/pom.xml` now uses the PostgreSQL driver too.
* **Notification datastore:** to be finalised in Phase 2.5 (proposed: separate `notification_db` on the same Postgres container so each service owns its data).
* **Messaging:** Apache ActiveMQ Artemis via Spring's JMS support. Planned queue: `telemetry.queue` (final names decided in Phase 2.5).
* **Package naming:** entities live in `domain/` (shipment-service renamed `model` to `domain`). Notification-service should follow the same convention.

## Directory Tree

```text
logistics-connect/
├── .env                         ✅ Local secrets (gitignored, never commit)
├── .env.example                 ✅ Documents required variables without secrets
├── .gitignore                   ✅
├── .gitattributes               ✅
├── .github/workflows/ci.yml     ✅ GitHub Actions: build + test on push/PR to main
├── .mvn/wrapper/                ✅ Maven Wrapper configuration
├── mvnw, mvnw.cmd               ✅ Maven Wrapper scripts (Linux/macOS, Windows)
├── compose.yaml                 ✅ PostgreSQL 18 (host port 5433) and Artemis (61616 JMS, 8161 console)
├── Makefile                     ✅ up/down/destroy/psql/logs/status/test/build/run
├── pom.xml                      ✅ Parent POM: modules, Java 26, Docker profiles (infra-up / infra-down)
├── README.md                    ✅ Setup, API reference, current status
├── ROADMAP.md                   ✅ Phased plan and TDD rules
├── ARCHITECTURE.md              ✅ This file
├── KNOWN_ISSUES.md              ✅ Audit findings (LC-0xx)
├── microservice-architecture.html ✅ Visual architecture diagram
│
├── shipment-service/            MICROSERVICE 1: Synchronous REST API
│   ├── pom.xml                  ✅
│   ├── Dockerfile               🔲 Phase 3/4
│   └── src/
│       ├── main/java/com/logistics/shipment/
│       │   ├── ShipmentApplication.java          ✅
│       │   ├── controller/
│       │   │   ├── ShipmentController.java       ✅ POST /api/v1/shipments, GET /api/v1/shipments/{id}
│       │   │   ├── CreateShipmentRequest.java    ✅ DTO (record) for POST payloads
│       │   │   └── GlobalExceptionHandler.java   ✅ Maps duplicate tracking number to 409 (400 handling: 🔲 LC-001)
│       │   ├── domain/
│       │   │   └── Shipment.java                 ✅ Entity + guard clauses (status enum: 🔲 LC-004)
│       │   ├── repository/
│       │   │   └── ShipmentRepository.java       ✅ Spring Data JPA
│       │   └── service/                          🔲 Business logic layer (LC-003)
│       ├── main/resources/
│       │   └── application.properties            ✅ PostgreSQL + Hibernate config
│       └── test/java/com/logistics/shipment/
│           ├── ShipmentApplicationTests.java     ✅ Context-load test
│           ├── controller/
│           │   └── ShipmentControllerTest.java   ✅ Integration tests (201, 200); 🔲 404/409/400 (LC-002)
│           └── domain/
│               └── ShipmentTest.java             ✅ Guard-clause unit tests
│
├── tracking-service/            MICROSERVICE 2: High-frequency telemetry ingestion
│   ├── pom.xml                  ✅ (dependencies declared, no code yet)
│   ├── Dockerfile               🔲
│   └── src/                     🔲 Entire source tree
│       ├── main/java/com/logistics/tracking/
│       │   ├── TrackingApplication.java
│       │   ├── controller/      # REST endpoint for GPS ingestion
│       │   └── messaging/       # JMS producer (pushes to Artemis)
│       ├── main/resources/application.yml        # Artemis connection, server port
│       └── test/java/com/logistics/tracking/
│           └── messaging/       # Integration tests with Testcontainers (Artemis)
│
└── notification-service/        MICROSERVICE 3: Asynchronous background worker
    ├── pom.xml                  ✅ (PostgreSQL driver as of LC-010; no code yet)
    ├── Dockerfile               🔲
    └── src/                     🔲 Entire source tree
        ├── main/java/com/logistics/notification/
        │   ├── NotificationApplication.java
        │   ├── messaging/       # JMS consumer (listens to Artemis)
        │   ├── domain/          # Alert/log entities
        │   └── repository/      # Persistence for alerts
        ├── main/resources/application.yml        # Artemis + PostgreSQL configuration
        └── test/java/com/logistics/notification/
            └── messaging/       # Integration tests: queue consumption + DB save
```

## Infrastructure & Ports

| Component | Container | Host port | Notes |
|-----------|-----------|-----------|-------|
| PostgreSQL 18 | `logistics-postgres` | 5433 | Credentials come from `.env` via Docker Compose |
| ActiveMQ Artemis 2.44 | `logistics-artemis` | 61616 (JMS), 8161 (web console) | Credentials currently hardcoded in `compose.yaml` (LC-007) |
| shipment-service | n/a (run via `make run`) | 8080 (Spring default) | |
| tracking-service | 🔲 | 🔲 proposed 8081 | |
| notification-service | 🔲 | 🔲 proposed 8082 | |

## Configuration & Secrets Flow (current behaviour)
1. `.env` (gitignored) holds `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`. Copy `.env.example` to create it.
2. **Docker Compose** reads `.env` to create the database.
3. **shipment-service** does *not* read `.env`. It reads the `POSTGRES_USER` / `POSTGRES_PASSWORD` environment variables and otherwise falls back to defaults in `application.properties`; the database URL is hardcoded. The Makefile also hardcodes the credentials for `make psql`.
4. Therefore, until LC-006 and LC-009 are fixed, the values in `.env` (and the CI GitHub Secrets) must match those defaults.
5. **CI** writes `.env` from GitHub Secrets, then runs `make up` and `./mvnw clean test`.

## Testing Strategy
* **Unit:** JUnit 5 for domain guard clauses (written first, TDD).
* **Integration:** `@SpringBootTest` with `RestTestClient` against PostgreSQL. Currently this uses the shared dev database (LC-016); target state is Testcontainers PostgreSQL. Phase 3 adds Testcontainers for Artemis and PostgreSQL.
* **CI:** GitHub Actions runs the full suite on every push and pull request to `main`.
