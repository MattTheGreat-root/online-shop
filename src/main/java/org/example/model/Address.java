package org.example.model;

public class Address {

    private final String id;
    private String title;
    private AddressDetails details;
    private boolean defaultAddress;

    public Address(String id, String title, AddressDetails details) {
        this.id = id;
        applyChanges(title, details);
    }

    public void update(String title, AddressDetails details) {
        applyChanges(title, details);
    }

    private void applyChanges(String title, AddressDetails details) {
        if (title == null || title.isBlank()) {
            throw new DomainException("Address title cannot be empty");
        }
        if (details == null) {
            throw new DomainException("Address details are required");
        }
        this.title = title.trim();
        this.details = details;
    }

    void markAsDefault() {
        defaultAddress = true;
    }

    void unmarkAsDefault() {
        defaultAddress = false;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public AddressDetails getDetails() {
        return details;
    }

    public boolean isDefault() {
        return defaultAddress;
    }
}
