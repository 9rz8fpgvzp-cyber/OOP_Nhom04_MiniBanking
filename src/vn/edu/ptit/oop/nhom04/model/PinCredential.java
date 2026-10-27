package vn.edu.ptit.oop.nhom04.model;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.time.Duration;
import java.time.Instant;
import java.time.Clock;
import java.util.Objects;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PinCredential {

    private static final int MAX_ATTEMPTS = 3;
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;

    private final byte[] salt;
    private final byte[] pinHash;
    private final Clock authenticationClock;

    private int failedAttempts = 0;
    private Instant lockedUntil;

    public PinCredential(char[] pin) {
        this(pin, Clock.systemUTC());
    }

    public PinCredential(char[] pin, Clock authenticationClock) {
        this.authenticationClock = Objects.requireNonNull(authenticationClock, "Đồng hồ xác thực không được null.");
        if (!isValidPin(pin)) {
            throw new IllegalArgumentException("PIN phải gồm đúng 6 chữ số.");
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
        PBEKeySpec spec = new PBEKeySpec(pin, salt, ITERATIONS, KEY_LENGTH);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return factory.generateSecret(spec).getEncoded();

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Không thể xử lý PIN.", e);
        } finally {
            spec.clearPassword();
        }
    }

    public boolean matches(char[] inputPin) {
        if (isLocked() || !isValidPin(inputPin)) {
            return false;
        }

        byte[] inputHash = hashPin(inputPin, salt);

        try {
            return MessageDigest.isEqual(pinHash, inputHash);
        } finally {
            Arrays.fill(inputHash, (byte) 0);
        }
    }

    public void recordFailedAttempt() {
        Instant now = authenticationClock.instant();
        if (isLockedAt(now)) {
            return;
        }

        failedAttempts++;

        if (failedAttempts >= MAX_ATTEMPTS) {
            lockedUntil = now.plusSeconds(30);
        }
    }

    public void resetFailedAttempts() {
        if (isLocked()) {
            return;
        }

        failedAttempts = 0;
    }

    public int getRemainingAttempts() {
        if (isLocked()) {
            return 0;
        }

        return MAX_ATTEMPTS - failedAttempts;
    }

    public boolean isLocked() {
        return isLockedAt(authenticationClock.instant());
    }

    private boolean isLockedAt(Instant now) {
        if (lockedUntil == null) {
            return false;
        }

        if (now.isBefore(lockedUntil)) {
            return true;
        }

        lockedUntil = null;
        failedAttempts = 0;
        return false;
    }

    public long getRemainingLockSeconds() {
        Instant now = authenticationClock.instant();
        if (!isLockedAt(now)) {
            return 0;
        }

        Duration remaining = Duration.between(now, lockedUntil);
        long seconds = remaining.getSeconds();

        if (remaining.getNano() > 0) {
            seconds++;
        }

        return seconds;
    }
}
