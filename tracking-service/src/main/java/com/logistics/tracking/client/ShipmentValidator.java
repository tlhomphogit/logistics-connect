package com.logistics.tracking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class ShipmentValidator {

    private final RestClient restClient;

    public ShipmentValidator(@Value("${shipment.service.url}") String shipmentServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(shipmentServiceUrl)
                .build();
    }

    /**
     * Synchronously validates a tracking number against the shipment-service.
     * 
     * @param trackingNumber the ID from the telemetry payload
     * @return true if the shipment exists, false if it is invalid or missing
     */
    public boolean isValid(String trackingNumber) {
        try {
            restClient.get()
                    .uri("/api/shipments/{trackingNumber}", trackingNumber)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientResponseException e) {
            // A 404 Not Found or 400 Bad Request means the tracking number is invalid
            return false;
        } catch (Exception e) {
            // In a production scenario, we might want to implement a retry mechanism or Circuit Breaker here
            // in case the shipment-service is temporarily down. For now, we reject.
            return false;
        }
    }
}