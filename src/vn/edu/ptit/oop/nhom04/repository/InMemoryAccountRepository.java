package vn.edu.ptit.oop.nhom04.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import vn.edu.ptit.oop.nhom04.model.Account;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> accounts = new LinkedHashMap<>();

    public InMemoryAccountRepository(List<Account> initial) {
        Objects.requireNonNull(initial, "Danh sách tài khoản không được null.");
        for (Account account : initial) {
            if (account == null || account.getOwner() == null) {
                throw new IllegalArgumentException("Tài khoản hoặc chủ tài khoản không hợp lệ.");
            }
            String number = account.getAccountNumber();
            if (number == null || number.isBlank()) {
                throw new IllegalArgumentException("Số tài khoản không được rỗng.");
            }
            if (accounts.putIfAbsent(number.trim(), account) != null) {
                throw new IllegalArgumentException("Trùng số tài khoản: " + number);
            }
        }
    }

    @Override
    public Optional<Account> findByNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(accounts.get(accountNumber.trim()));
    }

    @Override
    public List<Account> findAll() {
        return List.copyOf(accounts.values());
    }

    @Override
    public List<Account> findByCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return List.of();
        }
        String id = customerId.trim();
        List<Account> result = new ArrayList<>();
        for (Account account : accounts.values()) {
            if (account.getOwner().getCustomerId().equals(id)) {
                result.add(account);
            }
        }
        return List.copyOf(result);
    }
}
