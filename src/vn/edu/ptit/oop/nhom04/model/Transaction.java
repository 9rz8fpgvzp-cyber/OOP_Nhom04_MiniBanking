package vn.edu.ptit.oop.nhom04.model;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {
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
    
    public Transaction(String transactionId, String referenceId, String accountNumber, AccountType accountType, TransactionType type, TransactionDirection direction, BigDecimal amount, LocalDateTime timestamp, BigDecimal balanceAfter, String description ){
        this.transactionId = Objects.requireNonNull(transactionId,"Mã giao dịch không được để trống");
        this.referenceId = Objects.requireNonNull(referenceId," Mã tham chiếu không được để trống");
        this.accountNumber = Objects.requireNonNull(accountNumber,"Số tài khoản không được để trống");
        this.accountType = Objects.requireNonNull(accountType,"Loại tài khoản không được để trống");
        this.type = Objects.requireNonNull(type,"Loại giao dịch không được để trống");
        this.direction = Objects.requireNonNull(direction,"Hướng giao dịch không được để trống");
        this.amount = Objects.requireNonNull(amount,"Số tiền giao dịch không được để trống");
        this.timestamp = Objects.requireNonNull(timestamp,"Thời gian giao dịch không được để trống");
        this.balanceAfter = Objects.requireNonNull(balanceAfter,"S  ố dư sau giao dịch không được để trống");
        this.description = description == null ? "" : description;

    }
    
    public String getTransactionId(){
        return transactionId;
    }

    public String getReferenceId(){
        return referenceId;
    }

    public String getAccountNumber(){
        return accountNumber;
    }

    public AccountType getAccountType(){
        return accountType;
    }

    public TransactionType getType(){
        return type;
    }

    public TransactionDirection getDirection(){
        return direction;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public LocalDateTime getTimestamp(){
        return timestamp;
    }

    public BigDecimal getBalanceAfter(){
        return balanceAfter;
    }

    public String getDescription(){
        return description;
    }   

    

}
