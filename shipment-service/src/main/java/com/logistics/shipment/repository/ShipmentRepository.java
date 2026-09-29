package com.logistics.shipment.repository;

import com.logistics.shipment.domain.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    // By extending JpaRepository, Spring automatically implements standard CRUD operations:
    // save(), findById(), findAll(), deleteById(), etc.

    // We can add custom queries here later if needed (e.g., findByTrackingNumber(String trackingNumber))
}