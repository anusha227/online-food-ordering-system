package com.foodordering.orderservice.workflow;

import com.foodordering.orderservice.entity.OrderStatus;
import com.foodordering.orderservice.repository.OrderRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component("processPaymentDelegate")
public class ProcessPaymentDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ProcessPaymentDelegate.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final OrderRepository orderRepository;

    public ProcessPaymentDelegate(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        Double amount = (Double) execution.getVariable("amount");

        log.info("Camunda Workflow [ProcessPaymentDelegate]: Triggering payment process for Order ID: {}, Amount: {}", orderId, amount);

        String paymentUrl = "http://localhost:8082/api/payments";
        Map<String, Object> request = new HashMap<>();
        request.put("orderId", orderId);
        request.put("amount", amount);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(paymentUrl, new HttpEntity<>(request), Map.class);
            Map responseBody = responseEntity.getBody();

            if (responseBody != null && "COMPLETED".equals(responseBody.get("status"))) {
                log.info("Camunda Workflow [ProcessPaymentDelegate]: Payment SUCCESS for Order ID: {}", orderId);
                execution.setVariable("paymentSuccess", true);
                orderRepository.findById(orderId).ifPresent(order -> {
                    order.setStatus(OrderStatus.PAID);
                    orderRepository.save(order);
                });
            } else {
                log.warn("Camunda Workflow [ProcessPaymentDelegate]: Payment FAILED or rejected for Order ID: {}", orderId);
                execution.setVariable("paymentSuccess", false);
            }
        } catch (Exception e) {
            log.error("Camunda Workflow [ProcessPaymentDelegate]: REST call failed for Order ID: {}. Error: {}", orderId, e.getMessage());
            execution.setVariable("paymentSuccess", false);
        }
    }
}
