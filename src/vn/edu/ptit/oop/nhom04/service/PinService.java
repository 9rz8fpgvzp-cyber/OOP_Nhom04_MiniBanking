package vn.edu.ptit.oop.nhom04.service;

import java.time.Clock;
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
            customerId = normalize(customerId);
            if (customerId == null || customerId.isBlank()) {
                throw new IllegalArgumentException("Mã khách hàng không hợp lệ.");
            }

            if (credentials.containsKey(customerId)) {
                throw new IllegalArgumentException("Khách hàng đã đăng ký PIN, không thể ghi đè.");
            }

            PinCredential credential = new PinCredential(pin, authenticationClock);
            credentials.put(customerId, credential);
        } finally {
            clearPin(pin);
        }
    }

    public synchronized boolean authenticate(String customerId, char[] pin) {
        logout();

        try {
            customerId = normalize(customerId);
            PinCredential credential = getCredential(customerId);

            checkUnlocked(credential);

            if (credential.matches(pin)) {
                credential.resetFailedAttempts();
                authenticatedCustomerId = customerId;
                return true;
            }

            credential.recordFailedAttempt();

            checkUnlocked(credential);
            return false;
        } finally {
            clearPin(pin);
        }
    }

    public synchronized int getRemainingAttempts(String customerId) {
        PinCredential credential = getCredential(customerId);
        return credential.getRemainingAttempts();
    }

    public synchronized long getRemainingLockSeconds(String customerId) {
        PinCredential credential = getCredential(customerId);
        return credential.getRemainingLockSeconds();
    }

    public synchronized boolean isLocked(String customerId) {
        PinCredential credential = getCredential(customerId);
        return credential.isLocked();
    }

    public synchronized boolean isAuthenticated(String customerId) {
        customerId = normalize(customerId);
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

        return !credential.isLocked();
    }

    public synchronized Optional<String> getAuthenticatedCustomerId() {
        if (isAuthenticated(authenticatedCustomerId)) {
            return Optional.of(authenticatedCustomerId);
        }

        return Optional.empty();
    }

    public synchronized void requireAuthenticated(String customerId) {
        customerId = normalize(customerId);
        PinCredential credential = getCredential(customerId);

        checkUnlocked(credential);

        if (!customerId.equals(authenticatedCustomerId)) {
            throw new AuthenticationRequiredException( "Bạn cần đăng nhập khách hàng này.");
        }
    }

    public synchronized void logout() {
        authenticatedCustomerId = null;
    }

    private PinCredential getCredential(String customerId) {
        PinCredential credential = credentials.get(normalize(customerId));

        if (credential == null) {
            throw new AccountNotFoundException("Không tìm thấy khách hàng: " + customerId);
        }

        return credential;
    }

    private void checkUnlocked(PinCredential credential) {
        long seconds = credential.getRemainingLockSeconds();

        if (seconds > 0) {
            throw new AccountLockedException("Đăng nhập khách hàng bị khóa. Vui lòng thử lại sau "+ seconds + " giây.");
        }
    }

    private static void clearPin(char[] pin) {
        if (pin != null) {
            Arrays.fill(pin, '\0');
        }
    }

    private static String normalize(String customerId) {
        return customerId == null ? null : customerId.trim();
    }
}
