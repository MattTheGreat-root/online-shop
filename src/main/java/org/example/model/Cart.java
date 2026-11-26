package org.example.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Cart {

    private final String customerId;
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public Cart(String customerId) {
        this.customerId = customerId;
    }

    public void add(Product product, int quantity) {
        CartItem existing = items.get(product.getId());
        int finalQuantity = existing == null ? quantity : existing.getQuantity() + quantity;
        requireAvailable(product, finalQuantity);
        if (existing == null) {
            items.put(product.getId(), new CartItem(product.getId(), finalQuantity));
        } else {
            existing.setQuantity(finalQuantity);
        }
    }

    public void changeQuantity(Product product, int newQuantity) {
        CartItem item = findItem(product.getId());
        requireAvailable(product, newQuantity);
        item.setQuantity(newQuantity);
    }

    public void remove(String productId) {
        findItem(productId);
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    private CartItem findItem(String productId) {
        CartItem item = items.get(productId);
        if (item == null) {
            throw new DomainException("This product is not in the cart: " + productId);
        }
        return item;
    }

    private static void requireAvailable(Product product, int quantity) {
        if (!product.canBePurchased(quantity)) {
            throw new DomainException("Requested quantity is not available for " + product.getName()
                    + " (stock: " + product.getStock() + ")");
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }
}
