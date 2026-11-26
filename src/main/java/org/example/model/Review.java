package org.example.model;

import java.time.LocalDateTime;

public class Review {

    public static final int MAX_TEXT_LENGTH = 500;

    private final String id;
    private final String productId;
    private final String customerId;
    private final String text;
    private final Rating rating;
    private final LocalDateTime createdAt;

    public Review(String id, String productId, String customerId, String text, Rating rating) {
        if (text == null || text.isBlank()) {
            throw new DomainException("Review text cannot be empty");
        }
        if (text.length() > MAX_TEXT_LENGTH) {
            throw new DomainException("Review text cannot be longer than " + MAX_TEXT_LENGTH + " characters");
        }
        if (rating == null) {
            throw new DomainException("Rating is required");
        }
        this.id = id;
        this.productId = productId;
        this.customerId = customerId;
        this.text = text.trim();
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getText() {
        return text;
    }

    public Rating getRating() {
        return rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
