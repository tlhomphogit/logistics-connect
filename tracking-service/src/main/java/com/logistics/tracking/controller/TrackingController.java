package com.logistics.tracking.controller;

import com.logistics.tracking.dto.TelemetryMessage;
import com.logistics.tracking.producer.TelemetryProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TrackingController {

    private final TelemetryProducer producer;

    public TrackingController(TelemetryProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/telemetry")
    public ResponseEntity<Void> receiveTelemetry(@RequestBody TelemetryMessage telemetryMessage) {
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