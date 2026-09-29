// package com.logistics.shipment.controller;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;

// import static org.assertj.core.api.Assertions.assertThat;

// // 1. Boot up a real embedded server on a random port
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//     class ShipmentControllerIT {

//     // 2. Inject the client used to make actual HTTP network calls
//     @Autowired
//     private TestRestTemplate restTemplate;

//     @Test
//     void getShipment_shouldReturn404_whenEndpointDoesNotExist() {
//         // 3. ACT: Attempt to fetch a shipment that doesn't exist yet
//         ResponseEntity<String> response = restTemplate.getForEntity("/api/shipments/123", String.class);

//         // 4. ASSERT: Verify the server returns a 404 Not Found status
//         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//     }
// }