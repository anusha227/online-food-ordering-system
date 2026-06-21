package com.foodordering.kitchenservice.controller;

import com.foodordering.kitchenservice.entity.KitchenTicket;
import com.foodordering.kitchenservice.service.KitchenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/kitchen/tickets")
public class KitchenController {

    private final KitchenService kitchenService;

    public KitchenController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTicket(@RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        String item = request.get("item").toString();

        KitchenTicket ticket = kitchenService.createTicket(orderId, item);

        Map<String, Object> response = new HashMap<>();
        response.put("ticketId", ticket.getId());
        response.put("orderId", ticket.getOrderId());
        response.put("item", ticket.getItem());
        response.put("status", ticket.getStatus().name());

        return ResponseEntity.ok(response);
    }
}
