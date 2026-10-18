package vn.edu.ptit.oop.nhom04.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Lớp trừu tượng đại diện cho một tài khoản ngân hàng.
 * Thuộc sở hữu của Người 2, phối hợp Người 5 cho transferTo.
 */
public abstract class Account {
    private final String accountNumber;
    private final Customer owner;
    private final BigDecimal openingBalance;
    private final LocalDateTime openedAt;
    protected BigDecimal balance;
    protected final List<Transaction> transactions = new ArrayList<>();
    private final Set<YearMonth> creditedPeriods = new HashSet<>();

    protected Account(String accountNumber, Customer owner,
                      BigDecimal openingBalance, LocalDateTime openedAt) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Số tài khoản không được để trống");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Chủ tài khoản không được null");
        }
        if (openingBalance == null || openingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số dư ban đầu phải >= 0");
        }
        if (openedAt == null) {
            throw new IllegalArgumentException("Thời điểm mở tài khoản không được null");
        }

        this.accountNumber = accountNumber;
        this.owner = owner;
        this.openingBalance = openingBalance;
        this.openedAt = openedAt;
        this.balance = openingBalance;
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
        return Collections.unmodifiableList(new ArrayList<>(transactions));
    }

    public final Transaction deposit(BigDecimal amount, LocalDateTime time) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Số tiền nạp phải là số nguyên dương");
        }
        if (time == null) {
            throw new IllegalArgumentException("Thời gian không được null");
        }

        this.balance = this.balance.add(amount);
        String txId = UUID.randomUUID().toString();
        Transaction tx = new Transaction(
                txId, txId, accountNumber, getType(),
                TransactionType.DEPOSIT, TransactionDirection.NONE,
                amount, time, this.balance, "Nạp tiền vào tài khoản"
        );
        this.transactions.add(tx);
        return tx;
    }

    public abstract Transaction withdraw(BigDecimal amount, LocalDateTime time);

    protected abstract void validateWithdrawal(BigDecimal amount, LocalDate date);

    protected final Transaction withdrawAfterValidation(BigDecimal amount, LocalDateTime time) {
        this.balance = this.balance.subtract(amount);
        String txId = UUID.randomUUID().toString();
        Transaction tx = new Transaction(
                txId, txId, accountNumber, getType(),
                TransactionType.WITHDRAW, TransactionDirection.NONE,
                amount, time, this.balance, "Rút tiền từ tài khoản"
        );
        this.transactions.add(tx);
        onWithdrawalCommitted(time.toLocalDate());
        return tx;
    }

    protected void onWithdrawalCommitted(LocalDate date) {
        // Mặc định không làm gì; SavingsAccount override để tính lượt rút
    }

    /**
     * Chuyển khoản toàn vẹn (atomic) sang tài khoản đích.
     */
    public final List<Transaction> transferTo(Account target, BigDecimal amount, LocalDateTime time) {
        if (target == null) {
            throw new IllegalArgumentException("Tài khoản đích không được null");
        }
        if (this.accountNumber.equals(target.getAccountNumber())) {
            throw new IllegalArgumentException("Không thể chuyển khoản cho chính mình");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Số tiền chuyển phải là số nguyên dương");
        }
        if (time == null) {
            throw new IllegalArgumentException("Thời gian không được null");
        }

        // 1. Kiểm tra điều kiện rút của nguồn
        validateWithdrawal(amount, time.toLocalDate());

        // 2. Snapshot trạng thái cũ để phục hồi nếu lỗi
        BigDecimal oldSourceBalance = this.balance;
        int oldSourceTxCount = this.transactions.size();
        BigDecimal oldTargetBalance = target.balance;
        int oldTargetTxCount = target.transactions.size();

        String refId = UUID.randomUUID().toString();
        String txOutId = UUID.randomUUID().toString();
        String txInId = UUID.randomUUID().toString();

        try {
            // 3. Thực hiện trừ nguồn và cộng đích
            this.balance = this.balance.subtract(amount);
            target.balance = target.balance.add(amount);

            Transaction txOut = new Transaction(
                    txOutId, refId, this.accountNumber, this.getType(),
                    TransactionType.TRANSFER, TransactionDirection.OUT,
                    amount, time, this.balance,
                    "Chuyển tiền đến " + target.getAccountNumber()
            );

            Transaction txIn = new Transaction(
                    txInId, refId, target.getAccountNumber(), target.getType(),
                    TransactionType.TRANSFER, TransactionDirection.IN,
                    amount, time, target.balance,
                    "Nhận tiền từ " + this.accountNumber
            );

            this.transactions.add(txOut);
            target.transactions.add(txIn);

            this.onWithdrawalCommitted(time.toLocalDate());

            return List.of(txOut, txIn);
        } catch (Exception e) {
            // Khôi phục hoàn toàn nếu có ngoại lệ bất ngờ
            this.balance = oldSourceBalance;
            while (this.transactions.size() > oldSourceTxCount) {
                this.transactions.remove(this.transactions.size() - 1);
            }
            target.balance = oldTargetBalance;
            while (target.transactions.size() > oldTargetTxCount) {
                target.transactions.remove(target.transactions.size() - 1);
            }
            throw e;
        }
    }

    /**
     * Cộng tiền lãi định kỳ (chỉ MonthlyInterestService gọi).
     */
    public final Optional<Transaction> creditInterest(BigDecimal amount, YearMonth period, LocalDateTime time) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số tiền lãi không hợp lệ");
        }
        if (period == null || time == null) {
            throw new IllegalArgumentException("Kỳ và thời gian không được null");
        }

        if (creditedPeriods.contains(period)) {
            return Optional.empty();
        }

        creditedPeriods.add(period);

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return Optional.empty();
        }

        this.balance = this.balance.add(amount);
        String txId = UUID.randomUUID().toString();
        Transaction tx = new Transaction(
                txId, txId, accountNumber, getType(),
                TransactionType.INTEREST, TransactionDirection.NONE,
                amount, time, this.balance, "INTEREST:" + period
        );
        this.transactions.add(tx);
        return Optional.of(tx);
    }
}
