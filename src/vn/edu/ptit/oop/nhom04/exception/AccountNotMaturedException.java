package vn.edu.ptit.oop.nhom04.exception;

/**
 * Ngoại lệ ném ra khi cố gắng rút tiền từ tài khoản kỳ hạn trước ngày đáo hạn.
 * Thuộc sở hữu của Người 4 theo API v1.
 */
public class AccountNotMaturedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AccountNotMaturedException(String message) {
        super(message);
    }
}
