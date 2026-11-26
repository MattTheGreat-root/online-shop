package org.example.service;

import org.example.model.Category;
import org.example.model.Money;
import org.example.model.Product;
import org.example.model.StockChange;
import org.example.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ProductService {

    private final ShopStorage.Catalog store;
    private final AccessPolicy access;
    private final ReviewService reviewService;

    public ProductService(ShopStorage.Catalog store, ReviewService reviewService) {
        this.store = store;
        this.access = new AccessPolicy(store);
        this.reviewService = reviewService;
    }

    public List<Product> search(ProductSearchCriteria criteria) {
        criteria.validate();
        Stream<Product> result = store.listProducts().stream()
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
                        || reviewService.averageRating(product.getId()) >= criteria.getMinRating())
                .filter(product -> !criteria.isOnlyInStock() || product.getStock() > 0);
        return result.sorted(comparatorFor(criteria.getSortOption())).toList();
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
                    (Product product) -> reviewService.averageRating(product.getId())).reversed();
        };
    }
}
