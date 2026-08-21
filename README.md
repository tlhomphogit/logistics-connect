# LogisticsConnect

A robust, multi-module microservices application designed to handle logistics tracking, synchronous order management, and asynchronous telemetry processing.

## Tech Stack
* **Language:** Java 26
* **Framework:** Spring Boot 4.1.1
* **Build Tool:** Maven (Wrapper included)
* **Databases:** MySQL
* **Message Broker:** Apache ActiveMQ Artemis
* **Infrastructure:** Docker & Docker Compose

## Architecture Overview
LogisticsConnect is built using a Test-Driven Development (TDD) approach and is divided into three core microservices:

1. **Shipment Service (REST):** Manages standard CRUD operations for shipping orders and saves data to the `shipment_db`.
2. **Tracking Service (REST & JMS Producer):** Ingests high-frequency GPS telemetry from delivery trucks and immediately offloads it to an ActiveMQ queue to ensure high availability.
3. **Notification Service (JMS Consumer):** Operates as a background worker, consuming telemetry messages from the queue, processing delay alerts, and storing them in the `notification_db`.

## Local Development Setup
*(Instructions for starting the Docker infrastructure and running the Maven modules will be added here as development progresses).*