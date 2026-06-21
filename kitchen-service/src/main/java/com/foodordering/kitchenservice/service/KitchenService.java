package com.foodordering.kitchenservice.service;

import com.foodordering.kitchenservice.entity.KitchenTicket;
import com.foodordering.kitchenservice.entity.TicketStatus;
import com.foodordering.kitchenservice.repository.KitchenTicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KitchenService {

    private final KitchenTicketRepository kitchenTicketRepository;

    public KitchenService(KitchenTicketRepository kitchenTicketRepository) {
        this.kitchenTicketRepository = kitchenTicketRepository;
    }

    @Transactional
    public KitchenTicket createTicket(Long orderId, String item) {
        KitchenTicket ticket = new KitchenTicket(orderId, item, TicketStatus.RECEIVED);
        KitchenTicket savedTicket = kitchenTicketRepository.save(ticket);

        // Required Console Log Format
        System.out.println("[KitchenService] Order #" + orderId + " Kitchen ticket RECEIVED");

        return savedTicket;
    }
}
