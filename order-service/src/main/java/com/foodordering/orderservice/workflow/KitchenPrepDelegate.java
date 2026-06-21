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

@Component("kitchenPrepDelegate")
public class KitchenPrepDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(KitchenPrepDelegate.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final OrderRepository orderRepository;

    public KitchenPrepDelegate(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        String item = (String) execution.getVariable("item");

        log.info("Camunda Workflow [KitchenPrepDelegate]: Sending ticket to Kitchen Service for Order ID: {}, Item: {}", orderId, item);

        String kitchenUrl = "http://localhost:8083/api/kitchen/tickets";
        Map<String, Object> request = new HashMap<>();
        request.put("orderId", orderId);
        request.put("item", item);

        try {
            restTemplate.postForEntity(kitchenUrl, new HttpEntity<>(request), Map.class);
            log.info("Camunda Workflow [KitchenPrepDelegate]: Kitchen ticket initialized successfully for Order ID: {}", orderId);
            orderRepository.findById(orderId).ifPresent(order -> {
                order.setStatus(OrderStatus.PREPARING);
                orderRepository.save(order);
            });
        } catch (Exception e) {
            log.error("Camunda Workflow [KitchenPrepDelegate]: REST call to Kitchen Service failed for Order ID: {}. Error: {}", orderId, e.getMessage());
            throw e; // Fail workflow task so it can be retried/handled in Camunda engine
        }
    }
}
