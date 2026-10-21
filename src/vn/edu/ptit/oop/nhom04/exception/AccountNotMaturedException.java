package vn.edu.ptit.oop.nhom04.exception;

public class AccountNotMaturedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public AccountNotMaturedException(String message) { super(message); }
}
