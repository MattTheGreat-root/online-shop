package org.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {

    private final String id;
    private String fullName;
    private Email email;
    private PhoneNumber phone;
    private PasswordHash passwordHash;
    private final UserRole role;
    private boolean active;
    private final LocalDateTime createdAt;
    private final List<Address> addresses = new ArrayList<>();

    public User(String id, String fullName, Email email, PhoneNumber phone,
                PasswordHash passwordHash, UserRole role) {
        this.id = id;
        this.role = role;
        this.passwordHash = passwordHash;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        applyProfile(fullName, email, phone);
    }

    public void updateProfile(String fullName, Email email, PhoneNumber phone) {
        applyProfile(fullName, email, phone);
    }

    private void applyProfile(String fullName, Email email, PhoneNumber phone) {
        if (fullName == null || fullName.isBlank()) {
            throw new DomainException("Full name cannot be empty");
        }
        if (email == null && phone == null) {
            throw new DomainException("Email or mobile number is required");
        }
        this.fullName = fullName.trim();
        this.email = email;
        this.phone = phone;
    }

    public boolean hasContact(String contact) {
        if (contact == null) {
            return false;
        }
        String normalized = contact.trim().toLowerCase();
        return (email != null && email.value().equals(normalized))
                || (phone != null && phone.value().equals(normalized));
    }

    public boolean checkPassword(String rawPassword) {
        return passwordHash.matches(rawPassword);
    }

    public void changePassword(PasswordHash newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public void deactivate() {
        active = false;
    }

    public void addAddress(Address address) {
        addresses.add(address);
        if (addresses.size() == 1) {
            address.markAsDefault();
        }
    }

    public void editAddress(String addressId, String title, AddressDetails details) {
        findAddress(addressId).update(title, details);
    }

    public void removeAddress(String addressId) {
        Address removed = findAddress(addressId);
        addresses.remove(removed);
        if (removed.isDefault() && !addresses.isEmpty()) {
            addresses.get(0).markAsDefault();
        }
    }

    public void setDefaultAddress(String addressId) {
        Address chosen = findAddress(addressId);
        addresses.forEach(Address::unmarkAsDefault);
        chosen.markAsDefault();
    }

    public Address findAddress(String addressId) {
        return addresses.stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new DomainException("Address not found in your account: " + addressId));
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public Email getEmail() {
        return email;
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }
}
