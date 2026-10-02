package com.logistics.tracking.producer;

import com.logistics.tracking.dto.TelemetryMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class TelemetryProducer {

    private static final Logger log = LoggerFactory.getLogger(TelemetryProducer.class);
    private static final String TELEMETRY_QUEUE = "telemetry.queue";

    private final JmsTemplate jmsTemplate;

    public TelemetryProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendTelemetry(TelemetryMessage message) {
        log.info("Publishing telemetry for truck {} to queue {}", message.truckId(), TELEMETRY_QUEUE);
        jmsTemplate.convertAndSend(TELEMETRY_QUEUE, message);
    }
}