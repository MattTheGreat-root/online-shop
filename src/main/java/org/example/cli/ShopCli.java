package org.example.cli;

import org.example.model.Session;
import org.example.model.User;
import org.example.service.ShopServices;

public class ShopCli {

    private final ConsoleIO io;
    private final ShopServices services;
    private final GuestMenu guestMenu;
    private final CustomerMenu customerMenu;
    private final AdminMenu adminMenu;

    public ShopCli(ConsoleIO io, ShopServices services) {
        this.io = io;
        this.services = services;
        ConsolePrinter printer = new ConsolePrinter(io);
        ProductBrowser browser = new ProductBrowser(io, printer, services);
        this.guestMenu = new GuestMenu(io, services, browser);
        this.customerMenu = new CustomerMenu(io, printer, services, browser);
        this.adminMenu = new AdminMenu(io, printer, services);
    }

    public void run() {
        try {
            Session session;
            while ((session = guestMenu.show()) != null) {
                openUserMenu(session);
                services.auth().logout(session.getId());
                io.println("You have logged out.");
            }
        } catch (InputClosedException e) {
            io.println("");
        }
        io.println("Goodbye!");
    }

    private void openUserMenu(Session session) {
        User user = services.auth().currentUser(session);
        if (user.isAdmin()) {
            adminMenu.show(user.getId());
        } else {
            customerMenu.show(user.getId());
        }
    }
}
