package com.logistics.notification.repository;

import com.logistics.notification.entity.DelayAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DelayAlertRepository extends JpaRepository<DelayAlert, Long> {
}