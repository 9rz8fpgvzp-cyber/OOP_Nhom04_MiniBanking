package vn.edu.ptit.oop.nhom04.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import vn.edu.ptit.oop.nhom04.exception.AccountLockedException;
import vn.edu.ptit.oop.nhom04.exception.AccountNotFoundException;
import vn.edu.ptit.oop.nhom04.exception.AuthenticationRequiredException;
import vn.edu.ptit.oop.nhom04.exception.InvalidAmountException;
import vn.edu.ptit.oop.nhom04.exception.InvalidTransferException;
import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.AccountSummary;
import vn.edu.ptit.oop.nhom04.model.Customer;
import vn.edu.ptit.oop.nhom04.model.Transaction;
import vn.edu.ptit.oop.nhom04.repository.AccountRepository;

public class Bank implements BankingOperations {
    private final AccountRepository repository;
    private final PinService pins;
    private final SimulationClock clock;
    private final TransferService transfers;
    private final MonthlyInterestService interestService;

    public Bank(AccountRepository repository, PinService pins, SimulationClock clock,
                TransferService transfers, MonthlyInterestService interestService) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.pins = Objects.requireNonNull(pins, "pins");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.transfers = Objects.requireNonNull(transfers, "transfers");
        this.interestService = Objects.requireNonNull(interestService, "interestService");
    }

    private LocalDateTime now() {
        return LocalDateTime.ofInstant(clock.instant(), clock.getZone());
    }

    private static String normalize(String code) {
        return code == null ? "" : code.trim();
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAmountException("Số tiền phải lớn hơn 0.");
        }
        if (amount.stripTrailingZeros().scale() > 0) {
            throw new InvalidAmountException("Số tiền phải là số đồng nguyên, không có phần lẻ.");
        }
    }

    private Optional<String> activeCustomerId() {
        Optional<String> id = pins.getAuthenticatedCustomerId();
        if (id.isEmpty() || pins.isLocked(id.get())) {
            return Optional.empty();
        }
        return id;
    }

    private Account find(String accountNumber) {
        return repository.findByNumber(normalize(accountNumber))
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy tài khoản."));
    }

    /** Kiểm tra quyền TRƯỚC khi trả dữ liệu hoặc đổi model. */
    private Account authorize(String accountNumber) {
        Account account = find(accountNumber);
        String ownerId = account.getOwner().getCustomerId();
        Optional<String> session = pins.getAuthenticatedCustomerId();

        if (session.isEmpty()) {
            // Không có phiên: phân biệt chủ STK đang bị khóa với chưa đăng nhập
            if (pins.isLocked(ownerId)) {
                throw new AccountLockedException("Tài khoản đang bị khóa. Vui lòng thử lại sau.");
            }
            throw new AuthenticationRequiredException("Vui lòng đăng nhập.");
        }
        if (!ownerId.equals(session.get())) {
            // Phiên của người khác: không lộ trạng thái khóa hay dữ liệu của chủ STK
            throw new AuthenticationRequiredException("Bạn không có quyền thao tác tài khoản này.");
        }
        pins.requireAuthenticated(ownerId); // phiên đúng chủ nhưng bị khóa -> AccountLockedException
        return account;
    }

    @Override
    public Optional<Customer> getCurrentCustomer() {
        return activeCustomerId().flatMap(id ->
                repository.findByCustomerId(id).stream().findFirst().map(Account::getOwner));
    }

    @Override
    public List<AccountSummary> listAccounts() {
        Optional<String> id = activeCustomerId();
        if (id.isEmpty()) {
            return List.of();
        }
        return repository.findByCustomerId(id.get()).stream()
                .map(a -> new AccountSummary(a.getAccountNumber(), a.getType()))
                .toList();
    }

    @Override
    public synchronized Account getAccount(String accountNumber) {
        return authorize(accountNumber);
    }

    @Override
    public synchronized List<Transaction> getTransactions(String accountNumber) {
        return List.copyOf(authorize(accountNumber).getTransactions());
    }

    @Override
    public synchronized Transaction deposit(String accountNumber, BigDecimal amount) {
        Account account = authorize(accountNumber);
        validateAmount(amount);
        return account.deposit(amount, now());
    }

    @Override
    public synchronized Transaction withdraw(String accountNumber, BigDecimal amount) {
        Account account = authorize(accountNumber);
        validateAmount(amount);
        return account.withdraw(amount, now());
    }

    @Override
    public synchronized List<Transaction> transfer(String sourceNumber, String targetNumber,
                                                   BigDecimal amount) {
        Account source = authorize(sourceNumber);
        validateAmount(amount);
        Account target = find(targetNumber);
        if (source.getAccountNumber().equals(target.getAccountNumber())) {
            throw new InvalidTransferException("Không thể chuyển cho chính tài khoản nguồn.");
        }
        return transfers.transfer(source, target, amount, now());
    }

    /** Tác vụ hệ thống: không cần PIN, xử lý mọi tài khoản trong repository. */
    @Override
    public synchronized List<Transaction> simulateMonthEnd() {
        YearMonth closingMonth = YearMonth.from(now());
        LocalDateTime closingTime = closingMonth.atEndOfMonth().atTime(23, 59, 59);
        List<Transaction> result =
                interestService.applyMonthEnd(repository.findAll(), closingMonth, closingTime);
        clock.advanceToNextMonth();
        return List.copyOf(result);
    }
}