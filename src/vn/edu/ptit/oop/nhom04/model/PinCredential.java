package vn.edu.ptit.oop.nhom04.model;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PinCredential {

    private static final int MAX_ATTEMPTS = 3;
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;

    private final byte[] salt;
    private final byte[] pinHash;

    private int failedAttempts = 0;
    private boolean locked = false;

    public PinCredential(char[] pin) {
        if (!isValidPin(pin)) {
            throw new IllegalArgumentException(
                "PIN phải gồm đúng 6 chữ số.");
        }

        salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        pinHash = hashPin(pin, salt);
    }

    public static boolean isValidPin(char[] pin) {
        if (pin == null || pin.length != 6) {
            return false;
        }

        for (char c : pin) {
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    private static byte[] hashPin(char[] pin, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(
            pin, salt, ITERATIONS, KEY_LENGTH
        );

        try {
            SecretKeyFactory factory =
                SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA256"
                );

            return factory.generateSecret(spec).getEncoded();

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                "Không thể xử lý PIN.", e
            );
        } finally {
            spec.clearPassword();
        }
    }

    public boolean matches(char[] inputPin) {
        if (locked || !isValidPin(inputPin)) {
            return false;
        }

        byte[] inputHash = hashPin(inputPin, salt);

        try {
            return MessageDigest.isEqual(
                pinHash, inputHash
            );
        } finally {
            Arrays.fill(inputHash, (byte) 0);
        }
    }

    public void recordFailedAttempt() {
        if (locked) {
            return;
        }

        failedAttempts++;

        if (failedAttempts >= MAX_ATTEMPTS) {
            locked = true;
        }
    }

    public void resetFailedAttempts() {
        if (!locked) {
            failedAttempts = 0;
        }
    }

    public int getRemainingAttempts() {
        return locked
            ? 0
            : MAX_ATTEMPTS - failedAttempts;
    }

    public boolean isLocked() {
        return locked;
    }
}
