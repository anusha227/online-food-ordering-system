package com.foodordering.orderservice.service;

import com.foodordering.orderservice.dto.OrderEvent;
import com.foodordering.orderservice.entity.Order;
import com.foodordering.orderservice.entity.OrderStatus;
import com.foodordering.orderservice.messaging.OrderEventProducer;
import com.foodordering.orderservice.repository.OrderRepository;
import org.camunda.bpm.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final RuntimeService runtimeService;

    public OrderService(OrderRepository orderRepository, OrderEventProducer orderEventProducer, RuntimeService runtimeService) {
        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
        this.runtimeService = runtimeService;
    }

    @Transactional
    public Order placeOrder(Order order) {
        order.setStatus(OrderStatus.PLACED);
        Order savedOrder = orderRepository.save(order);

        // Required Console Log Format
        System.out.println("[OrderService] Order #" + savedOrder.getId() + " Workflow started");

        // Map and publish order created event
        OrderEvent event = new OrderEvent(
                savedOrder.getId(),
                savedOrder.getCustomerName(),
                savedOrder.getItem(),
                savedOrder.getAmount(),
                savedOrder.getStatus().name()
        );
        orderEventProducer.sendOrderCreatedEvent(event);

        // Start Camunda workflow process instance
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", savedOrder.getId());
        variables.put("customerName", savedOrder.getCustomerName());
        variables.put("item", savedOrder.getItem());
        variables.put("amount", savedOrder.getAmount());

        runtimeService.startProcessInstanceByKey("orderProcess", variables);
        log.info("Camunda process instance started with key: orderProcess for Order ID: {}", savedOrder.getId());

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            // Required Console Log Format
            System.out.println("[OrderService] Fetching Order #" + order.getId() + " status: " + order.getStatus().name());
        } else {
            log.warn("Order not found with ID: {}", id);
        }
        return orderOptional;
    }
}
