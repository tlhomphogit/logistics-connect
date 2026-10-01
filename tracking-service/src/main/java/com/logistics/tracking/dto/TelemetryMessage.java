package com.logistics.tracking.dto;

import java.time.Instant;

/**
 * Data Transfer Object representing an incoming GPS telemetry payload from a truck.
 */
public record TelemetryMessage(
        String truckId,
        String trackingNumber,
        Double latitude,
        Double longitude,
        Double speedKmh,
        Instant recordedAt
) {
}