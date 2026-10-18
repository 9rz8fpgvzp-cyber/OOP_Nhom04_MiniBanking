package vn.edu.ptit.oop.nhom04.exception;

public class InvalidAmountException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidAmountException(String message) {
        super(message);
    }
}
