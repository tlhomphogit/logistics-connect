package com.logistics.shipment.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShipmentTest {

    @Test
    void shouldCreateShipmentWhenValidDataProvided() {
        // Arrange & Act
        Shipment shipment = new Shipment("TRK-8899", "Benoni", "Cape Town", 12.5);

        // Assert
        assertNotNull(shipment);
        assertEquals("TRK-8899", shipment.getTrackingNumber());
        assertEquals("Benoni", shipment.getOrigin());
    }

    @Test
    void shouldThrowExceptionWhenWeightIsNegativeOrZero() {
        // Arrange, Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Shipment("TRK-9900", "Benoni", "Durban", -5.0);
        });

        assertEquals("Shipment weight must be greater than zero.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenOriginIsBlank() {
        // Arrange, Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Shipment("TRK-9901", "", "Pretoria", 10.0);
        });

        assertEquals("Origin cannot be null or blank.", exception.getMessage());
    }
}