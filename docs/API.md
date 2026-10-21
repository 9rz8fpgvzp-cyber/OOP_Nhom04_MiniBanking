# API chung v2 — đăng nhập theo khách hàng, chốt ngày 21/10/2026

Tài liệu quy định package, tên lớp, constructor, phương thức và kiểu dữ liệu dùng chung. Mỗi người triển khai phần mình theo các chữ ký dưới đây. Nếu cần đổi API, trao đổi với người dùng phương thức đó và cập nhật tài liệu trước khi ghép code.

**Trạng thái:** đây là hợp đồng mới đã thống nhất để sửa ứng dụng. Lần cập nhật này chỉ sửa tài liệu; mã Java trên `main` vẫn có thể đang theo v1 cho đến khi các phần được triển khai và kiểm thử cùng nhau.

**Luồng v2:** nhập mã khách hàng (user) + PIN → chỉ thấy STK của khách hàng đó → chọn STK để xem/giao dịch; đổi STK cùng chủ không hỏi lại PIN. Sai PIN 3 lần liên tiếp khóa đăng nhập **khách hàng trong 30 giây**. Đăng xuất xóa phiên và quay lại đăng nhập. Dữ liệu vẫn ở bộ nhớ; mở lại ứng dụng nạp lại dữ liệu mẫu.

Không tạo lớp cùng tên ở package khác. Tên constructor không có kiểu trả về; các chữ ký ở đây là mô tả, không phải các đoạn hoàn chỉnh để dán nguyên vào một file.

## 1. Quy ước chung

| Nội dung | Quy ước v2 |
| --- | --- |
| Java/package | Java 17, UTF-8; gốc `vn.edu.ptit.oop.nhom04` |
| Tiền | `BigDecimal`, VND nguyên đồng. Số tiền giao dịch phải > 0 và không có phần lẻ; `1000.00` hợp lệ, `1000.5` không hợp lệ. So sánh bằng `compareTo`, không chuyển qua double |
| Lãi suất | `BigDecimal` dạng tỷ lệ năm: `0.06` là 6%/năm. Lãi tháng = số dư × tỷ lệ / 12, làm tròn về đồng bằng `RoundingMode.HALF_UP`; số dư <= 0 không có lãi |
| Thời gian | `LocalDateTime` cho giao dịch; `LocalDate` cho ngày mở/đáo hạn; `YearMonth` cho giới hạn tháng và kỳ lãi. Một `SimulationClock` dùng chung cho nghiệp vụ; đồng hồ thật riêng cho khóa PIN 30 giây |
| Mã | String; mã khách hàng/số tài khoản duy nhất, không rỗng. Mã giao dịch UUID String; hai phía chuyển khoản dùng chung `referenceId` |
| Danh sách | Trả bản sao không sửa được (`List.copyOf`); sắp lịch sử theo thời gian rồi thứ tự ghi nhận, không dùng Map theo timestamp làm mất giao dịch |
| Lỗi nghiệp vụ | Exception tự định nghĩa đều kế thừa `RuntimeException`; GUI bắt và hiển thị message tiếng Việt. Không dùng `false` để báo giao dịch thất bại |
| Luồng chạy | Một ứng dụng, thao tác Swing trên EDT. Bank tuần tự hóa mọi thao tác thay đổi; không thêm luồng tự động chạy lãi. Không tuyên bố hỗ trợ đồng thời nhiều máy |
| Phân quyền | Một `PinService` dùng chung, xác thực theo `customerId`. `Bank` kiểm tra STK nguồn/đang xem thuộc khách hàng đã đăng nhập và khách hàng không bị khóa. Model/repository là nội bộ nghiệp vụ, không mở dialog |
| User và STK | User là mã khách hàng `customerId` (ví dụ `KH001`); STK là `accountNumber` (ví dụ `100001`). Một khách hàng có nhiều STK nhưng chỉ một PIN đăng nhập. Bỏ khoảng trắng đầu/cuối mã khi nhập; mã phân biệt hoa/thường |
| Hiển thị | Tiếng Việt có dấu; ngày `dd/MM/yyyy`, thời điểm `dd/MM/yyyy HH:mm:ss`, tháng `MM/yyyy`. Enum vẫn giữ tên trong code; chuyển nhãn ở GUI. CSV giữ định dạng dữ liệu máy đọc ở mục 9 |
| Lưu trữ | Repository dùng `Map` trong bộ nhớ. Không lưu/khôi phục trạng thái ứng dụng khi đóng/mở; file CSV đã xuất không được tự nhập lại |

**Nhóm chốt thêm `TransactionType.INTEREST` để phân biệt tiền lãi với tiền người dùng nạp.** CSV giữ cột như trước; lịch sử hiển thị “Cộng lãi”. Tổng nạp/rút của biểu đồ cột chỉ tính DEPOSIT/WITHDRAW; không tính TRANSFER/INTEREST. Biểu đồ đường tính mọi biến động số dư, kể cả chuyển khoản và lãi.

## 2. Model nền — người 2

File nằm trong `model`, trừ `util/DemoDataFactory.java`.

```java
public enum AccountType { SAVINGS, CHECKING, FIXED_TERM }

// Danh sách STK thuộc khách hàng đã đăng nhập; không chứa số dư hay PIN.
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

- `Customer` giữ ba trường `customerId`, `fullName`, `phoneNumber`; dùng `customerId` làm user, không thêm username riêng vào model. Cùng một mã khách hàng phải có thông tin nhất quán trên các STK.
- `createAccounts` trả 4–6 tài khoản, đủ ba loại, ít nhất ba khách hàng; có FixedTerm chưa/đã đáo hạn. Lịch sử mẫu khớp số dư và không nằm sau thời điểm mô phỏng hiện tại.
- Giữ chữ ký `registerDemoPins(PinService, List<Account>)`, nhưng đổi ý nghĩa: gom chủ sở hữu theo `customerId`, gọi `registerPin(customerId, pin)` **một lần cho mỗi khách hàng**, không đăng ký theo STK. Hai STK của một người dùng chung PIN đăng nhập.
- Bảng PIN mẫu khóa theo mã khách hàng; phải có PIN được khai báo cho mọi chủ tài khoản. Kiểm tra toàn bộ dữ liệu trước khi đăng ký: không null, không mã khách hàng rỗng, không thông tin khách hàng mâu thuẫn, không thiếu PIN. Không tự gán PIN mặc định cho mã chưa khai báo. Nhiều STK cùng một chủ là hợp lệ, không coi là trùng đăng ký.
- PIN mẫu ghi trong `docs/DEMO.md` và cấu hình bootstrap, không hiện trên UI hoặc log. Xóa mỗi mảng `char[]` sau khi truyền vào `PinService`, kể cả khi đăng ký lỗi. Gọi bootstrap một lần trên dịch vụ mới; không tự đăng nhập hoặc reset khóa.
- Bộ dữ liệu tham chiếu hiện trên `main`: `KH001` có `100001/100002`, `KH002` có `100003/100004`, `KH003` có `100005/100006`. Khi chọn bộ dữ liệu khác phải cập nhật đồng bộ hướng dẫn và test. Trong v2, các PIN cũ theo từng STK được thay bằng một PIN đã chốt cho mỗi khách hàng.

Bảng PIN tham chiếu để triển khai v2 trên bộ dữ liệu trên (chỉ là PIN giả phục vụ demo):

| User / mã khách hàng | Họ tên | PIN demo v2 |
| --- | --- | --- |
| `KH001` | Nguyễn Văn An | `001234` |
| `KH002` | Trần Thị Bình | `234567` |
| `KH003` | Lê Minh Chi | `456789` |

Khi triển khai phải đổi bảng đăng ký PIN và test theo bảng này; không dùng PIN từng STK trong hướng dẫn v1 để đăng nhập v2.

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

// model/InterestBearing.java — người 4
public interface InterestBearing {
    BigDecimal calculateMonthlyInterest();
}
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

`TransactionTableModel` giữ chữ ký hiện có. Trên GUI, loại INTEREST hiện “Cộng lãi”; mô tả mã hóa `INTEREST:yyyy-MM` được trình bày thành tiếng Việt, ví dụ “Cộng lãi tháng 10/2026”. Không sửa bản ghi Transaction hoặc dữ liệu CSV chỉ để đổi cách hiển thị.

## 5. PIN theo khách hàng và khóa 30 giây — người 8

```java
// service.PinService; mọi tham số định danh dưới đây đều là customerId, KHÔNG phải STK.
public PinService(); // dùng Clock.systemUTC() cho xác thực
public PinService(Clock authenticationClock); // để kiểm thử thời gian khóa
public void registerPin(String customerId, char[] pin);
public boolean authenticate(String customerId, char[] pin);
public int getRemainingAttempts(String customerId);
public boolean isLocked(String customerId);
public long getRemainingLockSeconds(String customerId);
public boolean isAuthenticated(String customerId);
public Optional<String> getAuthenticatedCustomerId();
public void requireAuthenticated(String customerId);
public void logout();

// model.PinCredential: nội bộ người 8, không để GUI đọc hash hoặc thời điểm khóa trực tiếp.
public PinCredential(char[] pin);
public PinCredential(char[] pin, Clock authenticationClock);
public static boolean isValidPin(char[] pin);
public boolean matches(char[] inputPin);
public void recordFailedAttempt();
public void resetFailedAttempts();
public int getRemainingAttempts();
public boolean isLocked();
public long getRemainingLockSeconds();

// ui.dialogs.LoginDialog: hai ô nhập user (mã khách hàng) và PIN, modal.
public LoginDialog(Window owner, PinService pinService);
public boolean showDialog();
```

### Phiên và quyền đăng nhập

- PIN đúng 6 chữ số ASCII, giữ số 0 đầu; lưu hash + salt. `registerPin` chỉ bootstrap, không cho ghi đè mã đã đăng ký, kể cả khi đang khóa. Không giữ nguyên mảng PIN đầu vào; bên gọi xóa `char[]` sau khi gọi.
- `PinService` giữ một `PinCredential` cho mỗi **customerId** và nhiều nhất một `authenticatedCustomerId`. Mọi STK của cùng khách hàng dùng chung trạng thái đăng nhập, bộ đếm sai và khóa.
- Trước mỗi lần `authenticate`, xóa phiên cũ. Đúng PIN khi không khóa → trả true, reset số sai và xác thực khách hàng đó. Sai lần 1/2 → trả false. PIN null/sai định dạng khi xác thực cũng tính một lần sai cho user đã đăng ký; đăng ký PIN sai định dạng ném `IllegalArgumentException`.
- User không tồn tại → `AccountNotFoundException` với thông báo rõ “Không tìm thấy khách hàng…”, không tăng số sai của người khác. Giữ tên exception để dùng chung cho định danh không tìm thấy; tại `Bank`, exception này vẫn chỉ STK không tồn tại. Mã rỗng/null là nhập liệu không hợp lệ; GUI yêu cầu nhập trước khi gọi xác thực.
- `getAuthenticatedCustomerId()` chỉ trả mã khi phiên vẫn hợp lệ; không trả STK. `isAuthenticated` không tạo phiên. `requireAuthenticated(customerId)` kiểm tra khóa trước, rồi kiểm tra đúng user; tương ứng ném `AccountLockedException` hoặc `AuthenticationRequiredException`.
- `isAuthenticated` trả false với mã null/rỗng/chưa đăng ký. Các hàm hỏi số lượt/hạn khóa và `requireAuthenticated` từ chối mã chưa đăng ký bằng `AccountNotFoundException`, không tự tạo credential. Định danh đã đăng ký được chuẩn hóa bằng bỏ khoảng trắng đầu/cuối; không tự đổi hoa/thường.
- `logout` chỉ xóa phiên; không xóa số lần sai, không xóa thời hạn khóa. Chọn STK khác cùng chủ không gọi `authenticate` hoặc `logout`.

### Quy tắc 30 giây

- Sai liên tiếp lần thứ ba → đặt hạn khóa bằng thời điểm thật hiện tại cộng **30 giây**, xóa quyền và ném `AccountLockedException`.
- Khi còn khóa, PIN đúng vẫn bị từ chối. Thử lại trong lúc khóa không tăng số sai và không kéo dài hạn khóa. Khóa là khóa đăng nhập khách hàng, không xóa tiền, không cấm tài khoản nhận chuyển khoản hoặc nhận lãi hệ thống.
- Tại hoặc sau hạn khóa, lần kiểm tra tiếp theo tự bỏ khóa và đưa số sai về 0. `getRemainingAttempts` trả 3 khi vừa hết khóa; vẫn phải đăng nhập đúng PIN mới có phiên. Không tự đăng nhập sau đếm ngược.
- `getRemainingLockSeconds` trả 0 nếu không còn khóa; khi còn khóa làm tròn phần giây còn lại lên trên, ví dụ còn 0,2 giây trả 1. `isLocked`, `getRemainingAttempts`, `matches`, `recordFailedAttempt` và `requireAuthenticated` phải nhất quán tại ranh giới hết hạn.
- `PinCredential` là nơi giữ bộ đếm/hạn khóa và dùng `authenticationClock` do `PinService` truyền vào; không tạo hai bộ đếm độc lập ở service và model.
- Đồng hồ xác thực mặc định là **đồng hồ thật**, độc lập hoàn toàn với `SimulationClock`. Nút Cuối tháng không làm hết khóa. Khi test, truyền Clock có thể điều khiển để kiểm tra mốc 29,999/30 giây; không chờ thật hoặc ngủ trên EDT.
- Đóng rồi mở lại ứng dụng sẽ tạo mới dữ liệu/PIN/trạng thái khóa trong bộ nhớ, đúng phạm vi đã chốt; không lưu khóa xuống ổ đĩa.

### LoginDialog

- Dialog có trường user và `JPasswordField`. User nhập `customerId`, ví dụ `KH001`, thay vì chọn STK. Hiển thị sai PIN/còn lượt/khóa và thời gian còn lại bằng tiếng Việt.
- `showDialog` trả true chỉ khi dịch vụ đã xác thực; controller lấy user từ `getAuthenticatedCustomerId`, không lấy từ STK hoặc tin riêng giá trị ô nhập. Mỗi lần mở mới không dùng lại cờ thành công cũ.
- Hủy/đóng X trả false và không gọi `authenticate`; phiên cũ đã được controller xóa trước khi mở dialog.
- Đếm ngược bằng `javax.swing.Timer`, hỏi dịch vụ thời gian còn lại; không tự trừ một bộ đếm riêng hoặc dùng `Thread.sleep`. Hết hạn cho nhập lại, không tự gửi yêu cầu đăng nhập.
- Khi user đang khóa có thể đổi ô user để đăng nhập khách hàng khác; trạng thái vô hiệu hóa nút/PIN chỉ áp dụng cho user đang bị khóa đó. Đổi user hoặc mở lại dialog không reset số sai/khóa của user cũ. Dừng Timer khi đóng dialog; luôn xóa mảng PIN sau mỗi lần thử.

## 6. Bank, quyền sở hữu và repository — người 5 phối hợp người 1/2

```java
// service.BankingOperations: interface GUI được phép gọi
Optional<Customer> getCurrentCustomer();
List<AccountSummary> listAccounts();
Account getAccount(String accountNumber);
List<Transaction> getTransactions(String accountNumber);
Transaction deposit(String accountNumber, BigDecimal amount);
Transaction withdraw(String accountNumber, BigDecimal amount);
List<Transaction> transfer(String sourceNumber, String targetNumber, BigDecimal amount);
List<Transaction> simulateMonthEnd();

// service.Bank implements BankingOperations
public Bank(AccountRepository repository, PinService pins, SimulationClock clock,
            TransferService transfers, MonthlyInterestService interestService);

// service.TransferService: chỉ dùng nội bộ sau khi Bank kiểm tra quyền nguồn
public TransferService();
public List<Transaction> transfer(Account source, Account target,
                                  BigDecimal amount, LocalDateTime time);
```

- `getCurrentCustomer` trả `Optional.empty()` khi chưa có phiên hợp lệ. Khi có phiên, chỉ trả Customer khớp `customerId` đang đăng nhập, gồm mã/tên/điện thoại. Không cung cấp danh sách chi tiết tất cả khách hàng cho GUI trước đăng nhập.
- `listAccounts` trả danh sách rỗng trước đăng nhập hoặc khi phiên không hợp lệ. Sau đăng nhập chỉ trả AccountSummary thuộc user hiện tại; không lộ STK của user khác trên danh sách chính.
- `getAccount/getTransactions/deposit/withdraw` tìm STK, kiểm tra user sở hữu bằng `account.getOwner().getCustomerId()` và yêu cầu `PinService.requireAuthenticated(customerId)` tương ứng **trước khi trả dữ liệu hoặc thay đổi model**. User A không được gọi các hàm này trên STK của B dù biết STK đó hoặc tự gọi API ngoài nút GUI.
- User sai quyền/chưa đăng nhập → `AuthenticationRequiredException`; chủ STK bị khóa → `AccountLockedException`; STK không tồn tại → `AccountNotFoundException`. Không trả model, lịch sử hoặc số dư của người khác trong thông báo lỗi.
- `transfer` kiểm tra quyền sở hữu **nguồn**, tìm đủ hai tài khoản, từ chối cùng nguồn/đích và kiểm tra tiền, rồi gọi TransferService. Đích có thể cùng chủ hoặc khác chủ; người nhận không cần đăng nhập, kể cả khi đăng nhập của họ đang khóa. Không yêu cầu đích nằm trong `listAccounts()` của người gửi.
- GUI nhập STK đích trong TransactionDialog; không mở API lấy chi tiết/số dư người nhận hoặc liệt kê toàn bộ STK để dựng bộ chọn. Mã không tồn tại do Bank từ chối trước khi chuyển, không làm thay đổi nguồn.
- Account trả ra là model chỉ đọc từ góc nhìn GUI; GUI không gọi hàm thay đổi trực tiếp. Repository chỉ được Bank/bootstrap dùng nội bộ; không truyền repository cho GUI để đi vòng kiểm tra quyền.
- Các hàm thay đổi của Bank chạy tuần tự, chặn lồng một thao tác khác vào giữa quá trình chuẩn bị/commit. Lấy timestamp đúng một lần từ SimulationClock cho mỗi giao dịch.
- `simulateMonthEnd` vẫn là tác vụ **mô phỏng hệ thống**: lấy tất cả tài khoản nội bộ từ repository, xử lý mọi tài khoản hưởng lãi, kể cả chủ đang khóa. Không cần PIN từng khách hàng/STK; quyền này không cho GUI đọc chi tiết tất cả tài khoản hoặc thực hiện giao dịch thay người khác.

### Repository trong bộ nhớ — người 1 phối hợp người 2/5

Thêm đúng hai file trong package `repository`:

```java
// repository.AccountRepository
public interface AccountRepository {
    Optional<Account> findByNumber(String accountNumber);
    List<Account> findAll();
    List<Account> findByCustomerId(String customerId);
}

// repository.InMemoryAccountRepository implements AccountRepository
public InMemoryAccountRepository(List<Account> accounts);
```

- `InMemoryAccountRepository` giữ `Map<String, Account>`; nạp danh sách một lần khi bootstrap và giữ thứ tự tài khoản ổn định. Bank dùng repository này thay cho một Map thứ hai chứa bản sao tài khoản.
- Constructor kiểm tra toàn bộ đầu vào, từ chối null, phần tử null, trùng STK hoặc chủ không hợp lệ. Cùng một khách hàng có nhiều STK là hợp lệ. Không tự tạo tài khoản thay thế cho mã không tồn tại.
- `findByNumber` trả Optional.empty nếu mã không tồn tại/null/rỗng. `findByCustomerId` trả rỗng nếu không có hoặc mã null/rỗng; chỉ lọc đúng `owner.customerId`. Mã hợp lệ được xử lý nhất quán với quy tắc bỏ khoảng trắng đầu/cuối.
- Danh sách trả về là snapshot không sửa được; đối tượng Account bên trong vẫn là **cùng đối tượng nghiệp vụ đang được Bank cập nhật**, không clone mỗi lần tìm. Không cung cấp setter số dư hay hàm sửa lịch sử để lưu lại giao dịch.
- Repository chỉ giữ/tìm dữ liệu; không xác thực PIN, không chuyển tiền, không tính lãi, không mở dialog. Quyền truy cập và thao tác ghi nằm ở Bank/model.
- Không dùng database hoặc lưu/khôi phục ứng dụng bằng file. Khởi động lại tạo repository mới với dữ liệu mẫu. CSV là bản sao kê xuất ra, không phải file khôi phục.

## 7. Đồng hồ và cuối tháng — người 4

File `service/SimulationClock.java` kế thừa `java.time.Clock`; đây là nguồn thời gian chung cho nghiệp vụ ngân hàng, không dùng để tính thời hạn khóa PIN. Người 1 khởi tạo, các phần khác chỉ đọc. `withZone` trả Clock phù hợp không làm lệch thời điểm; không tự đặt tháng riêng ở GUI/biểu đồ.

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
- Savings dùng YearMonth của thời gian mô phỏng nên tháng mới có lại 3 lượt, không reset số lần sai hoặc thời hạn khóa PIN. Mọi giao dịch mới dùng clock mô phỏng mới; chỉ bộ khóa PIN 30 giây dùng đồng hồ thật riêng.
- Người 4 gọi Account.creditInterest theo chữ ký mục 2, người 2 triển khai phần cập nhật nhất quán. Không gọi deposit rồi sửa lại loại giao dịch sau khi tạo immutable Transaction.

## 8. UI tài khoản và giao dịch — người 7

```java
// ui.AccountListRenderer implements ListCellRenderer<AccountSummary>
public AccountListRenderer();

// ui.panels.AccountDetailsPanel extends JPanel
public AccountDetailsPanel();
public void setCustomer(Customer customer);
public void setAccount(Account account, YearMonth currentMonth);
public void clearAccount(); // xóa riêng phần STK, giữ thông tin khách hàng
public void clear();        // xóa cả khách hàng lẫn STK

// ui.dialogs.TransactionDialog extends JDialog, modal
public TransactionDialog(Window owner, BankingOperations bank,
                         TransactionType operation, String sourceNumber,
                         Runnable afterSuccess);
public void showDialog();

// ui.dialogs.AboutDialog extends JDialog, modal
public AboutDialog(Window owner);
public void showDialog();
```

- Renderer nhận AccountSummary thuộc khách hàng đã đăng nhập. Hiện số tài khoản, icon khác nhau theo loại và nhãn tiếng Việt: SAVINGS → “Tiết kiệm”, CHECKING → “Thanh toán”, FIXED_TERM → “Tiền gửi có kỳ hạn”. Enum trong model không đổi.
- Sau đăng nhập, `setCustomer` hiện đủ mã khách hàng, họ tên, số điện thoại, không hiện PIN. Hàm này chỉ cập nhật phần khách hàng; không làm thay đổi dữ liệu nghiệp vụ. Trước khi chọn STK, phần số dư/lịch sử/biểu đồ để trống hoặc placeholder.
- `setAccount` cập nhật cả thông tin khách hàng và STK sau khi Bank cho phép; tên loại tài khoản dùng tiếng Việt, ngày đáo hạn `dd/MM/yyyy`. `clearAccount` giữ thông tin khách hàng, xóa STK/số dư/lãi/lượt/đáo hạn; `clear` xóa tất cả khi đăng xuất.
- operation của TransactionDialog chỉ DEPOSIT/WITHDRAW/TRANSFER; INTEREST không phải loại nhập tay. Với chuyển khoản có ô nhập STK đích, bỏ khoảng trắng đầu/cuối và kiểm tra không rỗng. Không lấy danh sách đích từ `bank.listAccounts()` vì danh sách đó chỉ chứa STK của người gửi. Dialog gọi Bank; Bank kiểm tra đích tồn tại và quyền nguồn, GUI không cập nhật trực tiếp Account.
- Hủy đóng dialog, không gọi nghiệp vụ/callback. Thành công đóng hoặc vô hiệu hóa dialog giao dịch rồi gọi afterSuccess đúng một lần. Lỗi nghiệp vụ giữ dialog để sửa; lỗi refresh sau thành công phải báo riêng, không cho nhập lại cùng thao tác vì tưởng giao dịch chưa chạy. Không phát sinh giao dịch trong constructor.
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

Action tự lấy tài khoản đang chọn, gọi Bank.getTransactions để kiểm tra quyền sở hữu của khách hàng đang đăng nhập trước khi chọn/ghi file, mở JFileChooser và hỏi ghi đè, gọi exporter, báo kết quả. Hủy không ghi. Khi ghi vẫn lấy lại lịch sử được phép; không dùng snapshot tài khoản khác hoặc đọc trực tiếp Account.getTransactions từ GUI.

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
// ui.MainWindow
public MainWindow();
public void bindController(BankController controller);
public void showCustomer(Customer customer);
public void setAccountChoices(List<AccountSummary> accounts);
public Optional<String> getSelectedAccountNumber();
public void showAccount(Account account, YearMonth currentMonth);
public void showTransactions(List<Transaction> transactions);
public void showCharts(JPanel balanceChart, JPanel depositWithdrawalChart);
public void clearAccountContent();  // xóa riêng phần STK, giữ khách hàng và danh sách STK của họ
public void clearCustomerContent(); // xóa khách hàng, danh sách STK, lựa chọn, chi tiết, lịch sử, biểu đồ
public void showStatus(String message);

// ui.BankController
public BankController(MainWindow window, BankingOperations bank,
                      PinService pins, Clock clock, ChartHelper charts);
public void start();
public void login();
public void selectAccount(String accountNumber);
public void openTransaction(TransactionType operation);
public void refreshSelectedAccount();
public void logout();
public void simulateMonthEnd();
```

### Luồng đăng nhập đã chốt

1. `start` bind controller, gắn CsvExportAction vào menu/toolbar, xóa toàn bộ nội dung khách hàng và mở LoginDialog. Không nạp danh sách STK trước đăng nhập, không tự đăng nhập khách hàng đầu tiên. Gọi start nhiều lần không nhân đôi listener hoặc tự mở thêm dialog.
2. `login` xóa phiên/nội dung cũ trước khi mở dialog user + PIN. Nếu thành công, đọc user từ PinService, lấy `bank.getCurrentCustomer()` và `bank.listAccounts()`, hiện mã/tên/điện thoại cùng danh sách STK đã lọc. Không tin riêng giá trị true của dialog nếu service/Bank không còn phiên hợp lệ.
3. Hủy/đóng X ở LoginDialog để lại màn hình chào trống với nút Đăng nhập; không hiện lại thông tin khách hàng cũ. Khách hàng có thể mở lại dialog, nhưng số sai/hạn khóa vẫn giữ trong phiên chạy ứng dụng.
4. `selectAccount` xóa riêng nội dung STK trước, kiểm tra quyền qua Bank rồi refresh tài khoản được chọn. **Không logout, không mở LoginDialog, không nhập lại PIN** khi đổi giữa STK cùng khách hàng. Lựa chọn rỗng giữ phiên và thông tin khách hàng; chọn STK không thuộc quyền bị từ chối, không lộ dữ liệu.
5. `openTransaction` chỉ mở khi đã chọn STK hợp lệ và Bank cho phép. Tạo TransactionDialog với callback `this::refreshSelectedAccount`. GUI không truyền customerId tự nhập để thay phiên dịch vụ.
6. `logout` xóa phiên PinService, gọi `clearCustomerContent`, vô hiệu hóa thao tác tài khoản và quay lại LoginDialog. Nếu hủy dialog, về màn hình chào. Việc xóa phiên dùng chung bên trong `login` phải tách khỏi thao tác mở dialog để không gọi đệ quy login/logout.
7. `refreshSelectedAccount` kiểm tra phiên khách hàng; hết phiên phải xóa toàn bộ dữ liệu riêng tư. Nếu phiên hợp lệ nhưng chưa chọn STK, giữ thông tin khách hàng/danh sách và để phần STK trống. Có lựa chọn thì lấy Account/history qua Bank rồi dựng lại chi tiết, bảng, biểu đồ.
8. `simulateMonthEnd` gọi Bank, luôn làm mới dữ liệu đang được phép xem cả khi thành công hoặc lỗi một phần. Không hiển thị chi tiết các user khác dù đợt lãi xử lý toàn hệ thống. Thông báo phân biệt lỗi nghiệp vụ với lỗi làm mới giao diện sau giao dịch đã thành công.

### Giao diện và tài nguyên

- MainWindow giữ bố cục, nhúng renderer/details/table/chart. `showCustomer` chuyển thông tin cho AccountDetailsPanel; `clearAccountContent` gọi `clearAccount`, còn `clearCustomerContent` gọi `clear` và xóa cả danh sách STK, tránh sự kiện chọn cũ mở lại dữ liệu.
- Màn hình chưa đăng nhập chỉ có nội dung chào/nút Đăng nhập, không hiện danh sách STK hoặc thông tin khách hàng. Khi có phiên hiện user/mã/tên/điện thoại rõ ràng. Nút Đăng xuất dùng được cả khi chưa chọn STK; các nút nạp/rút/chuyển/CSV chỉ bật khi STK đang chọn hợp lệ.
- Nút Cuối tháng trên giao diện demo chỉ bật khi đã đăng nhập, không phụ thuộc đã chọn STK hay chưa. Tại tầng Bank đây vẫn là tác vụ hệ thống xử lý toàn bộ tài khoản theo mục 6, không đòi xác thực lần lượt từng chủ tài khoản.
- Menu có mnemonic/phím tắt; toolbar có icon PNG 16×16 và tooltip tiếng Việt. Thêm ảnh trong `src/resources/icons/`, dùng đường dẫn classpath `/resources/icons/...`; có `app.png` làm icon cửa sổ. Copy tài nguyên và đóng gói đủ thư viện khi tạo JAR, không dùng đường dẫn ảnh riêng trên máy.
- Tên loại tài khoản, nhãn, mô tả lịch sử và mọi thông báo lỗi dùng tiếng Việt có dấu. Ngày/giờ ở GUI theo mục 1; dữ liệu Java/CSV không đổi chỉ vì định dạng hiển thị. Thông báo đáo hạn trong FixedTermAccount cũng dùng ngày `dd/MM/yyyy` và câu có dấu.
- Các listener/field cần dùng trong initUI được tạo tại initUI vì BaseWindow gọi nó trong constructor. Mọi thao tác Swing trên EDT; Timer đếm ngược không chặn EDT. `showStatus` gọi `setStatus` được kế thừa.

### Ví dụ nối các phần ở Main — triển khai sau khi các API v2 có mặt

```java
SwingUtilities.invokeLater(() -> {
    SimulationClock clock = new SimulationClock(
        Instant.parse("2026-10-21T02:00:00Z"), ZoneId.of("Asia/Ho_Chi_Minh"));
    PinService pins = new PinService(Clock.systemUTC()); // đồng hồ thật, không truyền clock mô phỏng
    List<Account> accounts = DemoDataFactory.createAccounts(clock);
    DemoDataFactory.registerDemoPins(pins, accounts);   // đăng ký theo customerId
    AccountRepository repository = new InMemoryAccountRepository(accounts);
    Bank bank = new Bank(repository, pins, clock, new TransferService(),
                         new MonthlyInterestService());
    MainWindow window = new MainWindow();
    BankController controller = new BankController(window, bank, pins, clock,
        new ChartHelper(new StatisticsService()));
    window.setVisible(true);
    controller.start(); // mở dialog đăng nhập, chưa hiện STK
});
```

Ví dụ bỏ import cho dễ đọc. Mốc thời gian mô phỏng là dữ liệu demo, độc lập đồng hồ xác thực. Chưa thay Main bằng ví dụ này khi các chữ ký v2 chưa được triển khai cùng nhau.

## 12. Exception và file bổ sung cần tạo

Tất cả ở `exception`, constructor `(String message)` và kế thừa RuntimeException; có serialVersionUID. Không đặt một exception cùng tên ở nhiều package.

| Chủ trì | Exception | Khi dùng |
| --- | --- | --- |
| 3 | InsufficientFundsException | Không đủ tiền/khả dụng |
| 3 | WithdrawalLimitExceededException | Hết 3 lượt Savings |
| 4 | AccountNotMaturedException | Rút/chuyển ra trước hạn |
| 5 | InvalidAmountException | Tiền null, <= 0, có phần lẻ |
| 5/8 | AccountNotFoundException | STK không tồn tại ở Bank; user không tồn tại ở PinService, thông báo phân biệt rõ |
| 5 | InvalidTransferException | Nguồn = đích hoặc điều kiện chuyển không hợp lệ |
| 8 | AccountLockedException | Đăng nhập khách hàng đang khóa 30 giây |
| 8 | AuthenticationRequiredException | Chưa đăng nhập hoặc STK không thuộc khách hàng đã đăng nhập |

Sai cấu hình/constructor (mã rỗng, tài khoản trùng, hạn mức/lãi suất âm) dùng IllegalArgumentException, không tiếp tục khởi động với dữ liệu sai. IOException chỉ cho I/O CSV, không trộn thành lỗi thiếu tiền.

File bổ sung so với bảng cũ: người 2 tạo `model/AccountType.java`, `model/AccountSummary.java`; người 6 tạo `model/TransactionDirection.java`; người 4 tạo `service/SimulationClock.java`; người 5/8 tạo hai exception bổ sung ở bảng trên. Interface, record, enum và exception không thay thế yêu cầu ít nhất hai lớp có ý nghĩa mỗi thành viên.

## 13. Chuyển từ v1 sang v2 và nghiệm thu

V2 thay thế luồng PIN theo STK của v1. Đây là thay đổi thiết kế nhóm đã chốt; luồng đăng nhập theo user và thời hạn 30 giây là yêu cầu bổ sung, không ghi là chi tiết bắt buộc nguyên văn trong PDF của thầy.

| Điểm gọi v1 | Hợp đồng v2 |
| --- | --- |
| `registerPin/authenticate/...` nhận STK | Giữ tên phương thức nhưng mọi bên phải truyền **customerId**; không chỉ đổi tên biến ở service |
| `getAuthenticatedAccountNumber()` | Thay bằng `getAuthenticatedCustomerId()`; không dùng STK làm phiên hoặc giữ alias dễ nhầm |
| `new LoginDialog(owner, accountNumber, pins)` | `new LoginDialog(owner, pins)`, dialog có ô user + PIN |
| `new Bank(accounts, pins, ...)` | `new Bank(repository, pins, ...)` |
| `listAccounts()` trả tất cả STK trước PIN | Chưa đăng nhập trả rỗng; có phiên chỉ trả STK của user đó |
| Chọn STK mới thì logout/hỏi PIN | Giữ phiên khách hàng; Bank kiểm tra quyền sở hữu STK |
| `clearAccountContent()` xóa cả thông tin chủ | V2 chỉ xóa phần STK; `clearCustomerContent()` mới xóa cả user và danh sách |
| Combo đích lấy từ `listAccounts()` | Nhập STK đích, Bank xác thực nguồn và tìm đích nội bộ |
| Khóa đến khi khởi động lại | Khóa 30 giây theo đồng hồ thật; hết hạn cho thử PIN lại |

### Checklist triển khai

- [ ] Người 8: đổi PinService/PinCredential/LoginDialog sang customerId, khóa 30 giây và đếm ngược.
- [ ] Người 2 phối hợp người 1: đổi đăng ký PIN mẫu sang mỗi khách hàng một PIN; cập nhật `docs/DEMO.md` với đúng user/PIN của bộ dữ liệu được chọn.
- [ ] Người 1/5: thêm AccountRepository/InMemoryAccountRepository, đổi constructor Bank và kiểm tra quyền nguồn/đọc theo owner.customerId.
- [ ] Người 1/7: đổi luồng controller/cửa sổ, hiện ID/tên/điện thoại sau đăng nhập, Việt hóa loại tài khoản/ngày/thông báo, nhập STK nhận và thêm icon PNG.
- [ ] Cập nhật Main, các bên gọi API, test, hướng dẫn và mô tả luồng cùng nhau trước khi đánh dấu v2 hoàn thành. Không giữ các điểm gọi cũ truyền STK vào PinService chỉ để biên dịch được.
- [ ] Giữ nguyên quy tắc tiền, nguyên tử giao dịch, ba loại tài khoản, lãi, CSV, thống kê/biểu đồ và dữ liệu trong bộ nhớ; kiểm tra hồi quy các phần này.

### Ca kiểm tra bắt buộc cho v2

- [ ] Mở app chưa có phiên không hiện STK, ID/tên/điện thoại hoặc lịch sử khách hàng; Hủy/X không làm lộ dữ liệu.
- [ ] Đăng nhập đúng PIN có số 0 đầu; hiện đúng khách hàng và chỉ STK của họ. Đổi hai STK cùng chủ không yêu cầu PIN lại.
- [ ] Gọi trực tiếp Bank để xem/nạp/rút/chuyển từ STK người khác hoặc xuất lịch sử CSV phải bị từ chối, giữ nguyên số dư/lịch sử.
- [ ] Chuyển từ nguồn được phép đến STK khác chủ, kể cả chủ đích đang khóa, vẫn được nhận tiền. Sai đích/cùng STK/sai tiền không thay đổi dữ liệu.
- [ ] Sai PIN lần 1/2 còn 2/1 lượt; đúng trước lần thứ ba reset số sai. Sai định dạng khi xác thực user có thật cũng tính một lần sai.
- [ ] Sai lần thứ ba khóa toàn bộ đăng nhập user đó; STK cùng chủ không thể dùng để đi vòng. User khác không bị khóa theo.
- [ ] Mở lại dialog/đăng xuất/đổi user/thử PIN đúng trong lúc khóa không reset hoặc kéo dài hạn khóa. Timer không khóa việc nhập user khác.
- [ ] Sau 29,999 giây vẫn khóa; tại 30 giây hết khóa, còn 3 lượt nhưng chưa tự xác thực. Dùng đồng hồ kiểm thử, không chờ thật.
- [ ] Nút cuối tháng làm đổi SimulationClock nhưng không làm hết hạn khóa 30 giây; thời hạn khóa không làm đổi timestamp giao dịch.
- [ ] Đăng xuất xóa cả phiên, STK, lựa chọn, chi tiết, thông tin chủ, lịch sử và biểu đồ. User B đăng nhập không thấy nội dung A.
- [ ] Cuối tháng thành công/một phần vẫn refresh đúng dữ liệu được phép; lỗi refresh không bị coi là giao dịch thất bại và không cho gửi trùng.
- [ ] Đóng rồi mở app nạp lại dữ liệu mẫu và phiên mới; file CSV đã xuất vẫn ở ổ đĩa nhưng không tự nhập lại.
- [ ] JAR chạy được với thư viện/icon trên máy khác; tên loại, ngày, lỗi và thông tin khách hàng hiển thị đúng tiếng Việt.

Phân công tham khảo: [BAN_GIAO.md](BAN_GIAO.md). Quy tắc/test cũ: [BONUS.md](BONUS.md). Sơ đồ cũ: [LUONG_TONG_QUAN.md](LUONG_TONG_QUAN.md). Những phần mô tả xác thực theo STK/khóa vĩnh viễn trong tài liệu cũ phải được cập nhật theo v2 trước bàn giao; không dùng chúng để ghi đè hợp đồng này. Checklist trên là yêu cầu nghiệm thu, không phải báo cáo các test đã chạy.
