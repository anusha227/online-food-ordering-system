package com.foodordering.kitchenservice.repository;

import com.foodordering.kitchenservice.entity.KitchenTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitchenTicketRepository extends JpaRepository<KitchenTicket, Long> {
}
