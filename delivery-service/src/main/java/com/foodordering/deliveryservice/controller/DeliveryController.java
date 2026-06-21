package com.foodordering.deliveryservice.controller;

import com.foodordering.deliveryservice.entity.Delivery;
import com.foodordering.deliveryservice.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDelivery(@RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());

        Delivery delivery = deliveryService.assignDriver(orderId);

        Map<String, Object> response = new HashMap<>();
        response.put("deliveryId", delivery.getId());
        response.put("orderId", delivery.getOrderId());
        response.put("driverName", delivery.getDriverName());
        response.put("status", delivery.getStatus().name());

        return ResponseEntity.ok(response);
    }
}
