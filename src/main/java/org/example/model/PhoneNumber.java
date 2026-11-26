package org.example.model;

import java.util.regex.Pattern;

public record PhoneNumber(String value) {

    private static final Pattern FORMAT = Pattern.compile("^09\\d{9}$");

    public PhoneNumber {
        if (value == null || !FORMAT.matcher(value.trim()).matches()) {
            throw new DomainException("Invalid mobile number (expected 09xxxxxxxxx): " + value);
        }
        value = value.trim();
    }

    @Override
    public String toString() {
        return value;
    }
}
