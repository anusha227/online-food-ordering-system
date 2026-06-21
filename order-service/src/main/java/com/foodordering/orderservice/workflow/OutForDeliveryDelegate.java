package com.foodordering.orderservice.workflow;

import com.foodordering.orderservice.entity.OrderStatus;
import com.foodordering.orderservice.repository.OrderRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component("outForDeliveryDelegate")
public class OutForDeliveryDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(OutForDeliveryDelegate.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final OrderRepository orderRepository;

    public OutForDeliveryDelegate(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");

        log.info("Camunda Workflow [OutForDeliveryDelegate]: Dispatching rider for Order ID: {}", orderId);

        String deliveryUrl = "http://localhost:8084/api/deliveries";
        Map<String, Object> request = new HashMap<>();
        request.put("orderId", orderId);

        try {
            restTemplate.postForEntity(deliveryUrl, new HttpEntity<>(request), Map.class);
            log.info("Camunda Workflow [OutForDeliveryDelegate]: Delivery successfully scheduled for Order ID: {}", orderId);
            orderRepository.findById(orderId).ifPresent(order -> {
                order.setStatus(OrderStatus.DELIVERED);
                orderRepository.save(order);
            });
        } catch (Exception e) {
            log.error("Camunda Workflow [OutForDeliveryDelegate]: REST call to Delivery Service failed for Order ID: {}. Error: {}", orderId, e.getMessage());
            throw e; // Fail workflow task so it can be retried/handled in Camunda engine
        }
    }
}
