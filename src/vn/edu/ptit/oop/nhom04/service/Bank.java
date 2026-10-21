package vn.edu.ptit.oop.nhom04.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vn.edu.ptit.oop.nhom04.exception.AccountNotFoundException;
import vn.edu.ptit.oop.nhom04.exception.InvalidAmountException;
import vn.edu.ptit.oop.nhom04.exception.InvalidTransferException;
import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.AccountSummary;
import vn.edu.ptit.oop.nhom04.model.Transaction;

public class Bank implements BankingOperations {
    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private final PinService pins;
    private final SimulationClock clock;
    private final TransferService transfers;
    private final MonthlyInterestService interestService;

    public Bank(List<Account> accounts, PinService pins, SimulationClock clock,
            TransferService transfers, MonthlyInterestService interestService) {
        if (accounts == null || pins == null || clock == null || transfers == null || interestService == null) {
            throw new IllegalArgumentException("Các tham số khởi tạo Bank không được null");
        }
        for (Account acc : accounts) {
            if (acc == null) {
                throw new IllegalArgumentException("Tài khoản trong danh sách không được null");
            }
            if (this.accounts.containsKey(acc.getAccountNumber())) {
                throw new IllegalArgumentException("Trùng số tài khoản: " + acc.getAccountNumber());
            }
            this.accounts.put(acc.getAccountNumber(), acc);
        }
        this.pins = pins;
        this.clock = clock;
        this.transfers = transfers;
        this.interestService = interestService;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.stripTrailingZeros().scale() > 0) {
            throw new InvalidAmountException("Số tiền giao dịch phải là số nguyên đồng lớn hơn 0");
        }
    }

    private Account findAccountOrThrow(String accountNumber) {
        if (accountNumber == null || !accounts.containsKey(accountNumber)) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản: " + accountNumber);
        }
        return accounts.get(accountNumber);
    }

    private LocalDateTime currentSimulationTime() {
        return LocalDateTime.ofInstant(clock.instant(), clock.getZone());
    }

    @Override
    public List<AccountSummary> listAccounts() {
        List<AccountSummary> summaries = new ArrayList<>();
        for (Account acc : accounts.values()) {
            summaries.add(new AccountSummary(acc.getAccountNumber(), acc.getType()));
        }
        return Collections.unmodifiableList(summaries);
    }

    @Override
    public Account getAccount(String accountNumber) {
        pins.requireAuthenticated(accountNumber);
        return findAccountOrThrow(accountNumber);
    }

    @Override
    public List<Transaction> getTransactions(String accountNumber) {
        pins.requireAuthenticated(accountNumber);
        Account acc = findAccountOrThrow(accountNumber);
        return acc.getTransactions();
    }

    @Override
    public synchronized Transaction deposit(String accountNumber, BigDecimal amount) {
        pins.requireAuthenticated(accountNumber);
        validateAmount(amount);
        Account acc = findAccountOrThrow(accountNumber);
        LocalDateTime time = currentSimulationTime();
        return acc.deposit(amount, time);
    }

    @Override
    public synchronized Transaction withdraw(String accountNumber, BigDecimal amount) {
        pins.requireAuthenticated(accountNumber);
        validateAmount(amount);
        Account acc = findAccountOrThrow(accountNumber);
        LocalDateTime time = currentSimulationTime();
        return acc.withdraw(amount, time);
    }

    @Override
    public synchronized List<Transaction> transfer(String sourceNumber, String targetNumber, BigDecimal amount) {
        if (sourceNumber == null || targetNumber == null) {
            throw new IllegalArgumentException("Số tài khoản không được null");
        }
        if (sourceNumber.equals(targetNumber)) {
            throw new InvalidTransferException("Tài khoản nguồn và đích không được trùng nhau");
        }
        pins.requireAuthenticated(sourceNumber);
        validateAmount(amount);

        Account source = findAccountOrThrow(sourceNumber);
        Account target = findAccountOrThrow(targetNumber);
        LocalDateTime time = currentSimulationTime();

        return transfers.transfer(source, target, amount, time);
    }

    @Override
    public synchronized List<Transaction> simulateMonthEnd() {
        LocalDateTime now = currentSimulationTime();
        YearMonth closingMonth = YearMonth.from(now);
        LocalDate lastDay = closingMonth.atEndOfMonth();
        LocalDateTime closingTime = lastDay.atTime(23, 59, 59);

        List<Transaction> interestTransactions = interestService.applyMonthEnd(
                new ArrayList<>(accounts.values()), closingMonth, closingTime);

        clock.advanceToNextMonth();
        return interestTransactions;
    }
}
