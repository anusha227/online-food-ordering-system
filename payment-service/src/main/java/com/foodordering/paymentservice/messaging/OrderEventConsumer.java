package com.foodordering.paymentservice.messaging;

import com.foodordering.paymentservice.config.JmsConfig;
import com.foodordering.paymentservice.dto.OrderEvent;
import com.foodordering.paymentservice.entity.Payment;
import com.foodordering.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final PaymentService paymentService;

    public OrderEventConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @JmsListener(destination = JmsConfig.ORDER_CREATED_QUEUE)
    public void consumeOrderCreatedEvent(OrderEvent event) {
        // Required Audit Log Format
        System.out.println("[PaymentService] Order #" + event.getOrderId() + " Received Order Created Event");

        // Delegate to Payment Service to process payment
        Payment payment = paymentService.authorizePayment(event.getOrderId(), event.getAmount());
        log.info("JMS Event Processing complete for Order ID: {}. Resulting Payment Status: {}", event.getOrderId(), payment.getStatus());
    }
}
