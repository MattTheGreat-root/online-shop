package org.example.service;

import org.example.model.User;

class AccessPolicy {

    private final DataStore store;

    AccessPolicy(DataStore store) {
        this.store = store;
    }

    User requireActiveUser(String userId) {
        User user = store.findUser(userId);
        if (!user.isActive()) {
            throw new AccessDeniedException("This account is not active");
        }
        return user;
    }

    User requireCustomer(String userId) {
        User user = requireActiveUser(userId);
        if (user.isAdmin()) {
            throw new AccessDeniedException("Only customers can do this");
        }
        return user;
    }

    User requireAdmin(String userId) {
        User user = requireActiveUser(userId);
        if (!user.isAdmin()) {
            throw new AccessDeniedException("Only the store admin can do this");
        }
        return user;
    }
}
