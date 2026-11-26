package org.example.service;

import org.example.model.Cart;
import org.example.model.CartItem;
import org.example.model.Money;
import org.example.model.Product;

import java.util.List;

public class CartService {

    private final DataStore store;
    private final AccessPolicy access;

    public CartService(DataStore store) {
        this.store = store;
        this.access = new AccessPolicy(store);
    }

    public CartSummary addToCart(String customerId, String productId, int quantity) {
        Cart cart = customerCart(customerId);
        cart.add(store.findProduct(productId), quantity);
        return summarize(cart);
    }

    public CartSummary changeQuantity(String customerId, String productId, int newQuantity) {
        Cart cart = customerCart(customerId);
        cart.changeQuantity(store.findProduct(productId), newQuantity);
        return summarize(cart);
    }

    public CartSummary removeItem(String customerId, String productId) {
        Cart cart = customerCart(customerId);
        cart.remove(productId);
        return summarize(cart);
    }

    public CartSummary clearCart(String customerId) {
        Cart cart = customerCart(customerId);
        cart.clear();
        return summarize(cart);
    }

    public CartSummary viewCart(String customerId) {
        return summarize(customerCart(customerId));
    }

    private Cart customerCart(String customerId) {
        access.requireCustomer(customerId);
        return store.cartOf(customerId);
    }

    private CartSummary summarize(Cart cart) {
        List<CartLine> lines = cart.getItems().stream()
                .map(this::toLine)
                .toList();
        Money total = lines.stream().map(CartLine::subtotal).reduce(Money.ZERO, Money::plus);
        return new CartSummary(lines, total);
    }

    private CartLine toLine(CartItem item) {
        Product product = store.findProduct(item.getProductId());
        Money subtotal = product.getPrice().times(item.getQuantity());
        return new CartLine(product.getId(), product.getName(), product.getPrice(), item.getQuantity(), subtotal);
    }
}
