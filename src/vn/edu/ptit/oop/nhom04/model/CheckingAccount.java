package vn.edu.ptit.oop.nhom04.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import vn.edu.ptit.oop.nhom04.exception.InsufficientFundsException;

/**
 * Lớp đại diện cho Tài khoản Thanh toán (Có thấu chi).
 * Thuộc sở hữu của Người 3 — Khớp chính xác chữ ký thiết kế API v1.
 */
public class CheckingAccount extends Account {
    private final BigDecimal overdraftLimit; // Hạn mức thấu chi >= 0

    // Khởi tạo khớp chuẩn xác tên tham số thiết kế: (number, owner, openingBalance, openedAt, overdraftLimit)
    public CheckingAccount(String number, Customer owner, BigDecimal openingBalance,
                           LocalDateTime openedAt, BigDecimal overdraftLimit) {
        super(number, owner, openingBalance, openedAt);
        if (overdraftLimit == null || overdraftLimit.compareTo(BigDecimal.ZERO) < 0 || overdraftLimit.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Hạn mức thấu chi phải là số nguyên và không được nhỏ hơn 0");
        }
        this.overdraftLimit = overdraftLimit;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    /**
     * Khả dụng = balance + overdraftLimit (Cho phép số dư tài khoản chi tiêu âm trong hạn mức)
     */
    public BigDecimal getAvailableBalance() {
        return this.balance.add(this.overdraftLimit);
    }

    @Override
    public AccountType getType() {
        return AccountType.CHECKING;
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

        // Kiểm tra thấu chi biên nghiêm ngặt: Rút quá số dư khả dụng tổng hợp dù chỉ 1 đồng cũng chặn báo lỗi ngay
        if (amount.compareTo(getAvailableBalance()) > 0) {
            throw new InsufficientFundsException("Giao dịch thất bại: Vượt quá số dư khả dụng và hạn mức thấu chi cho phép");
        }
    }
}
