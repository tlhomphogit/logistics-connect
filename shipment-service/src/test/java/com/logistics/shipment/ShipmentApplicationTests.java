package com.logistics.shipment;

import org.springframework.test.context.ActiveProfiles;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext
class ShipmentApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    @Test
    void contextLoads() {
        // This test passes if the Spring context successfully starts.
        // The startup process will automatically trigger Hibernate to generate the 'shipments' table.
    }
}