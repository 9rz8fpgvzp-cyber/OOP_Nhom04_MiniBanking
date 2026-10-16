# MiniBank — Nhóm 04

Bài tập lớn môn Lập trình hướng đối tượng: xây dựng ứng dụng ngân hàng giả lập bằng **Java Swing**, chạy trên **một máy**. Nhóm có **9 thành viên**, thời gian thực hiện **1 tuần**.

- [Repository của nhóm](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking)
- [Bảng phân công tổng — Issue #1](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/1)
- [Các công việc trong Issues](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues)
- [Các Pull Request](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/pulls)
- Bảng tiến độ: mở tab **Projects** của repository, chọn **MiniBank - oop**.

**Thành viên mới:** đọc mục 3 để tải và chạy project, sau đó làm theo mục 5 mỗi khi bắt đầu một nhiệm vụ.

## 1. Phạm vi và trạng thái hiện tại

### Bản hiện tại

- Đã có project Eclipse, cấu hình biên dịch Java 17 và mã hóa UTF-8.
- Đã có `Main`, `BaseWindow`, `MainWindow`.
- Chạy được cửa sổ MiniBank với menu File/View/Help và thanh trạng thái.
- Các phần nghiệp vụ, dialog riêng, icon và biểu đồ đang chờ triển khai theo phân công.
- Chưa tích hợp JFreeChart. Bản khung hiện tại chạy được mà chưa cần thư viện này.

### Các chức năng cần hoàn thành

- Customer và Account trừu tượng; số dư private, không có `setBalance()` public.
- SavingsAccount: lãi suất, tối đa 3 lần rút/tháng.
- CheckingAccount: hạn mức thấu chi.
- Loại tài khoản thứ ba có quy tắc riêng để đáp ứng yêu cầu chung ít nhất 3 lớp con nghiệp vụ; nhóm đề xuất FixedTermAccount.
- Nạp, rút, chuyển khoản và lịch sử giao dịch bất biến.
- Chuyển khoản toàn vẹn: nếu lỗi thì số dư, lịch sử và bộ đếm rút không bị thay đổi một phần.
- Danh sách tài khoản, thông tin chi tiết, bảng lịch sử; dialog giao dịch và About.
- Menu, toolbar có icon/tooltip, status bar, icon cửa sổ và tiếng Việt.
- Hai biểu đồ JFreeChart khác loại: đường biến động số dư và cột tổng nạp/rút theo ngày.
- Mô phỏng cuối tháng và cộng lãi, có interface được sử dụng thực tế.
- UML, kiểm thử, báo cáo, slide, JAR chạy độc lập và hồ sơ nộp.

Tuần này tập trung yêu cầu bắt buộc. Đăng ký/đăng nhập, server, nhiều máy, database, lưu file và xuất CSV nằm ngoài phạm vi đã chốt. Dữ liệu khách hàng và tiền đều là giả lập. Khi chưa có lưu trữ, dữ liệu trong bộ nhớ sẽ mất khi đóng ứng dụng; ghi rõ hạn chế trong báo cáo.

## 2. Phân công

Thay “Chờ chốt tên” bằng tên và username GitHub sau khi nhóm chọn nhiệm vụ. Hạn chi tiết xem từng Issue; “ngày 1” là ngày bắt đầu nhóm đã thống nhất, không phải ngày clone của từng người.

| Nhiệm vụ | Người phụ trách | Phần chính / lớp | Nhánh đề xuất |
| --- | --- | --- | --- |
| 1 | Chủ repo — đã nhận | MainWindow, BankController; tích hợp và đóng gói | `feat/main-window` |
| 2 | Chờ chốt tên | Customer, Account, DemoDataFactory | `feat/account-model` |
| 3 | Chờ chốt tên | SavingsAccount, CheckingAccount | `feat/account-types` |
| 4 | Chờ chốt tên | FixedTermAccount, MonthlyInterestService, InterestBearing | `feat/monthly-interest` |
| 5 | Chờ chốt tên | Bank, TransferService, BankingOperations | `feat/transfers` |
| 6 | Chờ chốt tên | Transaction, TransactionTableModel; tổng hợp kiểm thử | `feat/transaction-history` |
| 7 | Chờ chốt tên | TransactionDialog, AboutDialog; tổng hợp slide | `feat/dialogs` |
| 8 | Chờ chốt tên | AccountListRenderer, AccountDetailsPanel; tổng hợp báo cáo | `feat/account-ui` |
| 9 | Chờ chốt tên | ChartHelper, StatisticsService; video demo dự phòng | `feat/charts` |

Mỗi thành viên viết ít nhất 2 lớp có ý nghĩa, có commit, kiểm thử phần mình, viết phần báo cáo tương ứng và có phần nói khi bảo vệ. Người tổng hợp tài liệu nhận nội dung từ mọi người. Tỷ lệ đóng góp ghi theo việc thực tế.

## 3. Lần đầu tải và chạy project

### 3.1. Chuẩn bị

1. Có tài khoản GitHub riêng và **chấp nhận lời mời vào repository**. Repository của nhóm là private.
2. Có quyền **Write** trên Project để cập nhật tiến độ; quyền Project và repository là hai quyền riêng.
3. Cài [Git](https://git-scm.com/downloads) và [Eclipse IDE for Java Developers](https://www.eclipse.org/downloads/packages/).
4. Dùng JDK 17 hoặc mới hơn. Project biên dịch theo Java 17 với `--release`; Eclipse có thể dùng một Java mới hơn để chạy chính phần mềm IDE.

Chạy trong terminal để kiểm tra Git và Java:

```bash
git --version
java -version
javac -version
```

Trên Windows có thể dùng Git Bash. Các lệnh bên dưới chạy trong terminal, không nhập vào file Java.

### 3.2. Đăng nhập GitHub cho Git

Nếu máy đã clone/push được bằng HTTPS hoặc SSH, có thể dùng cách xác thực đang có.

Với người mới dùng HTTPS, có thể cài [GitHub CLI](https://cli.github.com/) rồi chạy:

```bash
gh auth login --hostname github.com --git-protocol https --web
gh auth setup-git
```

Làm theo hướng dẫn đăng nhập trên trình duyệt bằng tài khoản của mình. Đăng nhập GitHub trên trình duyệt không tự xác thực Git trong terminal. Git qua HTTPS không dùng mật khẩu GitHub thông thường; dùng xác thực qua công cụ, token hoặc SSH theo hướng dẫn GitHub. Không đưa token vào URL clone, code, Issue hay ảnh chụp gửi nhóm.

### 3.3. Clone một lần

Mở terminal tại nơi muốn lưu project, ví dụ thư mục Projects trên máy mình:

```bash
git clone https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking.git
cd OOP_Nhom04_MiniBanking
```

`clone` tạo bản sao có Git để làm việc nhóm. Các lần sau dùng `pull` để cập nhật, không clone lồng thêm vào project đang có.

Trong thư mục project, đặt tên và email tác giả commit; thay nội dung mẫu bằng thông tin của mình:

```bash
git config user.name "Họ tên của bạn"
git config user.email "Email đã xác minh trên GitHub của bạn"
```

Có thể dùng email noreply do GitHub cung cấp trong Settings → Emails. Cấu hình trên áp dụng cho repository này; tên/email tác giả không phải thông tin đăng nhập.

### 3.4. Mở project có sẵn trong Eclipse

1. Mở Eclipse và chọn workspace của mình.
2. Chọn **File → Import → General → Existing Projects into Workspace → Next**.
3. Chọn **Select root directory → Browse**, trỏ đến thư mục `OOP_Nhom04_MiniBanking` vừa clone.
4. Tích project tìm được; **bỏ chọn Copy projects into workspace** nếu tùy chọn đó đang bật, để Eclipse làm trực tiếp trong thư mục Git.
5. Bấm **Finish**.

Project đã có cấu hình Eclipse nên thành viên tải về dùng **Import**, không tạo lại project bằng New Java Project. Nếu workspace đã có project cùng tên, xem mục 8.

Git không lưu thư mục rỗng. Package chưa có lớp có thể chưa xuất hiện sau khi clone; tạo bằng **src → New → Package** khi làm phần đó, đúng tên trong mục 4.

### 3.5. Chạy thử

Trong Eclipse mở `src → vn.edu.ptit.oop.nhom04`, chuột phải **Main.java → Run As → Java Application**.

Kết quả của bản khung: một cửa sổ **MiniBank — Nhóm 04**, menu File/View/Help, dòng Sẵn sàng ở dưới. Đây là mốc xác nhận máy đã chạy được project chung.

Nếu Eclipse không nhận Java, mở **Window → Preferences → Java → Installed JREs**, thêm JDK trên máy bằng Standard VM rồi chọn làm mặc định. Dùng JDK 17 trở lên. Giữ **Properties → Java Compiler** của project ở mức **17**, `--release` bật, và **Properties → Resource → Text file encoding** là **UTF-8**.

## 4. Code đặt ở đâu và phối hợp thế nào?

Package gốc: `vn.edu.ptit.oop.nhom04`.

| Package / thư mục | Nội dung |
| --- | --- |
| Package gốc | Main.java, điểm khởi động |
| `common` | BaseWindow theo mẫu của thầy |
| `model` | Customer, Account, các loại tài khoản, Transaction, enum |
| `service` | Bank, TransferService, MonthlyInterestService, StatisticsService, interface nghiệp vụ |
| `repository` | Dành cho lớp lưu trữ nếu phạm vi thay đổi; hiện chưa triển khai |
| `exception` | Ngoại lệ tự định nghĩa |
| `ui` | MainWindow, BankController, renderer và TableModel theo thống nhất của nhóm |
| `ui.panels` | AccountDetailsPanel và panel con |
| `ui.dialogs` | TransactionDialog, AboutDialog |
| `util` | ChartHelper, DemoDataFactory và tiện ích |
| `src/resources/icons` | Icon khi tích hợp; nạp qua `/resources/icons/...` |
| `docs` | UML, ảnh, kiểm thử và tài liệu khi bổ sung |

### File cụ thể cho từng nhiệm vụ

Mọi đường dẫn Java bên dưới tính từ `src/vn/edu/ptit/oop/nhom04/`. Ví dụ `model/Customer.java` là file trong package `vn.edu.ptit.oop.nhom04.model`.

| Nhiệm vụ | File sẽ viết / tiếp tục sửa | Phạm vi phối hợp |
| --- | --- | --- |
| 1 | `ui/MainWindow.java` (đã có), `ui/BankController.java` (tạo mới); duy trì `Main.java` | Giữ khung `common/BaseWindow.java` của thầy; tích hợp thành phần của mọi người |
| 2 | `model/Customer.java`, `model/Account.java`, `util/DemoDataFactory.java` | Chốt Account và cách cập nhật trạng thái với người 3/4/5/6 |
| 3 | `model/SavingsAccount.java`, `model/CheckingAccount.java`, `exception/InsufficientFundsException.java`, `exception/WithdrawalLimitExceededException.java` | Dùng Account của người 2; thống nhất exception trước khi các lớp khác gọi |
| 4 | `model/FixedTermAccount.java`, `model/InterestBearing.java` (interface), `service/MonthlyInterestService.java`, `exception/AccountNotMaturedException.java` | Cộng lãi cho các loại hưởng lãi; phối hợp Bank và GUI |
| 5 | `service/Bank.java`, `service/TransferService.java`, `service/BankingOperations.java` (interface), `exception/InvalidAmountException.java`, `exception/AccountNotFoundException.java` | Chốt InvalidAmountException sớm cho người 2/3; cung cấp API cho người 1/7 |
| 6 | `model/Transaction.java`, `model/TransactionType.java` (enum), `ui/TransactionTableModel.java` | Chốt dữ liệu giao dịch với người 2/5/9 |
| 7 | `ui/dialogs/TransactionDialog.java`, `ui/dialogs/AboutDialog.java` | Nạp/rút/chuyển dùng chung dialog, gọi API nghiệp vụ |
| 8 | `ui/AccountListRenderer.java`, `ui/panels/AccountDetailsPanel.java` | Bàn giao danh sách/chi tiết cho người 1; không tự sửa số dư |
| 9 | `util/ChartHelper.java`, `service/StatisticsService.java` | Đọc lịch sử và tạo hai biểu đồ; phối hợp thư viện JFreeChart với người 1 |

Danh sách trên là **vị trí file đã thống nhất để bắt đầu**, không có nghĩa các chức năng đã được cài đặt. Hiện các file Java đã có vẫn là Main, BaseWindow và MainWindow. Người phụ trách tạo file của mình trên nhánh riêng; các interface, enum và exception được tạo sớm khi phần khác cần dùng. Không tạo một lớp cùng tên ở package khác để tạm thay thế lớp của bạn trong nhóm.

Git không lưu thư mục rỗng. Các thư mục mới có README giải thích người phụ trách và tên file cần tạo, nhờ đó chúng xuất hiện trên GitHub ngay cả khi chưa có Java. Thư mục `repository` chưa cần lớp vì bản tuần này dùng bộ nhớ; không tạo lớp chỉ để lấp thư mục.

### Tạo lớp trong Eclipse

1. Clone/import project và tạo nhánh của mình theo mục 5.
2. Mở `src`, chọn đúng package. Nếu chưa thấy package đó: chuột phải `src` → **New → Package**, nhập tên đầy đủ.
3. Chuột phải package → **New → Class**, chỉ nhập tên lớp, ví dụ `Customer`; Eclipse tự thêm `.java` và dòng `package`.
4. Với `InterestBearing`/`BankingOperations`, chọn **New → Interface**; với `TransactionType`, chọn **New → Enum**. Với Account, chọn modifier **abstract** khi tạo lớp.
5. Chỉ đánh dấu tạo `public static void main` cho Main. Các lớp khác không cần hàm main riêng.
6. Viết code, Ctrl + S, chạy kiểm tra rồi commit/push/PR phần mình. Tệp README trong package là hướng dẫn, không phải chỗ dán code Java.

Ví dụ người 2: chuột phải package `vn.edu.ptit.oop.nhom04.model` → New → Class → Name: `Customer` → Finish. File được tạo tại `src/vn/edu/ptit/oop/nhom04/model/Customer.java`.

Trước khi làm sâu, người phụ trách model/nghiệp vụ thống nhất constructor, tên phương thức, kiểu tiền, thời gian mô phỏng, exception và cấu trúc Transaction trong Issue. Đề xuất dùng **BigDecimal** cho tiền. Quy tắc chuyển tiền ra có tính lượt rút, cách chốt tháng/lãi và phạm vi thống kê phải được cả nhóm ghi rõ.

- Chỉ người phụ trách nhiệm vụ 1 sửa bố cục chính của MainWindow; các thành viên khác bàn giao dialog/panel/dataset qua API thống nhất.
- GUI gọi nghiệp vụ; không tự sửa số dư, tính lãi hay cập nhật lịch sử riêng.
- Model không mở JOptionPane hoặc phụ thuộc GUI.
- Đổi API ảnh hưởng người khác phải báo trong Issue và nhóm trước khi ghép.
- BaseWindow gọi `initUI()` trong constructor: các trường MainWindow dùng trong `initUI()` được khởi tạo trong phương thức đó, tránh initializer của lớp con ghi đè sau khi constructor cha chạy.

### Thư viện biểu đồ

Nhóm dự kiến dùng **JFreeChart 1.5.4**, theo ví dụ trong tài liệu môn học. Người 9 thống nhất và bàn giao cấu hình thư viện với người 1 trước khi ghép code biểu đồ. Cần ghi bổ sung cách cài/chạy tại đây khi thư viện đã được tích hợp.

Nếu dùng JAR, ưu tiên file trong thư mục `lib` của repository và **Build Path → Add JARs** để cấu hình không chứa đường dẫn riêng của một máy. `.gitignore` hiện bỏ qua `*.jar`, nên cần thêm ngoại lệ cho đúng JAR thư viện nếu nhóm chọn đưa nó vào Git. Chỉ đưa thư viện cần thiết vào Git; file JAR ứng dụng nộp đặt theo gói sản phẩm cuối.

## 5. Quy trình GitHub cho mỗi nhiệm vụ

### Hiểu nhanh các từ

| Từ | Ý nghĩa |
| --- | --- |
| Issue | Công việc được giao, có người phụ trách và tiêu chí hoàn thành |
| Branch / nhánh | Nơi làm phần của mình, tách khỏi bản chung |
| `main` | Nhánh bản chung đã được kiểm tra và ghép |
| Commit | Lưu một mốc thay đổi trên máy, kèm mô tả |
| Push | Gửi các commit lên GitHub |
| Pull | Nhận và ghép cập nhật từ GitHub về máy |
| Pull Request / PR | Đề nghị kiểm tra và ghép nhánh của mình vào `main` |
| Merge | Ghép thay đổi giữa các nhánh |

### Bước 1 — Nhận việc

Mở Issue phần mình, kiểm tra **Assignees**, đọc hạn và checklist. Đưa Issue lên Project và chuyển trạng thái sang **In progress**. Bình luận nếu đang chờ API hoặc thiếu thông tin.

### Bước 2 — Tạo nhánh từ bản chung mới nhất

Mở terminal trong thư mục repository. Chỉ chuyển nhánh khi đã lưu công việc hiện tại bằng commit; dùng `git status` kiểm tra trước.

Ví dụ cho nhiệm vụ 2, tạo nhánh **một lần**:

```bash
git status
git switch main
git pull --ff-only origin main
git switch -c feat/account-model
```

Thành viên khác thay `feat/account-model` bằng nhánh phần mình ở bảng phân công. Khi nhánh đã tồn tại, dùng `git switch feat/account-model`, không chạy lại `switch -c`.

### Bước 3 — Viết code và kiểm tra

Làm phần mình trong Eclipse. Nhấn **Ctrl + S**, kiểm tra Problems, chạy lại Main và kiểm thử chức năng đã thay đổi. Nếu nhánh cần lớp chưa có từ người khác, ghi rõ phụ thuộc và chờ/chốt cách ghép; không đưa bản không biên dịch vào `main`.

### Bước 4 — Commit và push nhánh của mình

Ví dụ vẫn trên nhánh `feat/account-model`:

```bash
git status
git diff
git add .
git diff --cached
git commit -m "feat: add Customer and Account model"
git push -u origin feat/account-model
```

Đọc danh sách thay đổi trước khi commit; chỉ đưa phần liên quan nhiệm vụ vào commit. `git add .` sẽ chọn mọi thay đổi trong thư mục hiện tại. Nếu có file không thuộc phần này, dùng `git add đường-dẫn-file` để chọn riêng thay cho `git add .`.

Các lần push tiếp theo trên cùng nhánh đã đặt upstream chỉ cần `git push`. Viết thông điệp commit rõ việc đã làm, ví dụ `fix: reject negative deposit amount`; tránh `update`, `abc` hoặc `done` không có nội dung.

### Bước 5 — Tạo Pull Request

1. Mở repository trên GitHub, bấm **Compare & pull request**. Nếu không thấy, mở **Pull requests → New pull request**.
2. Chọn **base: main**, **compare: nhánh của mình**.
3. Kiểm tra tab **Files changed** để bảo đảm đúng phần việc.
4. Điền tiêu đề mô tả chức năng và nội dung bên dưới.
5. Bấm **Create pull request**, gửi link cho người 1 để kiểm tra và ghép; đặt Reviewer nếu chọn được.
6. Chuyển Issue trên Project sang **In review**.

Mẫu nội dung PR; thay dòng Issue bằng số/link thật, không sao chép một số Issue ví dụ:

```markdown
Công việc liên quan: [dán link Issue của mình]

Thay đổi:
- Các lớp/chức năng đã thực hiện.
- Quy tắc nghiệp vụ hoặc API cần người khác biết.

Kiểm tra:
- Project biên dịch được.
- Các trường hợp đã chạy và kết quả thực tế.

Còn thiếu hoặc đang chờ:
- Ghi rõ nếu có; nếu không thì ghi "Không".
```

Nếu PR **hoàn thành toàn bộ** một Issue, có thể dùng `Closes #SO_ISSUE` và thay `SO_ISSUE` bằng số thật. Khi PR vào nhánh mặc định, GitHub có thể tự đóng Issue được liên kết. Nếu mới hoàn thành một phần, chỉ dẫn link/`Refs #...`, giữ Issue mở.

### Bước 6 — Kiểm tra và ghép

Người 1 hoặc người được phân công review đọc thay đổi, chạy thử và kiểm tra ảnh hưởng đến bản chung. Tác giả PR sửa theo góp ý bằng commit/push tiếp trên **cùng nhánh**, PR tự cập nhật.

Đề xuất nhóm dùng **Create a merge commit** để giữ các commit riêng của thành viên, rồi **Confirm merge** sau khi kiểm tra đạt. Nếu có xung đột, xử lý theo mục 8 trước khi ghép. Chỉ chuyển Issue sang **Done** khi tiêu chí của Issue đã đạt, code đã ghép và chạy đúng; đóng Issue nếu chưa được đóng tự động.

### Bước 7 — Nhận bản chung sau khi ghép

Sau khi PR được ghép và công việc trên nhánh đã được commit đầy đủ:

```bash
git switch main
git pull --ff-only origin main
```

Quay lại Eclipse, chọn project rồi nhấn **F5**; chạy lại Main. Với nhiệm vụ mới, tạo **nhánh mới từ main đã cập nhật**, không tiếp tục dùng nhánh cũ cho công việc khác.

## 6. Cập nhật bản chung vào nhánh đang làm

Khi đang làm dở và cần phần của người khác vừa vào `main`, commit phần hiện tại trước; không chuyển sang main rồi viết tiếp phần mình tại đó.

Ví dụ đang trên nhánh `feat/account-model`:

```bash
git status
git fetch origin
git merge origin/main
```

Lệnh merge đưa bản chung mới vào nhánh đang làm. Nếu Git yêu cầu nhập thông điệp merge, lưu và đóng trình soạn thảo. Sau khi ghép, kiểm tra/ chạy lại rồi `git push`. Nếu báo conflict, không tiếp tục copy lệnh khác; làm theo mục 8 và phối hợp người cùng sửa file.

## 7. Quy ước làm việc của nhóm

- Mỗi người làm trên nhánh riêng, gửi PR về `main`; người 1 hoặc người được giao kiểm tra rồi ghép.
- `main` giữ bản chạy được. Không force push, không xóa code của người khác để giải quyết conflict.
- Mỗi ngày cập nhật Issue: đã làm gì, còn gì, đang vướng gì; gửi phần chạy được để ghép sớm.
- Project dùng **Todo → In progress → In review → Done**.
- Không chờ ngày cuối mới ghép. Mốc kỹ thuật theo Issue: nghiệp vụ ngày 3, ghép chức năng ngày 4, thử JAR ngày 5, hồ sơ ngày 6 và tập bảo vệ ngày 7.
- Ngày 1 thống nhất API; ngày 4 dừng thêm tính năng; các ngày sau ưu tiên kiểm thử, sửa lỗi và hồ sơ. Deadline chính thức của thầy được nhóm bổ sung khi xác nhận.
- Có phụ lục AI và Git theo đề. Mỗi người ghi phần được hỗ trợ, đã kiểm chứng/chỉnh sửa gì và hiểu code mình đóng góp.
- Giữ `.project`, `.classpath`, `.settings` trong Git vì đây là cấu hình project chung. Kiểm tra trước khi thay đổi, tránh đưa đường dẫn tuyệt đối riêng của máy vào cấu hình.
- `bin` và `.class` là sản phẩm biên dịch, được bỏ qua. Không thêm workspace Eclipse hoặc thư mục `.metadata` vào repository.

## 8. Lỗi thường gặp

| Hiện tượng | Cách xử lý |
| --- | --- |
| `Repository not found` / không mở được repo | Kiểm tra URL, tài khoản đang đăng nhập và đã nhận lời mời repo private chưa |
| `Permission denied (publickey)` | URL remote đang dùng SSH nhưng máy chưa cấu hình khóa; thiết lập SSH theo GitHub hoặc dùng HTTPS đã xác thực |
| HTTPS hỏi mật khẩu nhưng đăng nhập thất bại | Git không dùng mật khẩu tài khoản; dùng GitHub CLI hoặc token theo hướng dẫn xác thực |
| `Author identity unknown` | Chạy `git config user.name` và `git config user.email` trong repository với thông tin của mình |
| `fatal: not a git repository` | Terminal chưa đứng trong thư mục clone; chuyển vào thư mục có `.git` |
| `branch ... already exists` | Dùng `git switch tên-nhánh`, bỏ `-c` vì nhánh đã được tạo |
| `nothing to commit` | Không có thay đổi mới, hoặc chưa lưu file trong Eclipse; kiểm tra `git status` và Ctrl + S |
| `non-fast-forward` khi push / pull bị từ chối | Có cập nhật khác trên nhánh. Dừng, kiểm tra `git status`, nhánh hiện tại và báo người 1; không dùng force push |
| Không thấy package rỗng sau clone | Git chỉ lưu file; tạo package đó khi bắt đầu viết lớp |
| Eclipse chưa thấy file vừa pull | Chọn project và nhấn F5; nếu cần dùng Project → Clean để build lại |
| Không import được vì trùng tên project | Kiểm tra vị trí project đang mở trong Properties → Resource. Nếu đang mở nhầm bản, gỡ project khỏi workspace và bỏ chọn xóa nội dung trên ổ đĩa, rồi import đúng bản clone |
| JRE/Java version bị lỗi | Thêm JDK vào Installed JREs, chọn JDK 17 trở lên; giữ compiler/release 17 và UTF-8 theo cấu hình nhóm |
| Thiếu JFreeChart | Kiểm tra hướng dẫn thư viện mục 4; liên hệ người 9/1 để nhận cấu hình chung, tránh dùng đường dẫn JAR riêng |

### Khi Git báo conflict

1. Ghi lại thao tác vừa chạy, kiểm tra `git status`, báo người 1 và người cùng sửa file.
2. Nếu cần quay về trạng thái trước **lệnh merge đang xung đột**, có thể dùng `git merge --abort`. Lệnh này chỉ dành cho merge đang diễn ra; không dùng tùy tiện với thao tác khác.
3. Khi xử lý conflict, mở từng file và thống nhất nội dung đúng. Bỏ các dấu `<<<<<<<`, `=======`, `>>>>>>>` sau khi đã giữ phần code cần thiết của hai phía.
4. Kiểm tra biên dịch và chạy chức năng bị ảnh hưởng.
5. `git add` từng file đã giải quyết, `git commit` hoàn tất merge, rồi `git push` nhánh của mình.

## 9. Checklist trước khi bàn giao một nhiệm vụ

- [ ] Code ở đúng package, đúng nhánh và không có lỗi biên dịch.
- [ ] Chức năng đúng tiêu chí trong Issue, có kiểm thử dữ liệu sai/biên liên quan.
- [ ] Đã chạy lại Main sau thay đổi; ghi rõ phụ thuộc/chức năng còn thiếu.
- [ ] Có commit và push bằng tài khoản cá nhân.
- [ ] PR chỉ chứa thay đổi liên quan; có link Issue và kết quả kiểm tra.
- [ ] API thay đổi đã được thông báo cho người sử dụng.
- [ ] Đã gửi phần báo cáo/ảnh/test tương ứng cho người tổng hợp.
- [ ] Code đã được review, ghép và chạy đúng trước khi đánh dấu Done.

Sản phẩm cuối gồm source Eclipse, JAR, báo cáo PDF 15–25 trang, slide 10–12 trang, UML ảnh + nguồn, ít nhất 10 test case, bảng đóng góp, phụ lục AI/Git và README. Demo bắt buộc: **rút quá số dư → chuyển khoản → xem lịch sử và biến động số dư → cộng lãi cuối tháng**. Cả 9 người có phần nói, tổng trình bày/demo 10 phút và phản biện 5 phút.

## 10. Hướng dẫn chính thức để tra cứu

- [Clone repository](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)
- [Đăng nhập qua GitHub CLI](https://cli.github.com/manual/gh_auth_login) và [cấu hình xác thực Git](https://cli.github.com/manual/gh_auth_setup-git)
- [Import project Eclipse có sẵn](https://help.eclipse.org/latest/topic/org.eclipse.platform.doc.user/tasks/tasks-importproject.htm)
- [Tạo Pull Request](https://docs.github.com/en/pull-requests/how-tos/create-pull-requests/creating-a-pull-request)
- [Liên kết PR với Issue](https://docs.github.com/en/issues/tracking-your-work-with-issues/using-issues/linking-a-pull-request-to-an-issue)

Yêu cầu môn học đối chiếu từ tài liệu `Java_OOP_ThuyetTrinh_10Nhom.pdf` của thầy, đặc biệt mục yêu cầu chung, thẻ MiniBank trang 11 và checklist nộp bài. Tên thành viên, hạn nộp và mức hoàn thành sẽ được nhóm cập nhật theo thực tế.
