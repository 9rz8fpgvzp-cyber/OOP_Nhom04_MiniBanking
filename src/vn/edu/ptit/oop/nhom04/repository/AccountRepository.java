package vn.edu.ptit.oop.nhom04.repository;

import java.util.List;
import java.util.Optional;

import vn.edu.ptit.oop.nhom04.model.Account;

public interface AccountRepository {
    Optional<Account> findByNumber(String accountNumber);
    List<Account> findAll();
    List<Account> findByCustomerId(String customerId);
}
