package com.foodordering.orderservice.workflow;

import com.foodordering.orderservice.entity.Order;
import com.foodordering.orderservice.entity.OrderStatus;
import com.foodordering.orderservice.repository.OrderRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("cancelOrderDelegate")
public class CancelOrderDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CancelOrderDelegate.class);

    private final OrderRepository orderRepository;

    public CancelOrderDelegate(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");

        log.info("Camunda Workflow [CancelOrderDelegate]: Executing order cancellation logic for Order ID: {}", orderId);

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            log.info("Camunda Workflow [CancelOrderDelegate]: Order ID: {} has been marked as CANCELLED in database.", orderId);
        } else {
            log.error("Camunda Workflow [CancelOrderDelegate]: Order ID: {} was not found in the database.", orderId);
        }
    }
}
