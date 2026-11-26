package org.example.service;

public record ShopServices(AuthService auth,
                           UserService users,
                           CategoryService categories,
                           ProductService products,
                           ReviewService reviews,
                           CartService carts,
                           OrderService orders,
                           PaymentService payments,
                           SimulatedPaymentGateway gateway) {

    public static ShopServices inMemory() {
        DataStore store = new DataStore();
        SimulatedPaymentGateway gateway = new SimulatedPaymentGateway();
        ReviewService reviews = new ReviewService(store);
        return new ShopServices(
                new AuthService(store),
                new UserService(store),
                new CategoryService(store),
                new ProductService(store, reviews),
                reviews,
                new CartService(store),
                new OrderService(store),
                new PaymentService(store, gateway),
                gateway);
    }
}
