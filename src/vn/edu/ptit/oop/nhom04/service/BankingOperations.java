package vn.edu.ptit.oop.nhom04.service;

import java.math.BigDecimal;
import java.util.List;
import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.AccountSummary;
import vn.edu.ptit.oop.nhom04.model.Transaction;

public interface BankingOperations {
    List<AccountSummary> listAccounts();

    Account getAccount(String accountNumber);

    List<Transaction> getTransactions(String accountNumber);

    Transaction deposit(String accountNumber, BigDecimal amount);

    Transaction withdraw(String accountNumber, BigDecimal amount);

    List<Transaction> transfer(String sourceNumber, String targetNumber, BigDecimal amount);

    List<Transaction> simulateMonthEnd();
}
