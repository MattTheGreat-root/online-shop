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
import java.util.Optional;

/**
 * In-memory storage shared by all services. Nothing is persisted after the program exits.
 */
public class DataStore implements ShopStorage.Authentication, ShopStorage.Categories,
        ShopStorage.Catalog, ShopStorage.Orders, ShopStorage.Payments, ShopStorage.Reviews {

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

    @Override
    public String nextId(String prefix) {
        int next = idCounters.merge(prefix, 1, Integer::sum);
        return prefix + next;
    }

    @Override
    public User findUser(String userId) {
        return require(users.get(userId), "User not found: " + userId);
    }

    @Override
    public Category findCategory(String categoryId) {
        return require(categories.get(categoryId), "Category not found: " + categoryId);
    }

    @Override
    public Product findProduct(String productId) {
        return require(products.get(productId), "Product not found: " + productId);
    }

    @Override
    public Order findOrder(String orderId) {
        return require(orders.get(orderId), "Order not found: " + orderId);
    }

    @Override
    public Cart cartOf(String customerId) {
        return carts.computeIfAbsent(customerId, Cart::new);
    }

    private static <T> T require(T value, String message) {
        if (value == null) {
            throw new NotFoundException(message);
        }
        return value;
    }

    @Override
    public List<User> listUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void addSession(Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    @Override
    public void saveResetCode(String userId, String code) {
        passwordResetCodes.put(userId, code);
    }

    @Override
    public Optional<String> findResetCode(String userId) {
        return Optional.ofNullable(passwordResetCodes.get(userId));
    }

    @Override
    public void removeResetCode(String userId) {
        passwordResetCodes.remove(userId);
    }

    @Override
    public List<Category> listCategories() {
        return List.copyOf(categories.values());
    }

    @Override
    public void addCategory(Category category) {
        categories.put(category.getId(), category);
    }

    @Override
    public List<Product> listProducts() {
        return List.copyOf(products.values());
    }

    @Override
    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    @Override
    public List<Order> listOrders() {
        return List.copyOf(orders.values());
    }

    @Override
    public void addOrder(Order order) {
        orders.put(order.getId(), order);
    }

    @Override
    public List<Payment> listPayments() {
        return List.copyOf(payments.values());
    }

    @Override
    public void addPayment(Payment payment) {
        payments.put(payment.getId(), payment);
    }

    @Override
    public List<Review> listReviews() {
        return List.copyOf(reviews.values());
    }

    @Override
    public void addReview(Review review) {
        reviews.put(review.getId(), review);
    }

    @Override
    public List<StockChange> listStockChanges() {
        return List.copyOf(stockChanges);
    }

    @Override
    public void addStockChange(StockChange change) {
        stockChanges.add(change);
    }
}
