package com.logistics.notification.dto;

import java.time.Instant;

/**
 * Immutable DTO representing an incoming GPS telemetry payload from the message broker.
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