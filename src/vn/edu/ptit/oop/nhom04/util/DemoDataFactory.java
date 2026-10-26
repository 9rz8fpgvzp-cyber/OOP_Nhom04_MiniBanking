package vn.edu.ptit.oop.nhom04.util;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.CheckingAccount;
import vn.edu.ptit.oop.nhom04.model.Customer;
import vn.edu.ptit.oop.nhom04.model.FixedTermAccount;
import vn.edu.ptit.oop.nhom04.model.SavingsAccount;
import vn.edu.ptit.oop.nhom04.service.PinService;

/** Tạo dữ liệu mẫu v2 và đăng ký PIN theo khách hàng. */
public final class DemoDataFactory {
    private static final Map<String, String> DEMO_PIN_MAP = Map.of(
        "KH001", "001234",
        "KH002", "234567",
        "KH003", "456789"
    );

    private DemoDataFactory() {
    }

    public static List<Account> createAccounts(Clock clock) {
        Objects.requireNonNull(clock, "Clock không được null");
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDate today = now.toLocalDate();

        Customer c1 = new Customer("KH001", "Nguyễn Văn An", "0901234567");
        Customer c2 = new Customer("KH002", "Trần Thị Bình", "0912345678");
        Customer c3 = new Customer("KH003", "Lê Minh Chi", "0987654321");
        List<Account> accounts = new ArrayList<>();

        SavingsAccount a100001 = new SavingsAccount("100001", c1,
            new BigDecimal("10000000"), now.minusDays(10), new BigDecimal("0.06"));
        a100001.deposit(new BigDecimal("2000000"), now.minusDays(7));
        a100001.withdraw(new BigDecimal("1000000"), now.minusDays(4));
        accounts.add(a100001);

        CheckingAccount a100002 = new CheckingAccount("100002", c1,
            new BigDecimal("1000000"), now.minusDays(7), new BigDecimal("5000000"));
        a100002.withdraw(new BigDecimal("2500000"), now.minusDays(4));
        accounts.add(a100002);

        CheckingAccount a100003 = new CheckingAccount("100003", c2,
            new BigDecimal("5000000"), now.minusDays(8), new BigDecimal("2000000"));
        a100003.deposit(new BigDecimal("3000000"), now.minusDays(6));
        a100003.withdraw(new BigDecimal("2000000"), now.minusDays(3));
        accounts.add(a100003);

        SavingsAccount a100004 = new SavingsAccount("100004", c2,
            new BigDecimal("12000000"), now.minusDays(6), new BigDecimal("0.06"));
        accounts.add(a100004);

        FixedTermAccount a100005 = new FixedTermAccount("100005", c3,
            new BigDecimal("50000000"), now.minusDays(5), new BigDecimal("0.08"),
            today.plusMonths(3));
        accounts.add(a100005);

        FixedTermAccount a100006 = new FixedTermAccount("100006", c3,
            new BigDecimal("20000000"), now.minusDays(30), new BigDecimal("0.07"), today);
        accounts.add(a100006);

        return List.copyOf(accounts);
    }

    /** Đăng ký một lần cho mỗi customerId; không đăng ký lại theo từng STK. */
    public static void registerDemoPins(PinService pinService, List<Account> accounts) {
        Objects.requireNonNull(pinService, "PinService không được null");
        Objects.requireNonNull(accounts, "Danh sách tài khoản không được null");

        Map<String, Customer> customers = validateDemoData(accounts);
        for (String customerId : customers.keySet()) {
            char[] pin = DEMO_PIN_MAP.get(customerId).toCharArray();
            try {
                pinService.registerPin(customerId, pin);
            } finally {
                Arrays.fill(pin, '\0');
            }
        }
    }

    private static Map<String, Customer> validateDemoData(List<Account> accounts) {
        Map<String, Customer> customers = new LinkedHashMap<>();
        Map<String, Boolean> accountNumbers = new LinkedHashMap<>();
        for (Account account : accounts) {
            if (account == null || account.getOwner() == null) {
                throw new IllegalArgumentException("Dữ liệu mẫu không được chứa tài khoản/chủ null");
            }
            Customer customer = account.getOwner();
            String customerId = requireNonBlank(customer.getCustomerId(), "Mã khách hàng");
            requireNonBlank(customer.getFullName(), "Họ tên khách hàng");
            requireNonBlank(customer.getPhoneNumber(), "Số điện thoại khách hàng");
            String accountNumber = requireNonBlank(account.getAccountNumber(), "Số tài khoản");
            if (accountNumbers.put(accountNumber, Boolean.TRUE) != null) {
                throw new IllegalArgumentException("Trùng số tài khoản mẫu: " + accountNumber);
            }
            Customer previous = customers.putIfAbsent(customerId, customer);
            if (previous != null && (!previous.getFullName().equals(customer.getFullName())
                    || !previous.getPhoneNumber().equals(customer.getPhoneNumber()))) {
                throw new IllegalArgumentException("Thông tin khách hàng không nhất quán: " + customerId);
            }
            if (!DEMO_PIN_MAP.containsKey(customerId)) {
                throw new IllegalArgumentException("Chưa khai báo PIN mẫu cho khách hàng: " + customerId);
            }
        }
        if (customers.isEmpty()) {
            throw new IllegalArgumentException("Dữ liệu mẫu phải có khách hàng");
        }
        return Collections.unmodifiableMap(customers);
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " trong dữ liệu mẫu không được để trống");
        }
        return value.trim();
    }
}
