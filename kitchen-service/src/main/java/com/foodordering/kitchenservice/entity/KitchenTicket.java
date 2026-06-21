package com.foodordering.kitchenservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "kitchen_tickets")
public class KitchenTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "item", nullable = false)
    private String item;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private TicketStatus status;

    // Constructors
    public KitchenTicket() {
    }

    public KitchenTicket(Long orderId, String item, TicketStatus status) {
        this.orderId = orderId;
        this.item = item;
        this.status = status;
    }

    public KitchenTicket(Long id, Long orderId, String item, TicketStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.item = item;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    // toString
    @Override
    public String toString() {
        return "KitchenTicket{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", item='" + item + '\'' +
                ", status=" + status +
                '}';
    }
}
