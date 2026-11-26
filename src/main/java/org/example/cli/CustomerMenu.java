package org.example.cli;

import org.example.model.AddressDetails;
import org.example.model.Order;
import org.example.model.Payment;
import org.example.model.PhoneNumber;
import org.example.service.ShopServices;
import org.example.service.SimulatedPaymentGateway.Outcome;

public class CustomerMenu {

    private final ConsoleIO io;
    private final ConsolePrinter printer;
    private final ShopServices services;
    private final ProductBrowser browser;
    private String customerId;

    public CustomerMenu(ConsoleIO io, ConsolePrinter printer, ShopServices services, ProductBrowser browser) {
        this.io = io;
        this.printer = printer;
        this.services = services;
        this.browser = browser;
    }

    public void show(String customerId) {
        this.customerId = customerId;
        new Menu(io, "Customer Menu")
                .option("Products", browser::showMenu)
                .option("My cart", this::cartMenu)
                .option("Orders and payment", this::orderMenu)
                .option("Write a review", this::writeReview)
                .option("My account", this::accountMenu)
                .exitLabel("Logout")
                .run();
    }

    private void cartMenu() {
        new Menu(io, "My Cart")
                .option("View cart", () -> printer.cart(services.carts().viewCart(customerId)))
                .option("Add product to cart", this::addToCart)
                .option("Change quantity", this::changeQuantity)
                .option("Remove a product", this::removeFromCart)
                .option("Empty the cart", this::emptyCart)
                .run();
    }

    private void addToCart() {
        String productId = io.readLine("Product id: ");
        int quantity = io.readInt("Quantity: ", 1, Integer.MAX_VALUE);
        printer.cart(services.carts().addToCart(customerId, productId, quantity));
    }

    private void changeQuantity() {
        String productId = io.readLine("Product id: ");
        int quantity = io.readInt("New quantity: ", 1, Integer.MAX_VALUE);
        printer.cart(services.carts().changeQuantity(customerId, productId, quantity));
    }

    private void removeFromCart() {
        printer.cart(services.carts().removeItem(customerId, io.readLine("Product id: ")));
    }

    private void emptyCart() {
        if (io.confirm("Remove everything from your cart?")) {
            printer.cart(services.carts().clearCart(customerId));
        }
    }

    private void orderMenu() {
        new Menu(io, "Orders and Payment")
                .option("Checkout", this::checkout)
                .option("Pay an order", () -> pay(io.readLine("Order id: ")))
                .option("My orders", () -> printer.orders(services.orders().listCustomerOrders(customerId)))
                .option("Order details", this::showOrderDetails)
                .run();
    }

    private void checkout() {
        printer.cart(services.carts().viewCart(customerId));
        printer.addresses(services.users().listAddresses(customerId));
        String addressId = io.readLine("Shipping address id: ");
        Order order = services.orders().checkout(customerId, addressId);
        io.println("Order " + order.getId() + " was created. Amount to pay: " + order.getTotalAmount());
        if (io.confirm("Pay now?")) {
            pay(order.getId());
        }
    }

    private void pay(String orderId) {
        if (!services.orders().getCustomerOrder(customerId, orderId).isPayable()) {
            io.println("Order " + orderId + " is already paid.");
            return;
        }
        services.gateway().setNextOutcome(readGatewayOutcome());
        Payment payment = services.payments().payOrder(customerId, orderId);
        switch (payment.getStatus()) {
            case SUCCESSFUL -> io.println("Payment was successful. Order " + orderId + " is paid.");
            case FAILED -> io.println("Payment failed. The order is still unpaid; you can try again.");
            case REJECTED -> io.println("Paid amount did not match the order, so the payment was not accepted.");
        }
    }

    private Outcome readGatewayOutcome() {
        io.println("Simulate the gateway result: 1. success  2. failure  3. wrong amount");
        return Outcome.values()[io.readInt("Result: ", 1, Outcome.values().length) - 1];
    }

    private void showOrderDetails() {
        Order order = services.orders().getCustomerOrder(customerId, io.readLine("Order id: "));
        printer.orderDetails(order, services.orders().listPayments(order.getId()));
    }

    private void writeReview() {
        String productId = io.readLine("Product id: ");
        String text = io.readLine("Your review: ");
        int rating = io.readInt("Rating (1-5): ", 1, 5);
        services.reviews().addReview(customerId, productId, text, rating);
        io.println("Thanks! Your review was saved.");
    }

    private void accountMenu() {
        new Menu(io, "My Account")
                .option("View profile", () -> printer.profile(services.users().getProfile(customerId)))
                .option("Edit profile", this::editProfile)
                .option("My addresses", () -> printer.addresses(services.users().listAddresses(customerId)))
                .option("Add address", this::addAddress)
                .option("Edit address", this::editAddress)
                .option("Delete address", this::deleteAddress)
                .option("Set default address", this::setDefaultAddress)
                .run();
    }

    private void editProfile() {
        printer.profile(services.users().getProfile(customerId));
        String fullName = io.readLine("Full name: ");
        String email = io.readLine("Email (empty = none): ");
        String phone = io.readLine("Mobile (empty = none): ");
        printer.profile(services.users().updateProfile(customerId, fullName, email, phone));
    }

    private void addAddress() {
        String title = io.readLine("Title (e.g. Home): ");
        services.users().addAddress(customerId, title, readAddressDetails());
        io.println("Address saved.");
    }

    private void editAddress() {
        String addressId = io.readLine("Address id: ");
        String title = io.readLine("New title: ");
        services.users().editAddress(customerId, addressId, title, readAddressDetails());
        io.println("Address updated.");
    }

    private void deleteAddress() {
        services.users().deleteAddress(customerId, io.readLine("Address id: "));
        printer.addresses(services.users().listAddresses(customerId));
    }

    private void setDefaultAddress() {
        services.users().setDefaultAddress(customerId, io.readLine("Address id: "));
        io.println("Default address changed.");
    }

    private AddressDetails readAddressDetails() {
        return new AddressDetails(
                io.readLine("Recipient name: "),
                new PhoneNumber(io.readLine("Recipient mobile: ")),
                io.readLine("Country: "),
                io.readLine("City: "),
                io.readLine("Street address: "),
                io.readLine("Postal code (10 digits): "));
    }
}
