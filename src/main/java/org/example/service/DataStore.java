package org.example.service;

import org.example.model.Cart;
import org.example.model.Category;
import org.example.model.Order;
import org.example.model.Payment;
import org.example.model.Product;
import org.example.model.Review;
import org.example.model.Session;
import org.example.model.StockChange;
import org.example.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory storage shared by all services. Nothing is persisted after the program exits.
 */
public class DataStore {

    private final Map<String, User> users = new LinkedHashMap<>();
    private final Map<String, Session> sessions = new HashMap<>();
    private final Map<String, String> passwordResetCodes = new HashMap<>();
    private final Map<String, Category> categories = new LinkedHashMap<>();
    private final Map<String, Product> products = new LinkedHashMap<>();
    private final Map<String, Cart> carts = new HashMap<>();
    private final Map<String, Order> orders = new LinkedHashMap<>();
    private final Map<String, Payment> payments = new LinkedHashMap<>();
    private final Map<String, Review> reviews = new LinkedHashMap<>();
    private final List<StockChange> stockChanges = new ArrayList<>();
    private final Map<String, Integer> idCounters = new HashMap<>();

    String nextId(String prefix) {
        int next = idCounters.merge(prefix, 1, Integer::sum);
        return prefix + next;
    }

    User findUser(String userId) {
        return require(users.get(userId), "User not found: " + userId);
    }

    Category findCategory(String categoryId) {
        return require(categories.get(categoryId), "Category not found: " + categoryId);
    }

    Product findProduct(String productId) {
        return require(products.get(productId), "Product not found: " + productId);
    }

    Order findOrder(String orderId) {
        return require(orders.get(orderId), "Order not found: " + orderId);
    }

    Cart cartOf(String customerId) {
        return carts.computeIfAbsent(customerId, Cart::new);
    }

    private static <T> T require(T value, String message) {
        if (value == null) {
            throw new NotFoundException(message);
        }
        return value;
    }

    Map<String, User> users() {
        return users;
    }

    Map<String, Session> sessions() {
        return sessions;
    }

    Map<String, String> passwordResetCodes() {
        return passwordResetCodes;
    }

    Map<String, Category> categories() {
        return categories;
    }

    Map<String, Product> products() {
        return products;
    }

    Map<String, Order> orders() {
        return orders;
    }

    Map<String, Payment> payments() {
        return payments;
    }

    Map<String, Review> reviews() {
        return reviews;
    }

    List<StockChange> stockChanges() {
        return stockChanges;
    }
}
