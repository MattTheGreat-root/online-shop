package org.example.model;

import java.time.LocalDateTime;

public class Payment {

    private final String id;
    private final String orderId;
    private final Money amount;
    private final PaymentStatus status;
    private final String gatewayReference;
    private final LocalDateTime createdAt;

    public Payment(String id, String orderId, Money amount, PaymentStatus status, String gatewayReference) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.gatewayReference = gatewayReference;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isSuccessful() {
        return status == PaymentStatus.SUCCESSFUL;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public Money getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getGatewayReference() {
        return gatewayReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
