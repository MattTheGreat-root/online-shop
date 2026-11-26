package org.example.model;

import java.util.regex.Pattern;

public record AddressDetails(String recipientName, PhoneNumber phone, String country,
                             String city, String street, String postalCode) {

    private static final Pattern POSTAL_CODE = Pattern.compile("^\\d{10}$");

    public AddressDetails {
        requireText(recipientName, "Recipient name");
        requireText(country, "Country");
        requireText(city, "City");
        requireText(street, "Street address");
        if (phone == null) {
            throw new DomainException("Recipient phone is required");
        }
        if (postalCode == null || !POSTAL_CODE.matcher(postalCode).matches()) {
            throw new DomainException("Postal code must have 10 digits");
        }
    }

    private static void requireText(String text, String fieldName) {
        if (text == null || text.isBlank()) {
            throw new DomainException(fieldName + " cannot be empty");
        }
    }

    @Override
    public String toString() {
        return recipientName + ", " + phone + ", " + country + ", " + city + ", " + street + ", " + postalCode;
    }
}
