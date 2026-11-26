package org.example.service;

@FunctionalInterface
public interface ProductRatings {
    double averageRating(String productId);
}
