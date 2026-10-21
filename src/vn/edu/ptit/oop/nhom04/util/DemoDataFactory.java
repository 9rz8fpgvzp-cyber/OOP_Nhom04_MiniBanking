package vn.edu.ptit.oop.nhom04.util;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

/**
 * Lớp khởi tạo dữ liệu mẫu cho ứng dụng MiniBank.
 * Tạo danh sách khách hàng và các loại tài khoản (Tiết kiệm, Thanh toán, Kỳ hạn)
 * với lịch sử giao dịch ban đầu nhất quán với số dư và đăng ký mã PIN demo 6 chữ số.
 * 
 * Phụ trách: Thành viên 2 - Trần Tiến Đạt
 */
public final class DemoDataFactory {

    private static final Map<String, String> DEMO_PIN_MAP = new HashMap<>();

    static {
        // Mã PIN demo 6 chữ số cho từng tài khoản (có các mã chứa số 0 ở đầu để test BP01)
        DEMO_PIN_MAP.put("SA001", "012345"); // Tiết kiệm - bắt đầu bằng số 0
        DEMO_PIN_MAP.put("CA001", "123456"); // Thanh toán
        DEMO_PIN_MAP.put("CA002", "000000"); // Thanh toán thấu chi âm - toàn số 0
        DEMO_PIN_MAP.put("FA001", "654321"); // Kỳ hạn chưa đáo hạn
        DEMO_PIN_MAP.put("FA002", "099999"); // Kỳ hạn đã đến hạn - bắt đầu bằng số 0
    }

    private DemoDataFactory() {
        // Lớp tiện ích không khởi tạo đối tượng
    }

    /**
     * Tạo danh sách tài khoản mẫu ban đầu với dữ liệu nghiệp vụ nhất quán.
     * 
     * @param clock Nguồn thời gian mô phỏng dùng chung
     * @return Danh sách tài khoản mẫu (5 tài khoản, 3 khách hàng, đủ 3 loại tài khoản)
     */
    public static List<Account> createAccounts(Clock clock) {
        Objects.requireNonNull(clock, "Clock không được null");

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDate today = now.toLocalDate();

        // 1. Tạo 3 khách hàng mẫu
        Customer c1 = new Customer("KH001", "Nguyễn Văn An", "0901234567");
        Customer c2 = new Customer("KH002", "Trần Thị Bình", "0912345678");
        Customer c3 = new Customer("KH003", "Lê Hoàng Cường", "0987654321");

        List<Account> accounts = new ArrayList<>();

        // 2. Tài khoản tiết kiệm SA001 - lãi suất 6%/năm, có lịch sử nạp/rút
        LocalDateTime saOpenedAt = now.minusDays(10);
        SavingsAccount sa = new SavingsAccount(
            "SA001", c1,
            new BigDecimal("10000000"), // Số dư ban đầu: 10 triệu
            saOpenedAt,
            new BigDecimal("0.06")      // Lãi suất: 6%/năm
        );
        // Tạo lịch sử mẫu bằng nghiệp vụ thực để số dư khớp chính xác
        sa.deposit(new BigDecimal("2000000"), saOpenedAt.plusDays(3));  // Nạp 2tr lúc 7 ngày trước -> 12tr
        sa.withdraw(new BigDecimal("1000000"), saOpenedAt.plusDays(6)); // Rút 1tr lúc 4 ngày trước -> 11tr
        accounts.add(sa);

        // 3. Tài khoản thanh toán CA001 - có hạn mức thấu chi 2 triệu, số dư dương
        LocalDateTime ca1OpenedAt = now.minusDays(8);
        CheckingAccount ca1 = new CheckingAccount(
            "CA001", c2,
            new BigDecimal("5000000"),  // Số dư ban đầu: 5 triệu
            ca1OpenedAt,
            new BigDecimal("2000000")   // Hạn mức thấu chi: 2 triệu
        );
        ca1.deposit(new BigDecimal("3000000"), ca1OpenedAt.plusDays(2));  // Nạp 3tr -> 8tr
        ca1.withdraw(new BigDecimal("2000000"), ca1OpenedAt.plusDays(5)); // Rút 2tr -> 6tr
        accounts.add(ca1);

        // 4. Tài khoản thanh toán CA002 - demo trạng thái rút thấu chi âm số dư được phép
        LocalDateTime ca2OpenedAt = now.minusDays(7);
        CheckingAccount ca2 = new CheckingAccount(
            "CA002", c1,
            new BigDecimal("1000000"),  // Số dư ban đầu: 1 triệu
            ca2OpenedAt,
            new BigDecimal("5000000")   // Hạn mức thấu chi: 5 triệu
        );
        // Rút 2.500.000đ -> số dư trở thành -1.500.000đ (vẫn trong hạn mức thấu chi 5tr)
        ca2.withdraw(new BigDecimal("2500000"), ca2OpenedAt.plusDays(3));
        accounts.add(ca2);

        // 5. Tài khoản kỳ hạn FA001 - CHƯA đến ngày đáo hạn (đáo hạn sau 3 tháng)
        LocalDateTime fa1OpenedAt = now.minusDays(5);
        FixedTermAccount fa1 = new FixedTermAccount(
            "FA001", c3,
            new BigDecimal("50000000"), // 50 triệu
            fa1OpenedAt,
            new BigDecimal("0.08"),     // Lãi suất 8%/năm
            today.plusMonths(3)         // Chưa đáo hạn
        );
        accounts.add(fa1);

        // 6. Tài khoản kỳ hạn FA002 - ĐÃ ĐẾN HẠN (đáo hạn vào đúng ngày hôm nay)
        LocalDateTime fa2OpenedAt = now.minusDays(30);
        FixedTermAccount fa2 = new FixedTermAccount(
            "FA002", c2,
            new BigDecimal("20000000"), // 20 triệu
            fa2OpenedAt,
            new BigDecimal("0.07"),     // Lãi suất 7%/năm
            today                       // Đã đến hạn, cho phép rút nếu đủ tiền
        );
        accounts.add(fa2);

        return List.copyOf(accounts);
    }

    /**
     * Đăng ký mã PIN demo 6 chữ số cho từng tài khoản vào PinService.
     * Xóa sạch mảng char[] sau khi đăng ký để bảo đảm an toàn bộ nhớ.
     * 
     * @param pinService Dịch vụ quản lý mã PIN chung
     * @param accounts   Danh sách tài khoản cần đăng ký PIN
     */
    public static void registerDemoPins(PinService pinService, List<Account> accounts) {
        Objects.requireNonNull(pinService, "Dịch vụ PIN không được null.");
        Objects.requireNonNull(accounts, "Danh sách tài khoản không được null.");
        List<Account> snapshot = List.copyOf(accounts);

        // Kiểm tra cả danh sách trước khi đăng ký, tránh làm dở khi dữ liệu mẫu sai.
        Set<String> accountNumbers = new HashSet<>();
        for (Account account : snapshot) {
            String accNum = account.getAccountNumber();
            if (!DEMO_PIN_MAP.containsKey(accNum)) {
                throw new IllegalArgumentException("Không có PIN mẫu cho tài khoản: " + accNum);
            }
            if (!accountNumbers.add(accNum)) {
                throw new IllegalArgumentException("Trùng số tài khoản mẫu: " + accNum);
            }
        }

        for (Account account : snapshot) {
            String accNum = account.getAccountNumber();
            String pinStr = DEMO_PIN_MAP.get(accNum);
            char[] pinChars = pinStr.toCharArray();
            try {
                pinService.registerPin(accNum, pinChars);
            } finally {
                // Xóa mảng ký tự nhạy cảm khỏi bộ nhớ theo quy chuẩn bảo mật
                Arrays.fill(pinChars, '\0');
            }
        }
    }
}
