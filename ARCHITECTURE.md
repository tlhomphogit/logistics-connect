# LogisticsConnect Architecture & Directory Tree

## Microservices Workspace Overview
This project uses a Maven multi-module layout to manage three microservices in a single monorepo. The root directory acts as the parent POM and infrastructure host; business logic and tests live in the sub-modules.

**Legend:** ✅ implemented · 🔲 planned (scheduled in [ROADMAP.md](ROADMAP.md)). Open problems are tracked in [KNOWN_ISSUES.md](KNOWN_ISSUES.md).

## Module Status

| Module | Role | Data store | Messaging | Status |
|--------|------|------------|-----------|--------|
| `shipment-service` | Synchronous REST CRUD for shipping orders | PostgreSQL (`shipment_db` in Compose) | none | ✅ POST/GET implemented |
| `tracking-service` | REST ingestion of GPS telemetry, JMS producer | none | Artemis producer | ✅ `POST /api/telemetry` |
| `notification-service` | JMS consumer, delay evaluation, alert storage | PostgreSQL (`notification_db`) | Artemis consumer | ✅ Saves alerts; `GET /api/v1/alerts` for inspection |

## Key Decisions
* **PostgreSQL 18 is the only relational database.** The initial scaffold used MySQL; the project migrated to PostgreSQL. shipment-service was migrated first; `notification-service/pom.xml` now uses the PostgreSQL driver too.
* **Datastores:** shipment-service and notification-service use separate databases (`shipment_db` and `notification_db`) on the shared Postgres container.
* **Messaging:** Apache ActiveMQ Artemis via Spring's JMS support. Tracking publishes telemetry to `telemetry.queue` and notification consumes it.
* **Service coupling:** tracking trusts the telemetry's `trackingNumber`; it does not make a synchronous shipment-service lookup.
* **Delay rule:** an alert is stored when `recordedAt` is older than 15 minutes at consumption time.
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
│   ├── Dockerfile               ✅
│   └── src/
│       ├── main/java/com/logistics/shipment/
│       │   ├── ShipmentApplication.java          ✅
│       │   ├── controller/
│       │   │   ├── ShipmentController.java       ✅ POST /api/v1/shipments, GET /api/v1/shipments/{id}
│       │   │   ├── CreateShipmentRequest.java    ✅ DTO (record) for POST payloads
│       │   │   └── GlobalExceptionHandler.java   ✅ Maps invalid input to 400 and duplicate tracking number to 409
│       │   ├── domain/
│       │   │   └── Shipment.java                 ✅ Entity + guard clauses and ShipmentStatus enum
│       │   ├── repository/
│       │   │   └── ShipmentRepository.java       ✅ Spring Data JPA
│       │   └── service/                          ✅ Business logic layer
│       ├── main/resources/
│       │   └── application.properties            ✅ PostgreSQL + Hibernate config
│       └── test/java/com/logistics/shipment/
│           ├── ShipmentApplicationTests.java     ✅ Context-load test
│           ├── controller/
│           │   └── ShipmentControllerTest.java   ✅ Integration tests (201, 200, 404, 409, 400)
│           └── domain/
│               └── ShipmentTest.java             ✅ Guard-clause unit tests
│
├── tracking-service/            MICROSERVICE 2: High-frequency telemetry ingestion
│   ├── pom.xml                  ✅
│   ├── Dockerfile               ✅
│   └── src/
│       ├── main/java/com/logistics/tracking/
│       │   ├── TrackingApplication.java
│       │   ├── controller/      # REST endpoint for GPS ingestion
│       │   └── producer/        # JMS producer (pushes to Artemis)
│       ├── main/resources/application.properties # Artemis connection, server port
│       └── test/java/com/logistics/tracking/
│           └── messaging/       # Integration tests with Testcontainers (Artemis)
│
└── notification-service/        MICROSERVICE 3: Asynchronous background worker
    ├── pom.xml                  ✅
    ├── Dockerfile               ✅
    └── src/
        ├── main/java/com/logistics/notification/
        │   ├── NotificationApplication.java
        │   ├── consumer/        # JMS consumer (listens to Artemis)
        │   ├── controller/      # GET /api/v1/alerts
        │   ├── entity/          # Alert/log entities
        │   └── repository/      # Persistence for alerts
        ├── main/resources/application.properties # Artemis + PostgreSQL configuration
        └── test/java/com/logistics/notification/
            └── messaging/       # Integration tests: queue consumption + DB save
```

## Infrastructure & Ports

| Component | Container | Host port | Notes |
|-----------|-----------|-----------|-------|
| PostgreSQL 18 | `logistics-postgres` | 5433 | Credentials come from `.env` via Docker Compose |
| ActiveMQ Artemis 2.44 | `logistics-artemis` | 61616 (JMS), 8161 (web console) | Credentials currently hardcoded in `compose.yaml` (LC-007) |
| shipment-service | `logistics-shipment-service` | 8080 | `shipment_db` |
| tracking-service | `logistics-tracking-service` | 8081 | Telemetry REST API |
| notification-service | `logistics-notification-service` | 8082 | Alert REST API; uses `notification_db` |

## Configuration & Secrets Flow
1. `.env` (gitignored) holds `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`. Copy `.env.example` to create it.
2. **Docker Compose** reads `.env` for database and Artemis credentials and passes them into the application containers.
3. Applications connect to Postgres and Artemis via Compose service names on the internal network; local host ports are published for development tools.
4. The shipment Compose service enables schema updates for its own `shipment_db`; automated tests use Testcontainers databases.
5. **CI** runs Maven tests; integration tests provision isolated dependencies through Testcontainers.

## Testing Strategy
* **Unit:** JUnit 5 for domain guard clauses (written first, TDD).
* **Integration:** `@SpringBootTest` with `RestTestClient` against Testcontainers PostgreSQL. Notification integration tests also use an Artemis Testcontainer. Compose is the manual end-to-end environment for checking all three services together.
* **CI:** GitHub Actions runs the full suite on every push and pull request to `main`.
