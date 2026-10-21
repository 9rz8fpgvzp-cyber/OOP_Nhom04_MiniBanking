package vn.edu.ptit.oop.nhom04.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.Transaction;
import vn.edu.ptit.oop.nhom04.model.TransactionType;
import vn.edu.ptit.oop.nhom04.service.Bank;
import vn.edu.ptit.oop.nhom04.service.MonthlyInterestService;
import vn.edu.ptit.oop.nhom04.service.PinService;
import vn.edu.ptit.oop.nhom04.service.SimulationClock;
import vn.edu.ptit.oop.nhom04.service.StatisticsService;
import vn.edu.ptit.oop.nhom04.service.TransferService;
import vn.edu.ptit.oop.nhom04.ui.dialogs.LoginDialog;
import vn.edu.ptit.oop.nhom04.ui.dialogs.TransactionDialog;
import vn.edu.ptit.oop.nhom04.util.ChartHelper;
import vn.edu.ptit.oop.nhom04.util.DemoDataFactory;

/** Chạy trên EDT với Bank thật; cần màn hình đồ họa, không chạy headless. */
public final class BankControllerTest {
    private static int checks;
    private static final List<Timer> TIMERS = new ArrayList<>();
    private static Throwable asynchronousFailure;

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                testSessionAndTransactions();
                testPartialMonthEnd();
                testActualDialogsAndCsv();
            } catch (Exception ex) {
                throw new AssertionError(ex);
            } finally {
                TIMERS.forEach(Timer::stop);
                for (Window window : Window.getWindows()) {
                    window.dispose();
                }
            }
        });
        if (asynchronousFailure != null) {
            throw new AssertionError("Tự động thao tác dialog thất bại", asynchronousFailure);
        }
        vn.edu.ptit.oop.nhom04.Main.main(new String[0]);
        SwingUtilities.invokeAndWait(() -> {
            try {
                MainWindow main = Arrays.stream(Window.getWindows())
                        .filter(w -> w instanceof MainWindow && w.isShowing())
                        .map(w -> (MainWindow) w).findFirst().orElseThrow();
                check(find(main, JList.class, c -> "accountList".equals(c.getName())).getModel().getSize() == 6,
                        "Main thật khởi tạo dữ liệu và controller");
                check(main.getSelectedAccountNumber().isEmpty(), "Main thật không tự đăng nhập");
            } finally {
                for (Window window : Window.getWindows()) { window.dispose(); }
            }
        });
        System.out.println("BankControllerTest: PASS (" + checks + " checks)");
    }

    private static void testSessionAndTransactions() {
        Fixture f = new Fixture(new MonthlyInterestService());
        ScriptController c = f.controller;
        check(f.list().getModel().getSize() == 6, "Nạp 6 AccountSummary");
        check(f.window.getSelectedAccountNumber().isEmpty(), "Không tự chọn tài khoản đầu");
        check(f.pins.getAuthenticatedAccountNumber().isEmpty(), "Không tự đăng nhập");
        f.checkBlank();
        check(!f.button("Nạp tiền").isEnabled(), "Chặn giao dịch trước PIN");
        check(f.button("Cuối tháng").isEnabled(), "Cuối tháng không yêu cầu PIN");
        c.start();
        check(f.list().getModel().getSize() == 6, "start lần hai không nhân đôi dữ liệu");
        check(f.button("Xuất CSV").getAction()
                == f.window.getJMenuBar().getMenu(0).getItem(3).getAction(), "Menu và toolbar dùng chung CsvExportAction");

        f.choose(0);
        check(f.pins.isAuthenticated("100001"), "PIN xác thực đúng nguồn");
        check(f.table().getRowCount() == f.accounts.get(0).getTransactions().size(), "Hiện lịch sử sau PIN");
        check(f.hasLabel("Nguyễn Văn An"), "Hiện chủ sở hữu sau PIN");
        check(f.charts().getComponentCount() == 2, "Nhúng hai biểu đồ");
        check(f.button("Nạp tiền").isEnabled(), "Mở thao tác sau PIN");
        Component oldChart = f.charts().getComponent(0);
        int rows = f.table().getRowCount();
        BigDecimal before = f.accounts.get(0).getBalance();
        f.button("Nạp tiền").doClick();
        check(f.accounts.get(0).getBalance().compareTo(before.add(new BigDecimal("1000"))) == 0, "Nạp qua Bank một lần");
        check(f.table().getRowCount() == rows + 1, "Callback cập nhật lịch sử");
        check(f.charts().getComponent(0) != oldChart, "Thay biểu đồ khi refresh");
        f.button("Rút tiền").doClick();
        check(f.accounts.get(0).getBalance().compareTo(before) == 0, "Nút rút gọi đúng nghiệp vụ");
        // Savings mẫu đã dùng 2/3 lượt; chuyển sang Checking để thử chuyển khoản riêng.
        f.choose(1);
        int checkingRows = f.table().getRowCount();
        BigDecimal targetBefore = f.accounts.get(0).getBalance();
        f.button("Chuyển khoản").doClick();
        check(f.accounts.get(0).getBalance().compareTo(targetBefore.add(new BigDecimal("1000"))) == 0, "Chuyển tới đích chưa xác thực");
        check(f.table().getRowCount() == checkingRows + 1, "Chỉ thêm một bản ghi nguồn khi chuyển");
        int opened = c.dialogs;
        c.openTransaction(TransactionType.INTEREST);
        check(c.dialogs == opened, "Không mở dialog cộng lãi thủ công");

        c.cancelLogin = true;
        f.choose(0);
        check(f.pins.getAuthenticatedAccountNumber().isEmpty(), "Đổi tài khoản xóa quyền trước khi nhập PIN");
        check(f.window.getSelectedAccountNumber().isEmpty(), "Hủy PIN bỏ lựa chọn, cho phép chọn lại");
        f.checkBlank();
        c.cancelLogin = false;
        f.choose(1);
        check(f.pins.isAuthenticated("100002"), "Chọn lại sau hủy đăng nhập được");
        f.button("Đăng xuất").doClick();
        f.checkBlank();
        check(f.window.getSelectedAccountNumber().isEmpty(), "Đăng xuất xóa lựa chọn");
        c.openTransaction(TransactionType.DEPOSIT);
        check(c.dialogs == opened, "Không mở giao dịch khi chưa chọn nguồn");

        for (int i = 0; i < 3; i++) {
            try {
                f.pins.authenticate("100003", "999999".toCharArray());
            } catch (RuntimeException expected) {
                // Lần thứ ba khóa tài khoản.
            }
        }
        f.choose(2);
        check(f.pins.isLocked("100003"), "PIN đúng không mở khóa");
        f.checkBlank();
        f.choose(0);
        check(f.pins.isAuthenticated("100001"), "Khóa B không cản đăng nhập A");

        c.logout();
        c.simulateMonthEnd();
        check(YearMonth.now(f.clock).equals(YearMonth.of(2026, 11)), "Chốt tháng khi chưa đăng nhập");
        f.checkBlank();
        check(f.hasLabel("01/11/2026 00:00:00"), "Thanh trạng thái dùng đồng hồ mô phỏng");
        f.choose(0);
        check(f.hasLabel("Lượt rút còn lại (2026-11): 3"), "Lượt Savings sang tháng mới");

        f.charts.failNext = true;
        before = f.accounts.get(0).getBalance();
        f.button("Nạp tiền").doClick();
        check(f.accounts.get(0).getBalance().compareTo(before.add(new BigDecimal("1000"))) == 0, "Lỗi chart không hoàn tác hoặc nạp trùng");
        check(c.errors.get(c.errors.size() - 1).contains("Lỗi cập nhật giao diện"), "Phân biệt lỗi refresh với giao dịch");
        check(!f.button("Nạp tiền").isEnabled(), "Tạm khóa nút giao dịch khi refresh lỗi");
        check(f.button("Đăng xuất").isEnabled(), "Vẫn cho đăng xuất khi refresh lỗi");
        f.window.getJMenuBar().getMenu(1).getItem(0).doClick();
        check(f.button("Nạp tiền").isEnabled(), "Làm mới phục hồi giao diện");
        check(f.pins.isAuthenticated("100001"), "Refresh không làm mất phiên đúng");
        f.pins.logout();
        c.openTransaction(TransactionType.WITHDRAW);
        f.checkBlank();
        check(f.window.getSelectedAccountNumber().isEmpty(), "Quyền hết hiệu lực không dùng lựa chọn UI để giao dịch");
        f.window.dispose();
    }

    private static void testPartialMonthEnd() {
        MonthlyInterestService partial = new MonthlyInterestService() {
            private boolean fail = true;

            @Override
            public List<Transaction> applyMonthEnd(List<Account> accounts, YearMonth period, LocalDateTime time) {
                if (fail) {
                    fail = false;
                    super.applyMonthEnd(accounts.subList(0, 1), period, time);
                    throw new IllegalStateException("Lỗi giả lập tại tài khoản kế tiếp");
                }
                return super.applyMonthEnd(accounts, period, time);
            }
        };
        Fixture f = new Fixture(partial);
        f.choose(0);
        int before = f.table().getRowCount();
        f.controller.simulateMonthEnd();
        check(YearMonth.now(f.clock).equals(YearMonth.of(2026, 10)), "Lỗi một phần giữ nguyên kỳ");
        check(f.table().getRowCount() == before + 1, "Vẫn refresh khoản lãi đã thành công trước lỗi");
        check(f.controller.errors.get(0).contains("chưa hoàn tất"), "Không báo cả đợt thành công");
        f.controller.simulateMonthEnd();
        check(YearMonth.now(f.clock).equals(YearMonth.of(2026, 11)), "Thử lại chốt kỳ thành công");
        check(f.table().getRowCount() == before + 1, "Thử lại không cộng trùng tài khoản đầu");
        check(f.pins.isAuthenticated("100001"), "Cuối tháng không đổi quyền PIN");
        f.window.dispose();
    }

    private static void testActualDialogsAndCsv() throws Exception {
        Fixture f = new Fixture(new MonthlyInterestService());
        f.controller.realDialogs = true;
        onceDialog(LoginDialog.class, dialog -> {
            check(f.pins.getAuthenticatedAccountNumber().isEmpty(), "Dialog thật mở sau logout");
            f.checkBlank();
            find(dialog, JPasswordField.class, c -> true).setText("001234");
            button(dialog, "Đăng nhập").doClick();
        });
        f.choose(0);
        check(f.pins.isAuthenticated("100001"), "LoginDialog thật đăng nhập thành công");

        BigDecimal before = f.accounts.get(0).getBalance();
        int historyBefore = f.table().getRowCount();
        onceDialog(TransactionDialog.class, dialog -> {
            List<JTextField> fields = all(dialog, JTextField.class);
            fields.stream().filter(JTextField::isEditable).findFirst().orElseThrow().setText("2000");
            onceDialog(JDialog.class, message -> message.dispose(),
                    message -> findOrNull(message, JOptionPane.class, c -> true) != null);
            button(dialog, "Xác nhận").doClick();
        });
        f.button("Nạp tiền").doClick();
        check(f.accounts.get(0).getBalance().compareTo(before.add(new BigDecimal("2000"))) == 0, "TransactionDialog thật nạp qua Bank");
        check(f.table().getRowCount() == historyBefore + 1, "Dialog thật gọi callback refresh");

        Path output = Files.createTempDirectory("minibank-controller-csv-").resolve("saoke.csv");
        onceDialog(JDialog.class, dialog -> {
            JFileChooser chooser = find(dialog, JFileChooser.class, c -> true);
            chooser.setSelectedFile(output.toFile());
            onceDialog(JDialog.class, message -> message.dispose(),
                    message -> findOrNull(message, JOptionPane.class, c -> true) != null);
            chooser.approveSelection();
        }, dialog -> findOrNull(dialog, JFileChooser.class, c -> true) != null);
        f.button("Xuất CSV").doClick();
        check(Files.readAllLines(output).size() == f.table().getRowCount() + 1, "Nút CSV thật ghi header và đúng lịch sử");
        check(Files.readString(output).contains("100001,"), "CSV thuộc tài khoản đang xác thực");

        f.window.setVisible(true);
        f.window.validate();
        BufferedImage image = new BufferedImage(f.window.getWidth(), f.window.getHeight(), BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D graphics = image.createGraphics();
        f.window.printAll(graphics);
        graphics.dispose();
        ImageIO.write(image, "png", Path.of("/tmp/minibank-main-window.png").toFile());
        find(f.window, JTabbedPane.class, c -> true).setSelectedIndex(1);
        f.window.validate();
        graphics = image.createGraphics();
        f.window.printAll(graphics);
        graphics.dispose();
        ImageIO.write(image, "png", Path.of("/tmp/minibank-main-charts.png").toFile());

        onceDialog(LoginDialog.class, dialog -> button(dialog, "Hủy").doClick());
        f.choose(1);
        f.checkBlank();
        check(f.pins.getAuthenticatedAccountNumber().isEmpty(), "Hủy LoginDialog thật không giữ quyền cũ");
        check(f.window.getSelectedAccountNumber().isEmpty(), "Hủy dialog thật bỏ lựa chọn");
        f.window.dispose();
    }

    private static final class Fixture {
        final SimulationClock clock = new SimulationClock(Instant.parse("2026-10-21T02:00:00Z"), ZoneId.of("Asia/Ho_Chi_Minh"));
        final PinService pins = new PinService();
        final List<Account> accounts = DemoDataFactory.createAccounts(clock);
        final MainWindow window = new MainWindow();
        final FlakyCharts charts = new FlakyCharts();
        final Bank bank;
        final ScriptController controller;

        Fixture(MonthlyInterestService monthly) {
            DemoDataFactory.registerDemoPins(pins, accounts);
            bank = new Bank(accounts, pins, clock, new TransferService(), monthly);
            controller = new ScriptController(this);
            controller.start();
        }

        JList<?> list() { return find(window, JList.class, c -> "accountList".equals(c.getName())); }
        JTable table() { return find(window, JTable.class, c -> "historyTable".equals(c.getName())); }
        JPanel charts() { return find(window, JPanel.class, c -> "chartContainer".equals(c.getName())); }
        AbstractButton button(String text) { return BankControllerTest.button(window, text); }
        void choose(int index) { list().setSelectedIndex(index); }
        boolean hasLabel(String text) { return all(window, JLabel.class).stream().anyMatch(l -> l.getText() != null && l.getText().contains(text)); }

        void checkBlank() {
            check(table().getRowCount() == 0, "Xóa lịch sử cũ");
            check(!hasLabel("Nguyễn Văn An") && !hasLabel("Trần Thị Bình"), "Xóa thông tin khách hàng cũ");
            check(charts().getComponentCount() == 1 && charts().getComponent(0) instanceof JLabel, "Xóa biểu đồ cũ");
        }
    }

    private static final class FlakyCharts extends ChartHelper {
        boolean failNext;
        FlakyCharts() { super(new StatisticsService()); }

        @Override
        public JPanel createBalanceChart(Account account, List<Transaction> transactions) {
            if (failNext) {
                failNext = false;
                throw new IllegalStateException("Lỗi vẽ biểu đồ giả lập");
            }
            return super.createBalanceChart(account, transactions);
        }
    }

    private static final class ScriptController extends BankController {
        private final Fixture f;
        boolean cancelLogin;
        boolean realDialogs;
        int dialogs;
        final List<String> errors = new ArrayList<>();

        ScriptController(Fixture f) { super(f.window, f.bank, f.pins, f.clock, f.charts); this.f = f; }

        @Override
        boolean requestLogin(String number) {
            check(f.pins.getAuthenticatedAccountNumber().isEmpty(), "Xóa phiên trước mỗi lần chọn");
            f.checkBlank();
            if (realDialogs) { return super.requestLogin(number); }
            if (cancelLogin) { return false; }
            String[] demoPins = {"001234", "123456", "234567", "345678", "456789", "567890"};
            char[] pin = demoPins[Integer.parseInt(number) - 100001].toCharArray();
            try { return f.pins.authenticate(number, pin); }
            finally { Arrays.fill(pin, '\0'); }
        }

        @Override
        void showTransactionDialog(TransactionType operation, String source, Runnable afterSuccess) {
            dialogs++;
            if (realDialogs) { super.showTransactionDialog(operation, source, afterSuccess); return; }
            BigDecimal amount = new BigDecimal("1000");
            switch (operation) {
                case DEPOSIT: f.bank.deposit(source, amount); break;
                case WITHDRAW: f.bank.withdraw(source, amount); break;
                case TRANSFER: f.bank.transfer(source, "100001", amount); break;
                default: throw new AssertionError(operation);
            }
            afterSuccess.run();
        }

        @Override
        void showError(String title, String message) { errors.add(title + ": " + message); }
    }

    private static void onceDialog(Class<? extends Window> type, java.util.function.Consumer<Window> handler) {
        onceDialog(type, handler, window -> true);
    }

    private static void onceDialog(Class<? extends Window> type, java.util.function.Consumer<Window> handler,
                                   Predicate<Window> predicate) {
        long deadline = System.nanoTime() + 10_000_000_000L;
        Timer timer = new Timer(50, null);
        timer.addActionListener(event -> {
            try {
                for (Window dialog : Window.getWindows()) {
                    if (dialog.isShowing() && type.isInstance(dialog) && predicate.test(dialog)) {
                        timer.stop();
                        handler.accept(dialog);
                        return;
                    }
                }
                if (System.nanoTime() > deadline) {
                    throw new AssertionError("Không tìm thấy dialog: " + type.getSimpleName());
                }
            } catch (Throwable ex) {
                asynchronousFailure = ex;
                timer.stop();
                for (Window dialog : Window.getWindows()) {
                    if (dialog instanceof JDialog) { dialog.dispose(); }
                }
            }
        });
        TIMERS.add(timer);
        timer.start();
    }

    private static AbstractButton button(Container parent, String text) {
        return find(parent, AbstractButton.class, c -> text.equals(c.getText()));
    }

    private static <T extends Component> T find(Container parent, Class<T> type, Predicate<T> predicate) {
        T component = findOrNull(parent, type, predicate);
        if (component == null) { throw new AssertionError("Không tìm thấy " + type.getSimpleName()); }
        return component;
    }

    private static <T extends Component> T findOrNull(Container parent, Class<T> type, Predicate<T> predicate) {
        return all(parent, type).stream().filter(predicate).findFirst().orElse(null);
    }

    private static <T extends Component> List<T> all(Container parent, Class<T> type) {
        List<T> matches = new ArrayList<>();
        for (Component component : parent.getComponents()) {
            if (type.isInstance(component)) { matches.add(type.cast(component)); }
            if (component instanceof Container) { matches.addAll(all((Container) component, type)); }
        }
        return matches;
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) { throw new AssertionError(message); }
    }
}
