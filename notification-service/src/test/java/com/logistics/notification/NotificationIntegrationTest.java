package com.logistics.notification;

import com.logistics.notification.dto.TelemetryMessage;
import com.logistics.notification.repository.DelayAlertRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.jms.core.JmsTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class NotificationIntegrationTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    static GenericContainer<?> artemis = new GenericContainer<>("apache/activemq-artemis:latest-alpine")
            .withExposedPorts(61616)
            .withEnv("ARTEMIS_USER", "artemis")
            .withEnv("ARTEMIS_PASSWORD", "artemis")
            .withStartupTimeout(Duration.ofMinutes(3));

    @DynamicPropertySource
    static void configureArtemis(DynamicPropertyRegistry registry) {
        postgres.start();
        artemis.start();

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.artemis.broker-url", () ->
                "tcp://" + artemis.getHost() + ":" + artemis.getMappedPort(61616));
        registry.add("spring.artemis.user", () -> "artemis");
        registry.add("spring.artemis.password", () -> "artemis");
    }

    @AfterAll
    static void stopContainers() {
        artemis.stop();
        postgres.stop();
    }

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private DelayAlertRepository alertRepository;

    @Test
    void shouldConsumeMessageAndSaveDelayAlert() {
        TelemetryMessage delayedMessage = new TelemetryMessage(
                "TRK-999", "SHP-1234", -26.1, 28.3, 0.0, 
                Instant.now().minus(20, ChronoUnit.MINUTES)
        );

        jmsTemplate.convertAndSend("telemetry.queue", delayedMessage);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> 
            assertThat(alertRepository.findAll()).hasSize(1)
        );

        assertThat(alertRepository.findAll().get(0).getTruckId()).isEqualTo("TRK-999");
    }
}