package org.example.model;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern FORMAT = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    public Email {
        if (value == null || !FORMAT.matcher(value.trim()).matches()) {
            throw new DomainException("Invalid email: " + value);
        }
        value = value.trim().toLowerCase();
    }

    public static boolean looksLikeEmail(String text) {
        return text != null && text.contains("@");
    }

    @Override
    public String toString() {
        return value;
    }
}
