package org.example.service;

import org.example.model.DomainException;
import org.example.model.Order;
import org.example.model.OrderItem;
import org.example.model.Payment;
import org.example.model.PaymentStatus;
import org.example.model.Product;

public class PaymentService {

    private final ShopStorage.Payments store;
    private final AccessPolicy access;
    private final PaymentGateway gateway;

    public PaymentService(ShopStorage.Payments store, PaymentGateway gateway) {
        this.store = store;
        this.access = new AccessPolicy(store);
        this.gateway = gateway;
    }

    public Payment payOrder(String customerId, String orderId) {
        access.requireCustomer(customerId);
        Order order = store.findOrder(orderId);
        if (!order.isOwnedBy(customerId)) {
            throw new AccessDeniedException("You can only pay for your own orders");
        }
        if (!order.isPayable()) {
            throw new DomainException("Order " + orderId + " is already paid");
        }
        requireStockFor(order);

        GatewayResult result = gateway.charge(orderId, order.getTotalAmount());
        PaymentStatus status = decideStatus(order, result);
        Payment payment = new Payment(store.nextId("PAY"), orderId, result.paidAmount(), status, result.reference());
        store.addPayment(payment);

        if (payment.isSuccessful()) {
            order.markAsPaid();
            order.getItems().forEach(this::recordSale);
        }
        return payment;
    }

    private static PaymentStatus decideStatus(Order order, GatewayResult result) {
        if (!result.successful()) {
            return PaymentStatus.FAILED;
        }
        if (!result.paidAmount().equals(order.getTotalAmount())) {
            return PaymentStatus.REJECTED;
        }
        return PaymentStatus.SUCCESSFUL;
    }

    private void requireStockFor(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = store.findProduct(item.productId());
            if (!product.canBePurchased(item.quantity())) {
                throw new DomainException("Not enough stock for " + product.getName() + " any more");
            }
        }
    }

    private void recordSale(OrderItem item) {
        store.findProduct(item.productId()).recordSale(item.quantity());
    }
}
