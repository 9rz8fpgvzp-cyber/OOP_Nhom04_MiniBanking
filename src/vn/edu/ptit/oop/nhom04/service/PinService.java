package vn.edu.ptit.oop.nhom04.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import vn.edu.ptit.oop.nhom04.exception.AccountLockedException;
import vn.edu.ptit.oop.nhom04.exception.AccountNotFoundException;
import vn.edu.ptit.oop.nhom04.exception.AuthenticationRequiredException;

/**
 * Quản lý xác thực mã PIN và trạng thái khóa tài khoản.
 * Thuộc sở hữu của Người 8 theo API v1.
 */
public class PinService {
    private static final int MAX_FAILED_ATTEMPTS = 3;

    private static class PinRecord {
        private final String pinString; // Lưu trữ PIN chuỗi 6 ký tự
        private int failedAttempts = 0;
        private boolean locked = false;

        PinRecord(String pin) {
            this.pinString = pin;
        }
    }

    private final Map<String, PinRecord> pinDatabase = new HashMap<>();
    private String authenticatedAccountNumber = null;

    public PinService() {
    }

    private void validatePinFormat(char[] pin) {
        if (pin == null || pin.length != 6) {
            throw new IllegalArgumentException("Mã PIN phải có đúng 6 chữ số");
        }
        for (char c : pin) {
            if (!Character.isDigit(c)) {
                throw new IllegalArgumentException("Mã PIN chỉ được chứa ký tự số");
            }
        }
    }

    public void registerPin(String accountNumber, char[] pin) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Số tài khoản không được để trống");
        }
        validatePinFormat(pin);
        if (pinDatabase.containsKey(accountNumber)) {
            throw new IllegalArgumentException("Tài khoản đã đăng ký PIN, không thể ghi đè");
        }
        pinDatabase.put(accountNumber, new PinRecord(new String(pin)));
    }

    public boolean authenticate(String accountNumber, char[] pin) {
        // Trước mỗi lần authenticate, xóa phiên đăng nhập cũ
        logout();

        if (accountNumber == null || !pinDatabase.containsKey(accountNumber)) {
            throw new AccountNotFoundException("Tài khoản chưa đăng ký PIN: " + accountNumber);
        }

        PinRecord record = pinDatabase.get(accountNumber);
        if (record.locked) {
            throw new AccountLockedException("Tài khoản " + accountNumber + " đã bị khóa");
        }

        boolean validFormat = (pin != null && pin.length == 6);
        if (validFormat) {
            for (char c : pin) {
                if (!Character.isDigit(c)) {
                    validFormat = false;
                    break;
                }
            }
        }

        if (validFormat && Arrays.equals(pin, record.pinString.toCharArray())) {
            record.failedAttempts = 0;
            this.authenticatedAccountNumber = accountNumber;
            return true;
        }

        // Nhập sai hoặc sai định dạng: tăng số lần sai
        record.failedAttempts++;
        if (record.failedAttempts >= MAX_FAILED_ATTEMPTS) {
            record.locked = true;
            throw new AccountLockedException("Tài khoản " + accountNumber + " đã bị khóa do nhập sai PIN 3 lần");
        }
        return false;
    }

    public int getRemainingAttempts(String accountNumber) {
        if (accountNumber == null || !pinDatabase.containsKey(accountNumber)) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản: " + accountNumber);
        }
        PinRecord record = pinDatabase.get(accountNumber);
        return Math.max(0, MAX_FAILED_ATTEMPTS - record.failedAttempts);
    }

    public boolean isLocked(String accountNumber) {
        if (accountNumber == null || !pinDatabase.containsKey(accountNumber)) {
            return false;
        }
        return pinDatabase.get(accountNumber).locked;
    }

    public boolean isAuthenticated(String accountNumber) {
        return accountNumber != null && accountNumber.equals(authenticatedAccountNumber);
    }

    public Optional<String> getAuthenticatedAccountNumber() {
        return Optional.ofNullable(authenticatedAccountNumber);
    }

    public void requireAuthenticated(String accountNumber) {
        if (accountNumber == null || !pinDatabase.containsKey(accountNumber)) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản: " + accountNumber);
        }
        if (isLocked(accountNumber)) {
            throw new AccountLockedException("Tài khoản đã bị khóa: " + accountNumber);
        }
        if (!isAuthenticated(accountNumber)) {
            throw new AuthenticationRequiredException("Yêu cầu xác thực PIN cho tài khoản: " + accountNumber);
        }
    }

    public void logout() {
        this.authenticatedAccountNumber = null;
    }
}
