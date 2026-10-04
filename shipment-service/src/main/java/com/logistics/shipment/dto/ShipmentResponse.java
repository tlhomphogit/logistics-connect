package com.logistics.shipment.dto;

import java.time.Instant;

public record ShipmentResponse(
        String trackingNumber,
        String truckId,
        String origin,
        String destination,
        String status,
        Instant createdAt
) {
}