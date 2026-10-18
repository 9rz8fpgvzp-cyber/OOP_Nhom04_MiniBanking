package vn.edu.ptit.oop.nhom04.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Bản ghi giao dịch bất biến (immutable).
 * Thuộc sở hữu của Người 6 theo API v1.
 */
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
        if (transactionId == null || referenceId == null || accountNumber == null ||
                accountType == null || type == null || direction == null ||
                amount == null || timestamp == null || balanceAfter == null || description == null) {
            throw new IllegalArgumentException("Các trường thông tin của Transaction không được null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền giao dịch phải lớn hơn 0");
        }
        this.transactionId = transactionId;
        this.referenceId = referenceId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.type = type;
        this.direction = direction;
        this.amount = amount;
        this.timestamp = timestamp;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionDirection getDirection() {
        return direction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + transactionId + '\'' +
                ", refId='" + referenceId + '\'' +
                ", acc='" + accountNumber + '\'' +
                ", type=" + type +
                ", direction=" + direction +
                ", amount=" + amount +
                ", time=" + timestamp +
                ", balanceAfter=" + balanceAfter +
                ", desc='" + description + '\'' +
                '}';
    }
}
