package com.logistics.notification.consumer;

import com.logistics.notification.dto.TelemetryMessage;
import com.logistics.notification.worker.DelayEvaluationWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class TelemetryConsumer {

    private static final Logger log = LoggerFactory.getLogger(TelemetryConsumer.class);
    private final DelayEvaluationWorker worker;

    public TelemetryConsumer(DelayEvaluationWorker worker) {
        this.worker = worker;
    }

    @JmsListener(destination = "telemetry.queue")
    public void receiveTelemetry(TelemetryMessage message) {
        log.info("Asynchronously received telemetry -> Truck: {}", message.truckId());
        // Pass the payload to the background worker to evaluate delays
        worker.evaluate(message);
    }
}