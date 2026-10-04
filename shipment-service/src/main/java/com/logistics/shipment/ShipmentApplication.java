package com.logistics.shipment;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(
        title = "Shipment Service API",
        version = "v1",
        description = "Create and retrieve shipment records for the LogisticsConnect platform."
))
@SpringBootApplication
public class ShipmentApplication {

    public static void main(String[] args) {
        // This line bootstraps the entire microservice, loading configuration, JPA, and web servers
        SpringApplication.run(ShipmentApplication.class, args);
    }
}
