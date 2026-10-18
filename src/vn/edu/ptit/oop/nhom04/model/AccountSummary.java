package vn.edu.ptit.oop.nhom04.model;

/**
 * Bản tóm tắt tài khoản dùng để chọn tài khoản trước khi xác thực;
 * không chứa tên, số dư hay PIN.
 * Thuộc sở hữu của Người 2 theo API v1.
 */
public record AccountSummary(String accountNumber, AccountType type) {
}
