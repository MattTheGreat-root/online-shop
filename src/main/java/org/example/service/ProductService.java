package org.example.service;

import org.example.model.Category;
import org.example.model.Money;
import org.example.model.Product;
import org.example.model.StockChange;
import org.example.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ProductService {

    private final ShopStorage.Catalog store;
    private final AccessPolicy access;
    private final ProductSearch search;

    public ProductService(ShopStorage.Catalog store, ProductRatings ratings) {
        this.store = store;
        this.access = new AccessPolicy(store);
        this.search = new ProductSearch(ratings);
    }

    public List<Product> search(ProductSearchCriteria criteria) {
        criteria.validate();
        return search.search(store.listProducts(), criteria);
    }

    public List<Product> listActiveProducts() {
        return search(new ProductSearchCriteria());
    }

    public Product getActiveProduct(String productId) {
        Product product = store.findProduct(productId);
        if (!product.isActive()) {
            throw new NotFoundException("Product is not available: " + productId);
        }
        return product;
    }

    public Product createProduct(String adminId, String name, String description, String brand,
                                 long price, int stock, String categoryId) {
        access.requireAdmin(adminId);
        Category category = store.findCategory(categoryId);
        Product product = new Product(store.nextId("P"), name, description, brand,
                new Money(price), stock, category);
        store.addProduct(product);
        return product;
    }

    public Product editProduct(String adminId, String productId, String name, String description,
                               String brand, long price) {
        access.requireAdmin(adminId);
        Product product = store.findProduct(productId);
        product.updateDetails(name, description, brand, new Money(price));
        return product;
    }

    public StockChange changeStock(String adminId, String productId, int newStock) {
        User admin = access.requireAdmin(adminId);
        Product product = store.findProduct(productId);
        int oldStock = product.getStock();
        product.changeStock(newStock);
        StockChange change = new StockChange(store.nextId("SC"), productId, admin.getId(),
                oldStock, newStock, LocalDateTime.now());
        store.addStockChange(change);
        return change;
    }

    public void deactivateProduct(String adminId, String productId) {
        access.requireAdmin(adminId);
        store.findProduct(productId).deactivate();
    }

    public List<Product> listAllProducts(String adminId) {
        access.requireAdmin(adminId);
        return store.listProducts();
    }

    public List<StockChange> stockHistory(String adminId, String productId) {
        access.requireAdmin(adminId);
        store.findProduct(productId);
        return store.listStockChanges().stream()
                .filter(change -> change.productId().equals(productId))
                .toList();
    }
}
