package com.shotmaniacs.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Hashes passwords using SHA-256 algorithm before storing them into a database.
 */
public class PasswordHasher {
    private static final String ALGORITHM = "SHA-256";

    /**
     * Hashes a given value using the SHA-256 algorithm and converts it into a string.
     *
     * @param value given value
     * @return value hash converted to a string
     */
    public String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("[ERROR] Failed to find hash algorithm: " + ALGORITHM);
            return null;
        }
    }
}