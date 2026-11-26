package org.example;

import org.example.cli.ConsoleIO;
import org.example.cli.ShopCli;
import org.example.service.DemoDataSeeder;
import org.example.service.ShopServices;

public class Main {

    public static void main(String[] args) {
        ShopServices services = ShopServices.inMemory();
        new DemoDataSeeder(services).seed();

        System.out.println("Demo admin: " + DemoDataSeeder.ADMIN_EMAIL + " / " + DemoDataSeeder.ADMIN_PASSWORD);
        new ShopCli(new ConsoleIO(System.in, System.out), services).run();
    }
}
