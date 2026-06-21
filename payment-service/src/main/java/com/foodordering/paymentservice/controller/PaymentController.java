package com.foodordering.paymentservice.controller;

import com.foodordering.paymentservice.entity.Payment;
import com.foodordering.paymentservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        Double amount = Double.valueOf(request.get("amount").toString());

        Payment payment = paymentService.authorizePayment(orderId, amount);

        Map<String, Object> response = new HashMap<>();
        response.put("paymentId", payment.getId());
        response.put("orderId", payment.getOrderId());
        response.put("amount", payment.getAmount());
        response.put("status", payment.getStatus().name());
        response.put("transactionDate", payment.getTransactionDate().toString());

        return ResponseEntity.ok(response);
    }
}
