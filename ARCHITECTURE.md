# LogisticsConnect Architecture & Directory Tree

## Microservices Workspace Overview
This project utilizes a Maven multi-module architecture to manage three distinct microservices within a single monorepo. The root directory acts as a project manager (Parent POM) and infrastructure host, while all business logic, testing, and containerization instructions are isolated within the sub-modules.

## Directory Tree

```text
LogisticsConnect/
├── .env                            # Secure environment variables (Excluded from version control)
├── .gitignore                      # Git ignore rules for IDE, env files, and compiled targets
├── compose.yaml                    # Docker Compose configuration for MySQL and ActiveMQ
├── ROADMAP.md                      # Step-by-step project tracking and TDD rules
├── ARCHITECTURE.md                 # This file
├── pom.xml                         # Parent POM: Manages dependencies, sub-modules, and Java 26 config
├── mvnw                            # Maven Wrapper script for Linux/macOS
├── mvnw.cmd                        # Maven Wrapper script for Windows
└── .mvn/                           # Maven Wrapper internal configuration folder
    │
    ├── shipment-service/           # MICROSERVICE 1: Synchronous REST APIs
    │   ├── pom.xml                 # Child POM (Inherits from Parent)
    │   ├── Dockerfile              # Containerization instructions for the Shipment Service
    │   └── src/
    │       ├── main/java/com/logistics/shipment/
    │       │   ├── ShipmentApplication.java
    │       │   ├── controller/     # REST Endpoints
    │       │   ├── model/          # Domain Entities
    │       │   ├── repository/     # Spring Data JPA Interfaces
    │       │   └── service/        # Business Logic & Guard Clauses
    │       ├── main/resources/
    │       │   └── application.yml # Database connection and server port configuration
    │       └── test/java/com/logistics/shipment/
    │           ├── controller/     # Unit and Integration tests for REST APIs
    │           └── service/        # Unit tests verifying business logic and guard clauses
    │
    ├── tracking-service/           # MICROSERVICE 2: High-Frequency Telemetry Ingestion
    │   ├── pom.xml                 
    │   ├── Dockerfile              # Containerization instructions for the Tracking Service
    │   └── src/
    │       ├── main/java/com/logistics/tracking/
    │       │   ├── TrackingApplication.java
    │       │   ├── controller/     # REST Endpoints for GPS ingestion
    │       │   └── messaging/      # JMS Producer (Pushes to ActiveMQ)
    │       ├── main/resources/
    │       │   └── application.yml # ActiveMQ connection and server port configuration
    │       └── test/java/com/logistics/tracking/
    │           └── messaging/      # Integration tests using Testcontainers for ActiveMQ
    │
    └── notification-service/       # MICROSERVICE 3: Asynchronous Background Worker
        ├── pom.xml                     
        ├── Dockerfile              # Containerization instructions for the Notification Service
        └── src/
            ├── main/java/com/logistics/notification/
            │   ├── NotificationApplication.java
            │   ├── messaging/      # JMS Consumer (Listens to ActiveMQ)
            │   ├── model/          # Alert/Log Entities
            │   └── repository/     # Database persistence for alerts
            ├── main/resources/
            │   └── application.yml # ActiveMQ and Notification Database configuration
            └── test/java/com/logistics/notification/
                └── messaging/      # Integration tests verifying queue consumption and database saves