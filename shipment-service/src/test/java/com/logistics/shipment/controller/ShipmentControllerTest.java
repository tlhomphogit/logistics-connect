package com.logistics.shipment.controller;

import com.logistics.shipment.domain.Shipment;
import com.logistics.shipment.domain.ShipmentStatus;
import com.logistics.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext
class ShipmentControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    @Autowired
    private RestTestClient restClient;

    @Autowired
    private ShipmentRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateShipmentAndReturn201() {
        // Arrange
        Map<String, Object> requestPayload = shipmentPayload("TRK-2026-X", "Benoni", "Cape Town", 450.5);

        // Act
        Shipment response = restClient.post()
            .uri("/api/v1/shipments")
            .body(requestPayload)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Shipment.class)
            .returnResult()
            .getResponseBody();

        // Assert
        assertNotNull(response);
        assertEquals("TRK-2026-X", response.getTrackingNumber());
        assertEquals(ShipmentStatus.PENDING, response.getStatus());
        
        // Verify it was actually saved to PostgreSQL
        assertEquals(1, repository.count());
    }

    @Test
    void shouldGetShipmentByIdAndReturn200() {
        // Arrange: Seed the database directly via the repository
        Shipment shipmentToSave = new Shipment("TRK-GET-123", "Pretoria", "Durban", 200.0);
        Shipment savedShipment = repository.save(shipmentToSave);
        Long shipmentId = savedShipment.getId();

        // Act: Fetch the shipment via the REST API
        Shipment response = restClient.get()
            .uri("/api/v1/shipments/" + shipmentId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Shipment.class)
            .returnResult()
            .getResponseBody();

        // Assert
        assertNotNull(response);
        assertEquals(shipmentId, response.getId());
        assertEquals("TRK-GET-123", response.getTrackingNumber());
        assertEquals("Pretoria", response.getOrigin());
        assertEquals("Durban", response.getDestination());
        assertEquals(ShipmentStatus.PENDING, response.getStatus());
    }

    @Test
    void shouldReturn400WhenOriginIsBlank() {
        // Arrange: Missing/blank origin
        Map<String, Object> requestPayload = shipmentPayload("TRK-ERR-001", "", "Cape Town", 450.5);

        // Act & Assert
        restClient.post()
            .uri("/api/v1/shipments")
            .body(requestPayload)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.error").isEqualTo("Bad Request")
            .jsonPath("$.message").isEqualTo("Origin cannot be null or blank.");
    }

    @Test
    void shouldReturn400WhenWeightIsNegative() {
        // Arrange: Negative weight
        Map<String, Object> requestPayload = shipmentPayload("TRK-ERR-002", "Benoni", "Cape Town", -10.0);

        // Act & Assert
        restClient.post()
            .uri("/api/v1/shipments")
            .body(requestPayload)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.error").isEqualTo("Bad Request")
            .jsonPath("$.message").isEqualTo("Shipment weight must be greater than zero.");
    }

    @Test
    void shouldReturn404WhenShipmentDoesNotExist() {
        // Act & Assert: Request an ID that is virtually guaranteed not to exist
        restClient.get()
            .uri("/api/v1/shipments/99999")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void shouldReturn409WhenTrackingNumberAlreadyExists() {
        // Arrange: Seed the database with a specific tracking number
        Shipment existingShipment = new Shipment("TRK-DUP-999", "Benoni", "Cape Town", 150.0);
        repository.save(existingShipment);

        // Prepare a new POST request trying to reuse that exact tracking number
        Map<String, Object> requestPayload = shipmentPayload("TRK-DUP-999", "Pretoria", "Durban", 200.0);

        // Act & Assert
        restClient.post()
            .uri("/api/v1/shipments")
            .body(requestPayload)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.error").isEqualTo("Conflict")
            .jsonPath("$.message").isEqualTo("A record with this unique identifier (e.g., tracking number) already exists.");
    }

    private Map<String, Object> shipmentPayload(String trackingNumber, String origin, String destination, double weight) {
        return Map.of(
            "trackingNumber", trackingNumber,
            "origin", origin,
            "destination", destination,
            "weight", weight
        );
    }
}