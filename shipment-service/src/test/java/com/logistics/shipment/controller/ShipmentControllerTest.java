package com.logistics.shipment.controller;

import com.logistics.shipment.domain.Shipment;
import com.logistics.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class ShipmentControllerTest {

    @Autowired
    private RestTestClient restClient;

    @Autowired
    private ShipmentRepository repository;

    @BeforeEach
    void setUp() {
        // Ensure a clean database state before each test
        repository.deleteAll();
    }

    @Test
    void shouldCreateShipmentAndReturn201() {
        // Arrange
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("trackingNumber", "TRK-2026-X");
        requestPayload.put("origin", "Benoni");
        requestPayload.put("destination", "Cape Town");
        requestPayload.put("weight", 450.5);

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
        assertEquals("PENDING", response.getStatus());
        
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
        assertEquals("PENDING", response.getStatus());
    }
}