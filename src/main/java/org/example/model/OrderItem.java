package org.example.model;

public record OrderItem(String productId, String productName, Money unitPrice, int quantity) {

    public OrderItem {
        if (quantity < 1) {
            throw new DomainException("Order item quantity must be at least 1");
        }
    }

    public Money subtotal() {
        return unitPrice.times(quantity);
    }
}
