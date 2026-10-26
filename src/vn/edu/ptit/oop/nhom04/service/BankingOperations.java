package vn.edu.ptit.oop.nhom04.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.AccountSummary;
import vn.edu.ptit.oop.nhom04.model.Customer;
import vn.edu.ptit.oop.nhom04.model.Transaction;

/** Cổng nghiệp vụ duy nhất GUI được gọi (API v2). */
public interface BankingOperations {
    Optional<Customer> getCurrentCustomer();
    List<AccountSummary> listAccounts();
    Account getAccount(String accountNumber);
    List<Transaction> getTransactions(String accountNumber);
    Transaction deposit(String accountNumber, BigDecimal amount);
    Transaction withdraw(String accountNumber, BigDecimal amount);
    List<Transaction> transfer(String sourceNumber, String targetNumber, BigDecimal amount);
    List<Transaction> simulateMonthEnd();
}