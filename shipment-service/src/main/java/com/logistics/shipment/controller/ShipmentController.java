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
        Shipment shipment = new Shipment(
                request.trackingNumber(),
                request.origin(),
                request.destination(),
                request.weight()
        );
        Shipment savedShipment = repository.save(shipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShipment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}