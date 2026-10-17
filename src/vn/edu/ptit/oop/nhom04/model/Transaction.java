package vn.edu.ptit.oop.nhom04.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class Transaction {
    private final String transactionId;
    private final String referenceId;
    private final String accountNumber;
    private final AccountType accountType;
    private final TransactionType type;
    private final TransactionDirection direction;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final BigDecimal balanceAfter;
    private final String description;

    public Transaction(String transactionId, String referenceId,
                       String accountNumber, AccountType accountType,
                       TransactionType type, TransactionDirection direction,
                       BigDecimal amount, LocalDateTime timestamp,
                       BigDecimal balanceAfter, String description) {
        requireText(transactionId, "transactionId");
        requireText(referenceId, "referenceId");
        requireText(accountNumber, "accountNumber");
        if (accountType == null || type == null || direction == null
                || timestamp == null || balanceAfter == null || description == null) {
            throw new IllegalArgumentException("Dữ liệu giao dịch không được để trống.");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Số tiền giao dịch phải lớn hơn 0.");
        }
        boolean isTransfer = (type == TransactionType.TRANSFER);
        if (isTransfer && direction == TransactionDirection.NONE) {
            throw new IllegalArgumentException("Chuyển khoản phải có chiều IN hoặc OUT.");
        }
        if (!isTransfer && direction != TransactionDirection.NONE) {
            throw new IllegalArgumentException("Chỉ chuyển khoản mới có chiều IN/OUT.");
        }
        this.transactionId = transactionId;
        this.referenceId = referenceId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.type = type;
        this.direction = direction;
        this.amount = amount;          // BigDecimal bất biến
        this.timestamp = timestamp;    // LocalDateTime bất biến
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " không được rỗng.");
        }
    }

    public String getTransactionId() { return transactionId; }
    public String getReferenceId() { return referenceId; }
    public String getAccountNumber() { return accountNumber; }
    public AccountType getAccountType() { return accountType; }
    public TransactionType getType() { return type; }
    public TransactionDirection getDirection() { return direction; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }
}