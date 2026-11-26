package org.example.service;

import org.example.model.Category;
import org.example.model.User;

/**
 * Fills the in-memory store with a few records so the program can be tried right away.
 */
public class DemoDataSeeder {

    public static final String ADMIN_EMAIL = "admin@shop.com";
    public static final String ADMIN_PASSWORD = "admin123";

    private final ShopServices services;

    public DemoDataSeeder(ShopServices services) {
        this.services = services;
    }

    public void seed() {
        User admin = services.auth().createAdmin("Store Admin", ADMIN_EMAIL, ADMIN_PASSWORD);
        String adminId = admin.getId();

        Category phones = services.categories().createCategory(adminId, "Mobile");
        Category laptops = services.categories().createCategory(adminId, "Laptop");
        Category books = services.categories().createCategory(adminId, "Book");
        services.categories().createCategory(adminId, "Toys");

        services.products().createProduct(adminId, "Galaxy A55", "6.6 inch, 256GB", "Samsung",
                25_000_000, 10, phones.getId()).addImage("images/galaxy-a55-front.jpg");
        services.products().createProduct(adminId, "Redmi Note 13", "6.7 inch, 128GB", "Xiaomi",
                14_500_000, 5, phones.getId());
        services.products().createProduct(adminId, "ThinkPad E14", "Core i5, 16GB RAM", "Lenovo",
                62_000_000, 3, laptops.getId()).addImage("images/thinkpad-e14.jpg");
        services.products().createProduct(adminId, "Clean Code", "Robert C. Martin", "Prentice Hall",
                950_000, 20, books.getId());
    }
}
