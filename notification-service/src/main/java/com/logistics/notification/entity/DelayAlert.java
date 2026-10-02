package com.logistics.notification.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "delay_alerts")
public class DelayAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String truckId;
    private String trackingNumber;
    private String reason;
    private Instant alertTime;

    public DelayAlert() {}

    public DelayAlert(String truckId, String trackingNumber, String reason, Instant alertTime) {
        this.truckId = truckId;
        this.trackingNumber = trackingNumber;
        this.reason = reason;
        this.alertTime = alertTime;
    }

    // Standard Getters
    public Long getId() { return id; }
    public String getTruckId() { return truckId; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getReason() { return reason; }
    public Instant getAlertTime() { return alertTime; }
}