package vn.edu.ptit.oop.nhom04.exception;

/**
 * Ngoại lệ ném ra khi tài khoản tiết kiệm vượt quá giới hạn số lần rút trong tháng.
 * Thuộc sở hữu của Người 3 theo API v1.
 */
public class WithdrawalLimitExceededException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public WithdrawalLimitExceededException(String message) {
        super(message);
    }
}
