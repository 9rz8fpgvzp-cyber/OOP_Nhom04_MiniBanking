package vn.edu.ptit.oop.nhom04.exception;

/**
 * Ngoại lệ ném ra khi tài khoản không đủ số dư để thực hiện giao dịch.
 * Thuộc sở hữu của Người 3 theo API v1.
 */
public class InsufficientFundsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(String message) {
        super(message);
    }
}
