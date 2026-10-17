package vn.edu.ptit.oop.nhom04.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

// Trần Tiến Đạt
public abstract class Account {
    private final String accountNumber;
    private final Customer owner;
    private final BigDecimal openingBalance;
    private final LocalDateTime openedAt;

    // Thuộc tính bảo mật đóng gói tuyệt đối
    private BigDecimal balance;
    private final List<Transaction> transactions = new ArrayList<>();
    private final Set<YearMonth> processedInterestPeriods = new HashSet<>();

    protected Account(String accountNumber, Customer owner, BigDecimal openingBalance, LocalDateTime openedAt) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Số tài khoản không được để trống");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Chủ tài khoản không được để trống");
        }
        if (openingBalance == null || openingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số dư mở tài khoản phải lớn hơn hoặc bằng 0");
        }
        if (openedAt == null) {
            throw new IllegalArgumentException("Ngày mở tài khoản không được để trống");
        }

        this.accountNumber = accountNumber;
        this.owner = owner;
        this.openingBalance = openingBalance;
        this.balance = openingBalance;
        this.openedAt = openedAt;
    }

    public final String getAccountNumber() {
        return accountNumber;
    }

    public final Customer getOwner() {
        return owner;
    }

    public final BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public final BigDecimal getBalance() {
        return balance;
    }

    public final LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public abstract AccountType getType();

    public final List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    // Nạp tiền
    public final Transaction deposit(BigDecimal amount, LocalDateTime time) {
        validatePositiveAmount(amount);
        Objects.requireNonNull(time, "Thời gian giao dịch không được để trống");

        this.balance = this.balance.add(amount);

        String txId = UUID.randomUUID().toString();
        Transaction tx = new Transaction(
            txId, txId, this.accountNumber, this.getType(),
            TransactionType.DEPOSIT, TransactionDirection.NONE,
            amount, time, this.balance, "Nạp tiền vào tài khoản"
        );
        this.transactions.add(tx);
        return tx;
    }

    // Rút tiền
    public abstract Transaction withdraw(BigDecimal amount, LocalDateTime time);

    // Kiểm tra điều kiện để rút tiền từng loại tài khoản cụ thể
    protected abstract void validateWithdrawal(BigDecimal amount, LocalDate date);

    // Thực hiện trừ tiền và lưu lịch sử giao dịch sau khi kiểm tra tài khoản hợp lệ
    protected final Transaction withdrawAfterValidation(BigDecimal amount, LocalDateTime time) {
        validatePositiveAmount(amount);
        Objects.requireNonNull(time, "Thời gian giao dịch không được để trống");

        this.balance = this.balance.subtract(amount);
        onWithdrawalCommitted(time.toLocalDate());

        String txId = UUID.randomUUID().toString();
        Transaction tx = new Transaction(
            txId, txId, this.accountNumber, this.getType(),
            TransactionType.WITHDRAW, TransactionDirection.NONE,
            amount, time, this.balance, "Rút tiền từ tài khoản"
        );
        this.transactions.add(tx);
        return tx;
    }

    // Hook method: các lớp con (như SavingsAccount) ghi nhận số lần rút thành công
    protected void onWithdrawalCommitted(LocalDate date) {
        // Lớp con ghi đè nếu cần
    }

    // Chuyển khoản toàn vẹn (Atomic transfer)
    public final List<Transaction> transferTo(Account target, BigDecimal amount, LocalDateTime time) {
        if (target == null) {
            throw new IllegalArgumentException("Tài khoản đích không được để trống");
        }
        if (this.accountNumber.equals(target.getAccountNumber())) {
            throw new IllegalArgumentException("Không thể chuyển khoản cho chính tài khoản này");
        }
        validatePositiveAmount(amount);
        Objects.requireNonNull(time, "Thời gian chuyển khoản không được trống");

        // 1. Kiểm tra rút tiền tại nguồn
        this.validateWithdrawal(amount, time.toLocalDate());

        // 2. Snapshot trạng thái để rollback nếu có lỗi
        BigDecimal srcOldBalance = this.balance;
        int srcTxCount = this.transactions.size();
        BigDecimal targetOldBalance = target.balance;
        int targetTxCount = target.transactions.size();

        String refId = UUID.randomUUID().toString();
        List<Transaction> result = new ArrayList<>();

        try {
            // Trừ tiền nguồn
            this.balance = this.balance.subtract(amount);
            this.onWithdrawalCommitted(time.toLocalDate());
            Transaction outTx = new Transaction(
                UUID.randomUUID().toString(), refId, this.accountNumber, this.getType(),
                TransactionType.TRANSFER, TransactionDirection.OUT,
                amount, time, this.balance, "Chuyển tiền tới " + target.getAccountNumber()
            );
            this.transactions.add(outTx);

            // Cộng tiền đích
            target.balance = target.balance.add(amount);
            Transaction inTx = new Transaction(
                UUID.randomUUID().toString(), refId, target.getAccountNumber(), target.getType(),
                TransactionType.TRANSFER, TransactionDirection.IN,
                amount, time, target.balance, "Nhận tiền từ " + this.accountNumber
            );
            target.transactions.add(inTx);

            result.add(outTx);
            result.add(inTx);
            return Collections.unmodifiableList(result);
        } catch (Exception ex) {
            // Phục hồi nguyên trạng nếu có lỗi bất ngờ
            this.balance = srcOldBalance;
            while (this.transactions.size() > srcTxCount) {
                this.transactions.remove(this.transactions.size() - 1);
            }
            target.balance = targetOldBalance;
            while (target.transactions.size() > targetTxCount) {
                target.transactions.remove(target.transactions.size() - 1);
            }
            throw ex;
        }
    }

    /**
     * Cộng lãi cuối tháng (chỉ gọi bởi MonthlyInterestService).
     */
    public final Optional<Transaction> creditInterest(BigDecimal amount, YearMonth period, LocalDateTime time) {
        if (period == null || processedInterestPeriods.contains(period)) {
            return Optional.empty();
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            processedInterestPeriods.add(period);
            return Optional.empty();
        }
        Objects.requireNonNull(time, "Thời gian tính lãi không được null");

        this.balance = this.balance.add(amount);
        this.processedInterestPeriods.add(period);

        String txId = UUID.randomUUID().toString();
        Transaction tx = new Transaction(
            txId, txId, this.accountNumber, this.getType(),
            TransactionType.INTEREST, TransactionDirection.NONE,
            amount, time, this.balance, "Lãi kỳ " + period.toString()
        );
        this.transactions.add(tx);
        return Optional.of(tx);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền giao dịch phải lớn hơn 0");
        }
        // Kiểm tra tiền nguyên đồng theo quy ước v1
        if (amount.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Số tiền giao dịch không được có phần lẻ của đồng");
        }
    }
}