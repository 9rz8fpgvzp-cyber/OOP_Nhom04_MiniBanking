package vn.edu.ptit.oop.nhom04.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.*;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import vn.edu.ptit.oop.nhom04.model.*;
import vn.edu.ptit.oop.nhom04.repository.InMemoryAccountRepository;
import vn.edu.ptit.oop.nhom04.service.*;
import vn.edu.ptit.oop.nhom04.ui.dialogs.*;
import vn.edu.ptit.oop.nhom04.util.*;

/** Kiểm tra điều phối với Bank thật, Swing thật và các dialog/CSV thật. */
public final class BankControllerTest {
    private static int checks;
    private static final List<Timer> TIMERS = new ArrayList<>();
    private static Throwable asynchronousFailure;
    private static final Instant START = Instant.parse("2026-10-21T02:00:00Z");
    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                testSessions();
                testTransactionsAndRecovery();
                testPartialMonthEnd();
                testActualDialogsAndCsv();
            } catch (Exception ex) { throw new AssertionError(ex); }
            finally { cleanup(); }
        });
        if (asynchronousFailure != null) { throw new AssertionError(asynchronousFailure); }
        // Main thật phải mở dialog trước khi hiển thị bất kỳ danh sách STK nào.
        java.util.concurrent.CountDownLatch startup = new java.util.concurrent.CountDownLatch(1);
        SwingUtilities.invokeAndWait(() -> onceDialog(LoginDialog.class, dialog -> {
            MainWindow main = Arrays.stream(Window.getWindows()).filter(w -> w instanceof MainWindow && w.isShowing())
                    .map(w -> (MainWindow) w).findFirst().orElseThrow();
            check(find(main, JList.class, c -> "accountList".equals(c.getName())).getModel().getSize() == 0,
                    "Main không lộ STK trước đăng nhập");
            button(dialog, "Hủy").doClick();
            startup.countDown();
        }));
        vn.edu.ptit.oop.nhom04.Main.main(new String[0]);
        if (!startup.await(10, java.util.concurrent.TimeUnit.SECONDS)) {
            SwingUtilities.invokeAndWait(BankControllerTest::cleanup);
            throw new AssertionError("Main chưa mở dialog đăng nhập", asynchronousFailure);
        }
        SwingUtilities.invokeAndWait(() -> {
            for (Window w : Window.getWindows()) {
                if (w instanceof MainWindow main && main.isShowing()) {
                    check(main.getSelectedAccountNumber().isEmpty(), "Main không tự chọn STK");
                }
            }
            cleanup();
        });
        if (asynchronousFailure != null) { throw new AssertionError(asynchronousFailure); }
        System.out.println("BankControllerTest: PASS (" + checks + " checks)");
    }

    private static void testSessions() {
        Fixture f = new Fixture(false, new MonthlyInterestService());
        f.checkPrivateContentCleared();
        check(f.pins.getAuthenticatedCustomerId().isEmpty(), "Hủy đăng nhập không có phiên");
        check(f.button("Đăng nhập").isEnabled(), "Màn hình chào cho đăng nhập lại");
        check(!f.button("Cuối tháng").isEnabled(), "Chặn cuối tháng trước đăng nhập");
        check(!f.button("Đăng xuất").isEnabled(), "Chặn đăng xuất khi chưa có phiên");
        int logins = f.controller.loginRequests;
        f.controller.start();
        check(f.controller.loginRequests == logins, "start idempotent");
        f.controller.cancelLogin = false;
        f.controller.login();
        check(f.list().getModel().getSize() == 2, "KH001 chỉ có hai STK");
        check(f.hasLabel("Mã KH: KH001") && f.hasLabel("Họ tên: Nguyễn Văn An")
                && f.hasLabel("0901234567"), "Đủ mã/tên/điện thoại");
        check(f.window.getSelectedAccountNumber().isEmpty(), "Sau login chưa chọn STK");
        check(!f.button("Nạp tiền").isEnabled() && f.button("Đăng xuất").isEnabled()
                && f.button("Cuối tháng").isEnabled(), "Quyền nút khi login nhưng chưa chọn STK");
        logins = f.controller.loginRequests;
        f.choose(0); f.choose(1); f.choose(0);
        check(f.controller.loginRequests == logins && f.pins.isAuthenticated("KH001"), "Đổi STK không hỏi PIN hoặc logout");
        check(f.table().getRowCount() == f.accounts.get(0).getTransactions().size(), "Lịch sử của STK đang chọn");
        check(f.charts().getComponentCount() == 2, "Có hai biểu đồ");
        f.list().clearSelection();
        check(f.pins.isAuthenticated("KH001") && f.hasLabel("Mã KH: KH001"), "Bỏ chọn giữ phiên và hồ sơ");
        check(f.table().getRowCount() == 0 && !f.button("Nạp tiền").isEnabled(), "Bỏ chọn xóa phần STK");
        f.controller.scriptCustomer = "KH002";
        f.controller.logout();
        check(f.pins.isAuthenticated("KH002") && f.list().getModel().getSize() == 2, "Logout mở login và đổi sang B");
        check(!f.hasLabel("Nguyễn Văn An") && f.hasLabel("Trần Thị Bình"), "Đổi user không giữ hồ sơ A");
        f.choose(0);
        check(f.window.getSelectedAccountNumber().orElseThrow().equals("100003"), "B chỉ chọn STK của B");
        f.controller.cancelLogin = true;
        f.controller.logout(); f.checkPrivateContentCleared();
        check(f.pins.getAuthenticatedCustomerId().isEmpty(), "Logout rồi hủy không giữ phiên");
        f.controller.cancelLogin = false; f.controller.scriptCustomer = "KH001"; f.controller.login();f.choose(0);
        f.pins.authenticate("KH002", "234567".toCharArray());
        f.controller.refreshSelectedAccount(); f.checkPrivateContentCleared();
        check(f.pins.getAuthenticatedCustomerId().isEmpty(), "Phiên đổi ngoài controller xóa dữ liệu cũ");
        for (int i = 0; i < 3; i++) { try { f.pins.authenticate("KH002", "999999".toCharArray()); } catch (RuntimeException expected) { } }
        f.controller.scriptCustomer = "KH002";f.controller.login();f.checkPrivateContentCleared();
        check(f.pins.isLocked("KH002"), "Đóng/mở login không xóa khóa B");
        f.controller.scriptCustomer = "KH001";f.controller.login();f.controller.simulateMonthEnd();
        check(YearMonth.now(f.clock).equals(YearMonth.of(2026, 11)), "Cuối tháng chạy khi login và chưa chọn STK");
        check(f.pins.isLocked("KH002"), "Đổi tháng mô phỏng không hết khóa thật");
        f.window.dispose();
    }

    private static void testTransactionsAndRecovery() {
        Fixture f = new Fixture(true, new MonthlyInterestService());f.choose(0);
        for (String name : List.of("Đăng nhập", "Nạp tiền", "Rút tiền", "Chuyển khoản", "Xuất CSV", "Cuối tháng", "Đăng xuất")) {
            check(f.button(name).getIcon() != null && f.button(name).getIcon().getIconWidth() == 16, "PNG 16px: " + name);
            check(f.button(name).getToolTipText() != null, "Tooltip: " + name);
        }
        check(!f.window.getIconImages().isEmpty(), "Icon cửa sổ được nạp");
        check(f.button("Xuất CSV").getAction() == button(f.window.getJMenuBar(), "Xuất sao kê CSV").getAction(), "Menu/toolbar chung action CSV");
        BigDecimal before = f.accounts.get(0).getBalance();int rows = f.table().getRowCount();
        f.button("Nạp tiền").doClick();
        check(f.accounts.get(0).getBalance().compareTo(before.add(new BigDecimal("1000"))) == 0, "Nạp qua Bank đúng một lần");
        check(f.table().getRowCount() == rows + 1, "Lịch sử refresh sau nạp");
        f.button("Rút tiền").doClick();check(f.accounts.get(0).getBalance().compareTo(before) == 0, "Rút qua Bank");
        f.choose(1);BigDecimal receiver = f.accounts.get(2).getBalance();
        f.button("Chuyển khoản").doClick();
        check(f.accounts.get(2).getBalance().compareTo(receiver.add(new BigDecimal("1000"))) == 0, "Chuyển khác chủ qua Bank");
        int dialogs = f.controller.dialogs;
        f.controller.openTransaction(TransactionType.INTEREST);check(f.controller.dialogs == dialogs, "Không cộng lãi bằng dialog giao dịch");
        f.charts.failNext = true;before = f.accounts.get(1).getBalance();
        f.button("Nạp tiền").doClick();
        check(f.accounts.get(1).getBalance().compareTo(before.add(new BigDecimal("1000"))) == 0, "Refresh lỗi không giao dịch trùng");
        check(!f.button("Nạp tiền").isEnabled() && f.button("Đăng xuất").isEnabled(), "Refresh lỗi vẫn cho logout");
        check(f.controller.errors.stream().anyMatch(e -> e.contains("Lỗi cập nhật giao diện")), "Báo lỗi hiển thị riêng");
        button(f.window.getJMenuBar(), "Làm mới").doClick();
        check(f.button("Nạp tiền").isEnabled() && f.pins.isAuthenticated("KH001"), "Phục hồi refresh giữ phiên");
        f.pins.logout();f.controller.openTransaction(TransactionType.WITHDRAW);f.checkPrivateContentCleared();
        check(f.controller.dialogs == dialogs + 1, "Không mở giao dịch sau khi mất phiên");
        f.window.dispose();
    }

    private static void testPartialMonthEnd() {
        MonthlyInterestService partial = new MonthlyInterestService() {
            boolean fail = true;
            @Override public List<Transaction> applyMonthEnd(List<Account> accounts, YearMonth period, LocalDateTime time) {
                if (fail) { fail = false;super.applyMonthEnd(accounts.subList(0,1),period,time);throw new IllegalStateException("Lỗi giả lập tài khoản kế tiếp"); }
                return super.applyMonthEnd(accounts,period,time);
            }
        };
        Fixture f = new Fixture(true,partial);f.choose(0);int rows = f.table().getRowCount();
        f.controller.simulateMonthEnd();
        check(YearMonth.now(f.clock).equals(YearMonth.of(2026,10)), "Lỗi một phần giữ kỳ");
        check(f.table().getRowCount() == rows + 1, "Hiện khoản lãi đã cộng trước lỗi");
        check(f.controller.errors.stream().anyMatch(e -> e.contains("chưa hoàn tất")), "Báo đợt chưa hoàn tất");
        f.controller.simulateMonthEnd();
        check(YearMonth.now(f.clock).equals(YearMonth.of(2026,11)) && f.table().getRowCount() == rows+1, "Retry không cộng lãi trùng");
        check(f.hasLabel("Lượt rút còn lại (11/2026): 3"), "Tháng hiển thị MM/yyyy");f.window.dispose();
    }

    private static void testActualDialogsAndCsv() throws Exception {
        Fixture f = new Fixture(false,new MonthlyInterestService());f.controller.realDialogs = true;
        onceDialog(LoginDialog.class, dialog -> {
            f.checkPrivateContentCleared();
            find(dialog,JTextField.class,t -> !(t instanceof JPasswordField)).setText(" KH001 ");
            find(dialog,JPasswordField.class,t -> true).setText("001234");button(dialog,"Đăng nhập").doClick();
        });
        f.controller.login();check(f.pins.isAuthenticated("KH001") && f.list().getModel().getSize()==2,"LoginDialog thật lọc STK");f.choose(0);
        BigDecimal before = f.accounts.get(0).getBalance();
        onceDialog(TransactionDialog.class, dialog -> {
            find(dialog,JTextField.class,JTextField::isEditable).setText("2000");
            onceDialog(JDialog.class, message -> {
                check(!button(dialog,"Xác nhận").isEnabled(), "Khóa xác nhận ngay sau giao dịch thành công");
                button(dialog,"Xác nhận").doClick();
                message.dispose();
            }, message -> findOrNull(message,JOptionPane.class,c -> true)!=null);
            button(dialog,"Xác nhận").doClick();
        });
        f.button("Nạp tiền").doClick();
        check(f.accounts.get(0).getBalance().compareTo(before.add(new BigDecimal("2000")))==0,"Dialog nạp thật");
        f.choose(1);BigDecimal target = f.accounts.get(2).getBalance();
        onceDialog(TransactionDialog.class, dialog -> {
            List<JTextField> fields = all(dialog,JTextField.class).stream().filter(JTextField::isEditable).toList();
            check(fields.size()==2,"Chuyển khoản có ô STK đích và số tiền");
            fields.get(0).setText(" 100003 ");fields.get(1).setText("1000");onceMessage();button(dialog,"Xác nhận").doClick();
        });
        f.button("Chuyển khoản").doClick();
        check(f.accounts.get(2).getBalance().compareTo(target.add(new BigDecimal("1000")))==0,"Dialog chuyển khác chủ thật");
        Path directory = Files.createTempDirectory("minibank-v2-csv-");Path output = directory.resolve("saoke.csv");
        try {
            onceDialog(JDialog.class,dialog -> {
                JFileChooser chooser = find(dialog,JFileChooser.class,c -> true);chooser.setSelectedFile(output.toFile());chooser.approveSelection();
            },dialog -> findOrNull(dialog,JFileChooser.class,c -> true)!=null);
            onceMessage();f.button("Xuất CSV").doClick();
            check(Files.readAllLines(output).size()==f.table().getRowCount()+1,"CSV thật đủ header/lịch sử");
            check(Files.readString(output).contains("100002,"),"CSV đúng STK được phép");
        } finally { Files.deleteIfExists(output);Files.delete(directory); }
        f.window.setVisible(true);f.window.validate();capture(f.window,"/tmp/minibank-v2-accounts.png");
        find(f.window,JTabbedPane.class,t -> true).setSelectedIndex(1);capture(f.window,"/tmp/minibank-v2-charts.png");
        onceDialog(LoginDialog.class,dialog -> {f.checkPrivateContentCleared();button(dialog,"Hủy").doClick();});
        f.controller.logout();f.checkPrivateContentCleared();capture(f.window,"/tmp/minibank-v2-welcome.png");f.window.dispose();
    }

    private static void capture(MainWindow window,String path) throws Exception {
        window.validate();BufferedImage image = new BufferedImage(window.getWidth(),window.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();window.printAll(g);g.dispose();ImageIO.write(image,"png",Path.of(path).toFile());
    }
    private static class AuthClock extends Clock {
        @Override public Instant instant(){return START;}
        @Override public ZoneId getZone(){return ZONE;}
        @Override public Clock withZone(ZoneId zone){return this;}
    }
    private static final class Fixture {
        final SimulationClock clock = new SimulationClock(START,ZONE);
        final PinService pins = new PinService(new AuthClock());
        final List<Account> accounts = DemoDataFactory.createAccounts(clock);
        final MainWindow window = new MainWindow();final FlakyCharts charts = new FlakyCharts();
        final Bank bank;final ScriptController controller;
        Fixture(boolean login,MonthlyInterestService monthly) {
            DemoDataFactory.registerDemoPins(pins,accounts);
            bank = new Bank(new InMemoryAccountRepository(accounts),pins,clock,new TransferService(),monthly);
            controller = new ScriptController(this);controller.cancelLogin = !login;controller.start();
        }
        JList<?> list(){return find(window,JList.class,c -> "accountList".equals(c.getName()));}
        JTable table(){return find(window,JTable.class,c -> "historyTable".equals(c.getName()));}
        JPanel charts(){return find(window,JPanel.class,c -> "chartContainer".equals(c.getName()));}
        AbstractButton button(String name){return BankControllerTest.button(window,name);}
        void choose(int index){list().setSelectedIndex(index);}
        boolean hasLabel(String text){return all(window,JLabel.class).stream().anyMatch(l -> l.getText()!=null&&l.getText().contains(text));}
        void checkPrivateContentCleared(){
            check(list().getModel().getSize()==0&&window.getSelectedAccountNumber().isEmpty(),"Xóa toàn bộ STK/lựa chọn");
            check(table().getRowCount()==0,"Xóa lịch sử");
            check(!hasLabel("Nguyễn Văn An")&&!hasLabel("Trần Thị Bình")&&!hasLabel("Lê Minh Chi"),"Xóa hồ sơ cũ");
            check(charts().getComponentCount()==1&&charts().getComponent(0) instanceof JLabel,"Xóa biểu đồ cũ");
        }
    }
    private static final class FlakyCharts extends ChartHelper {
        boolean failNext;
        FlakyCharts(){super(new StatisticsService());}
        @Override public JPanel createBalanceChart(Account a,List<Transaction> history){
            if(failNext){failNext=false;throw new IllegalStateException("Lỗi vẽ biểu đồ giả lập");}return super.createBalanceChart(a,history);
        }
    }
    private static final class ScriptController extends BankController {
        final Fixture f;boolean cancelLogin,realDialogs;String scriptCustomer="KH001";int loginRequests,dialogs;
        final List<String> errors=new ArrayList<>();
        ScriptController(Fixture f){super(f.window,f.bank,f.pins,f.clock,f.charts);this.f=f;}
        @Override boolean requestLogin(){
            loginRequests++;f.checkPrivateContentCleared();check(f.pins.getAuthenticatedCustomerId().isEmpty(),"Xóa phiên trước dialog");
            if(realDialogs)return super.requestLogin();if(cancelLogin)return false;
            String pin=Map.of("KH001","001234","KH002","234567","KH003","456789").get(scriptCustomer);
            return f.pins.authenticate(scriptCustomer,pin.toCharArray());
        }
        @Override void showTransactionDialog(TransactionType op,String source,Runnable callback){
            dialogs++;if(realDialogs){super.showTransactionDialog(op,source,callback);return;}
            BigDecimal amount=new BigDecimal("1000");
            switch(op){case DEPOSIT:f.bank.deposit(source,amount);break;case WITHDRAW:f.bank.withdraw(source,amount);break;case TRANSFER:f.bank.transfer(source,"100003",amount);break;default:throw new AssertionError(op);}
            callback.run();
        }
        @Override void showError(String title,String message){errors.add(title+": "+message);}
    }
    private static void onceMessage(){onceDialog(JDialog.class,d -> d.dispose(),d -> findOrNull(d,JOptionPane.class,c -> true)!=null);}
    private static void onceDialog(Class<? extends Window> type,java.util.function.Consumer<Window> handler){onceDialog(type,handler,w -> true);}
    private static void onceDialog(Class<? extends Window> type,java.util.function.Consumer<Window> handler,Predicate<Window> predicate){
        long deadline=System.nanoTime()+10_000_000_000L;Timer timer=new Timer(30,null);
        timer.addActionListener(e -> {
            try{
                for(Window w:Window.getWindows())if(w.isShowing()&&type.isInstance(w)&&predicate.test(w)){timer.stop();handler.accept(w);return;}
                if(System.nanoTime()>deadline)throw new AssertionError("Không tìm thấy dialog "+type.getSimpleName());
            }catch(Throwable ex){asynchronousFailure=ex;timer.stop();for(Window w:Window.getWindows())if(w instanceof JDialog)w.dispose();}
        });TIMERS.add(timer);timer.start();
    }
    private static void cleanup(){TIMERS.forEach(Timer::stop);for(Window w:Window.getWindows())w.dispose();}
    private static AbstractButton button(Container c,String text){return find(c,AbstractButton.class,b -> text.equals(b.getText()));}
    private static <T extends Component>T find(Container c,Class<T> type,Predicate<T> p){T result=findOrNull(c,type,p);if(result==null)throw new AssertionError("Không thấy "+type.getSimpleName());return result;}
    private static <T extends Component>T findOrNull(Container c,Class<T> type,Predicate<T> p){return all(c,type).stream().filter(p).findFirst().orElse(null);}
    private static <T extends Component>List<T> all(Container c,Class<T> type){List<T> list=new ArrayList<>();for(Component child:(c instanceof JMenu menu ? menu.getMenuComponents() : c.getComponents())){if(type.isInstance(child))list.add(type.cast(child));if(child instanceof Container nested)list.addAll(all(nested,type));}return list;}
    private static void check(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
}
