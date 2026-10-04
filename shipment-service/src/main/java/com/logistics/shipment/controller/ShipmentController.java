package com.logistics.shipment.controller;

import com.logistics.shipment.domain.Shipment;
import com.logistics.shipment.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipments")
@Tag(name = "Shipments", description = "Create and fetch shipment records.")
public class ShipmentController {

    private final ShipmentService service;

    public ShipmentController(ShipmentService service) {
        this.service = service;
    }

    @Operation(summary = "Create a shipment", description = "Creates a shipment and returns the stored entity with a generated id and default PENDING status.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Shipment created successfully", content = @Content(schema = @Schema(implementation = Shipment.class))),
            @ApiResponse(responseCode = "400", description = "Request validation failed"),
            @ApiResponse(responseCode = "409", description = "A shipment with the same tracking number already exists")
    })
    @PostMapping
    public ResponseEntity<Shipment> createShipment(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Shipment details to persist",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateShipmentRequest.class)))
            @RequestBody CreateShipmentRequest request) {
        Shipment shipment = new Shipment(
                request.trackingNumber(),
                request.origin(),
                request.destination(),
                request.weight()
        );
        Shipment savedShipment = service.createShipment(shipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShipment);
    }

    @Operation(summary = "Fetch shipment by id", description = "Returns a shipment when a valid identifier is supplied.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shipment found", content = @Content(schema = @Schema(implementation = Shipment.class))),
            @ApiResponse(responseCode = "404", description = "Shipment not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@Parameter(description = "Shipment database id", example = "1") @PathVariable Long id) {
        return service.getShipmentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}