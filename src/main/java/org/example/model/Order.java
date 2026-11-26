package org.example.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private final String id;
    private final String customerId;
    private final List<OrderItem> items;
    private final AddressDetails shippingAddress;
    private final Money totalAmount;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public Order(String id, String customerId, List<OrderItem> items, AddressDetails shippingAddress) {
        if (items == null || items.isEmpty()) {
            throw new DomainException("Order must have at least one item");
        }
        if (shippingAddress == null) {
            throw new DomainException("Shipping address is required");
        }
        this.id = id;
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.shippingAddress = shippingAddress;
        this.totalAmount = calculateTotal(items);
        this.status = OrderStatus.PENDING_PAYMENT;
        this.createdAt = LocalDateTime.now();
    }

    private static Money calculateTotal(List<OrderItem> items) {
        return items.stream().map(OrderItem::subtotal).reduce(Money.ZERO, Money::plus);
    }

    public boolean isPayable() {
        return status == OrderStatus.PENDING_PAYMENT;
    }

    public void markAsPaid() {
        if (!isPayable()) {
            throw new DomainException("Order " + id + " is already paid");
        }
        status = OrderStatus.PAID;
        paidAt = LocalDateTime.now();
    }

    public boolean isOwnedBy(String userId) {
        return customerId.equals(userId);
    }

    public boolean containsProduct(String productId) {
        return items.stream().anyMatch(item -> item.productId().equals(productId));
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public AddressDetails getShippingAddress() {
        return shippingAddress;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }
}
