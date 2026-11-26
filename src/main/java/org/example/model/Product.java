package org.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Product {

    private final String id;
    private String name;
    private String description;
    private String brand;
    private Money price;
    private int stock;
    private final Category category;
    private final List<String> imageUrls = new ArrayList<>();
    private boolean active;
    private int soldCount;
    private final LocalDateTime createdAt;

    public Product(String id, String name, String description, String brand,
                   Money price, int stock, Category category) {
        if (category == null) {
            throw new DomainException("Product must belong to a category");
        }
        this.id = id;
        this.category = category;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        applyDetails(name, description, brand, price);
        applyStock(stock);
    }

    public void updateDetails(String name, String description, String brand, Money price) {
        applyDetails(name, description, brand, price);
    }

    public void changeStock(int newStock) {
        applyStock(newStock);
    }

    private void applyDetails(String name, String description, String brand, Money price) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Product name cannot be empty");
        }
        if (price == null) {
            throw new DomainException("Product price is required");
        }
        this.name = name.trim();
        this.description = description == null ? "" : description.trim();
        this.brand = brand == null ? "" : brand.trim();
        this.price = price;
    }

    private void applyStock(int newStock) {
        if (newStock < 0) {
            throw new DomainException("Stock cannot be negative");
        }
        this.stock = newStock;
    }

    public boolean canBePurchased(int quantity) {
        return active && quantity > 0 && quantity <= stock;
    }

    public void recordSale(int quantity) {
        if (!canBePurchased(quantity)) {
            throw new DomainException("Not enough stock for " + name);
        }
        stock -= quantity;
        soldCount += quantity;
    }

    public void deactivate() {
        active = false;
    }

    public void addImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isBlank()) {
            imageUrls.add(imageUrl.trim());
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getBrand() {
        return brand;
    }

    public Money getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public Category getCategory() {
        return category;
    }

    public List<String> getImageUrls() {
        return Collections.unmodifiableList(imageUrls);
    }

    public boolean isActive() {
        return active;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
