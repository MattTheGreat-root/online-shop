package org.example.service;

import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.model.Product;
import org.example.model.Rating;
import org.example.model.Review;

import java.util.List;

public class ReviewService {

    private final DataStore store;
    private final AccessPolicy access;

    public ReviewService(DataStore store) {
        this.store = store;
        this.access = new AccessPolicy(store);
    }

    public Review addReview(String customerId, String productId, String text, int ratingValue) {
        access.requireCustomer(customerId);
        Product product = store.findProduct(productId);
        if (!hasBought(customerId, product.getId())) {
            throw new AccessDeniedException("Only customers who bought this product can review it");
        }
        Review review = new Review(store.nextId("R"), productId, customerId, text, new Rating(ratingValue));
        store.reviews().put(review.getId(), review);
        return review;
    }

    public List<Review> listReviews(String productId) {
        store.findProduct(productId);
        return store.reviews().values().stream()
                .filter(review -> review.getProductId().equals(productId))
                .toList();
    }

    public double averageRating(String productId) {
        return store.reviews().values().stream()
                .filter(review -> review.getProductId().equals(productId))
                .mapToInt(review -> review.getRating().value())
                .average()
                .orElse(0);
    }

    private boolean hasBought(String customerId, String productId) {
        return store.orders().values().stream()
                .filter(order -> order.isOwnedBy(customerId))
                .filter(order -> order.getStatus() == OrderStatus.PAID)
                .anyMatch(order -> order.containsProduct(productId));
    }
}
