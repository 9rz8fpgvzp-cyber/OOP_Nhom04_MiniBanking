package vn.edu.ptit.oop.nhom04.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import vn.edu.ptit.oop.nhom04.exception.AccountNotMaturedException;
import vn.edu.ptit.oop.nhom04.exception.InsufficientFundsException;

/**
 * Tài khoản tiết kiệm có kỳ hạn.
 * Thuộc sở hữu của Người 4 theo API v1.
 */
public class FixedTermAccount extends Account implements InterestBearing {
    private final BigDecimal annualInterestRate;
    private final LocalDate maturityDate;

    public FixedTermAccount(String number, Customer owner, BigDecimal openingBalance,
                            LocalDateTime openedAt, BigDecimal annualInterestRate,
                            LocalDate maturityDate) {
        super(number, owner, openingBalance, openedAt);
        if (annualInterestRate == null || annualInterestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Lãi suất năm phải >= 0");
        }
        if (maturityDate == null || maturityDate.isBefore(openedAt.toLocalDate())) {
            throw new IllegalArgumentException("Ngày đáo hạn phải >= ngày mở tài khoản");
        }
        this.annualInterestRate = annualInterestRate;
        this.maturityDate = maturityDate;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    @Override
    public AccountType getType() {
        return AccountType.FIXED_TERM;
    }

    @Override
    protected void validateWithdrawal(BigDecimal amount, LocalDate date) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Số tiền rút phải là số nguyên dương");
        }
        if (date.isBefore(maturityDate)) {
            throw new AccountNotMaturedException("Tài khoản chưa đến ngày đáo hạn: " + maturityDate);
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Số dư không đủ: " + this.balance + " < " + amount);
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
    public BigDecimal calculateMonthlyInterest() {
        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return balance.multiply(annualInterestRate)
                .divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);
    }
}
