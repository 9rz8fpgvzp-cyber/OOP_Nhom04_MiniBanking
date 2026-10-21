package vn.edu.ptit.oop.nhom04.util;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import vn.edu.ptit.oop.nhom04.exception.AccountLockedException;
import vn.edu.ptit.oop.nhom04.exception.AuthenticationRequiredException;
import vn.edu.ptit.oop.nhom04.exception.WithdrawalLimitExceededException;
import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.AccountType;
import vn.edu.ptit.oop.nhom04.model.CheckingAccount;
import vn.edu.ptit.oop.nhom04.model.FixedTermAccount;
import vn.edu.ptit.oop.nhom04.model.SavingsAccount;
import vn.edu.ptit.oop.nhom04.model.Transaction;
import vn.edu.ptit.oop.nhom04.model.TransactionDirection;
import vn.edu.ptit.oop.nhom04.model.TransactionType;
import vn.edu.ptit.oop.nhom04.service.Bank;
import vn.edu.ptit.oop.nhom04.service.MonthlyInterestService;
import vn.edu.ptit.oop.nhom04.service.PinService;
import vn.edu.ptit.oop.nhom04.service.SimulationClock;
import vn.edu.ptit.oop.nhom04.service.TransferService;

/** Kiểm tra độc lập, không cần thư viện kiểm thử hoặc mở giao diện. */
public final class DemoDataFactoryTest {
    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final Instant START = Instant.parse("2026-10-21T02:00:00Z");

    public static void main(String[] args) throws Exception {
        checkDataAcrossClocks();
        checkFactoryIsolation();
        checkPinRegistration();
        checkInvalidRegistrationAndCleanup();
        checkBankAndCsvIntegration();
        System.out.println("DemoDataFactoryTest: PASS");
    }

    private static void checkDataAcrossClocks() {
        List<Clock> clocks = List.of(
                Clock.fixed(START, ZONE),
                Clock.fixed(Instant.parse("2027-01-01T00:00:00Z"), ZoneId.of("UTC")),
                Clock.fixed(Instant.parse("2024-03-01T00:00:00Z"), ZONE),
                Clock.fixed(Instant.parse("2026-10-31T17:30:00Z"), ZONE),
                Clock.fixed(START, ZoneId.of("America/Los_Angeles")));
        for (Clock clock : clocks) {
            LocalDateTime now = LocalDateTime.now(clock);
            List<Account> accounts = DemoDataFactory.createAccounts(clock);
            check(accounts.size() >= 4 && accounts.size() <= 6, "Phải có 4–6 tài khoản.");
            Set<String> numbers = new HashSet<>();
            Set<String> customers = new HashSet<>();
            Map<AccountType, Integer> types = new EnumMap<>(AccountType.class);
            Map<String, List<Transaction>> transfers = new HashMap<>();
            boolean hasMatured = false;
            boolean hasUnmatured = false;
            BigDecimal total = BigDecimal.ZERO;
            for (Account account : accounts) {
                check(numbers.add(account.getAccountNumber()), "Số tài khoản bị trùng.");
                customers.add(account.getOwner().getCustomerId());
                types.merge(account.getType(), 1, Integer::sum);
                total = total.add(account.getBalance());
                List<Transaction> history = account.getTransactions();
                check(!history.isEmpty(), "Mỗi tài khoản mẫu cần có lịch sử.");
                BigDecimal balance = account.getOpeningBalance();
                LocalDateTime previous = account.getOpenedAt();
                for (Transaction transaction : history) {
                    check(transaction.getAccountNumber().equals(account.getAccountNumber()),
                            "Lịch sử lẫn tài khoản.");
                    check(transaction.getAccountType() == account.getType(), "Sai loại tài khoản.");
                    check(!transaction.getTimestamp().isBefore(previous)
                                    && !transaction.getTimestamp().isAfter(now),
                            "Lịch sử sai thứ tự hoặc nằm sau thời gian mô phỏng.");
                    previous = transaction.getTimestamp();
                    boolean outgoing = transaction.getType() == TransactionType.WITHDRAW
                            || transaction.getDirection() == TransactionDirection.OUT;
                    balance = outgoing ? balance.subtract(transaction.getAmount())
                            : balance.add(transaction.getAmount());
                    check(balance.compareTo(transaction.getBalanceAfter()) == 0,
                            "Số dư sau giao dịch không khớp lịch sử.");
                    if (transaction.getType() == TransactionType.TRANSFER) {
                        transfers.computeIfAbsent(transaction.getReferenceId(), key -> new ArrayList<>())
                                .add(transaction);
                    }
                }
                check(balance.compareTo(account.getBalance()) == 0, "Số dư cuối không khớp.");
                if (account instanceof FixedTermAccount fixed) {
                    LocalDate today = now.toLocalDate();
                    hasMatured |= !fixed.getMaturityDate().isAfter(today);
                    hasUnmatured |= fixed.getMaturityDate().isAfter(today);
                    check(!fixed.getMaturityDate().isBefore(fixed.getOpenedAt().toLocalDate()),
                            "Đáo hạn trước ngày mở.");
                }
            }
            check(customers.size() >= 3, "Thiếu khách hàng mẫu.");
            check(types.size() == 3, "Thiếu loại tài khoản.");
            check(hasMatured && hasUnmatured, "Cần cả kỳ hạn đã/chưa đáo hạn.");
            check(total.compareTo(new BigDecimal("57000000")) == 0, "Tổng số dư sai hướng dẫn demo.");
            for (List<Transaction> pair : transfers.values()) {
                check(pair.size() == 2, "Chuyển khoản phải có hai phía.");
                Transaction first = pair.get(0);
                Transaction second = pair.get(1);
                check(first.getAmount().compareTo(second.getAmount()) == 0
                                && first.getTimestamp().equals(second.getTimestamp())
                                && first.getDirection() != second.getDirection()
                                && !first.getAccountNumber().equals(second.getAccountNumber()),
                        "Hai phía chuyển khoản không khớp.");
            }
            reject(UnsupportedOperationException.class, accounts::clear);
        }
    }

    private static void checkFactoryIsolation() {
        Clock clock = Clock.fixed(START, ZONE);
        List<Account> first = DemoDataFactory.createAccounts(clock);
        List<Account> second = DemoDataFactory.createAccounts(clock);
        first.get(0).deposit(BigDecimal.ONE, LocalDateTime.now(clock));
        check(first.get(0) != second.get(0), "Hai lần tạo không được dùng chung model.");
        check(second.get(0).getBalance().compareTo(new BigDecimal("10500000")) == 0,
                "Thay đổi bộ dữ liệu này làm đổi bộ dữ liệu khác.");
        CountingClock counting = new CountingClock();
        DemoDataFactory.createAccounts(counting);
        check(counting.reads == 1, "Phải lấy một mốc thời gian chung cho cả bộ mẫu.");
        reject(NullPointerException.class, () -> DemoDataFactory.createAccounts(null));
    }

    private static void checkPinRegistration() {
        List<Account> accounts = DemoDataFactory.createAccounts(Clock.fixed(START, ZONE));
        RecordingPins pins = new RecordingPins();
        DemoDataFactory.registerDemoPins(pins, accounts);
        check(pins.getAuthenticatedAccountNumber().isEmpty(), "Không được tự đăng nhập.");
        Map<String, String> expected = Map.of(
                "100001", "001234", "100002", "123456", "100003", "234567",
                "100004", "345678", "100005", "456789", "100006", "567890");
        for (Account account : accounts) {
            char[] pin = expected.get(account.getAccountNumber()).toCharArray();
            try {
                check(pins.authenticate(account.getAccountNumber(), pin), "PIN không khớp hướng dẫn.");
            } finally {
                Arrays.fill(pin, '\0');
            }
        }
        pins.logout();
        check(pins.inputs.size() == accounts.size(), "Phải đăng ký PIN cho từng tài khoản.");
        checkCleared(pins.inputs);
        pins.authenticate("100001", "999999".toCharArray());
        pins.authenticate("100001", "999999".toCharArray());
        reject(AccountLockedException.class,
                () -> pins.authenticate("100001", "999999".toCharArray()));
        reject(IllegalArgumentException.class, () -> DemoDataFactory.registerDemoPins(pins, accounts));
        check(pins.isLocked("100001"), "Đăng ký lại không được xóa khóa tài khoản.");
        checkCleared(pins.inputs);
    }

    private static void checkInvalidRegistrationAndCleanup() {
        List<Account> accounts = DemoDataFactory.createAccounts(Clock.fixed(START, ZONE));
        RecordingPins pins = new RecordingPins();
        reject(NullPointerException.class, () -> DemoDataFactory.registerDemoPins(null, accounts));
        reject(NullPointerException.class, () -> DemoDataFactory.registerDemoPins(pins, null));
        reject(NullPointerException.class,
                () -> DemoDataFactory.registerDemoPins(pins, Arrays.asList(accounts.get(0), null)));
        reject(IllegalArgumentException.class,
                () -> DemoDataFactory.registerDemoPins(pins, List.of(accounts.get(0), accounts.get(0))));
        Account unknown = new CheckingAccount("999999", accounts.get(0).getOwner(),
                BigDecimal.ZERO, LocalDateTime.now(Clock.fixed(START, ZONE)), BigDecimal.ZERO);
        reject(IllegalArgumentException.class,
                () -> DemoDataFactory.registerDemoPins(pins, List.of(accounts.get(0), unknown)));
        check(pins.inputs.isEmpty(), "Dữ liệu đầu vào sai phải bị chặn trước khi đăng ký.");
        RecordingPins failing = new RecordingPins();
        failing.fail = true;
        reject(IllegalStateException.class, () -> DemoDataFactory.registerDemoPins(failing, accounts));
        check(failing.inputs.size() == 1, "Kiểm tra lỗi dịch vụ PIN chưa chạy.");
        checkCleared(failing.inputs);
    }

    private static void checkBankAndCsvIntegration() throws Exception {
        SimulationClock clock = new SimulationClock(START, ZONE);
        List<Account> accounts = DemoDataFactory.createAccounts(clock);
        PinService pins = new PinService();
        DemoDataFactory.registerDemoPins(pins, accounts);
        Bank bank = new Bank(accounts, pins, clock, new TransferService(), new MonthlyInterestService());
        check(bank.listAccounts().size() == 6, "Bank không nhận đủ tài khoản.");
        reject(AuthenticationRequiredException.class, () -> bank.getAccount("100001"));
        check(pins.authenticate("100001", "001234".toCharArray()), "Không đăng nhập được PIN có số 0 đầu.");
        SavingsAccount source = (SavingsAccount) bank.getAccount("100001");
        check(source.getRemainingWithdrawals(YearMonth.from(LocalDateTime.now(clock))) == 1,
                "Sai lượt Savings còn lại tại mốc demo.");
        bank.transfer("100001", "100002", new BigDecimal("1000"));
        reject(WithdrawalLimitExceededException.class, () -> bank.withdraw("100001", BigDecimal.ONE));
        List<Transaction> history = bank.getTransactions("100001");
        Path csv = Files.createTempFile("minibank-demo-statement-", ".csv");
        try {
            int rows = new CsvStatementExporter().export(csv, "100001", history);
            check(rows == history.size(), "Xuất CSV sai số dòng.");
            check(Files.readAllLines(csv).size() == rows + 1, "CSV thiếu dòng hoặc header.");
        } finally {
            Files.deleteIfExists(csv);
        }
        BigDecimal before = totalBalance(accounts);
        pins.logout();
        List<Transaction> interest = bank.simulateMonthEnd();
        check(interest.size() == 4, "Cuối tháng phải có lãi cho 2 Savings và 2 FixedTerm.");
        BigDecimal credited = interest.stream().map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        check(totalBalance(accounts).compareTo(before.add(credited)) == 0, "Tổng số dư sau cộng lãi sai.");
        check(LocalDateTime.now(clock).equals(LocalDateTime.of(2026, 11, 1, 0, 0)),
                "Đồng hồ chưa chuyển đúng đầu tháng.");
        check(source.getRemainingWithdrawals(YearMonth.of(2026, 11)) == 3,
                "Savings chưa có lại lượt ở tháng mới.");
    }

    private static BigDecimal totalBalance(List<Account> accounts) {
        return accounts.stream().map(Account::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static void checkCleared(List<char[]> inputs) {
        for (char[] input : inputs) {
            for (char value : input) {
                check(value == '\0', "Mảng PIN chưa được xóa sau khi đăng ký.");
            }
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void reject(Class<? extends RuntimeException> expected, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException ex) {
            if (expected.isInstance(ex)) {
                return;
            }
            throw new AssertionError("Sai loại lỗi, cần " + expected.getSimpleName(), ex);
        }
        throw new AssertionError("Chưa từ chối trường hợp cần " + expected.getSimpleName());
    }

    private static final class RecordingPins extends PinService {
        private final List<char[]> inputs = new ArrayList<>();
        private boolean fail;

        @Override
        public synchronized void registerPin(String accountNumber, char[] pin) {
            inputs.add(pin);
            if (fail) {
                throw new IllegalStateException("Lỗi dịch vụ PIN giả lập.");
            }
            super.registerPin(accountNumber, pin);
        }
    }

    private static final class CountingClock extends Clock {
        private int reads;

        @Override
        public ZoneId getZone() {
            return ZONE;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return Clock.fixed(START, zone);
        }

        @Override
        public Instant instant() {
            reads++;
            return START.plusSeconds(reads - 1L);
        }
    }
}
