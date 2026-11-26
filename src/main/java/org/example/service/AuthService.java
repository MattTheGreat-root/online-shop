package org.example.service;

import org.example.model.DomainException;
import org.example.model.Email;
import org.example.model.PasswordHash;
import org.example.model.PhoneNumber;
import org.example.model.Session;
import org.example.model.User;
import org.example.model.UserRole;

import java.security.SecureRandom;
import java.util.Optional;

public class AuthService {

    private static final int RESET_CODE_BOUND = 1_000_000;

    private final DataStore store;
    private final SecureRandom random = new SecureRandom();

    public AuthService(DataStore store) {
        this.store = store;
    }

    public User register(String fullName, String contact, String rawPassword) {
        return createUser(fullName, contact, rawPassword, UserRole.CUSTOMER);
    }

    public User createAdmin(String fullName, String contact, String rawPassword) {
        return createUser(fullName, contact, rawPassword, UserRole.ADMIN);
    }

    public Session login(String contact, String rawPassword) {
        User user = findByContact(contact)
                .filter(found -> found.checkPassword(rawPassword))
                .filter(User::isActive)
                .orElseThrow(() -> new DomainException("Login information is not correct"));
        Session session = new Session(store.nextId("S"), user.getId());
        store.sessions().put(session.getId(), session);
        return session;
    }

    public void logout(String sessionId) {
        Session session = store.sessions().get(sessionId);
        if (session != null) {
            session.end();
        }
    }

    public User currentUser(Session session) {
        if (session == null || !session.isActive()) {
            throw new AccessDeniedException("Please log in first");
        }
        return store.findUser(session.getUserId());
    }

    /**
     * Creates a one-time code. In a real system it would be sent by email or SMS;
     * here it is returned so the CLI can show it.
     */
    public String requestPasswordReset(String contact) {
        User user = findByContact(contact)
                .orElseThrow(() -> new NotFoundException("No account is registered with " + contact));
        String code = String.format("%06d", random.nextInt(RESET_CODE_BOUND));
        store.passwordResetCodes().put(user.getId(), code);
        return code;
    }

    public void resetPassword(String contact, String code, String newRawPassword) {
        User user = findByContact(contact)
                .orElseThrow(() -> new NotFoundException("No account is registered with " + contact));
        String expectedCode = store.passwordResetCodes().get(user.getId());
        if (expectedCode == null || !expectedCode.equals(code)) {
            throw new DomainException("Verification code is not correct");
        }
        user.changePassword(PasswordHash.fromRawPassword(newRawPassword));
        store.passwordResetCodes().remove(user.getId());
    }

    private User createUser(String fullName, String contact, String rawPassword, UserRole role) {
        requireUniqueContact(contact);
        Email email = Email.looksLikeEmail(contact) ? new Email(contact) : null;
        PhoneNumber phone = email == null ? new PhoneNumber(contact) : null;
        User user = new User(store.nextId("U"), fullName, email, phone,
                PasswordHash.fromRawPassword(rawPassword), role);
        store.users().put(user.getId(), user);
        return user;
    }

    private void requireUniqueContact(String contact) {
        if (contact == null || contact.isBlank()) {
            throw new DomainException("Email or mobile number is required");
        }
        if (findByContact(contact).isPresent()) {
            throw new DomainException("An account with this email or mobile already exists");
        }
    }

    Optional<User> findByContact(String contact) {
        return store.users().values().stream()
                .filter(user -> user.hasContact(contact))
                .findFirst();
    }
}
