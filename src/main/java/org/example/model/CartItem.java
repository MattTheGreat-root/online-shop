package org.example.model;

public class CartItem {

    private final String productId;
    private int quantity;

    CartItem(String productId, int quantity) {
        this.productId = productId;
        setQuantity(quantity);
    }

    void setQuantity(int quantity) {
        if (quantity < 1) {
            throw new DomainException("Quantity must be at least 1");
        }
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
