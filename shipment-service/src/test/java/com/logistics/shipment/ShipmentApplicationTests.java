package com.logistics.shipment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShipmentApplicationTests {

    @Test
    void contextLoads() {
        // This test passes if the Spring context successfully starts.
        // The startup process will automatically trigger Hibernate to generate the 'shipments' table.
    }
}