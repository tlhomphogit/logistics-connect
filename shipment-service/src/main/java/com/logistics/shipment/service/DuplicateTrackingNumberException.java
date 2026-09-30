package com.logistics.shipment.service;

public class DuplicateTrackingNumberException extends RuntimeException {
    public DuplicateTrackingNumberException(String trackingNumber) {
        super("Shipment with tracking number '" + trackingNumber + "' already exists.");
    }
}
