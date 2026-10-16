# Kế hoạch MiniBank — nhóm 9 người, hoàn thành trong 1 tuần

Ngày lập: 14/10/2026. Cập nhật: 16/10/2026. Trạng thái: đã có bản khung; các chức năng tiếp theo đang chờ triển khai.

Nguồn yêu cầu: tài liệu `Java_OOP_ThuyetTrinh_10Nhom.pdf` trong thư mục môn học, đặc biệt mục 3–5, thẻ Mini Banking trang 11, mục 7–9 và checklist trang 27. Các quy tắc bổ sung được ghi rõ là quyết định thiết kế của nhóm, không phải yêu cầu nguyên văn của giảng viên.

Phạm vi đã chốt: 9 thành viên, 1 tuần, ứng dụng trên một máy, ưu tiên hoàn thành yêu cầu bắt buộc. Số nhóm đã chốt: 04. Chưa có tên đầy đủ của thành viên và hạn nộp chính thức. Dùng thành viên 1–9 làm ký hiệu; lịch tính từ ngày bắt đầu làm. Thẻ đề số 3 không nhất thiết là số nhóm thực tế.

## 1. Mục tiêu và phạm vi

Hoàn thành ứng dụng desktop Java Swing chạy trên một máy, thực hiện đầy đủ bước 1–7 của thẻ đề và yêu cầu chung. Sản phẩm có mã nguồn, JAR chạy độc lập, UML, báo cáo, slide và kịch bản demo.

Người thao tác ở bản này đóng vai trò quản lý các tài khoản giả lập. `Customer` là khách hàng trong mô hình nghiệp vụ; chưa phải tài khoản đăng nhập. Tiền và khách hàng demo đều là dữ liệu giả lập.

| Mức ưu tiên | Nội dung |
|---|---|
| Bắt buộc | Model OOP, nạp/rút/chuyển, toàn vẹn giao dịch, lịch sử, Swing, hai biểu đồ, cộng lãi, kiểm thử, hồ sơ nộp |
| Bổ sung để đủ yêu cầu chung | Loại tài khoản thứ ba; interface nghiệp vụ tự định nghĩa |
| BONUS đã chốt làm cùng | PIN theo tài khoản, khóa sau 3 lần sai liên tiếp, InterestBearing, xuất sao kê CSV |
| Ngoài phạm vi tuần này | Đăng ký người dùng, đăng nhập nhiều người dùng, lưu/phục hồi dữ liệu ứng dụng, database, server và triển khai nhiều máy |

Lưu dữ liệu qua lần đóng ứng dụng không được thẻ MiniBank nêu là bắt buộc. Dùng bộ nhớ và nạp lại dữ liệu mẫu khi khởi động; ghi rõ hạn chế này trong báo cáo. Nút cộng lãi cuối tháng thuộc bước 7 bắt buộc; đồng thời nhóm làm toàn bộ bonus đã chốt. CSV là xuất sao kê, không phải lưu/phục hồi dữ liệu ứng dụng. Khóa/PIN chỉ giữ trong phiên chạy, cần ghi rõ hạn chế này. InterestBearing được dùng để đáp ứng interface và nghiệp vụ cộng lãi, không coi đó là hoàn thành toàn bộ bonus.

## 2. Bảng đối chiếu yêu cầu và tiêu chí hoàn thành

| Yêu cầu của đề | Công việc | Bằng chứng hoàn thành |
|---|---|---|
| Customer và Account | Thuộc tính hợp lệ; Account trừu tượng; số dư private; không có setBalance public | UML và đoạn code giải thích đóng gói |
| SavingsAccount | Lãi suất, tối đa 3 lần rút mỗi tháng; override withdraw | Lần rút thứ tư bị từ chối; sang tháng mới được rút |
| CheckingAccount | Hạn mức thấu chi; override withdraw | Rút đến đúng giới hạn được chấp nhận, vượt giới hạn bị từ chối |
| Ít nhất 3 lớp con nghiệp vụ | Thêm FixedTermAccount với ngày đáo hạn và quy tắc rút riêng | Rút trước ngày đáo hạn bị từ chối; rút đúng ngày được phép |
| Interface tự định nghĩa | BankingOperations cho giao diện gọi nghiệp vụ; InterestBearing phục vụ cộng lãi | Có lớp cài đặt và lời gọi qua interface; giải thích được lý do |
| Ngoại lệ | InsufficientFundsException, InvalidAmountException; lỗi giới hạn rút và chưa đáo hạn | GUI hiển thị thông báo, không làm ứng dụng dừng |
| Transaction bất biến | ID, loại, số tiền, thời điểm, số dư sau giao dịch; không có setter | Không sửa được giao dịch đã tạo; không sửa được danh sách nội bộ qua getter |
| Bank quản lý Map | Map<String, Account>, kiểm tra số tài khoản trùng và tra cứu | Tra cứu đúng; không ghi đè khi trùng mã |
| Chuyển khoản toàn vẹn | Chuẩn bị trạng thái hai tài khoản trước khi cập nhật | Lỗi không đổi số dư, lịch sử, bộ đếm rút hay dữ liệu biểu đồ |
| MainWindow kế thừa BaseWindow | Giữ initUI; BorderLayout, JSplitPane | Cửa sổ mở đúng bố cục và chạy qua EDT |
| JList và renderer tự viết | Danh sách tài khoản, icon khác loại; JScrollPane | Chọn tài khoản cập nhật thông tin, lịch sử và biểu đồ |
| JTable lịch sử | TransactionTableModel extends AbstractTableModel | Số tiền, loại giao dịch, thời điểm và số dư hiển thị đúng |
| Ít nhất hai JDialog | TransactionDialog và AboutDialog | Mở được cả hai; dialog giao dịch thực hiện nạp/rút/chuyển |
| Menu, toolbar, status bar | File/View/Help, mnemonic, phím tắt, PNG 16×16, tooltip, icon cửa sổ | Kiểm tra trực tiếp mọi nút/menu; trạng thái cập nhật sau thao tác |
| Biểu đồ đường | Số dư theo thời gian của tài khoản đang chọn | Tiêu đề, trục thời gian, trục VND; cập nhật sau giao dịch |
| Biểu đồ cột | Tổng nạp–rút theo ngày | Hai nhóm dữ liệu, nhãn ngày, đơn vị VND; số liệu khớp lịch sử |
| Mô phỏng cuối tháng | Cộng lãi cho tài khoản hưởng lãi | Có giao dịch ghi nhận lãi; lịch sử và biểu đồ cùng cập nhật |
| Dữ liệu sai và tiếng Việt | Kiểm tra nhập liệu; định dạng VND; thông báo có dấu | Không văng lỗi khi nhập sai; font và nhãn hiển thị đúng |
| Nộp và bảo vệ | JAR, UML, báo cáo, slide, Git, nhật ký AI | Kiểm tra đủ bộ hồ sơ; mọi thành viên code và thuyết trình |

Không dùng các lớp kế thừa Swing để thay cho yêu cầu ba lớp con nghiệp vụ. Thẻ MiniBank chỉ gợi ý hai lớp con, còn mục yêu cầu chung và checklist ghi ít nhất ba; kế hoạch áp dụng yêu cầu chung để tránh thiếu tiêu chí.

## 3. Thiết kế lớp và phân tách trách nhiệm

Luồng chính: giao diện Swing → BankingOperations → Bank → Account và Transaction.

Giao diện không tự tính số dư, lãi hay điều kiện rút. BankingOperations giúp người viết GUI và người viết nghiệp vụ thống nhất cách gọi ngay ngày đầu. BankController nối thao tác giao diện với Bank và điều phối làm mới các vùng sau giao dịch.

| Thành phần | Trách nhiệm |
|---|---|
| Customer | ID, họ tên, số điện thoại; kiểm tra dữ liệu đầu vào |
| Account (abstract) | Số tài khoản, chủ tài khoản, số dư, lịch sử; deposit và withdraw trừu tượng |
| SavingsAccount | Không thấu chi, giới hạn ba lần rút/tháng, có lãi suất |
| CheckingAccount | Được âm số dư trong hạn mức thấu chi |
| FixedTermAccount | Loại thứ ba do nhóm đề xuất: không thấu chi, chỉ rút từ ngày đáo hạn; có lãi suất |
| InterestBearing | Hợp đồng tính lãi theo kỳ; dùng cho các tài khoản hưởng lãi |
| TransactionType | Giữ DEPOSIT, WITHDRAW, TRANSFER theo thẻ đề |
| Transaction | Bản ghi bất biến; thông tin đối tác và chiều chuyển là thuộc tính bổ sung nếu cần |
| BankingOperations | Liệt kê/lấy tài khoản, nạp, rút, chuyển, mô phỏng cuối tháng |
| Bank | Cài đặt BankingOperations; sở hữu Map; nạp/rút, tra cứu và điều phối các dịch vụ |
| TransferService | Kiểm tra, chuẩn bị và cập nhật hai phía chuyển khoản; Bank gọi dịch vụ này |
| MonthlyInterestService | Chốt kỳ, tính/cộng lãi qua InterestBearing, ngăn cộng trùng kỳ |
| BankController | Xử lý các yêu cầu từ GUI, gọi nghiệp vụ, điều phối làm mới dữ liệu hiển thị |
| Các exception | Phân biệt số tiền không hợp lệ, thiếu tiền, vượt số lần rút, chưa đáo hạn, không tìm thấy tài khoản |
| BaseWindow | Dùng mã khởi đầu của thầy; giữ phương thức abstract initUI |
| MainWindow | Ghép danh sách, thông tin, bảng, biểu đồ, menu và toolbar |
| AccountListRenderer | Icon và nội dung của từng dòng tài khoản |
| AccountDetailsPanel | Hiển thị chủ tài khoản, loại, số dư và giới hạn của tài khoản đang chọn |
| TransactionTableModel | Chuyển dữ liệu giao dịch thành các cột JTable |
| TransactionDialog | Nhận số tiền/tài khoản đích; gọi nghiệp vụ và hiển thị lỗi |
| AboutDialog | Tên đề tài, thành viên, thông tin ứng dụng |
| ChartHelper | Tạo/cập nhật dataset và hai loại biểu đồ JFreeChart |
| StatisticsService | Tính chuỗi số dư và tổng nạp/rút theo ngày từ lịch sử; không tạo GUI |
| DemoDataFactory | Tạo dữ liệu mẫu nhất quán để chạy thử và bảo vệ |

Tên project: `OOP_Nhom04_MiniBanking`. Package gốc: `vn.edu.ptit.oop.nhom04`, chia `common`, `model`, `service`, `repository`, `exception`, `ui`, `ui/panels`, `ui/dialogs`, `util`; icon trong `resources/icons`, UML trong `docs`. Giữ cấu trúc theo đề; thư mục repository chưa cần lớp lưu file trong phạm vi này, không tạo lớp rỗng để tăng số lượng.

Lưu ý mã BaseWindow của thầy gọi initUI từ constructor: các trường MainWindow được dùng trong initUI phải được khởi tạo tại đó theo hướng dẫn trong tài liệu, tránh initializer của lớp con ghi đè trạng thái sau khi constructor cha kết thúc.

## 4. Quy tắc cần chốt trước khi code

Các quy tắc dưới đây là đề xuất mô phỏng để nhóm thống nhất, không phải quy định ngân hàng ngoài đời.

1. Tiền dùng BigDecimal; dữ liệu demo theo đồng nguyên. Số tiền giao dịch phải lớn hơn 0; không chấp nhận phần lẻ của đồng. Không tạo BigDecimal từ double. Lãi làm tròn về đồng theo một quy tắc thống nhất, đề xuất HALF_UP.
2. SavingsAccount và FixedTermAccount không được âm; CheckingAccount không được thấp hơn âm hạn mức thấu chi. Số dư ban đầu và hạn mức cũng phải được kiểm tra.
3. Giới hạn ba lần rút dùng YearMonth của thời gian mô phỏng; chỉ giao dịch thành công tăng bộ đếm. Nhóm đề xuất chuyển tiền ra khỏi SavingsAccount cũng tính là một lần rút vì cùng làm giảm số dư.
4. Không cho chuyển vào chính tài khoản nguồn. Kiểm tra cả hai tài khoản tồn tại trước khi cập nhật.
5. Chuyển thành công ghi hai bản ghi TRANSFER có mã tham chiếu chung và chiều vào/ra; mỗi bản ghi lưu số dư sau giao dịch của tài khoản tương ứng. Số tiền luôn dương.
6. Đề xuất biểu đồ cột dùng tổng DEPOSIT và WITHDRAW của tài khoản đang chọn theo ngày. TRANSFER không cộng vào hai tổng này; thể hiện rõ phạm vi trên tiêu đề/chú thích. Đây là cách hiểu nhóm chọn vì thẻ đề chưa quy định phạm vi thống kê chuyển khoản.
7. Đề xuất lãi tháng = số dư tại thời điểm chốt kỳ × lãi suất năm / 12. Đây là công thức mô phỏng đơn giản, ghi rõ trong báo cáo; không giả lập lãi theo số dư từng ngày.
8. Thời gian mô phỏng dùng chung cho rút theo tháng, đáo hạn, timestamp và cộng lãi. Mỗi lần bấm nút xử lý kỳ kế tiếp; hiển thị kỳ đang chốt. Một tài khoản không được nhận lãi hai lần cho cùng kỳ.
9. Khoản lãi được ghi DEPOSIT với nội dung “Lãi kỳ ...”, giữ ba loại enum theo đề. FixedTermAccount cũng có thể hưởng lãi qua InterestBearing; ghi rõ đây là tính năng của loại tài khoản nhóm bổ sung.
10. Lịch sử là danh sách chỉ đọc hoặc bản sao; Transaction và các giá trị bên trong không được sửa sau khi tạo.

### Toàn vẹn chuyển khoản

Không gọi rút ở nguồn rồi mới kiểm tra đích. Tách xử lý thành: kiểm tra đầu vào → kiểm tra quy tắc từng loại tài khoản qua đa hình → chuẩn bị đầy đủ trạng thái mới và hai bản ghi → cập nhật cùng nhau.

Phần chuẩn bị có thể báo lỗi, còn phần cập nhật phải được thiết kế để không phát sinh lỗi nghiệp vụ giữa chừng. Nếu cách cài đặt vẫn có thể lỗi sau khi thay đổi một phía, phải khôi phục toàn bộ trạng thái, gồm lịch sử và bộ đếm; việc khôi phục nằm trong nội bộ model, không tạo setBalance public cho GUI.

Ứng dụng thực hiện các thao tác thay đổi tuần tự qua Bank. Chỉ thông báo thành công và làm mới giao diện sau khi toàn bộ giao dịch hoàn tất. Thành viên 2 và 5 phải cùng chốt cơ chế chuẩn bị/cập nhật hoặc khôi phục trạng thái ngay ngày 1; không viết hai cách thay đổi số dư độc lập.

## 5. Giao diện và dữ liệu demo

MainWindow có menu và toolbar phía trên, status bar phía dưới. Phần giữa là JSplitPane: trái JList tài khoản, phải thông tin tài khoản và các tab lịch sử/biểu đồ. Mọi bảng/danh sách bọc JScrollPane.

Toolbar tối thiểu: Nạp, Rút, Chuyển, Mô phỏng cuối tháng. File có Thoát; View chuyển phần đang xem; Help mở AboutDialog. TransactionDialog thay đổi trường nhập tùy loại thao tác, chuyển khoản có tài khoản đích và xác nhận rõ nguồn/đích/số tiền.

Sau giao dịch thành công, làm mới danh sách, chi tiết, bảng, hai dataset và status bar. Sau giao dịch lỗi, không đổi dữ liệu; dialog hiển thị nguyên nhân và cho sửa đầu vào. Dùng thao tác người dùng để cập nhật động; đề không bắt buộc Timer nếu đã cập nhật theo thao tác.

Chuẩn bị 4–6 tài khoản của ít nhất ba khách hàng, đủ ba loại tài khoản. Có tài khoản tiết kiệm với số dư dễ tính lãi, tài khoản thanh toán có thấu chi và hai tài khoản kỳ hạn ở hai trạng thái trước/đến hạn. Tạo lịch sử bằng nghiệp vụ thật và thời gian mô phỏng để bảng, biểu đồ và số dư không mâu thuẫn. Biểu đồ đường có điểm số dư đầu kỳ và không làm mất giao dịch khi timestamp trùng.

## 6. Tiến độ: chuẩn bị, hai giờ trên lớp và một tuần hoàn thiện

### Trước buổi lập trình

- Chốt số nhóm, người phụ trách và quy tắc mục 4.
- Tạo Git repository, mời giảng viên theo yêu cầu; mỗi người dùng tài khoản của mình.
- Chuẩn bị Eclipse, JDK 17, JFreeChart 1.5.x; có thể dùng phiên bản Maven 1.5.4 được tài liệu nêu.
- Phác thảo UML và chữ ký phương thức để các phần ghép được với nhau.
- Chuẩn bị icon PNG 16×16/32×32, không dành thời gian trên lớp để tìm icon.

### Hai giờ trên lớp

| Thời gian | Công việc | Mốc cần có |
|---|---|---|
| 0–15 phút | Tạo project/package, thư viện, Main và BaseWindow | Mở được cửa sổ |
| 15–50 phút | Customer, Account, hai loại tài khoản chính, exception | Nạp/rút và trường hợp thiếu tiền chạy được |
| 50–75 phút | JList, renderer, thông tin, bảng lịch sử | Chọn tài khoản xem được dữ liệu |
| 75–100 phút | TransactionDialog và nối nạp/rút | Thao tác giao dịch có thông báo lỗi |
| 100–120 phút | Một biểu đồ với dataset mẫu hoặc dữ liệu thật | Cửa sổ + model + ít nhất một JDialog + một biểu đồ |

Chốt commit `end of lab session`. Đây là mốc tối thiểu cuối buổi theo tài liệu; không coi hai giờ là thời gian hoàn thành toàn bộ ứng dụng.

### Một tuần hoàn thiện

| Ngày | Công việc chính | Điều kiện kết thúc |
|---|---|---|
| 1 | Cả nhóm chốt chữ ký phương thức, quy tắc và UML; tạo khung các lớp; nhóm model code nền, GUI dựng khung, biểu đồ dùng mẫu | Một project chung mở được; phân công và API đã chốt; nạp/rút cơ bản chạy được |
| 2 | Model hoàn chỉnh; Bank/chuyển khoản; GUI, bảng và biểu đồ tiếp tục song song | Ghép được nạp/rút từ dialog vào model; chuyển khoản chạy ở tầng nghiệp vụ |
| 3 | Ghép chuyển khoản, lịch sử, chi tiết, hai biểu đồ và cộng lãi | Một bản chạy xuyên suốt toàn bộ kịch bản bắt buộc, dù còn lỗi giao diện |
| 4 | Hoàn thiện menu/icon/status/About, ghép PIN/CSV/InterestBearing; sửa số liệu; kiểm tra bảng yêu cầu | Tất cả yêu cầu có bản hoạt động; đóng gói thử JAR; dừng thêm chức năng |
| 5 | Kiểm thử toàn bộ, sửa lỗi, thử trên máy trình chiếu; chụp ảnh bản ổn định | Các test đã có kết quả; JAR chạy ngoài Eclipse; ảnh và UML khớp bản chạy |
| 6 | Chốt code và tài liệu; tập demo lần 1; rà zip và nộp theo hạn | Báo cáo, slide, UML, README, AI/Git hoàn chỉnh; có video dự phòng |
| 7 | Tập phản biện từng người, demo lần 2; chỉ sửa lỗi chặn demo và kiểm tra lại | Cả 9 người có phần nói; tổng ≤10 phút; sẵn sàng bảo vệ |

Viết nhật ký Git/AI và nội dung báo cáo cùng lúc làm từng phần; không để toàn bộ tài liệu đến ngày 6. Ngày 3 là mốc phải có bản ghép chạy được, không chờ đến ngày 6 mới ghép. Nếu bảo vệ ngay sau tuần này, dự kiến hoàn thành/nộp ngày 6 để có ít nhất một ngày đệm; hạn thực tế theo thông báo của thầy.

## 7. Chia việc cho đúng 9 người

Thay các số bằng tên thật khi nhóm chốt. Mỗi người phụ trách ít nhất hai lớp ứng dụng có ý nghĩa, có commit và có phần nói. Không tính enum, interface, Main chỉ có vài dòng hoặc BaseWindow sao chép từ đề là hai lớp tự viết cho một thành viên. Không tạo lớp cho đủ số lượng. Cả nhóm cần hiểu toàn bộ ứng dụng, đồng thời chịu trách nhiệm sâu nhất với phần mình làm.

| Người | Phụ trách chính và lớp tự viết | Kết quả phải bàn giao | Hồ sơ phụ trách |
|---|---|---|---|
| 1 — trưởng nhóm | MainWindow, BankController; cấu hình Main/BaseWindow, Git và đóng gói | Cửa sổ chung, menu/toolbar/status; ghép các phần; JAR | README, kiểm tra zip, tổng hợp đóng góp/Git |
| 2 — model nền | Account, Customer; bổ sung DemoDataFactory | Đóng gói số dư/lịch sử, nạp tiền, dữ liệu mẫu; thống nhất cơ chế cập nhật với người 5 | UML tổng thể và phần đóng gói |
| 3 — quy tắc tài khoản | SavingsAccount, CheckingAccount; các exception tương ứng | Giới hạn rút theo tháng, thấu chi và lỗi biên | Phân cấp lớp, kế thừa/đa hình; test hai loại này |
| 4 — kỳ hạn và lãi | FixedTermAccount, MonthlyInterestService; InterestBearing và thời gian mô phỏng | Loại thứ ba, cộng lãi và chống cộng trùng kỳ | Trừu tượng/interface; test đáo hạn và tính lãi |
| 5 — ngân hàng và chuyển | Bank, TransferService; BankingOperations | Map tài khoản, nạp/rút/chuyển, toàn vẹn hai phía | Luồng giao dịch; test chuyển và lỗi giữa chừng |
| 6 — lịch sử | Transaction, TransactionTableModel; enum và thông tin chuyển vào/ra | Giao dịch bất biến, bảng lịch sử đúng dữ liệu | Phần immutable/composition; tập hợp bảng test từ cả nhóm |
| 7 — dialog | TransactionDialog, AboutDialog | Nhập liệu nạp/rút/chuyển; bắt lỗi và thông báo rõ | Ảnh dialog, phần xử lý sự kiện; dựng slide từ nội dung nhóm |
| 8 — hiển thị tài khoản | AccountListRenderer, AccountDetailsPanel | Danh sách/icon theo loại, chi tiết tiếng Việt và VND | Ảnh cửa sổ, tổng hợp báo cáo từ phần mọi người gửi |
| 9 — biểu đồ | ChartHelper, StatisticsService | Hai loại biểu đồ, tổng hợp số liệu chính xác và cập nhật | Phần biểu đồ; quay video demo dự phòng |

Người 6/7/8 làm đầu mối kiểm thử/slide/báo cáo, nhưng mọi thành viên đều gửi nội dung, ảnh và test của phần mình. Người 1 có việc tích hợp xuyên suốt nên cần hỗ trợ đóng gói/rà hồ sơ từ người 6 và 9. Tỷ lệ đóng góp ghi theo việc thực tế, không mặc định mỗi người bằng nhau.

### Phân công BONUS bổ sung — cập nhật 16/10/2026

Nhóm chốt làm cùng trong một tuần; giữ nguyên nhiệm vụ 1–9, bổ sung phần phối hợp sau:

| Công việc | Chủ trì | Phối hợp và file |
| --- | --- | --- |
| B01 — PIN và khóa sau 3 lần sai | Người 8 | PinService, PinCredential, AccountLockedException; người 7 làm LoginDialog, người 1 tích hợp, người 5 kiểm tra nghiệp vụ, người 2 chuẩn bị PIN demo |
| B02 — CSV | Người 6 | CsvStatementExporter; người 1 thêm menu/nút và JFileChooser, người 9 đối chiếu dữ liệu |
| B03 — InterestBearing | Người 4 | Người 3 cài đặt trên SavingsAccount, dùng các lớp đã giao; người 9 kiểm tra biểu đồ. Không tạo bản interface thứ hai |

Ngày 3 có nghiệp vụ và dữ liệu mẫu, ngày 4 ghép, ngày 5 kiểm thử, ngày 6 chốt hồ sơ. Mọi người tự kiểm thử phần mình; người 6 tổng hợp kết quả. PIN/CSV vẫn gọi nghiệp vụ chung, không sửa số dư trong GUI hoặc bỏ qua kiểm tra khóa tại Bank.

Quy tắc, tên file, ca BP01–BP09, BC01–BC05, BI01–BI03 và mẫu tạo Issue được công bố trong [tài liệu BONUS trên repository](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/blob/main/docs/BONUS.md). Đây là kế hoạch cần thực hiện, chưa có kết quả kiểm thử thực tế.

### Ghép các phần và làm việc hằng ngày

- Chỉ người 1 chỉnh bố cục MainWindow; người 7/8/9 bàn giao dialog, panel và biểu đồ qua các phương thức đã thống nhất.
- Người 2–5 chốt API model/nghiệp vụ ngày 1: kiểu tiền, kiểu thời gian, constructor, nạp/rút/chuyển, lấy lịch sử, tính lãi và exception. Ghi trong tài liệu API ngắn, thay đổi phải báo người dùng API đó.
- Người 6 thống nhất cấu trúc Transaction với người 2/5/9 trước khi viết bảng hoặc thống kê.
- Người 7 nhận BankingOperations/BankController để gọi nghiệp vụ, không truy cập trực tiếp trường số dư. Người 8 nhận Account để hiển thị. Người 9 nhận lịch sử chỉ đọc để tính dataset.
- Đầu ngày trao đổi 10–15 phút về phần cần ghép; cuối ngày từng người gửi phần chạy được và kết quả kiểm tra. Người 1 ghép thành một bản chung, thông báo ngay lỗi chặn người khác.
- Dùng nhánh theo phần việc, commit bằng tài khoản cá nhân. Một người rà thay đổi trước khi ghép; tránh sửa chung một file cùng lúc.
- Ngày 1–2 GUI/biểu đồ có thể dùng dữ liệu mẫu đúng cấu trúc để không chờ model; ngày 3 phải thay bằng nghiệp vụ/lịch sử thực tế.
- Hết ngày 4 dừng mở rộng chức năng. Ngày 5–7 ưu tiên sửa lỗi, hồ sơ và tập bảo vệ.

Nếu một phần trễ hơn nửa ngày và chặn việc ghép, người 1 điều phối người đã xong sang hỗ trợ. Vẫn ghi đóng góp đúng thực tế; người phụ trách phải hiểu phần được hỗ trợ.

## 8. Kế hoạch kiểm thử

Tài liệu yêu cầu ít nhất 10 test case, có dữ liệu sai và biên. Đề xuất bộ sau; các ca nghiệp vụ quan trọng nên tự động hóa, các ca giao diện/đóng gói kiểm tra trực tiếp.

| Mã | Tình huống | Kết quả mong đợi |
|---|---|---|
| T01 | Nạp số tiền dương | Số dư tăng đúng; một lịch sử mới |
| T02 | Nhập 0, số âm, chữ, bỏ trống hoặc phần lẻ đồng | Từ chối thân thiện; trạng thái không đổi |
| T03 | Rút đúng toàn bộ số dư tài khoản không thấu chi | Thành công; số dư bằng 0 |
| T04 | Rút vượt số dư tiết kiệm | InsufficientFundsException; không đổi lịch sử/bộ đếm |
| T05 | Rút tiết kiệm lần 1–3, sau đó lần 4 cùng tháng | Ba lần đầu thành công; lần thứ tư bị từ chối |
| T06 | Một lần rút lỗi trước lần rút hợp lệ; chuyển sang tháng mới | Lỗi không tiêu lượt; tháng mới có hạn mức rút mới |
| T07 | Rút CheckingAccount đến đúng mức âm cho phép | Thành công |
| T08 | Rút vượt hạn mức thấu chi 1 đồng | Bị từ chối; không đổi trạng thái |
| T09 | Chuyển khoản hợp lệ | Nguồn giảm, đích tăng cùng số tiền; hai lịch sử có tham chiếu chung |
| T10 | Chuyển đến mã không tồn tại hoặc chuyển cho chính mình | Từ chối; cả hai phía không đổi |
| T11 | Chuyển khi nguồn thiếu tiền hoặc hết lượt rút | Không đổi cả số dư, lịch sử và bộ đếm |
| T12 | Gây lỗi ở giai đoạn chuẩn bị giao dịch thứ hai | Không có cập nhật một nửa; không có giao dịch thành công giả |
| T13 | Cộng lãi với số dư và lãi suất đã biết | Khớp công thức và làm tròn; lịch sử/biểu đồ cập nhật |
| T14 | Xử lý lại cùng kỳ lãi; tiến đến kỳ kế tiếp | Cùng kỳ không cộng lại; kỳ kế tiếp được xử lý đúng |
| T15 | Rút FixedTermAccount trước và đúng ngày đáo hạn | Trước hạn từ chối; đến hạn cho phép nếu đủ tiền |
| T16 | Tạo hai tài khoản cùng số tài khoản | Không ghi đè tài khoản đã tồn tại |
| T17 | Lấy lịch sử rồi thử sửa; thử thay đổi Transaction | Không sửa được lịch sử nội bộ/giao dịch |
| T18 | Chọn tài khoản, giao dịch liên tiếp, thời điểm trùng | Bảng/biểu đồ đúng tài khoản; không mất điểm dữ liệu |
| T19 | Kiểm tra menu, shortcut, icon, About và tiếng Việt | Mọi thành phần hoạt động/hiển thị đúng |
| T20 | Chạy JAR ngoài Eclipse, trên máy trình chiếu | Có đủ thư viện và icon; demo hoàn thành không phụ thuộc IDE |

Trong báo cáo, thêm dữ liệu đầu vào cụ thể, kết quả thực tế, Đạt/Không đạt và người kiểm tra. T12 cần cách tạo lỗi có kiểm soát ở bản kiểm thử, không thêm nút gây lỗi vào ứng dụng nộp.

## 9. Hồ sơ và kịch bản bảo vệ

### Báo cáo

15–25 trang, không tính phụ lục; A4, Times New Roman 13, giãn dòng 1,3, lề trái 3 cm, các lề khác 2 cm, có số trang. Code trích dẫn tối đa 20 dòng/đoạn, Consolas 10.

Nội dung: bìa; phân công/đóng góp tổng 100% và chữ ký; giới thiệu; phân tích/thiết kế; cài đặt và ánh xạ OOP; kết quả; ít nhất 10 test case; kết luận; tài liệu tham khảo IEEE hoặc APA 7; phụ lục AI; phụ lục Git. Hình/bảng có số và tên. Ghi trung thực phần được AI hỗ trợ và nhóm đã kiểm chứng/chỉnh sửa gì, bao gồm việc dùng AI hỗ trợ lập kế hoạch nếu được sử dụng trong dự án.

### Slide và thời gian

Chuẩn bị 10–12 slide: tiêu đề; bài toán/phạm vi; phân công; UML tổng thể và phân cấp; ánh xạ OOP; ảnh cửa sổ; biểu đồ; khó khăn/giải pháp; mức hoàn thành bước 1–7 và bonus; kết luận/hướng phát triển.

| Phần | Thời gian |
|---|---|
| Giới thiệu | 1 phút |
| Demo trực tiếp | 3 phút |
| UML và quyết định thiết kế | 1,5 phút |
| Mở code minh họa OOP | 3 phút |
| Khó khăn và cách sửa | 1 phút |
| Kết luận | 0,5 phút |
| Phản biện | 5 phút riêng |

Phân bổ phần nói cho 9 người bên dưới là đề xuất điều chỉnh cho quy mô thực tế, tổng 10 phút; không phải lịch nguyên văn của tài liệu. Demo có thể chia giữa người 7/8/9, phần UML/code xen theo chủ đề. Một người điều khiển máy xuyên suốt để tránh mất thời gian chuyển máy.

| Người | Phần nói chính | Thời lượng tổng |
|---|---|---|
| 1 | Giới thiệu, UML tổng thể và kết luận | 90 giây |
| 2 | Account/Customer và đóng gói | 60 giây |
| 3 | Hai loại tài khoản và ví dụ gọi withdraw đa hình | 60 giây |
| 4 | Loại thứ ba, interface và cộng lãi | 60 giây |
| 5 | Chuyển khoản toàn vẹn, ví dụ lỗi không đổi trạng thái | 60 giây |
| 6 | Transaction bất biến và lịch sử | 60 giây |
| 7 | Demo nạp/rút/chuyển, thông báo lỗi | 90 giây |
| 8 | Danh sách/chi tiết, menu/dialog và cập nhật giao diện | 60 giây |
| 9 | Hai biểu đồ, dữ liệu thống kê và cập nhật sau giao dịch | 60 giây |

Nội dung demo và giải thích phải phối hợp để tổng không quá 10 phút; tập bằng đồng hồ ngày 6 và 7. Cả 9 người tập trả lời ngẫu nhiên, không chỉ học phần mình nói.

Demo bắt buộc: chọn tài khoản không thấu chi → rút quá số dư để hiện lỗi → chuyển khoản giữa hai tài khoản → xem lịch sử hai phía và đường số dư → mô phỏng cộng lãi cuối tháng. Trong demo cũng chỉ ra menu, toolbar, dialog và biểu đồ cột. Chuẩn bị số tiền/đối tượng trước, không nhập ngẫu nhiên khi bảo vệ.

Câu hỏi cần tập: vì sao balance private; vì sao không có setBalance; withdraw đa hình ở đâu; interface giải quyết việc gì; Transaction bất biến thế nào; chuyển khoản lỗi giữ nguyên những trạng thái nào; bộ đếm tháng/lãi xử lý ra sao; biểu đồ lấy dữ liệu từ đâu. Mọi thành viên có thể chỉ đúng vị trí code, không chỉ đọc định nghĩa.

### Gói nộp

```text
OOP_Nhom04_MiniBanking.zip
├── source/                 project Eclipse, không kèm bin/ và target/
├── app/OOP_Nhom04.jar       chạy bằng java -jar
├── report/BaoCao_Nhom04.pdf
├── slides/Slide_Nhom04.pptx hoặc .pdf
├── docs/                   UML ảnh + nguồn .drawio/.puml
└── README.md               thành viên, link Git, cách chạy, video nếu có
```

JAR phải kèm hoặc tìm được JFreeChart theo cách đóng gói đã hướng dẫn trong README; icon phải nằm trong tài nguyên của ứng dụng. Kiểm tra ở thư mục sạch ngoài Eclipse. Nộp trước buổi thuyết trình ít nhất một ngày theo hạn chính thức giảng viên thông báo. Chuẩn bị video demo dự phòng không quá ba phút.

## 10. Checklist kết thúc tuần

- [ ] Bước 1–7 và bảng yêu cầu chung đều đạt; đủ ba lớp con nghiệp vụ và interface có sử dụng thực tế.
- [ ] Hai JDialog gồm About; menu, toolbar, status bar, icon và tiếng Việt đầy đủ.
- [ ] Nạp/rút/chuyển, giới hạn tháng, thấu chi, kỳ hạn và cộng lãi đúng; giao dịch lỗi giữ nguyên trạng thái.
- [ ] Hai biểu đồ khác loại cập nhật đúng, có tiêu đề/nhãn/đơn vị.
- [ ] Ít nhất 10 test case có kết quả thực tế; đã kiểm tra ca sai và biên.
- [ ] JAR chạy ngoài Eclipse và đã thử trên máy trình chiếu.
- [ ] UML, báo cáo 15–25 trang, slide 10–12 trang và ảnh khớp code cuối.
- [ ] Cả 9 người có ít nhất hai lớp có nghĩa, lịch sử commit, phần nói và hiểu code.
- [ ] Có bảng đóng góp/chữ ký, phụ lục AI/Git, README và đủ cấu trúc zip.
- [ ] Demo ≤10 phút, video dự phòng ≤3 phút; nộp đúng hạn chính thức.

- [ ] BONUS B01: PIN và khóa sau 3 lần sai hoạt động đúng, đã kiểm tra quyền tại nghiệp vụ.
- [ ] BONUS B02: xuất CSV đúng tài khoản/dữ liệu/định dạng, xử lý hủy và lỗi ghi file.
- [ ] BONUS B03: InterestBearing được gọi thực tế, không cộng trùng kỳ, UML và demo thể hiện rõ.

Kế hoạch kết thúc ở bản một máy đủ yêu cầu bắt buộc và bonus đã chốt. Không triển khai nhiều máy; không tự cộng điểm bonus khi chưa được giảng viên đánh giá.
