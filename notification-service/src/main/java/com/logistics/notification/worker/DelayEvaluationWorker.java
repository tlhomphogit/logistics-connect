package com.logistics.notification.worker;

import com.logistics.notification.dto.TelemetryMessage;
import com.logistics.notification.entity.DelayAlert;
import com.logistics.notification.repository.DelayAlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class DelayEvaluationWorker {

    private static final Logger log = LoggerFactory.getLogger(DelayEvaluationWorker.class);
    private final DelayAlertRepository alertRepository;

    public DelayEvaluationWorker(DelayAlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public void evaluate(TelemetryMessage message) {
        Instant thresholdTime = Instant.now().minus(15, ChronoUnit.MINUTES);
        
        // If the payload's recorded time is older than 15 minutes, we flag a delay
        if (message.recordedAt().isBefore(thresholdTime)) {
            DelayAlert alert = new DelayAlert(
                    message.truckId(), 
                    message.trackingNumber(), 
                    "Transmission delayed > 15 minutes", 
                    Instant.now()
            );
            alertRepository.save(alert);
            log.warn("Delay alert saved to database for truck: {}", message.truckId());
        }
    }
}