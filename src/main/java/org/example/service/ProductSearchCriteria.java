package org.example.service;

import org.example.model.DomainException;
import org.example.model.Rating;

public class ProductSearchCriteria {

    public static final int MAX_QUERY_LENGTH = 100;

    private String query;
    private String categoryId;
    private Long minPrice;
    private Long maxPrice;
    private String brand;
    private Integer minRating;
    private boolean onlyInStock;
    private SortOption sortOption = SortOption.NEWEST;

    public ProductSearchCriteria withQuery(String query) {
        this.query = blankToNull(query);
        return this;
    }

    public ProductSearchCriteria withCategory(String categoryId) {
        this.categoryId = blankToNull(categoryId);
        return this;
    }

    public ProductSearchCriteria withPriceRange(Long minPrice, Long maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        return this;
    }

    public ProductSearchCriteria withBrand(String brand) {
        this.brand = blankToNull(brand);
        return this;
    }

    public ProductSearchCriteria withMinRating(Integer minRating) {
        this.minRating = minRating;
        return this;
    }

    public ProductSearchCriteria onlyInStock(boolean onlyInStock) {
        this.onlyInStock = onlyInStock;
        return this;
    }

    public ProductSearchCriteria sortBy(SortOption sortOption) {
        this.sortOption = sortOption == null ? SortOption.NEWEST : sortOption;
        return this;
    }

    public void validate() {
        if (query != null && query.length() > MAX_QUERY_LENGTH) {
            throw new DomainException("Search text cannot be longer than " + MAX_QUERY_LENGTH + " characters");
        }
        if ((minPrice != null && minPrice < 0) || (maxPrice != null && maxPrice < 0)) {
            throw new DomainException("Price cannot be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new DomainException("Minimum price cannot be greater than maximum price");
        }
        if (minRating != null && (minRating < Rating.MIN || minRating > Rating.MAX)) {
            throw new DomainException("Rating filter must be between " + Rating.MIN + " and " + Rating.MAX);
        }
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text.trim();
    }

    public String getQuery() {
        return query;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public Long getMinPrice() {
        return minPrice;
    }

    public Long getMaxPrice() {
        return maxPrice;
    }

    public String getBrand() {
        return brand;
    }

    public Integer getMinRating() {
        return minRating;
    }

    public boolean isOnlyInStock() {
        return onlyInStock;
    }

    public SortOption getSortOption() {
        return sortOption;
    }
}
