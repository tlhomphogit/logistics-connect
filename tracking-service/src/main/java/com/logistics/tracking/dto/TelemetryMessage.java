package com.logistics.tracking.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Data Transfer Object representing an incoming GPS telemetry payload from a truck.
 */
public record TelemetryMessage(
        @Schema(description = "Truck identifier reporting the telemetry.", example = "TRK-100")
        String truckId,

        @Schema(description = "Tracking number associated with the shipment itinerary.", example = "TRK-E2E-001")
        String trackingNumber,

        @Schema(description = "Latitude coordinate in decimal degrees.", example = "-26.1887")
        Double latitude,

        @Schema(description = "Longitude coordinate in decimal degrees.", example = "28.3207")
        Double longitude,

        @Schema(description = "Current truck speed in km/h.", example = "0")
        Double speedKmh,

        @Schema(description = "Time the telemetry reading was captured in ISO-8601 format.", example = "2020-01-01T00:00:00Z")
        Instant recordedAt
) {
}