package vn.edu.ptit.oop.nhom04.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import vn.edu.ptit.oop.nhom04.exception.WithdrawalLimitExceededException;
import vn.edu.ptit.oop.nhom04.exception.InsufficientFundsException;

/**
 * Lớp đại diện cho Tài khoản Tiết kiệm.
 * Thuộc sở hữu của Người 3 — Khớp chính xác chữ ký thiết kế API v1.
 */
public class SavingsAccount extends Account implements InterestBearing {
    private final BigDecimal annualInterestRate; // Tỷ lệ lãi suất năm

    // Khởi tạo khớp chuẩn xác tên tham số thiết kế: (number, owner, openingBalance, openedAt, annualInterestRate)
    public SavingsAccount(String number, Customer owner, BigDecimal openingBalance,
                          LocalDateTime openedAt, BigDecimal annualInterestRate) {
        super(number, owner, openingBalance, openedAt);
        if (annualInterestRate == null || annualInterestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Lãi suất năm không được phép nhỏ hơn 0");
        }
        this.annualInterestRate = annualInterestRate;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    /**
     * Tính toán số lượt rút/chuyển tiền còn lại trong chu kỳ tháng được chỉ định (Tối đa 3 lần).
     * Ràng buộc v1: Đọc động từ lịch sử giao dịch, không tự sửa đổi thuộc tính bộ đếm cục bộ.
     */
    public int getRemainingWithdrawals(YearMonth month) {
        if (month == null) {
            throw new IllegalArgumentException("Tháng kiểm tra lượt rút không được null");
        }
        
        long successWithdrawCount = getTransactions().stream()
                .filter(tx -> tx.getType() == TransactionType.WITHDRAW || tx.getType() == TransactionType.TRANSFER)
                .filter(tx -> tx.getDirection() == TransactionDirection.NONE || tx.getDirection() == TransactionDirection.OUT)
                .filter(tx -> YearMonth.from(tx.getTimestamp()).equals(month))
                .count();

        int remaining = 3 - (int) successWithdrawCount;
        return Math.max(remaining, 0);
    }

    @Override
    public AccountType getType() {
        return AccountType.SAVINGS;
    }

    @Override
    public Transaction withdraw(BigDecimal amount, LocalDateTime time) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Số tiền rút phải là số nguyên dương");
        }
        if (time == null) {
            throw new IllegalArgumentException("Thời gian giao dịch không được null");
        }

        validateWithdrawal(amount, time.toLocalDate());
        return withdrawAfterValidation(amount, time);
    }

    @Override
    protected void validateWithdrawal(BigDecimal amount, LocalDate date) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Số tiền giao dịch không hợp lệ");
        }
        if (date == null) {
            throw new IllegalArgumentException("Ngày thực hiện giao dịch không được null");
        }

        // Kiểm tra biên số lượt rút định kỳ theo chu kỳ YearMonth (Thao tác lỗi không tiêu lượt)
        YearMonth currentPeriod = YearMonth.from(date);
        if (getRemainingWithdrawals(currentPeriod) <= 0) {
            throw new WithdrawalLimitExceededException("Tài khoản tiết kiệm đã hết lượt rút hoặc chuyển tiền trong tháng " + currentPeriod);
        }

        // Kiểm tra biên số dư khả dụng gốc
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Số dư tài khoản tiết kiệm không đủ để thực hiện giao dịch");
        }
    }

    /**
     * Hiện thực từ InterestBearing: Lãi tháng = số dư × tỷ lệ / 12.
     * Làm tròn về đồng bằng RoundingMode.HALF_UP; số dư <= 0 không tính lãi.
     */
    @Override
    public BigDecimal calculateMonthlyInterest() {
        if (this.balance.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(0, RoundingMode.HALF_UP);
        }
        
        return this.balance.multiply(this.annualInterestRate)
                .divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);
    }
}
