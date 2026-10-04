package com.logistics.tracking.controller;

import com.logistics.tracking.dto.TelemetryMessage;
import com.logistics.tracking.producer.TelemetryProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Telemetry", description = "Telemetry ingestion and dispatch endpoints.")
public class TrackingController {

    private final TelemetryProducer producer;

    public TrackingController(TelemetryProducer producer) {
        this.producer = producer;
    }

    @Operation(summary = "Accept telemetry", description = "Validates the payload and publishes it to the Artemis queue for downstream alerting.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Telemetry accepted and queued"),
            @ApiResponse(responseCode = "400", description = "Telemetry payload validation failed")
    })
    @PostMapping("/telemetry")
    public ResponseEntity<Void> receiveTelemetry(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Telemetry payload reported by a truck",
            required = true,
            content = @Content(schema = @Schema(implementation = TelemetryMessage.class)))
            @RequestBody TelemetryMessage telemetryMessage) {
        if (telemetryMessage == null) {
            return ResponseEntity.badRequest().build();
        }
        if (telemetryMessage.truckId() == null || telemetryMessage.truckId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (telemetryMessage.trackingNumber() == null || telemetryMessage.trackingNumber().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (telemetryMessage.latitude() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (telemetryMessage.longitude() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (telemetryMessage.speedKmh() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (telemetryMessage.recordedAt() == null) {
            return ResponseEntity.badRequest().build();
        }

        // The project keeps tracking and shipment services decoupled and trusts the payload.
        producer.sendTelemetry(telemetryMessage);

        return ResponseEntity.accepted().build();
    }
}