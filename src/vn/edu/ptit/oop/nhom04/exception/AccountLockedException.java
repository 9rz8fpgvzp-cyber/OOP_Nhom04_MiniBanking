package vn.edu.ptit.oop.nhom04.exception;

/**
 * Ngoại lệ ném ra khi tài khoản bị khóa do nhập sai PIN quá số lần quy định.
 * Thuộc sở hữu của Người 8 theo API v1.
 */
public class AccountLockedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AccountLockedException(String message) {
        super(message);
    }
}
