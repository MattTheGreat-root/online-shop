package org.example.service;

import org.example.model.Product;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

final class ProductSearch {

    private final ProductRatings ratings;

    ProductSearch(ProductRatings ratings) {
        this.ratings = ratings;
    }

    List<Product> search(List<Product> products, ProductSearchCriteria criteria) {
        Stream<Product> result = products.stream()
                .filter(Product::isActive)
                .filter(product -> matchesQuery(product, criteria.getQuery()))
                .filter(product -> criteria.getCategoryId() == null
                        || product.getCategory().getId().equals(criteria.getCategoryId()))
                .filter(product -> criteria.getMinPrice() == null
                        || product.getPrice().amount() >= criteria.getMinPrice())
                .filter(product -> criteria.getMaxPrice() == null
                        || product.getPrice().amount() <= criteria.getMaxPrice())
                .filter(product -> criteria.getBrand() == null
                        || product.getBrand().equalsIgnoreCase(criteria.getBrand()))
                .filter(product -> criteria.getMinRating() == null
                        || ratings.averageRating(product.getId()) >= criteria.getMinRating())
                .filter(product -> !criteria.isOnlyInStock() || product.getStock() > 0);
        return result.sorted(comparatorFor(criteria.getSortOption())).toList();
    }

    private static boolean matchesQuery(Product product, String query) {
        if (query == null) {
            return true;
        }
        String text = query.toLowerCase();
        return product.getName().toLowerCase().contains(text)
                || product.getDescription().toLowerCase().contains(text)
                || product.getBrand().toLowerCase().contains(text);
    }

    private Comparator<Product> comparatorFor(SortOption option) {
        return switch (option) {
            case CHEAPEST -> Comparator.comparingLong(product -> product.getPrice().amount());
            case MOST_EXPENSIVE -> Comparator.comparingLong((Product product) -> product.getPrice().amount())
                    .reversed();
            case NEWEST -> Comparator.comparing(Product::getCreatedAt).reversed();
            case MOST_POPULAR -> Comparator.comparingInt(Product::getSoldCount).reversed();
            case HIGHEST_RATED -> Comparator.comparingDouble(
                    (Product product) -> ratings.averageRating(product.getId())).reversed();
        };
    }
}
