package com.foodordering.deliveryservice.service;

import com.foodordering.deliveryservice.entity.Delivery;
import com.foodordering.deliveryservice.entity.DeliveryStatus;
import com.foodordering.deliveryservice.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional
    public Delivery assignDriver(Long orderId) {
        // Simple random driver name allocation
        String assignedDriver = "Rider_" + (100 + (int)(Math.random() * 900));

        Delivery delivery = new Delivery(orderId, assignedDriver, DeliveryStatus.ASSIGNED);
        Delivery savedDelivery = deliveryRepository.save(delivery);

        // Required Console Log Format
        System.out.println("[DeliveryService] Order #" + orderId + " Driver " + assignedDriver + " ASSIGNED");

        return savedDelivery;
    }
}
