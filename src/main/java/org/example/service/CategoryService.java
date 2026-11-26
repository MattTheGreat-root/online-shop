package org.example.service;

import org.example.model.Category;
import org.example.model.DomainException;
import org.example.model.Product;

import java.util.List;

public class CategoryService {

    private final ShopStorage.Categories store;
    private final AccessPolicy access;

    public CategoryService(ShopStorage.Categories store) {
        this.store = store;
        this.access = new AccessPolicy(store);
    }

    public Category createCategory(String adminId, String name) {
        access.requireAdmin(adminId);
        boolean duplicate = store.listCategories().stream()
                .anyMatch(category -> category.getName().equalsIgnoreCase(name == null ? "" : name.trim()));
        if (duplicate) {
            throw new DomainException("A category with this name already exists");
        }
        Category category = new Category(store.nextId("C"), name);
        store.addCategory(category);
        return category;
    }

    public List<Category> listActiveCategories() {
        return store.listCategories().stream()
                .filter(Category::isActive)
                .toList();
    }

    public List<Product> listActiveProductsOf(String categoryId) {
        Category category = store.findCategory(categoryId);
        if (!category.isActive()) {
            throw new NotFoundException("Category not found: " + categoryId);
        }
        return store.listProducts().stream()
                .filter(Product::isActive)
                .filter(product -> product.getCategory().getId().equals(categoryId))
                .toList();
    }
}
