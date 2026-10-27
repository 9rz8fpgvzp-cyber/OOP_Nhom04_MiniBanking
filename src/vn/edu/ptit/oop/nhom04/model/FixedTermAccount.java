package vn.edu.ptit.oop.nhom04.model;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import vn.edu.ptit.oop.nhom04.exception.AccountNotMaturedException;
import vn.edu.ptit.oop.nhom04.exception.InsufficientFundsException;
import vn.edu.ptit.oop.nhom04.exception.InvalidAmountException;

public class FixedTermAccount extends Account implements InterestBearing {
    private static final DateTimeFormatter MATURITY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final BigDecimal annualInterestRate;
    private final LocalDate maturityDate;

    public FixedTermAccount(String number, Customer owner,
                            BigDecimal openingBalance, LocalDateTime openedAt,
                            BigDecimal annualInterestRate, LocalDate maturityDate) {
        super(number, owner, openingBalance, openedAt);
        if (annualInterestRate == null) {
            throw new IllegalArgumentException("Lãi suất năm không được để trống");
        }
        if (annualInterestRate.signum() < 0) {
            throw new IllegalArgumentException("Lãi suất năm không được âm");
        }
        this.maturityDate = Objects.requireNonNull(
                maturityDate, "Ngày đáo hạn không được để trống");
        if (maturityDate.isBefore(openedAt.toLocalDate())) {
            throw new IllegalArgumentException("Ngày đáo hạn không được trước ngày mở tài khoản");
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

    /* Dùng chung để kiểm tra rút tiền và chuyển tiền ra khỏi tài khoản. */
    @Override
    protected void validateWithdrawal(BigDecimal amount, LocalDate date) {
        validatePositiveWholeAmount(amount);
        Objects.requireNonNull(date, "Ngày giao dịch không được để trống");

        if (date.isBefore(maturityDate)) {
            throw new AccountNotMaturedException(
                    "Tài khoản chưa đáo hạn. Chỉ được rút hoặc chuyển tiền từ ngày "
                    + maturityDate.format(MATURITY_DATE_FORMAT));
        }
        if (getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Số dư không đủ để thực hiện giao dịch");
        }
    }

    @Override
    public Transaction withdraw(BigDecimal amount, LocalDateTime time) {
        validatePositiveWholeAmount(amount);
        Objects.requireNonNull(time, "Thời gian giao dịch không được để trống");
        validateWithdrawal(amount, time.toLocalDate());
        //Thực hiện rút tiền
        return withdrawAfterValidation(amount, time);
    }

    /*Tính lãi
    Lãi tháng = số dư hiện tại × lãi suất năm / 12*/
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
            throw new InvalidAmountException("Số tiền giao dịch phải lớn hơn 0");
        }
        if (amount.stripTrailingZeros().scale() > 0) {
            throw new InvalidAmountException("Số tiền giao dịch phải là số nguyên đồng");
        }
    }
}
