package org.example.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

public record PasswordHash(String salt, String hash) {

    private static final int MIN_PASSWORD_LENGTH = 6;

    public static PasswordHash fromRawPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new DomainException("Password must have at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        String salt = UUID.randomUUID().toString();
        return new PasswordHash(salt, digest(salt, rawPassword));
    }

    public boolean matches(String rawPassword) {
        return rawPassword != null && hash.equals(digest(salt, rawPassword));
    }

    private static String digest(String salt, String rawPassword) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] bytes = sha256.digest((salt + rawPassword).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    @Override
    public String toString() {
        return "PasswordHash[hidden]";
    }
}
