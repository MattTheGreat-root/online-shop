package org.example.service;

import org.example.model.Address;
import org.example.model.Cart;
import org.example.model.CartItem;
import org.example.model.DomainException;
import org.example.model.Order;
import org.example.model.OrderItem;
import org.example.model.OrderStatus;
import org.example.model.Payment;
import org.example.model.Product;
import org.example.model.User;

import java.util.List;

public class OrderService {

    private final DataStore store;
    private final AccessPolicy access;

    public OrderService(DataStore store) {
        this.store = store;
        this.access = new AccessPolicy(store);
    }

    public Order checkout(String customerId, String addressId) {
        User customer = access.requireCustomer(customerId);
        Cart cart = store.cartOf(customerId);
        if (cart.isEmpty()) {
            throw new DomainException("Your cart is empty");
        }
        Address address = customer.findAddress(addressId);
        List<OrderItem> items = cart.getItems().stream()
                .map(this::toOrderItem)
                .toList();
        Order order = new Order(store.nextId("O"), customerId, items, address.getDetails());
        store.orders().put(order.getId(), order);
        cart.clear();
        return order;
    }

    public List<Order> listCustomerOrders(String customerId) {
        access.requireCustomer(customerId);
        return store.orders().values().stream()
                .filter(order -> order.isOwnedBy(customerId))
                .toList();
    }

    public Order getCustomerOrder(String customerId, String orderId) {
        access.requireCustomer(customerId);
        Order order = store.findOrder(orderId);
        if (!order.isOwnedBy(customerId)) {
            throw new AccessDeniedException("You can only see your own orders");
        }
        return order;
    }

    public List<Order> listAllOrders(String adminId, OrderStatus statusFilter) {
        access.requireAdmin(adminId);
        return store.orders().values().stream()
                .filter(order -> statusFilter == null || order.getStatus() == statusFilter)
                .toList();
    }

    public Order getOrderForAdmin(String adminId, String orderId) {
        access.requireAdmin(adminId);
        return store.findOrder(orderId);
    }

    public List<Payment> listPayments(String orderId) {
        return store.payments().values().stream()
                .filter(payment -> payment.getOrderId().equals(orderId))
                .toList();
    }

    private OrderItem toOrderItem(CartItem item) {
        Product product = store.findProduct(item.getProductId());
        if (!product.canBePurchased(item.getQuantity())) {
            throw new DomainException("Not enough stock for " + product.getName()
                    + ". Please update your cart.");
        }
        return new OrderItem(product.getId(), product.getName(), product.getPrice(), item.getQuantity());
    }
}
