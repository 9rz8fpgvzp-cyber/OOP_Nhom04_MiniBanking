package vn.edu.ptit.oop.nhom04.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import vn.edu.ptit.oop.nhom04.model.PinCredential;
import vn.edu.ptit.oop.nhom04.exception.AccountLockedException;
import vn.edu.ptit.oop.nhom04.exception.AccountNotFoundException;
import vn.edu.ptit.oop.nhom04.exception.AuthenticationRequiredException;

public class PinService {

    private final Map<String, PinCredential> credentials = new HashMap<>();
    private final Clock authenticationClock;

    private String authenticatedCustomerId;

    public PinService() {
        this(Clock.systemUTC());
    }

    public PinService(Clock authenticationClock) {
        this.authenticationClock = Objects.requireNonNull(authenticationClock, "Đồng hồ xác thực không được null.");
    }

    public synchronized void registerPin(String customerId, char[] pin) {
        try {
            if (customerId == null || customerId.isBlank()) {
                throw new IllegalArgumentException("Mã khách hàng không hợp lệ.");
            }

            if (credentials.containsKey(customerId)) {
                throw new IllegalArgumentException("Khách hàng đã đăng ký PIN, không thể ghi đè.");
            }

            PinCredential credential = new PinCredential(pin);
            credentials.put(customerId, credential);
        } finally {
            clearPin(pin);
        }
    }

    public synchronized boolean authenticate(String customerId, char[] pin) {
        logout();

        try {
            PinCredential credential = getCredential(customerId);
            Instant now = authenticationClock.instant();

            checkUnlocked(credential, now);

            if (credential.matches(pin)) {
                credential.resetFailedAttempts(now);
                authenticatedCustomerId = customerId;
                return true;
            }

            now = authenticationClock.instant();
            credential.recordFailedAttempt(now);

            checkUnlocked(credential, now);
            return false;
        } finally {
            clearPin(pin);
        }
    }

    public synchronized int getRemainingAttempts(String customerId) {
        PinCredential credential = getCredential(customerId);
        Instant now = authenticationClock.instant();

        return credential.getRemainingAttempts(now);
    }

    public synchronized long getRemainingLockSeconds(String customerId) {
        PinCredential credential = getCredential(customerId);
        Instant now = authenticationClock.instant();

        return credential.getRemainingLockSeconds(now);
    }

    public synchronized boolean isLocked(String customerId) {
        PinCredential credential = getCredential(customerId);
        Instant now = authenticationClock.instant();

        return credential.isLocked(now);
    }

    public synchronized boolean isAuthenticated(String customerId) {
        if (customerId == null) {
            return false;
        }

        if (!customerId.equals(authenticatedCustomerId)) {
            return false;
        }

        PinCredential credential = credentials.get(customerId);

        if (credential == null) {
            return false;
        }

        return !credential.isLocked(authenticationClock.instant());
    }

    public synchronized Optional<String> getAuthenticatedCustomerId() {
        if (isAuthenticated(authenticatedCustomerId)) {
            return Optional.of(authenticatedCustomerId);
        }

        return Optional.empty();
    }

    public synchronized void requireAuthenticated(String customerId) {
        PinCredential credential = getCredential(customerId);
        Instant now = authenticationClock.instant();

        checkUnlocked(credential, now);

        if (!customerId.equals(authenticatedCustomerId)) {
            throw new AuthenticationRequiredException( "Bạn cần đăng nhập khách hàng này.");
        }
    }

    public synchronized void logout() {
        authenticatedCustomerId = null;
    }

    private PinCredential getCredential(String customerId) {
        PinCredential credential = credentials.get(customerId);

        if (credential == null) {
            throw new AccountNotFoundException("Không tìm thấy khách hàng: " + customerId);
        }

        return credential;
    }

    private void checkUnlocked(PinCredential credential, Instant now) {
        long seconds = credential.getRemainingLockSeconds(now);

        if (seconds > 0) {
            throw new AccountLockedException("Đăng nhập khách hàng bị khóa. Vui lòng thử lại sau "+ seconds + " giây.");
        }
    }

    private static void clearPin(char[] pin) {
        if (pin != null) {
            Arrays.fill(pin, '\0');
        }
    }
}