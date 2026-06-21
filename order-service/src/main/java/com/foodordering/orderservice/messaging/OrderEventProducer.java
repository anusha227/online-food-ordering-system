package com.foodordering.orderservice.messaging;

import com.foodordering.orderservice.config.JmsConfig;
import com.foodordering.orderservice.dto.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);

    private final JmsTemplate jmsTemplate;

    public OrderEventProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendOrderCreatedEvent(OrderEvent event) {
        log.info("Publishing order event to ActiveMQ (Queue: {}): {}", JmsConfig.ORDER_CREATED_QUEUE, event);
        jmsTemplate.convertAndSend(JmsConfig.ORDER_CREATED_QUEUE, event);
    }
}
