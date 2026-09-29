package com.logistics.shipment.controller;

public record CreateShipmentRequest(
        String trackingNumber,
        String origin,
        String destination,
        Double weight
) {}
