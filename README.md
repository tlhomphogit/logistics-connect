# LogisticsConnect

A robust, multi-module microservices application designed to handle logistics tracking, synchronous order management, and asynchronous telemetry processing.

## Tech Stack
* **Language:** Java 26
* **Framework:** Spring Boot 4.1.1
* **Build Tool:** Maven (Wrapper included)
* **Databases:** PostgreSQL 18
* **Message Broker:** Apache ActiveMQ Artemis
* **Infrastructure:** Docker, Docker Compose, & Make

## Architecture Overview
LogisticsConnect is built using a Test-Driven Development (TDD) approach and is divided into three core microservices:

1. **Shipment Service (REST):** Manages standard CRUD operations for shipping orders and saves data to the `logistics_db`.
2. **Tracking Service (REST & JMS Producer):** Ingests high-frequency GPS telemetry from delivery trucks and immediately offloads it to an ActiveMQ queue to ensure high availability.
3. **Notification Service (JMS Consumer):** Operates as a background worker, consuming telemetry messages from the queue, processing delay alerts, and storing them in the database.

## Local Development Setup

We use a central `Makefile` to simplify infrastructure and build commands. Ensure Docker Desktop is running before beginning.

```bash
# 1. Start the Infrastructure (PostgreSQL & ActiveMQ)
make up

# 2. Access the Database Interactively
make psql

# 3. Run the Global Test Suite
make test

# 4. Run Tests for a Specific Microservice
make test MODULE=shipment-service

# 5. Start a Specific Microservice Locally
make run MODULE=shipment-service

# 6. Tear Down Infrastructure
make down