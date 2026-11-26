package org.example.service;

import org.example.model.Cart;
import org.example.model.Category;
import org.example.model.Order;
import org.example.model.Payment;
import org.example.model.Product;
import org.example.model.Review;
import org.example.model.Session;
import org.example.model.StockChange;
import org.example.model.User;

import java.util.List;
import java.util.Optional;

/** Storage contracts used by the individual shop services. */
public final class ShopStorage {

    private ShopStorage() {
    }

    public interface Ids {
        String nextId(String prefix);
    }

    public interface UserLookup {
        User findUser(String userId);
    }

    public interface ProductLookup {
        Product findProduct(String productId);
    }

    public interface UserProfiles extends UserLookup, Ids {
        List<User> listUsers();
    }

    public interface Authentication extends UserProfiles {
        void addUser(User user);
        void addSession(Session session);
        Optional<Session> findSession(String sessionId);
        void saveResetCode(String userId, String code);
        Optional<String> findResetCode(String userId);
        void removeResetCode(String userId);
    }

    public interface Categories extends UserLookup, Ids {
        Category findCategory(String categoryId);
        List<Category> listCategories();
        void addCategory(Category category);
        List<Product> listProducts();
    }

    public interface Catalog extends UserLookup, ProductLookup, Ids {
        Category findCategory(String categoryId);
        List<Product> listProducts();
        void addProduct(Product product);
        void addStockChange(StockChange change);
        List<StockChange> listStockChanges();
    }

    public interface Carts extends UserLookup, ProductLookup {
        Cart cartOf(String customerId);
    }

    public interface Orders extends Carts, Ids {
        Order findOrder(String orderId);
        List<Order> listOrders();
        void addOrder(Order order);
        List<Payment> listPayments();
    }

    public interface Payments extends UserLookup, ProductLookup, Ids {
        Order findOrder(String orderId);
        void addPayment(Payment payment);
    }

    public interface Reviews extends UserLookup, ProductLookup, Ids {
        List<Order> listOrders();
        List<Review> listReviews();
        void addReview(Review review);
    }
}
