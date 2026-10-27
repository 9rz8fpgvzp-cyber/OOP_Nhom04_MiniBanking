package vn.edu.ptit.oop.nhom04.ui;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JOptionPane;

import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.Customer;
import vn.edu.ptit.oop.nhom04.model.Transaction;
import vn.edu.ptit.oop.nhom04.model.TransactionType;
import vn.edu.ptit.oop.nhom04.service.BankingOperations;
import vn.edu.ptit.oop.nhom04.service.PinService;
import vn.edu.ptit.oop.nhom04.ui.dialogs.LoginDialog;
import vn.edu.ptit.oop.nhom04.ui.dialogs.TransactionDialog;
import vn.edu.ptit.oop.nhom04.util.ChartHelper;

/** Nối sự kiện Swing với Bank; phiên thuộc khách hàng, không thuộc STK. */
public class BankController {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MM/yyyy");
    private final MainWindow window;
    private final BankingOperations bank;
    private final PinService pins;
    private final Clock clock;
    private final ChartHelper charts;
    private boolean started;
    private String displayedCustomerId;

    public BankController(MainWindow window, BankingOperations bank,
                          PinService pins, Clock clock, ChartHelper charts) {
        this.window = Objects.requireNonNull(window, "window");
        this.bank = Objects.requireNonNull(bank, "bank");
        this.pins = Objects.requireNonNull(pins, "pins");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.charts = Objects.requireNonNull(charts, "charts");
    }

    public void start() {
        if (started) { return; }
        window.bindController(this);
        window.setCsvExportAction(new CsvExportAction(window, bank,
                window::getSelectedAccountNumber, this::showStatus));
        started = true;
        login();
    }

    private void clearSession() {
        pins.logout();
        displayedCustomerId = null;
        window.clearCustomerContent();
    }

    public void login() {
        clearSession();
        showStatus("Đăng nhập bằng mã khách hàng và PIN");
        try {
            if (!requestLogin()) {
                clearSession();
                showStatus("Chưa đăng nhập — chọn Đăng nhập để bắt đầu");
                return;
            }
            Optional<String> id = pins.getAuthenticatedCustomerId();
            Customer customer = bank.getCurrentCustomer()
                    .filter(c -> id.isPresent() && c.getCustomerId().equals(id.get()))
                    .orElseThrow(() -> new IllegalStateException("Phiên đăng nhập không hợp lệ."));
            List<vn.edu.ptit.oop.nhom04.model.AccountSummary> choices = bank.listAccounts();
            displayedCustomerId = customer.getCustomerId();
            window.showCustomer(customer);
            window.setAccountChoices(choices);
            window.clearAccountContent();
            showStatus("Xin chào " + customer.getFullName() + " — chọn tài khoản để xem chi tiết");
        } catch (RuntimeException ex) {
            clearSession();
            showStatus("Chưa thể đăng nhập");
            showError("Lỗi đăng nhập", messageOf(ex));
        }
    }

    private boolean hasCurrentSession() {
        return displayedCustomerId != null
                && pins.isAuthenticated(displayedCustomerId)
                && bank.getCurrentCustomer()
                       .map(c -> c.getCustomerId().equals(displayedCustomerId)).orElse(false);
    }

    private boolean requireCurrentSession() {
        if (hasCurrentSession()) { return true; }
        clearSession();
        showStatus("Phiên đã kết thúc — chọn Đăng nhập để tiếp tục");
        return false;
    }

    public void selectAccount(String accountNumber) {
        if (!requireCurrentSession()) { return; }
        window.clearAccountContent();
        if (accountNumber == null || accountNumber.isBlank()) {
            window.clearAccountSelection();
            showStatus("Chọn tài khoản để xem chi tiết");
            return;
        }
        // Chỉ chọn mục có trong danh sách của khách hàng; Bank vẫn là cổng quyền.
        if (!window.getSelectedAccountNumber().filter(accountNumber.trim()::equals).isPresent()) {
            window.clearAccountSelection();
            showError("Không thể chọn tài khoản", "Hãy chọn tài khoản trong danh sách của bạn.");
            return;
        }
        refreshSelectedAccount();
    }

    public void openTransaction(TransactionType operation) {
        if (!requireCurrentSession()) { return; }
        if (operation != TransactionType.DEPOSIT && operation != TransactionType.WITHDRAW
                && operation != TransactionType.TRANSFER) {
            showError("Không thể mở giao dịch", "Chỉ hỗ trợ nạp tiền, rút tiền và chuyển khoản.");
            return;
        }
        Optional<String> selected = window.getSelectedAccountNumber();
        if (selected.isEmpty()) {
            showError("Chưa chọn tài khoản", "Hãy chọn tài khoản trước khi giao dịch.");
            return;
        }
        try {
            bank.getAccount(selected.get());
        } catch (RuntimeException ex) {
            clearFailedSelection();
            showError("Không thể mở giao dịch", messageOf(ex));
            return;
        }
        try {
            showTransactionDialog(operation, selected.get(), this::refreshSelectedAccount);
        } catch (RuntimeException ex) {
            showError("Không thể mở cửa sổ giao dịch", messageOf(ex));
        }
    }

    private void clearFailedSelection() {
        if (!hasCurrentSession()) { clearSession(); return; }
        window.clearAccountSelection();
        window.clearAccountContent();
    }

    public void refreshSelectedAccount() {
        if (!requireCurrentSession()) { return; }
        Optional<String> selected = window.getSelectedAccountNumber();
        if (selected.isEmpty()) {
            window.clearAccountContent();
            showStatus("Chọn tài khoản để xem chi tiết");
            return;
        }
        Account account;
        List<Transaction> history;
        try {
            account = bank.getAccount(selected.get());
            history = bank.getTransactions(selected.get());
        } catch (RuntimeException ex) {
            clearFailedSelection();
            showError("Không thể tải tài khoản", messageOf(ex));
            return;
        }
        try {
            window.showAccount(account, YearMonth.now(clock));
            window.showTransactions(history);
            window.clearCharts();
            window.showCharts(charts.createBalanceChart(account, history),
                    charts.createDepositWithdrawalChart(history));
            window.setAccountActionsEnabled(true);
            showStatus("Đang xem tài khoản " + selected.get());
        } catch (RuntimeException ex) {
            window.clearAccountContent();
            window.enableSessionRecovery();
            showStatus("Không thể làm mới giao diện; chọn Xem → Làm mới để thử lại");
            showError("Lỗi cập nhật giao diện",
                    "Giao dịch đã thành công sẽ không bị hoàn tác. Không nhập lại giao dịch.\n" + messageOf(ex));
        }
    }

    public void logout() {
        // login tự xóa phiên/nội dung trước khi mở dialog; không gọi ngược logout.
        login();
    }

    public void simulateMonthEnd() {
        if (!requireCurrentSession()) { return; }
        String month = YearMonth.now(clock).format(MONTH_FORMAT);
        String result;
        RuntimeException failure = null;
        try {
            List<Transaction> interest = bank.simulateMonthEnd();
            result = "Đã chốt tháng " + month + ": " + interest.size() + " khoản lãi";
        } catch (RuntimeException ex) {
            failure = ex;
            result = "Chốt tháng " + month + " chưa hoàn tất; có thể đã cộng lãi một phần";
        }
        refreshSelectedAccount();
        showStatus(result);
        if (failure != null) { showError("Lỗi mô phỏng cuối tháng", result + ".\n" + messageOf(failure)); }
    }

    private void showStatus(String message) {
        window.showStatus("Mô phỏng: " + LocalDateTime.now(clock).format(TIME_FORMAT) + " | " + message);
    }

    private static String messageOf(RuntimeException ex) {
        return ex.getMessage() == null ? "Không thể hoàn tất thao tác." : ex.getMessage();
    }

    boolean requestLogin() { return new LoginDialog(window, pins).showDialog(); }
    void showTransactionDialog(TransactionType operation, String sourceNumber, Runnable afterSuccess) {
        new TransactionDialog(window, bank, operation, sourceNumber, afterSuccess).showDialog();
    }
    void showError(String title, String message) {
        JOptionPane.showMessageDialog(window, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
