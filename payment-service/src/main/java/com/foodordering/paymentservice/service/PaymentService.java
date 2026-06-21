package com.foodordering.paymentservice.service;

import com.foodordering.paymentservice.entity.Payment;
import com.foodordering.paymentservice.entity.PaymentStatus;
import com.foodordering.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment authorizePayment(Long orderId, Double amount) {
        // Simple business logic: transactions > 1000.0 fail
        PaymentStatus status = (amount > 1000.0) ? PaymentStatus.FAILED : PaymentStatus.COMPLETED;

        Payment payment = new Payment(orderId, amount, status, LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        // Required Console Log Format
        if (savedPayment.getStatus() == PaymentStatus.COMPLETED) {
            System.out.println("[PaymentService] Order #" + orderId + " Payment SUCCESS");
        } else {
            System.out.println("[PaymentService] Order #" + orderId + " Payment FAILED");
        }

        return savedPayment;
    }
}
