package com.logistics.shipment.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_number", nullable = false, unique = true)
    private String trackingNumber;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "status", nullable = false)
    private String status;

    // Default constructor strictly required by JPA/Hibernate
    protected Shipment() {}

    // Business constructor enforcing domain rules
    public Shipment(String trackingNumber, String origin, String destination, Double weight) {
        if (origin == null || origin.trim().isEmpty()) {
            throw new IllegalArgumentException("Origin cannot be null or blank.");
        }
        if (weight == null || weight <= 0) {
            throw new IllegalArgumentException("Shipment weight must be greater than zero.");
        }
        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Tracking number cannot be null or blank.");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination cannot be null or blank.");
        }

        this.trackingNumber = trackingNumber;
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
        this.status = "PENDING";
    }

    // Getters required for JPA and JSON serialization
    public Long getId() { return id; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public Double getWeight() { return weight; }
    public String getStatus() { return status; }
}