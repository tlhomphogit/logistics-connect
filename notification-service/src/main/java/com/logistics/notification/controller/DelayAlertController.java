package com.logistics.notification.controller;

import com.logistics.notification.entity.DelayAlert;
import com.logistics.notification.repository.DelayAlertRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
public class DelayAlertController {

    private final DelayAlertRepository alertRepository;

    public DelayAlertController(DelayAlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @GetMapping
    public List<DelayAlert> getAlerts() {
        return alertRepository.findAll();
    }
}