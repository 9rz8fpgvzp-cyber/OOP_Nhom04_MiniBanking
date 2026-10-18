package vn.edu.ptit.oop.nhom04.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import vn.edu.ptit.oop.nhom04.exception.InsufficientFundsException;

/**
 * Tài khoản thanh toán có hạn mức thấu chi (overdraftLimit).
 * Thuộc sở hữu của Người 3 theo API v1.
 */
public class CheckingAccount extends Account {
    private final BigDecimal overdraftLimit;

    public CheckingAccount(String number, Customer owner, BigDecimal openingBalance,
                           LocalDateTime openedAt, BigDecimal overdraftLimit) {
        super(number, owner, openingBalance, openedAt);
        if (overdraftLimit == null || overdraftLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Hạn mức thấu chi phải >= 0");
        }
        this.overdraftLimit = overdraftLimit;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public BigDecimal getAvailableBalance() {
        return this.balance.add(overdraftLimit);
    }

    @Override
    public AccountType getType() {
        return AccountType.CHECKING;
    }

    @Override
    protected void validateWithdrawal(BigDecimal amount, LocalDate date) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Số tiền rút phải là số nguyên dương");
        }
        BigDecimal available = getAvailableBalance();
        if (available.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Số dư khả dụng (kể cả thấu chi) không đủ: " + available + " < " + amount);
        }
    }

    @Override
    public Transaction withdraw(BigDecimal amount, LocalDateTime time) {
        if (time == null) {
            throw new IllegalArgumentException("Thời gian không được null");
        }
        validateWithdrawal(amount, time.toLocalDate());
        return withdrawAfterValidation(amount, time);
    }
}
