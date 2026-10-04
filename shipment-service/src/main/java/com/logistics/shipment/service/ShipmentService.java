package com.logistics.shipment.service;

import com.logistics.shipment.domain.Shipment;
import com.logistics.shipment.dto.ShipmentResponse;
import com.logistics.shipment.repository.ShipmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<ShipmentResponse> getShipments(Pageable pageable) {
        // Method signature to satisfy the compiler; repository integration coming next
        return Page.empty(pageable);
    }

    public void updateStatus(String trackingNumber, String newStatus) {
        // Method signature to satisfy the compiler; transition logic coming next
    }
}