package org.example.cli;

import org.example.model.Session;
import org.example.service.ShopServices;

public class GuestMenu {

    private final ConsoleIO io;
    private final ShopServices services;
    private final ProductBrowser browser;
    private Session session;

    public GuestMenu(ConsoleIO io, ShopServices services, ProductBrowser browser) {
        this.io = io;
        this.services = services;
        this.browser = browser;
    }

    /**
     * Shows the guest menu until the user logs in or exits.
     *
     * @return the new session, or null if the user chose to exit
     */
    public Session show() {
        session = null;
        new Menu(io, "Online Shop")
                .option("Register", this::register)
                .option("Login", this::login)
                .option("Forgot password", this::resetPassword)
                .option("Browse products", browser::showMenu)
                .exitLabel("Exit")
                .until(() -> session != null)
                .run();
        return session;
    }

    private void register() {
        String fullName = io.readLine("Full name: ");
        String contact = io.readLine("Email or mobile (09xxxxxxxxx): ");
        String password = io.readLine("Password (min 6 characters): ");
        services.auth().register(fullName, contact, password);
        io.println("Your account was created. You can log in now.");
    }

    private void login() {
        String contact = io.readLine("Email or mobile: ");
        String password = io.readLine("Password: ");
        session = services.auth().login(contact, password);
        io.println("Welcome " + services.auth().currentUser(session).getFullName() + "!");
    }

    private void resetPassword() {
        String contact = io.readLine("Registered email or mobile: ");
        String code = services.auth().requestPasswordReset(contact);
        io.println("(Simulated message) Your verification code is: " + code);
        String enteredCode = io.readLine("Verification code: ");
        String newPassword = io.readLine("New password: ");
        services.auth().resetPassword(contact, enteredCode, newPassword);
        io.println("Your password was changed.");
    }
}
