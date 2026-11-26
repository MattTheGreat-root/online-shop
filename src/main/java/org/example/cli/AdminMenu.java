package org.example.cli;

import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.model.Product;
import org.example.model.StockChange;
import org.example.service.ShopServices;

public class AdminMenu {

    private final ConsoleIO io;
    private final ConsolePrinter printer;
    private final ShopServices services;
    private String adminId;

    public AdminMenu(ConsoleIO io, ConsolePrinter printer, ShopServices services) {
        this.io = io;
        this.printer = printer;
        this.services = services;
    }

    public void show(String adminId) {
        this.adminId = adminId;
        new Menu(io, "Admin Menu")
                .option("Products", this::productMenu)
                .option("Inventory", this::inventoryMenu)
                .option("Orders", this::orderMenu)
                .option("Categories", this::categoryMenu)
                .exitLabel("Logout")
                .run();
    }

    private void productMenu() {
        new Menu(io, "Manage Products")
                .option("All products", () -> printer.products(services.products().listAllProducts(adminId)))
                .option("Create product", this::createProduct)
                .option("Edit product", this::editProduct)
                .option("Deactivate product", this::deactivateProduct)
                .run();
    }

    private void createProduct() {
        printer.categories(services.categories().listActiveCategories());
        Product product = services.products().createProduct(adminId,
                io.readLine("Name: "),
                io.readLine("Description: "),
                io.readLine("Brand: "),
                io.readLong("Price: "),
                io.readInt("Stock: ", 0, Integer.MAX_VALUE),
                io.readLine("Category id: "));
        io.println("Product " + product.getId() + " was created.");
    }

    private void editProduct() {
        String productId = io.readLine("Product id: ");
        Product product = services.products().editProduct(adminId, productId,
                io.readLine("New name: "),
                io.readLine("New description: "),
                io.readLine("New brand: "),
                io.readLong("New price: "));
        io.println("Product " + product.getId() + " was updated.");
    }

    private void deactivateProduct() {
        String productId = io.readLine("Product id: ");
        if (io.confirm("Stop selling this product?")) {
            services.products().deactivateProduct(adminId, productId);
            io.println("Product is now inactive. Old orders are kept.");
        }
    }

    private void inventoryMenu() {
        new Menu(io, "Inventory")
                .option("Stock levels", () -> printer.products(services.products().listAllProducts(adminId)))
                .option("Change stock", this::changeStock)
                .option("Stock history of a product", () -> printer.stockHistory(
                        services.products().stockHistory(adminId, io.readLine("Product id: "))))
                .run();
    }

    private void changeStock() {
        String productId = io.readLine("Product id: ");
        int newStock = io.readInt("New stock: ", Integer.MIN_VALUE, Integer.MAX_VALUE);
        StockChange change = services.products().changeStock(adminId, productId, newStock);
        io.println("Stock changed from " + change.oldStock() + " to " + change.newStock() + ".");
    }

    private void orderMenu() {
        new Menu(io, "Store Orders")
                .option("All orders", () -> printer.orders(services.orders().listAllOrders(adminId, null)))
                .option("Filter by status", this::filterOrders)
                .option("Order details", this::showOrderDetails)
                .run();
    }

    private void filterOrders() {
        OrderStatus[] statuses = OrderStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            io.println((i + 1) + ". " + statuses[i]);
        }
        OrderStatus status = statuses[io.readInt("Status: ", 1, statuses.length) - 1];
        printer.orders(services.orders().listAllOrders(adminId, status));
    }

    private void showOrderDetails() {
        Order order = services.orders().getOrderForAdmin(adminId, io.readLine("Order id: "));
        printer.orderDetails(order, services.orders().listPayments(order.getId()));
    }

    private void categoryMenu() {
        new Menu(io, "Categories")
                .option("All categories", () -> printer.categories(services.categories().listActiveCategories()))
                .option("Create category", () -> io.println("Category " + services.categories()
                        .createCategory(adminId, io.readLine("Name: ")).getId() + " was created."))
                .run();
    }
}
