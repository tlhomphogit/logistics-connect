package com.logistics.shipment.controller;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateShipmentRequest(
        @Schema(description = "Unique tracking number for the shipment.", example = "TRK-2026-X")
        String trackingNumber,

        @Schema(description = "Origin city or hub.", example = "Benoni")
        String origin,

        @Schema(description = "Destination city or hub.", example = "Cape Town")
        String destination,

        @Schema(description = "Shipment weight in kilograms.", example = "450.5")
        Double weight
) {}
