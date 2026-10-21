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
import vn.edu.ptit.oop.nhom04.model.Transaction;
import vn.edu.ptit.oop.nhom04.model.TransactionType;
import vn.edu.ptit.oop.nhom04.service.BankingOperations;
import vn.edu.ptit.oop.nhom04.service.PinService;
import vn.edu.ptit.oop.nhom04.ui.dialogs.LoginDialog;
import vn.edu.ptit.oop.nhom04.ui.dialogs.TransactionDialog;
import vn.edu.ptit.oop.nhom04.util.ChartHelper;

/** Nối sự kiện Swing với cổng nghiệp vụ Bank và phiên PIN dùng chung. */
public class BankController {
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final MainWindow window;
    private final BankingOperations bank;
    private final PinService pins;
    private final Clock clock;
    private final ChartHelper charts;
    private boolean started;

    public BankController(MainWindow window, BankingOperations bank,
                          PinService pins, Clock clock, ChartHelper charts) {
        this.window = Objects.requireNonNull(window, "window");
        this.bank = Objects.requireNonNull(bank, "bank");
        this.pins = Objects.requireNonNull(pins, "pins");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.charts = Objects.requireNonNull(charts, "charts");
    }

    public void start() {
        if (started) {
            return;
        }
        pins.logout();
        window.bindController(this);
        window.setCsvExportAction(new CsvExportAction(window, bank,
                window::getSelectedAccountNumber, this::showStatus));
        window.setAccountChoices(bank.listAccounts());
        window.clearAccountContent();
        showStatus("Chọn tài khoản và nhập PIN để bắt đầu");
        started = true;
    }

    public void selectAccount(String accountNumber) {
        // Xóa quyền và toàn bộ dữ liệu cũ trước khi dialog mới được mở.
        pins.logout();
        window.clearAccountContent();
        if (accountNumber == null || accountNumber.isBlank()) {
            window.clearAccountSelection();
            showStatus("Chưa chọn tài khoản");
            return;
        }

        try {
            if (requestLogin(accountNumber) && pins.isAuthenticated(accountNumber)) {
                refreshSelectedAccount();
                return;
            }
            logout();
            showStatus("Chưa xác thực tài khoản " + accountNumber);
        } catch (RuntimeException ex) {
            logout();
            showError("Lỗi xác thực", messageOf(ex));
        }
    }

    public void openTransaction(TransactionType operation) {
        if (operation != TransactionType.DEPOSIT && operation != TransactionType.WITHDRAW
                && operation != TransactionType.TRANSFER) {
            showError("Không thể mở giao dịch", "Chỉ hỗ trợ nạp tiền, rút tiền và chuyển khoản.");
            return;
        }
        Optional<String> selected = window.getSelectedAccountNumber();
        if (selected.isEmpty()) {
            showError("Chưa chọn tài khoản", "Hãy chọn và xác thực tài khoản trước.");
            return;
        }
        try {
            // Lựa chọn trên UI không thay thế kiểm tra quyền tại Bank.
            bank.getAccount(selected.get());
        } catch (RuntimeException ex) {
            logout();
            showError("Không thể mở giao dịch", messageOf(ex));
            return;
        }
        try {
            showTransactionDialog(operation, selected.get(), this::refreshSelectedAccount);
        } catch (RuntimeException ex) {
            showError("Không thể mở cửa sổ giao dịch", messageOf(ex));
        }
    }

    public void refreshSelectedAccount() {
        Optional<String> selected = window.getSelectedAccountNumber();
        if (selected.isEmpty() || !pins.isAuthenticated(selected.get())) {
            logout();
            return;
        }

        Account account;
        List<Transaction> history;
        try {
            account = bank.getAccount(selected.get());
            history = bank.getTransactions(selected.get());
        } catch (RuntimeException ex) {
            logout();
            showError("Không thể tải tài khoản", messageOf(ex));
            return;
        }

        // Lỗi hiển thị sau khi Bank thành công không được báo thành lỗi giao dịch.
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
            showStatus("Không thể làm mới giao diện; hãy chọn Xem → Làm mới để thử lại");
            showError("Lỗi cập nhật giao diện",
                    "Lỗi này không hoàn tác giao dịch đã thành công. Không nhập lại giao dịch.\n"
                    + messageOf(ex));
        }
    }

    public void logout() {
        pins.logout();
        window.clearAccountSelection();
        window.clearAccountContent();
        showStatus("Đã đăng xuất — chọn tài khoản để xác thực");
    }

    public void simulateMonthEnd() {
        YearMonth closingMonth = YearMonth.now(clock);
        String result;
        RuntimeException failure = null;
        try {
            List<Transaction> interest = bank.simulateMonthEnd();
            result = "Đã chốt tháng " + closingMonth + ": " + interest.size() + " khoản lãi";
        } catch (RuntimeException ex) {
            failure = ex;
            result = "Chốt tháng " + closingMonth + " chưa hoàn tất; có thể đã cộng lãi một phần";
        }

        // Một số tài khoản có thể đã nhận lãi trước khi cả đợt báo lỗi.
        refreshSelectedAccount();
        showStatus(result);
        if (failure != null) {
            showError("Lỗi mô phỏng cuối tháng", result + ".\n" + messageOf(failure));
        }
    }

    private void showStatus(String message) {
        window.showStatus("Mô phỏng: " + LocalDateTime.now(clock).format(TIME_FORMAT)
                + " | " + message);
    }

    private static String messageOf(RuntimeException ex) {
        return ex.getMessage() == null ? "Không thể hoàn tất thao tác." : ex.getMessage();
    }

    // Các điểm mở dialog tách riêng để kiểm tra luồng điều phối với Bank thật.
    boolean requestLogin(String accountNumber) {
        return new LoginDialog(window, accountNumber, pins).showDialog();
    }

    void showTransactionDialog(TransactionType operation, String sourceNumber, Runnable afterSuccess) {
        new TransactionDialog(window, bank, operation, sourceNumber, afterSuccess).showDialog();
    }

    void showError(String title, String message) {
        JOptionPane.showMessageDialog(window, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
