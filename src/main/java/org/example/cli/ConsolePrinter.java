package org.example.cli;

import org.example.model.Address;
import org.example.model.Category;
import org.example.model.Order;
import org.example.model.OrderItem;
import org.example.model.Payment;
import org.example.model.Product;
import org.example.model.Review;
import org.example.model.StockChange;
import org.example.model.User;
import org.example.service.CartLine;
import org.example.service.CartSummary;

import java.util.List;

public class ConsolePrinter {

    private final ConsoleIO io;

    public ConsolePrinter(ConsoleIO io) {
        this.io = io;
    }

    public void products(List<Product> products) {
        if (products.isEmpty()) {
            io.println("No product was found.");
            return;
        }
        products.forEach(product -> io.println(String.format("%-5s %-20s %-14s %18s  stock: %d%s",
                product.getId(), product.getName(), product.getBrand(), product.getPrice(),
                product.getStock(), product.isActive() ? "" : "  (inactive)")));
    }

    public void productDetails(Product product, double averageRating, List<Review> reviews) {
        io.println(product.getName() + " (" + product.getId() + ")");
        io.println("Brand: " + product.getBrand() + " | Category: " + product.getCategory().getName());
        io.println("Description: " + product.getDescription());
        io.println("Price: " + product.getPrice() + " | Stock: " + product.getStock());
        if (!product.getImageUrls().isEmpty()) {
            io.println("Images: " + String.join(", ", product.getImageUrls()));
        }
        io.println(String.format("Rating: %.1f / 5 (%d reviews)", averageRating, reviews.size()));
        reviews.forEach(review -> io.println("  - [" + review.getRating().value() + "/5] " + review.getText()));
    }

    public void categories(List<Category> categories) {
        categories.forEach(category -> io.println(category.getId() + "  " + category.getName()));
    }

    public void cart(CartSummary cart) {
        if (cart.isEmpty()) {
            io.println("Your cart is empty.");
            return;
        }
        for (CartLine line : cart.lines()) {
            io.println(String.format("%-5s %-20s %18s x %d = %s", line.productId(), line.productName(),
                    line.unitPrice(), line.quantity(), line.subtotal()));
        }
        io.println("Total: " + cart.total());
    }

    public void orders(List<Order> orders) {
        if (orders.isEmpty()) {
            io.println("No order was found.");
            return;
        }
        orders.forEach(order -> io.println(String.format("%-5s %-16s %18s  %s", order.getId(),
                order.getStatus(), order.getTotalAmount(), order.getCreatedAt().toLocalDate())));
    }

    public void orderDetails(Order order, List<Payment> payments) {
        io.println("Order " + order.getId() + " | Status: " + order.getStatus());
        for (OrderItem item : order.getItems()) {
            io.println(String.format("  %-20s %18s x %d", item.productName(), item.unitPrice(), item.quantity()));
        }
        io.println("Total: " + order.getTotalAmount());
        io.println("Ship to: " + order.getShippingAddress());
        payments.forEach(payment -> io.println("  Payment " + payment.getId() + ": " + payment.getStatus()
                + " (" + payment.getAmount() + ", ref " + payment.getGatewayReference() + ")"));
    }

    public void profile(User user) {
        io.println("Name: " + user.getFullName());
        io.println("Email: " + (user.getEmail() == null ? "-" : user.getEmail()));
        io.println("Mobile: " + (user.getPhone() == null ? "-" : user.getPhone()));
    }

    public void addresses(List<Address> addresses) {
        if (addresses.isEmpty()) {
            io.println("You have no saved address.");
            return;
        }
        addresses.forEach(address -> io.println(address.getId() + "  " + address.getTitle()
                + (address.isDefault() ? " (default)" : "") + ": " + address.getDetails()));
    }

    public void stockHistory(List<StockChange> changes) {
        if (changes.isEmpty()) {
            io.println("No stock change has been recorded.");
            return;
        }
        changes.forEach(change -> io.println(String.format("%s  %d -> %d  by %s at %s", change.id(),
                change.oldStock(), change.newStock(), change.adminId(), change.changedAt().withNano(0))));
    }
}
