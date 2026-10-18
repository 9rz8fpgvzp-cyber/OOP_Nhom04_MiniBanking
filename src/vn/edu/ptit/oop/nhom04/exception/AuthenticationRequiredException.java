package vn.edu.ptit.oop.nhom04.exception;

/**
 * Ngoại lệ ném ra khi thao tác yêu cầu xác thực PIN nhưng chưa đăng nhập
 * hoặc không đúng tài khoản hiện tại.
 * Thuộc sở hữu của Người 8 theo API v1.
 */
public class AuthenticationRequiredException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AuthenticationRequiredException(String message) {
        super(message);
    }
}
