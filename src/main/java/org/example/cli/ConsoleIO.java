package org.example.cli;

import org.example.model.DomainException;
import org.example.service.AccessDeniedException;
import org.example.service.NotFoundException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class ConsoleIO {

    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleIO(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    public void println(String text) {
        out.println(text);
    }

    public void printTitle(String title) {
        out.println();
        out.println("=== " + title + " ===");
    }

    public void error(String message) {
        out.println("[!] " + message);
    }

    public String readLine(String prompt) {
        out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String text = readLine(prompt);
            try {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // asked again below
            }
            error("Please enter a number between " + min + " and " + max);
        }
    }

    public long readLong(String prompt) {
        while (true) {
            try {
                return Long.parseLong(readLine(prompt));
            } catch (NumberFormatException e) {
                error("Please enter a whole number");
            }
        }
    }

    public Long readOptionalLong(String prompt) {
        while (true) {
            String text = readLine(prompt);
            if (text.isEmpty()) {
                return null;
            }
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException e) {
                error("Please enter a whole number or leave it empty");
            }
        }
    }

    public boolean confirm(String prompt) {
        return readLine(prompt + " (y/n): ").equalsIgnoreCase("y");
    }

    public void runSafely(Runnable action) {
        try {
            action.run();
        } catch (DomainException | NotFoundException | AccessDeniedException e) {
            error(e.getMessage());
        }
    }
}
