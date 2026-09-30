package com.logistics.shipment.controller;

import com.logistics.shipment.service.DuplicateTrackingNumberException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Safely catches only our specific business duplicate exception
    @ExceptionHandler(DuplicateTrackingNumberException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateTrackingNumber(DuplicateTrackingNumberException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Conflict");
        errorResponse.put("message", "A record with this unique identifier (e.g., tracking number) already exists.");
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // 400 Bad Request Handler for Domain Guard Clauses (LC-001)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", ex.getMessage()); 
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}