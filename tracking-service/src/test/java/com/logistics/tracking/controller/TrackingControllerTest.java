package com.logistics.tracking.controller;

import com.logistics.tracking.dto.TelemetryMessage;
import com.logistics.tracking.producer.TelemetryProducer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class TrackingControllerTest {

    @Autowired
    private RestTestClient restClient;

    @MockitoBean
    private TelemetryProducer producer;

    @Test
    void shouldReturn202WhenValidTelemetryReceived() {
        TelemetryMessage message = new TelemetryMessage(
                "TRK-100",
                "SHP-9999",
                -26.1887,
                28.3207,
                85.5,
                Instant.now()
        );

        restClient.post()
                .uri("/api/telemetry")
                .contentType(MediaType.APPLICATION_JSON)
                .body(message)
                .exchange()
                .expectStatus().isAccepted();

        // Verify the producer was triggered
        Mockito.verify(producer).sendTelemetry(any(TelemetryMessage.class));
    }

    @Test
    void shouldReturn400WhenTelemetryIsInvalid() {
        TelemetryMessage invalidMessage = new TelemetryMessage(
                null,
                null,
                -26.1887,
                28.3207,
                85.5,
                Instant.now()
        );

        restClient.post()
                .uri("/api/telemetry")
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalidMessage)
                .exchange()
                .expectStatus().isBadRequest();
    }
}