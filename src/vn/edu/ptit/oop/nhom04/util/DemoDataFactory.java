package vn.edu.ptit.oop.nhom04.util;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.CheckingAccount;
import vn.edu.ptit.oop.nhom04.model.Customer;
import vn.edu.ptit.oop.nhom04.model.FixedTermAccount;
import vn.edu.ptit.oop.nhom04.model.SavingsAccount;
import vn.edu.ptit.oop.nhom04.service.PinService;

/** Tạo dữ liệu giả lập một lần khi khởi động, trước khi mở giao diện. */
public final class DemoDataFactory {
    // PIN giả phục vụ thuyết trình; xem bảng tài khoản trong docs/DEMO.md.
    private static final Map<String, String> DEMO_PINS = Map.of(
            "100001", "001234",
            "100002", "123456",
            "100003", "234567",
            "100004", "345678",
            "100005", "456789",
            "100006", "567890");

    private DemoDataFactory() {
    }

    public static List<Account> createAccounts(Clock clock) {
        Objects.requireNonNull(clock, "Đồng hồ mô phỏng không được null.");
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDate today = now.toLocalDate();
        LocalDateTime openedAt = now.minusMonths(3);

        Customer an = new Customer("KH001", "Nguyễn Văn An", "0900000001");
        Customer binh = new Customer("KH002", "Trần Thị Bình", "0900000002");
        Customer chi = new Customer("KH003", "Lê Minh Chi", "0900000003");

        SavingsAccount savingsAn = new SavingsAccount(
                "100001", an, new BigDecimal("10000000"), openedAt,
                new BigDecimal("0.06"));
        CheckingAccount checkingAn = new CheckingAccount(
                "100002", an, new BigDecimal("5000000"), openedAt,
                new BigDecimal("2000000"));
        SavingsAccount savingsBinh = new SavingsAccount(
                "100003", binh, new BigDecimal("8000000"), openedAt,
                new BigDecimal("0.05"));
        FixedTermAccount fixedBinh = new FixedTermAccount(
                "100004", binh, new BigDecimal("20000000"), openedAt,
                new BigDecimal("0.07"), today.plusMonths(3));
        CheckingAccount checkingChi = new CheckingAccount(
                "100005", chi, new BigDecimal("3000000"), openedAt,
                new BigDecimal("1000000"));
        FixedTermAccount fixedChi = new FixedTermAccount(
                "100006", chi, new BigDecimal("15000000"), openedAt,
                new BigDecimal("0.065"), today.minusDays(7));

        // Dùng nghiệp vụ model để lịch sử và số dư luôn khớp nhau.
        savingsAn.deposit(new BigDecimal("2000000"), now.minusDays(6));
        savingsAn.withdraw(new BigDecimal("500000"), now.minusDays(5));
        savingsAn.transferTo(checkingAn, new BigDecimal("1000000"), now.minusDays(4));
        checkingAn.withdraw(new BigDecimal("6500000"), now.minusDays(3));

        savingsBinh.deposit(new BigDecimal("1000000"), now.minusDays(5));
        savingsBinh.withdraw(new BigDecimal("1000000"), now.minusDays(3));
        fixedBinh.deposit(new BigDecimal("1000000"), now.minusDays(2));

        checkingChi.deposit(new BigDecimal("500000"), now.minusDays(2));
        checkingChi.transferTo(savingsBinh, new BigDecimal("250000"), now.minusDays(1));
        fixedChi.withdraw(new BigDecimal("500000"), now.minusDays(1));

        return List.of(savingsAn, checkingAn, savingsBinh, fixedBinh, checkingChi, fixedChi);
    }

    /** Đăng ký PIN cho các tài khoản mẫu; chỉ gọi một lần trên PinService mới. */
    public static void registerDemoPins(PinService pinService, List<Account> accounts) {
        Objects.requireNonNull(pinService, "Dịch vụ PIN không được null.");
        List<Account> snapshot = List.copyOf(accounts);
        Set<String> numbers = new HashSet<>();
        for (Account account : snapshot) {
            String number = account.getAccountNumber();
            if (!DEMO_PINS.containsKey(number)) {
                throw new IllegalArgumentException("Không có PIN mẫu cho tài khoản: " + number);
            }
            if (!numbers.add(number)) {
                throw new IllegalArgumentException("Trùng số tài khoản mẫu: " + number);
            }
        }

        for (Account account : snapshot) {
            char[] pin = DEMO_PINS.get(account.getAccountNumber()).toCharArray();
            try {
                pinService.registerPin(account.getAccountNumber(), pin);
            } finally {
                Arrays.fill(pin, '\0');
            }
        }
    }
}
