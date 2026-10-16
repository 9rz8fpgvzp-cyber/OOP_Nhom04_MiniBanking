# API chung v1 — chốt để triển khai từ 16/10/2026

Tài liệu quy định package, tên lớp, constructor, phương thức và kiểu dữ liệu dùng chung. Mỗi người triển khai phần mình theo các chữ ký dưới đây. Nếu cần đổi API, trao đổi với người dùng phương thức đó và cập nhật tài liệu trước khi ghép code.

Không tạo lớp cùng tên ở package khác. Tên constructor không có kiểu trả về; các chữ ký ở đây là mô tả, không phải các đoạn hoàn chỉnh để dán nguyên vào một file.

## 1. Quy ước chung

| Nội dung | Quy ước v1 |
| --- | --- |
| Java/package | Java 17, UTF-8; gốc `vn.edu.ptit.oop.nhom04` |
| Tiền | `BigDecimal`, VND nguyên đồng. Số tiền giao dịch phải > 0 và không có phần lẻ; `1000.00` hợp lệ, `1000.5` không hợp lệ. So sánh bằng `compareTo`, không chuyển qua double |
| Lãi suất | `BigDecimal` dạng tỷ lệ năm: `0.06` là 6%/năm. Lãi tháng = số dư × tỷ lệ / 12, làm tròn về đồng bằng `RoundingMode.HALF_UP`; số dư <= 0 không có lãi |
| Thời gian | `LocalDateTime` cho giao dịch; `LocalDate` cho ngày mở/đáo hạn; `YearMonth` cho giới hạn tháng và kỳ lãi. Một `Clock` mô phỏng dùng chung |
| Mã | String; mã khách hàng/số tài khoản duy nhất, không rỗng. Mã giao dịch UUID String; hai phía chuyển khoản dùng chung `referenceId` |
| Danh sách | Trả bản sao không sửa được (`List.copyOf`); sắp lịch sử theo thời gian rồi thứ tự ghi nhận, không dùng Map theo timestamp làm mất giao dịch |
| Lỗi nghiệp vụ | Exception tự định nghĩa đều kế thừa `RuntimeException`; GUI bắt và hiển thị message tiếng Việt. Không dùng `false` để báo giao dịch thất bại |
| Luồng chạy | Một ứng dụng, thao tác Swing trên EDT. Bank tuần tự hóa mọi thao tác thay đổi; không thêm luồng tự động chạy lãi. Không tuyên bố hỗ trợ đồng thời nhiều máy |
| Phân quyền | Một PinService dùng chung. Bank là cổng dùng từ GUI, kiểm tra tài khoản đang xác thực và khóa. Model là nội bộ nghiệp vụ, không tự mở dialog |

**Nhóm chốt thêm `TransactionType.INTEREST` để phân biệt tiền lãi với tiền người dùng nạp.** CSV giữ cột như trước; lịch sử hiển thị “Cộng lãi”. Tổng nạp/rút của biểu đồ cột chỉ tính DEPOSIT/WITHDRAW; không tính TRANSFER/INTEREST. Biểu đồ đường tính mọi biến động số dư, kể cả chuyển khoản và lãi.

## 2. Model nền — người 2

File nằm trong `model`, trừ `util/DemoDataFactory.java`.

```java
public enum AccountType { SAVINGS, CHECKING, FIXED_TERM }

// Dùng để chọn tài khoản trước khi xác thực; không chứa tên, số dư hay PIN.
public record AccountSummary(String accountNumber, AccountType type) {}

public Customer(String customerId, String fullName, String phoneNumber);
public String getCustomerId();
public String getFullName();
public String getPhoneNumber();

// Account là abstract, số dư/lịch sử private, không có setBalance public.
protected Account(String accountNumber, Customer owner,
                  BigDecimal openingBalance, LocalDateTime openedAt);
public final String getAccountNumber();
public final Customer getOwner();
public final BigDecimal getOpeningBalance();
public final BigDecimal getBalance();
public final LocalDateTime getOpenedAt();
public abstract AccountType getType();
public final List<Transaction> getTransactions();

// API model dùng nội bộ, GUI gọi Bank thay vì gọi trực tiếp.
public final Transaction deposit(BigDecimal amount, LocalDateTime time);
public abstract Transaction withdraw(BigDecimal amount, LocalDateTime time);
public final List<Transaction> transferTo(Account target, BigDecimal amount,
                                         LocalDateTime time);
// Chỉ MonthlyInterestService gọi, không phải thao tác người dùng.
public final Optional<Transaction> creditInterest(BigDecimal amount, YearMonth period,
                                                LocalDateTime time);
protected abstract void validateWithdrawal(BigDecimal amount, LocalDate date);
protected final Transaction withdrawAfterValidation(BigDecimal amount,
                                                     LocalDateTime time);
protected void onWithdrawalCommitted(LocalDate date);
```

- Số dư mở đầu >= 0; Customer và thời gian không null. Nạp không đổi lượt rút, kể cả nạp vào tài khoản kỳ hạn; không thay ngày đáo hạn. Rút/chuyển ra của FixedTerm phải đủ ngày.
- Hai/ba lớp con override `withdraw`: kiểm tra bằng `validateWithdrawal`, sau đó gọi `withdrawAfterValidation`. Phương thức chung thực hiện cập nhật và ghi đúng một Transaction WITHDRAW, gọi `onWithdrawalCommitted` đúng một lần khi đã thành công. Không ghi lịch sử hoặc tăng lượt hai lần.
- `transferTo` trả đúng hai Transaction, thứ tự OUT của nguồn rồi IN của đích, cùng referenceId; chỉ nguồn tiêu lượt rút (nếu là Savings). Không gọi deposit/withdraw công khai theo kiểu hai bước làm phát sinh lịch sử thừa.
- **Người 2 sở hữu Account; người 5 phối hợp viết riêng phần `transferTo` và cơ chế nguyên tử trong PR đã thống nhất.** Không sửa cùng file song song hoặc thêm setter số dư để ghép nhanh. Mọi cập nhật giao dịch, kể cả deposit/withdraw, phải nhất quán giữa số dư/lịch sử/lượt rút.

### Cơ chế nguyên tử nội bộ

`transferTo` là một thao tác nghiệp vụ: kiểm tra cả hai phía, chuẩn bị hai bản ghi, giữ trạng thái trước thao tác, rồi cập nhật. Nếu có lỗi trong lúc áp dụng, phục hồi **hai số dư, hai lịch sử và trạng thái lượt rút/tháng**. Không rollback bằng một giao dịch deposit mới. Các hàm snapshot/restore ở private/protected nội bộ Account/lớp con, không cung cấp restore public cho GUI. Chữ ký helper nội bộ do người 2/3/5 thống nhất trong cùng PR; chúng không phải API cho các thành viên khác gọi. Phải có test gây lỗi giữa chừng, không chỉ test thiếu tiền trước khi trừ.

### Dữ liệu mẫu

```java
// util.DemoDataFactory: người 2; chỉ gọi một lần khi khởi động, trước khi mở UI.
public static List<Account> createAccounts(Clock clock);
public static void registerDemoPins(PinService pinService, List<Account> accounts);
```

Trả 4–6 tài khoản, đủ ba loại, ít nhất ba khách hàng; có FixedTerm chưa/đã đáo hạn. Lịch sử mẫu phải khớp số dư và không nằm sau thời điểm mô phỏng hiện tại. PIN demo ghi trong hướng dẫn demo, không hiển thị ở UI hoặc log; phương thức đăng ký gọi `registerPin` cho từng tài khoản. Dữ liệu bootstrap không cần người dùng nhập PIN; sau khi mở UI, mọi thao tác người dùng qua Bank.

## 3. Ba loại tài khoản — người 3 và 4

```java
// model.SavingsAccount — người 3; implements InterestBearing
public SavingsAccount(String number, Customer owner, BigDecimal openingBalance,
                      LocalDateTime openedAt, BigDecimal annualInterestRate);
public BigDecimal getAnnualInterestRate();
public int getRemainingWithdrawals(YearMonth month);
public Transaction withdraw(BigDecimal amount, LocalDateTime time);

// model.CheckingAccount — người 3
public CheckingAccount(String number, Customer owner, BigDecimal openingBalance,
                      LocalDateTime openedAt, BigDecimal overdraftLimit);
public BigDecimal getOverdraftLimit();
public BigDecimal getAvailableBalance();
public Transaction withdraw(BigDecimal amount, LocalDateTime time);

// model.FixedTermAccount — người 4; implements InterestBearing
public FixedTermAccount(String number, Customer owner, BigDecimal openingBalance,
                        LocalDateTime openedAt, BigDecimal annualInterestRate,
                        LocalDate maturityDate);
public BigDecimal getAnnualInterestRate();
public LocalDate getMaturityDate();
public Transaction withdraw(BigDecimal amount, LocalDateTime time);

// model.InterestBearing — người 4
BigDecimal calculateMonthlyInterest();
```

- Savings: tối đa 3 lần rút/chuyển ra thành công trong YearMonth; không vượt số dư. `getRemainingWithdrawals` không tự sửa trạng thái. `onWithdrawalCommitted` ghi lượt theo tháng của thao tác. Khi đổi tháng, lượt tháng mới là 0; thao tác lỗi không đổi bộ đếm/tháng.
- Checking: hạn mức >= 0; khả dụng = balance + overdraftLimit; cho phép đến đúng hạn mức âm. Không implements InterestBearing.
- FixedTerm: maturityDate >= ngày mở; trước ngày đó cấm rút/chuyển ra; đúng ngày được phép nếu đủ tiền. Có lãi tháng theo số dư, không tự kéo dài kỳ hạn.
- Hai loại hưởng lãi tính cùng công thức mục 1. Lãi suất >= 0; không tự cộng số dư trong `calculateMonthlyInterest`.

## 4. Transaction và lịch sử — người 6

```java
// Các file model do người 6 sở hữu
public enum TransactionType { DEPOSIT, WITHDRAW, TRANSFER, INTEREST }
public enum TransactionDirection { NONE, IN, OUT }

public Transaction(String transactionId, String referenceId,
                   String accountNumber, AccountType accountType,
                   TransactionType type, TransactionDirection direction,
                   BigDecimal amount, LocalDateTime timestamp,
                   BigDecimal balanceAfter, String description);
public String getTransactionId();
public String getReferenceId();
public String getAccountNumber();
public AccountType getAccountType();
public TransactionType getType();
public TransactionDirection getDirection();
public BigDecimal getAmount();
public LocalDateTime getTimestamp();
public BigDecimal getBalanceAfter();
public String getDescription();

// ui.TransactionTableModel extends AbstractTableModel
public TransactionTableModel();
public void setTransactions(List<Transaction> transactions);
public void clear();
```

Transaction final, trường private final, không setter. amount > 0; balanceAfter có thể âm với Checking. direction IN/OUT chỉ cho TRANSFER, các loại khác NONE; referenceId luôn có giá trị (giao dịch một phía có thể dùng cùng transactionId). Interest làm tròn bằng 0 thì không tạo giao dịch 0 đồng. Description không null; với lãi ghi `INTEREST:yyyy-MM`, không dùng description để phân loại thay enum.

## 5. PIN trọn luồng — người 8

```java
// service.PinService; model.PinCredential là nội bộ người 8, không để GUI đọc hash.
public PinService();
public void registerPin(String accountNumber, char[] pin);
public boolean authenticate(String accountNumber, char[] pin);
public int getRemainingAttempts(String accountNumber);
public boolean isLocked(String accountNumber);
public boolean isAuthenticated(String accountNumber);
public Optional<String> getAuthenticatedAccountNumber();
public void requireAuthenticated(String accountNumber);
public void logout();

// ui.dialogs.LoginDialog extends JDialog, modal
public LoginDialog(Window owner, String accountNumber, PinService pinService);
public boolean showDialog();
```

- PIN đúng 6 chữ số, giữ số 0 đầu, lưu hash + salt. registerPin chỉ bootstrap, không cho ghi đè PIN đã đăng ký (tránh reset khóa). PIN đầu vào không lưu nguyên mảng; bên gọi xóa char[] sau khi gọi.
- authenticate thành công trả true, reset số sai và xác thực đúng một tài khoản; sai lần 1/2 trả false, lần thứ 3 khóa và ném AccountLockedException. Đã khóa thì PIN đúng vẫn ném lỗi. PIN gửi sai định dạng khi xác thực cũng tính một lần sai; đăng ký sai định dạng ném IllegalArgumentException.
- Trước mỗi lần authenticate xóa quyền cũ; tài khoản không đăng ký ném AccountNotFoundException, không tăng đếm tài khoản khác. Chỉ tồn tại một phiên xác thực, đăng nhập B không giữ quyền A.
- requireAuthenticated: khóa → AccountLockedException; chưa đúng tài khoản → AuthenticationRequiredException. logout chỉ xóa quyền, không reset số sai/khóa.
- showDialog mở cửa sổ và trả true chỉ khi đã xác thực; Hủy/đóng X trả false, không gọi authenticate. Dialog hiển thị sai/còn lượt/khóa rõ ràng, không tự reset bộ đếm. Controller phải logout và xóa dữ liệu trước khi mở dialog chọn tài khoản mới.

## 6. Bank và chuyển khoản — người 5

```java
// service.BankingOperations: interface GUI được phép gọi
List<AccountSummary> listAccounts();
Account getAccount(String accountNumber);
List<Transaction> getTransactions(String accountNumber);
Transaction deposit(String accountNumber, BigDecimal amount);
Transaction withdraw(String accountNumber, BigDecimal amount);
List<Transaction> transfer(String sourceNumber, String targetNumber, BigDecimal amount);
List<Transaction> simulateMonthEnd();

// service.Bank implements BankingOperations
public Bank(List<Account> accounts, PinService pins, SimulationClock clock,
            TransferService transfers, MonthlyInterestService interestService);

// service.TransferService: chỉ dùng nội bộ sau kiểm tra quyền tại Bank
public TransferService();
public List<Transaction> transfer(Account source, Account target,
                                  BigDecimal amount, LocalDateTime time);
```

- listAccounts chỉ trả mã/loại tài khoản cho bộ chọn trước PIN. getAccount/getTransactions/deposit/withdraw phải gọi requireAuthenticated đúng tài khoản. Account trả ra là model chỉ đọc từ góc nhìn GUI; GUI không gọi hàm thay đổi trực tiếp.
- transfer xác thực **nguồn**, tìm đủ hai tài khoản, từ chối cùng nguồn/đích, rồi gọi TransferService; service kiểm tra và gọi source.transferTo. Đích không cần xác thực và có thể đang khóa.
- Các hàm thay đổi của Bank chạy tuần tự, chặn lồng một thao tác khác vào giữa quá trình chuẩn bị/commit. Lấy timestamp đúng một lần từ SimulationClock cho mỗi giao dịch.
- Không có `getAllAccounts()` công khai để UI lấy chi tiết mọi tài khoản. Bootstrap và MonthlyInterestService nhận danh sách nội bộ, không đi vòng bằng API người dùng.
- simulateMonthEnd là tác vụ hệ thống từ nút mô phỏng demo, xử lý mọi tài khoản hưởng lãi, kể cả khóa; **không yêu cầu PIN từng tài khoản**, không phải endpoint người dùng thực. Không dùng quyền này để bỏ qua PIN cho deposit/withdraw/transfer hoặc đọc chi tiết.

## 7. Đồng hồ và cuối tháng — người 4

Thêm file `service/SimulationClock.java`, kế thừa `java.time.Clock`; đây là nguồn thời gian chung. Người 1 khởi tạo, các phần khác chỉ đọc. `withZone` trả Clock phù hợp không làm lệch thời điểm; không tự đặt tháng riêng ở GUI/biểu đồ.

```java
public SimulationClock(Instant initialInstant, ZoneId zone);
// Implement getZone(), withZone(ZoneId), instant() của Clock.
public void advanceToNextMonth();

// service.MonthlyInterestService
public MonthlyInterestService();
public List<Transaction> applyMonthEnd(List<Account> accounts,
                                      YearMonth closingMonth,
                                      LocalDateTime closingTime);
```

- Bank.simulateMonthEnd lấy tháng hiện tại, closingTime = ngày cuối tháng 23:59:59; MonthlyInterestService tính qua InterestBearing. Với mỗi tài khoản có lãi > 0, cập nhật số dư + Transaction INTEREST + đánh dấu kỳ đã xử lý một cách nhất quán.
- Account.creditInterest là nơi duy nhất lưu các YearMonth đã xử lý trong phiên chạy. Phương thức trả Optional.empty nếu kỳ đã xử lý hoặc lãi bằng 0; amount âm/null bị từ chối. Với lãi > 0, thêm số dư, Transaction INTEREST và kỳ đã xử lý trong cùng một thao tác, lỗi thì khôi phục tất cả. Lãi bằng 0 vẫn đánh dấu đã xét kỳ. Service không giữ một Set độc lập thứ hai gây lệch trạng thái; chỉ gọi cho Account implements InterestBearing, truyền kết quả calculateMonthlyInterest.
- Service trả các giao dịch lãi vừa tạo, không trả lại khoản đã xử lý. Gọi lại cùng kỳ không cộng trùng. Lỗi một tài khoản: ném lỗi, tài khoản đó giữ nguyên trạng thái; các tài khoản đã thành công được giữ và ghi dấu để lần thử lại không cộng trùng.
- Bank chỉ advanceToNextMonth khi cả đợt thành công; nếu lỗi thì giữ kỳ để thử lại và controller vẫn refresh các dữ liệu đã thay đổi thành công. advance đặt thời gian về ngày 1 tháng kế tiếp 00:00:00, kể cả ranh giới năm. Bấm lần tiếp theo sau thành công là mô phỏng **tháng tiếp theo**, không phải cùng kỳ.
- Savings dùng YearMonth của thời gian chung nên tháng mới có lại 3 lượt, không reset số lần sai PIN. Mọi giao dịch mới dùng clock mới; không trộn LocalDateTime.now() theo đồng hồ thật.
- Người 4 gọi Account.creditInterest theo chữ ký mục 2, người 2 triển khai phần cập nhật nhất quán. Không gọi deposit rồi sửa lại loại giao dịch sau khi tạo immutable Transaction.

## 8. UI tài khoản và giao dịch — người 7

```java
// ui.AccountListRenderer implements ListCellRenderer<AccountSummary>
public AccountListRenderer();

// ui.panels.AccountDetailsPanel extends JPanel
public AccountDetailsPanel();
public void setAccount(Account account, YearMonth currentMonth);
public void clear();

// ui.dialogs.TransactionDialog extends JDialog, modal
public TransactionDialog(Window owner, BankingOperations bank,
                         TransactionType operation, String sourceNumber,
                         Runnable afterSuccess);
public void showDialog();

// ui.dialogs.AboutDialog extends JDialog, modal
public AboutDialog(Window owner);
public void showDialog();
```

- Renderer nhận AccountSummary nên chỉ hiện số tài khoản/loại/icon trước PIN. Chi tiết chủ sở hữu/số dư chỉ set sau Bank.getAccount thành công.
- operation của TransactionDialog chỉ DEPOSIT/WITHDRAW/TRANSFER; INTEREST không phải loại nhập tay. Với chuyển khoản lấy đích từ bank.listAccounts, bỏ tài khoản nguồn. Dialog gọi Bank, không cập nhật trực tiếp Account.
- Hủy đóng dialog, không gọi nghiệp vụ/callback. Thành công gọi afterSuccess đúng một lần; lỗi giữ dialog để sửa, không gọi callback thành công. Không gọi BankingOperations trong constructor để tránh mở dialog là phát sinh giao dịch.
- setAccount cập nhật cả Savings lãi/lượt còn lại, Checking hạn mức/khả dụng, FixedTerm đáo hạn. Không thay đổi model; clear xóa dữ liệu cũ.

## 9. CSV trọn luồng — người 6

```java
// util.CsvStatementExporter, không mở UI
public CsvStatementExporter();
public int export(Path path, String accountNumber, List<Transaction> transactions)
        throws IOException;

// ui.CsvExportAction extends AbstractAction
public CsvExportAction(Window owner, BankingOperations bank,
                       Supplier<Optional<String>> selectedAccount,
                       Consumer<String> showStatus);
```

Action tự lấy tài khoản đang chọn, gọi Bank.getTransactions để kiểm tra quyền trước khi chọn/ghi file, mở JFileChooser và hỏi ghi đè, gọi exporter, báo kết quả. Hủy không ghi. Khi ghi vẫn lấy lại lịch sử được phép; không dùng snapshot tài khoản khác hoặc đọc trực tiếp Account.getTransactions từ GUI.

Exporter kiểm tra mọi bản ghi thuộc accountNumber, lỗi dữ liệu ném IllegalArgumentException; chỉ ghi UTF-8 khi kiểm tra hợp lệ. `int` là số dòng giao dịch, không gồm header; rỗng trả 0 và có header. Header giữ nguyên `accountNumber,transactionId,type,direction,amount,timestamp,balanceAfter`; direction NONE xuất rỗng. type có thêm INTEREST. Quote/escape theo CSV; IOException do action bắt và thông báo. Không xác thực trong exporter thuần I/O; cổng quyền là Bank trước khi đưa dữ liệu vào exporter.

## 10. Thống kê và biểu đồ — người 9

```java
// service.StatisticsService, không phụ thuộc Swing/JFreeChart
public StatisticsService();
public BigDecimal totalDeposits(List<Transaction> transactions);
public BigDecimal totalWithdrawals(List<Transaction> transactions);
public Map<LocalDate, BigDecimal> depositsByDay(List<Transaction> transactions);
public Map<LocalDate, BigDecimal> withdrawalsByDay(List<Transaction> transactions);

// util.ChartHelper; giấu JFreeChart trong lớp, bên ngoài dùng JPanel
public ChartHelper(StatisticsService statistics);
public JPanel createBalanceChart(Account account, List<Transaction> transactions);
public JPanel createDepositWithdrawalChart(List<Transaction> transactions);
```

Controller gọi Bank lấy Account/lịch sử được phép rồi truyền vào ChartHelper. Mỗi lần refresh tạo panel mới và thay panel cũ, revalidate/repaint; quy mô dữ liệu demo nhỏ, không cần chốt API dataset JFreeChart giữa nhiều người. Biểu đồ đường thêm điểm openingBalance/openedAt rồi các balanceAfter theo thứ tự; xử lý nhiều giao dịch cùng timestamp không mất điểm. Không dùng TreeMap<LocalDateTime, BigDecimal> làm rơi các điểm trùng. Biểu đồ cột DEPOSIT/WITHDRAW theo ngày, loại trừ transfer/interest. Mọi danh sách truyền vào phải cùng tài khoản; dữ liệu rỗng vẫn có biểu đồ trống/điểm mở đầu, không lỗi.

## 11. Cửa sổ và controller — người 1

```java
// ui.MainWindow: giữ constructor không tham số đang có để Main hiện tại biên dịch
public MainWindow();
public void bindController(BankController controller);
public void setAccountChoices(List<AccountSummary> accounts);
public Optional<String> getSelectedAccountNumber();
public void showAccount(Account account, YearMonth currentMonth);
public void showTransactions(List<Transaction> transactions);
public void showCharts(JPanel balanceChart, JPanel depositWithdrawalChart);
public void clearAccountContent();
public void showStatus(String message);

// ui.BankController
public BankController(MainWindow window, BankingOperations bank,
                      PinService pins, Clock clock, ChartHelper charts);
public void start();
public void selectAccount(String accountNumber);
public void openTransaction(TransactionType operation);
public void refreshSelectedAccount();
public void logout();
public void simulateMonthEnd();
```

- MainWindow giữ bố cục, nhúng renderer/details/table/chart của người 7/6/9; public showStatus gọi setStatus được kế thừa. CsvExportAction gắn vào menu và toolbar, không tự viết một luồng CSV khác.
- selectAccount: logout → clear nội dung → mở LoginDialog → nếu true thì lấy Bank.getAccount/history và refresh; false giữ nội dung trống. getSelectedAccountNumber chỉ là lựa chọn UI, không phải bằng chứng xác thực.
- openTransaction chỉ mở khi có tài khoản và Bank cho phép; tạo dialog với callback `this::refreshSelectedAccount`.
- simulateMonthEnd: gọi Bank; luôn refresh tài khoản đang xác thực sau khi trả về hoặc báo lỗi, vì một phần lãi có thể đã được xử lý. Thông báo lỗi rõ, không báo thành công toàn bộ khi có lỗi.
- start bindController, nạp AccountSummary, gắn action CSV, để nội dung chi tiết trống; không tự đăng nhập tài khoản đầu. Các listener/field cần dùng trong initUI phải được tạo tại initUI vì BaseWindow gọi nó trong constructor.

### Ví dụ nối các phần ở Main — khi các lớp đã được triển khai

```java
SwingUtilities.invokeLater(() -> {
    SimulationClock clock = new SimulationClock(
        Instant.parse("2026-10-16T02:00:00Z"), ZoneId.of("Asia/Ho_Chi_Minh"));
    PinService pins = new PinService();
    List<Account> accounts = DemoDataFactory.createAccounts(clock);
    DemoDataFactory.registerDemoPins(pins, accounts);
    Bank bank = new Bank(accounts, pins, clock, new TransferService(),
                         new MonthlyInterestService());
    MainWindow window = new MainWindow();
    BankController controller = new BankController(window, bank, pins, clock,
        new ChartHelper(new StatisticsService()));
    controller.start();
    window.setVisible(true);
});
```

Ví dụ bỏ import cho dễ đọc. Không thay Main hiện tại bằng ví dụ trước khi các lớp cần dùng có mặt và biên dịch được.

## 12. Exception và file bổ sung cần tạo

Tất cả ở `exception`, constructor `(String message)` và kế thừa RuntimeException; có serialVersionUID. Không đặt một exception cùng tên ở nhiều package.

| Chủ trì | Exception | Khi dùng |
| --- | --- | --- |
| 3 | InsufficientFundsException | Không đủ tiền/khả dụng |
| 3 | WithdrawalLimitExceededException | Hết 3 lượt Savings |
| 4 | AccountNotMaturedException | Rút/chuyển ra trước hạn |
| 5 | InvalidAmountException | Tiền null, <= 0, có phần lẻ |
| 5 | AccountNotFoundException | Mã tài khoản không tồn tại |
| 5 | InvalidTransferException | Nguồn = đích hoặc điều kiện chuyển không hợp lệ |
| 8 | AccountLockedException | Tài khoản khóa |
| 8 | AuthenticationRequiredException | Chưa xác thực đúng tài khoản |

Sai cấu hình/constructor (mã rỗng, tài khoản trùng, hạn mức/lãi suất âm) dùng IllegalArgumentException, không tiếp tục khởi động với dữ liệu sai. IOException chỉ cho I/O CSV, không trộn thành lỗi thiếu tiền.

File bổ sung so với bảng cũ: người 2 tạo `model/AccountType.java`, `model/AccountSummary.java`; người 6 tạo `model/TransactionDirection.java`; người 4 tạo `service/SimulationClock.java`; người 5/8 tạo hai exception bổ sung ở bảng trên. Interface, record, enum và exception không thay thế yêu cầu ít nhất hai lớp có ý nghĩa mỗi thành viên.

## 13. Bàn giao theo API v1

- [ ] Từng tác giả tạo lớp/constructor/phương thức công khai đúng mục mình; không thay đổi chữ ký lặng lẽ.
- [ ] Các phần dùng chung có khung biên dịch trước 12:00 17/10. Không tạo hàm giả luôn báo giao dịch thành công; phần chưa làm phải ghi rõ và không đưa vào demo thật.
- [ ] Người 2/3/5/6 kiểm tra cơ chế nguyên tử, cả hai số dư/lịch sử/lượt rút khi lỗi giữa chừng.
- [ ] Người 8/5 kiểm tra BP01–BP09 tại UI lẫn tầng nghiệp vụ; người 6 kiểm tra BC01–BC05; người 4/9 kiểm tra BI01–BI03.
- [ ] Mọi tác giả gọi thử phần mình qua Bank/controller, ghép đủ 19/10, chốt bản cuối 20/10 21:00.

Phân công và Issue: [BAN_GIAO.md](BAN_GIAO.md). Quy tắc/test: [BONUS.md](BONUS.md). Sơ đồ: [LUONG_TONG_QUAN.md](LUONG_TONG_QUAN.md). Những checklist này là kế hoạch triển khai/kiểm tra, chưa có kết quả thực tế.
