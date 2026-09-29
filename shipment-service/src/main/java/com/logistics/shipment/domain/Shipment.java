package com.logistics.shipment.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String trackingNumber;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private double weight;

    // A protected no-args constructor is strictly required by JPA for database mapping
    protected Shipment() {}

    // The primary constructor containing our strict business rules (Guard Clauses)
    public Shipment(String trackingNumber, String origin, String destination, double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Shipment weight must be greater than zero.");
        }
        if (origin == null || origin.trim().isEmpty()) {
            throw new IllegalArgumentException("Origin cannot be null or blank.");
        }

        this.trackingNumber = trackingNumber;
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
    }

    // Getters allowing the application to read the shipment data
    public Long getId() { return id; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public double getWeight() { return weight; }
}