package vn.edu.ptit.oop.nhom04.exception;

public class AuthenticationRequiredException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AuthenticationRequiredException(String message) {
        super(message);
    }
}