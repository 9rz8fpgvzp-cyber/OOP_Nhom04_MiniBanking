package vn.edu.ptit.oop.nhom04.model;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import vn.edu.ptit.oop.nhom04.exception.AccountNotMaturedException;
import vn.edu.ptit.oop.nhom04.exception.InsufficientFundsException;
import vn.edu.ptit.oop.nhom04.exception.InvalidAmountException;

public class FixedTermAccount extends Account implements InterestBearing {
    private final BigDecimal annualInterestRate;
    private final LocalDate maturityDate;

    public FixedTermAccount(String number, Customer owner,
                            BigDecimal openingBalance, LocalDateTime openedAt,
                            BigDecimal annualInterestRate, LocalDate maturityDate) {
        super(number, owner, openingBalance, openedAt);
        if (annualInterestRate == null || annualInterestRate.signum() < 0) {
            throw new IllegalArgumentException("Lai suat lon hon 0 va khong rong");
        }
        this.maturityDate = Objects.requireNonNull(maturityDate, "Ngay dao han khong duoc rong");
        if (maturityDate.isBefore(openedAt.toLocalDate())) {
            throw new IllegalArgumentException("Ngay dao han sau phai sau ngay mo");
        }
        this.annualInterestRate = annualInterestRate;
    }

    @Override
    public AccountType getType() {
        return AccountType.FIXED_TERM;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    //Kiểm tra điều kiện rút
    @Override
    protected void validateWithdrawal(BigDecimal amount, LocalDate date) {
        validatePositiveWholeAmount(amount);
        Objects.requireNonNull(date, "Ngay giao khong duoc trong");

        if (date.isBefore(maturityDate)) {
            throw new AccountNotMaturedException(
                    "Chi duoc ruts tu ngay " + maturityDate);
        }
        if (getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("So du khong du");
        }
    }

    @Override
    public Transaction withdraw(BigDecimal amount, LocalDateTime time) {
        validatePositiveWholeAmount(amount);
        Objects.requireNonNull(time, "Thoi gian giao dich khong duoc de trong");
        validateWithdrawal(amount, time.toLocalDate());
        //Thực hiện rút tiền
        return withdrawAfterValidation(amount, time);
    }

    //Tính lãi
    @Override
    public BigDecimal calculateMonthlyInterest() {
        if (getBalance().signum() <= 0) {
            return BigDecimal.ZERO;
        }
        return getBalance().multiply(annualInterestRate)
                .divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);
    }

    private static void validatePositiveWholeAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAmountException("So tien phai lon hon 0");
        }
        if (amount.stripTrailingZeros().scale() > 0) {
            throw new InvalidAmountException("Phai la so nguyen");
        }
    }
}
