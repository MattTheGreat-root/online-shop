package org.example.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class Menu {

    private record Option(String label, Runnable action) {
    }

    private final ConsoleIO io;
    private final String title;
    private final List<Option> options = new ArrayList<>();
    private String exitLabel = "Back";
    private BooleanSupplier finished = () -> false;

    public Menu(ConsoleIO io, String title) {
        this.io = io;
        this.title = title;
    }

    public Menu option(String label, Runnable action) {
        options.add(new Option(label, action));
        return this;
    }

    public Menu exitLabel(String label) {
        this.exitLabel = label;
        return this;
    }

    public Menu until(BooleanSupplier condition) {
        this.finished = condition;
        return this;
    }

    public void run() {
        while (!finished.getAsBoolean()) {
            io.printTitle(title);
            for (int i = 0; i < options.size(); i++) {
                io.println((i + 1) + ". " + options.get(i).label());
            }
            io.println("0. " + exitLabel);
            int choice = io.readInt("Choose: ", 0, options.size());
            if (choice == 0) {
                return;
            }
            io.runSafely(options.get(choice - 1).action());
        }
    }
}
