
package vn.edu.ptit.oop.nhom04.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import vn.edu.ptit.oop.nhom04.model.PinCredential;
import vn.edu.ptit.oop.nhom04.exception.AccountLockedException;
import vn.edu.ptit.oop.nhom04.exception.AuthenticationRequiredException;
import vn.edu.ptit.oop.nhom04.exception.AccountNotFoundException;

public class PinService {

    private final Map<String, PinCredential> credentials;
    private String authenticatedAccountNumber;

    public PinService() {
        credentials = new HashMap<>();
        authenticatedAccountNumber = null;
    }

    public synchronized void registerPin(String accountNumber, char[] pin) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Số tài khoản không hợp lệ.");
        }

        if (!PinCredential.isValidPin(pin)) {
            throw new IllegalArgumentException("PIN phải gồm đúng 6 chữ số.");
        }

        if (credentials.containsKey(accountNumber)) {
            throw new IllegalArgumentException("Tài khoản đã đăng ký PIN, không thể ghi đè.");
        }

        credentials.put(accountNumber, new PinCredential(pin));
    }

    public synchronized boolean authenticate(String accountNumber, char[] pin) {

        logout();

        PinCredential credential = getCredential(accountNumber);

        if (credential.isLocked()) {
            throw new AccountLockedException("Tài khoản đã bị khóa.");
        }

        if (credential.matches(pin)) {
            credential.resetFailedAttempts();
            authenticatedAccountNumber = accountNumber;
            return true;
        }

        credential.recordFailedAttempt();

        if (credential.isLocked()) {
            throw new AccountLockedException("Nhập sai PIN 3 lần. Tài khoản đã bị khóa.");
        }

        return false;
    }

    public synchronized int getRemainingAttempts(String accountNumber) {
        return getCredential(accountNumber).getRemainingAttempts();
    }

    public synchronized boolean isLocked(String accountNumber) {
        return getCredential(accountNumber).isLocked();
    }

    public synchronized boolean isAuthenticated(String accountNumber) {
        PinCredential credential = credentials.get(accountNumber);

        return credential != null && !credential.isLocked() && accountNumber.equals(authenticatedAccountNumber);
    }

    public synchronized Optional<String> getAuthenticatedAccountNumber() {
        if (authenticatedAccountNumber == null) {
            return Optional.empty();
        }

        if (!isAuthenticated(authenticatedAccountNumber)) {
            return Optional.empty();
        }

        return Optional.of(authenticatedAccountNumber);
    }


    public synchronized void requireAuthenticated(String accountNumber) {
        PinCredential credential = getCredential(accountNumber);

    	if (credential.isLocked()) {
    		throw new AccountLockedException("Tai khoan da bi khoa.");
    	}

    	if (!isAuthenticated(accountNumber)) {
    		throw new AuthenticationRequiredException("Ban can xac thuc PIN cua tai khoan nay.");
    	}
    }


    public synchronized void logout() {
        authenticatedAccountNumber = null;
    }

    private PinCredential getCredential(String accountNumber) {
        PinCredential credential = credentials.get(accountNumber);

        if (credential == null) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản: " + accountNumber);
        }

        return credential;
    }
}
