package com.logistics.shipment.controller;

import com.logistics.shipment.domain.Shipment;
import com.logistics.shipment.repository.ShipmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final ShipmentRepository repository;

    public ShipmentController(ShipmentRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Shipment> createShipment(@RequestBody CreateShipmentRequest request) {
        // 1. Construct the domain model (which enforces your guard clauses)
        Shipment shipment = new Shipment(
                request.trackingNumber(),
                request.origin(),
                request.destination(),
                request.weight()
        );

        // 2. Persist the entity to PostgreSQL
        Shipment savedShipment = repository.save(shipment);

        // 3. Return the saved entity with a 201 Created status
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShipment);
    }
}