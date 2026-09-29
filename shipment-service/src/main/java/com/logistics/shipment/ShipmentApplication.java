package com.logistics.shipment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShipmentApplication {

    public static void main(String[] args) {
        // This line bootstraps the entire microservice, loading configuration, JPA, and web servers
        SpringApplication.run(ShipmentApplication.class, args);
    }
}