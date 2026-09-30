package com.logistics.shipment.service;

import com.logistics.shipment.domain.Shipment;
import com.logistics.shipment.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShipmentService {

    private final ShipmentRepository repository;

    public ShipmentService(ShipmentRepository repository) {
        this.repository = repository;
    }

    public Shipment createShipment(Shipment shipment) {
        // Pre-emptive duplicate check preventing raw database constraint errors
        if (repository.existsByTrackingNumber(shipment.getTrackingNumber())) {
            throw new DuplicateTrackingNumberException(shipment.getTrackingNumber());
        }
        return repository.save(shipment);
    }

    public Optional<Shipment> getShipmentById(Long id) {
        return repository.findById(id);
    }
}