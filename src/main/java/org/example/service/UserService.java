package org.example.service;

import org.example.model.Address;
import org.example.model.AddressDetails;
import org.example.model.DomainException;
import org.example.model.Email;
import org.example.model.PhoneNumber;
import org.example.model.User;

import java.util.List;

public class UserService {

    private final DataStore store;
    private final AccessPolicy access;

    public UserService(DataStore store) {
        this.store = store;
        this.access = new AccessPolicy(store);
    }

    public User getProfile(String userId) {
        return access.requireActiveUser(userId);
    }

    public User updateProfile(String userId, String fullName, String emailText, String phoneText) {
        User user = access.requireActiveUser(userId);
        Email email = isBlank(emailText) ? null : new Email(emailText);
        PhoneNumber phone = isBlank(phoneText) ? null : new PhoneNumber(phoneText);
        requireNotUsedByOthers(userId, email == null ? null : email.value());
        requireNotUsedByOthers(userId, phone == null ? null : phone.value());
        user.updateProfile(fullName, email, phone);
        return user;
    }

    public Address addAddress(String customerId, String title, AddressDetails details) {
        User customer = access.requireCustomer(customerId);
        Address address = new Address(store.nextId("A"), title, details);
        customer.addAddress(address);
        return address;
    }

    public void editAddress(String customerId, String addressId, String title, AddressDetails details) {
        access.requireCustomer(customerId).editAddress(addressId, title, details);
    }

    public void deleteAddress(String customerId, String addressId) {
        access.requireCustomer(customerId).removeAddress(addressId);
    }

    public void setDefaultAddress(String customerId, String addressId) {
        access.requireCustomer(customerId).setDefaultAddress(addressId);
    }

    public List<Address> listAddresses(String customerId) {
        return access.requireCustomer(customerId).getAddresses();
    }

    private void requireNotUsedByOthers(String userId, String contact) {
        if (contact == null) {
            return;
        }
        boolean usedByOthers = store.users().values().stream()
                .anyMatch(other -> !other.getId().equals(userId) && other.hasContact(contact));
        if (usedByOthers) {
            throw new DomainException(contact + " is already used by another account");
        }
    }

    private static boolean isBlank(String text) {
        return text == null || text.isBlank();
    }
}
