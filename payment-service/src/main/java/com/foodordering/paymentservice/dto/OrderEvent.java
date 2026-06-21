package com.foodordering.paymentservice.dto;

import java.io.Serializable;

public class OrderEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;
    private String customerName;
    private String item;
    private Double amount;
    private String status;

    // Constructors
    public OrderEvent() {
    }

    public OrderEvent(Long orderId, String customerName, String item, Double amount, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.item = item;
        this.amount = amount;
        this.status = status;
    }

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderEvent{" +
                "orderId=" + orderId +
                ", customerName='" + customerName + '\'' +
                ", item='" + item + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}
