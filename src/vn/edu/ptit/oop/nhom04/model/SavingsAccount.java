package vn.edu.ptit.oop.nhom04.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import vn.edu.ptit.oop.nhom04.exception.InsufficientFundsException;
import vn.edu.ptit.oop.nhom04.exception.WithdrawalLimitExceededException;

/**
 * Tài khoản tiết kiệm có lãi suất và giới hạn tối đa 3 lần rút/tháng.
 * Thuộc sở hữu của Người 3 theo API v1.
 */
public class SavingsAccount extends Account implements InterestBearing {
    public static final int MAX_WITHDRAWALS_PER_MONTH = 3;

    private final BigDecimal annualInterestRate;
    private final Map<YearMonth, Integer> withdrawalCounts = new HashMap<>();

    public SavingsAccount(String number, Customer owner, BigDecimal openingBalance,
                          LocalDateTime openedAt, BigDecimal annualInterestRate) {
        super(number, owner, openingBalance, openedAt);
        if (annualInterestRate == null || annualInterestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Lãi suất năm phải >= 0");
        }
        this.annualInterestRate = annualInterestRate;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public int getRemainingWithdrawals(YearMonth month) {
        int used = withdrawalCounts.getOrDefault(month, 0);
        return Math.max(0, MAX_WITHDRAWALS_PER_MONTH - used);
    }

    @Override
    public AccountType getType() {
        return AccountType.SAVINGS;
    }

    @Override
    protected void validateWithdrawal(BigDecimal amount, LocalDate date) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Số tiền rút phải là số nguyên dương");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Số dư không đủ để rút: " + this.balance + " < " + amount);
        }
        YearMonth ym = YearMonth.from(date);
        if (getRemainingWithdrawals(ym) <= 0) {
            throw new WithdrawalLimitExceededException("Tài khoản tiết kiệm đã hết 3 lượt rút trong tháng " + ym);
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

    @Override
    protected void onWithdrawalCommitted(LocalDate date) {
        YearMonth ym = YearMonth.from(date);
        int current = withdrawalCounts.getOrDefault(ym, 0);
        withdrawalCounts.put(ym, current + 1);
    }

    @Override
    public BigDecimal calculateMonthlyInterest() {
        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return balance.multiply(annualInterestRate)
                .divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);
    }
}
