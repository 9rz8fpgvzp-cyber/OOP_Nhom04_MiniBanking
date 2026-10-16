# Phân công bàn giao 16/10 — thuyết trình sáng 22/10/2026

Phân công cho 9 thành viên: ứng dụng Java Swing trên một máy, gồm các chức năng bắt buộc và PIN, CSV, InterestBearing.

**Đọc trước:** [Sơ đồ luồng ứng dụng và cách nối phần của 9 người](LUONG_TONG_QUAN.md).

| Người | Issue | Phần phụ trách | Hồ sơ |
| --- | --- | --- | --- |
| 1 — Phạm Đình Doanh | [#3](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/3) | Cửa sổ chính và tích hợp | README, bảng đóng góp/Git và cấu trúc gói nộp |
| 2 | [#2](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/2) | Model nền, dữ liệu mẫu và tổng hợp báo cáo | Đầu mối báo cáo; viết phần Customer/Account và đóng gói |
| 3 | [#4](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/4) | Quy tắc tiết kiệm, thanh toán và UML | Đầu mối UML, viết kế thừa/đa hình và test hai loại tài khoản |
| 4 | [#5](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/5) | Tài khoản kỳ hạn và toàn bộ xử lý cuối tháng | Viết phần kỳ hạn/interface/lãi, test và nội dung slide gửi người 2/9 |
| 5 | [#6](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/6) | Nghiệp vụ ngân hàng và chuyển khoản toàn vẹn | Viết luồng chuyển khoản, test nguyên tử; rà nghiệp vụ bản JAR |
| 6 | [#7](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/7) | Lịch sử và xuất CSV trọn luồng | Đầu mối bảng kiểm thử; viết immutable/composition và CSV |
| 7 | [#8](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/8) | UI danh sách/chi tiết tài khoản, dialog giao dịch và About | Viết phần giao diện và ảnh/test; không tổng hợp slide/báo cáo |
| 8 | [#9](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/9) | PIN và khóa tài khoản trọn luồng | Viết phần PIN/khóa, ảnh và test gửi người 2/9 |
| 9 | [#10](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/10) | Biểu đồ, thống kê và tổng hợp slide | Đầu mối slide/video; viết phần thống kê/biểu đồ |

Constructor, phương thức và kiểu dữ liệu dùng chung: [API.md](API.md).

### Chuẩn bị UI chung trước khi code

1. **Người 9** dành 30–60 phút đầu để phác bố cục UI (giấy hoặc công cụ quen dùng), đăng ảnh vào [Issue #1](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/1). Người 7 hỗ trợ rà trường nhập nếu cần. Sau đó, người 9 làm phần biểu đồ.
2. **Cả nhóm xem và chốt bản phác**: danh sách tài khoản, chi tiết, tab Lịch sử, tab Biểu đồ, vị trí nút nạp/rút/chuyển/CSV/cuối tháng, dialog PIN/giao dịch/About, thông báo lỗi. Thống nhất font, định dạng VND và kích thước cơ bản; chưa cần thiết kế cầu kỳ. Bản phác chỉ được coi là đã chốt khi có xác nhận trong Issue #1.
3. **Người 1** dựng khung Swing theo bản phác đã chốt, có các vùng/tab trống và điểm gắn thành phần. Chỉ người 1 sửa bố cục MainWindow; khung có bản chạy trước 12:00 ngày 17/10. Trong lúc chờ chốt, các bạn vẫn làm model, nghiệp vụ và thử thành phần độc lập.
4. **Từng tác giả cùng người 1 ghép phần mình**: người 6 bàn giao bảng lịch sử/action CSV; người 7 bàn giao danh sách/chi tiết/dialog giao dịch/About; người 8 bàn giao PIN; người 9 bàn giao biểu đồ. Người 2–5 hỗ trợ nối nghiệp vụ và sửa lỗi phần mình.

**Phạm vi người 7:** UI danh sách/chi tiết tài khoản, dialog nạp–rút–chuyển dùng chung và About; không làm UI lịch sử, CSV, PIN hoặc biểu đồ.

## Nhánh và cách nhận việc

Mỗi người nhận một Issue tương ứng, ghi tên/username và dùng nhánh riêng: người 1 `feat/main-window`; 2 `feat/account-model`; 3 `feat/account-types`; 4 `feat/monthly-interest`; 5 `feat/transfers`; 6 `feat/transaction-history`; 7 `feat/dialogs`; 8 `feat/pin-auth`; 9 `feat/charts`. Nếu đã có nhánh và code đang làm, tiếp tục nhánh đó sau khi thống nhất phần file chuyển giao, không tạo lại hoặc xóa công việc cũ.

Bàn giao trong hôm nay nghĩa là mỗi người biết Issue, file, đầu ra, API cần thống nhất và hạn; chưa yêu cầu hoàn thành toàn bộ chức năng ngay 16/10. Giờ trong kế hoạch là hạn nội bộ đề xuất để kịp buổi thuyết trình.

## Lịch chốt trước thuyết trình

| Mốc (giờ Việt Nam) | Kết quả bắt buộc |
| --- | --- |
| Hôm nay, thứ Sáu 16/10 — bàn giao trước 23:00 | Nhận 9 nhiệm vụ, chạy được khung, tạo nhánh; nhận API v1 và người sở hữu file |
| Thứ Bảy 17/10 — 12:00 / 18:00 | API/lớp dùng chung biên dịch / giao diện và biểu đồ mẫu chạy độc lập; ghép nạp/rút bản đầu tối cùng ngày |
| Chủ nhật 18/10 — 18:00 / 21:00 | Từng phần đủ nghiệp vụ và test riêng / ghép chuyển khoản, lịch sử, cộng lãi và PIN; CSV/biểu đồ có bản chạy |
| Thứ Hai 19/10 — 18:00 / 21:00 | Nhận nội dung hồ sơ / ghép đủ bắt buộc + bonus, JAR thử; dừng thêm chức năng |
| Thứ Ba 20/10 — 18:00 / 21:00 | Chốt báo cáo/UML/slide/test / đóng băng source, JAR, CSV, video và gói nộp; sửa hết lỗi chặn demo |
| Thứ Tư 21/10 | Tập 2 lượt đủ 9 người, demo ≤10 phút, phản biện; kiểm tra máy trình chiếu và bản dự phòng |
| Sáng thứ Năm 22/10 | Thuyết trình; dùng bản đã kiểm tra, không đổi tính năng sát giờ |

Mốc 20/10 là hạn nội bộ để hoàn thiện trước buổi thuyết trình khoảng 1–2 ngày. Nếu hạn nộp chính thức của thầy sớm hơn thì theo hạn sớm hơn.

## Cách làm để không phải chờ nhau

- Trong 16/10: nhận Issue, tạo nhánh, pull bản chung và xác nhận chạy Main; dùng constructor, phương thức, kiểu và exception đã chốt trong [API v1](API.md). Các tác giả triển khai đúng chữ ký; đổi API phải báo người dùng và cập nhật tài liệu.
- Khung lớp/API biên dịch phải bàn giao trước 12:00 ngày 17/10 cho các phần dùng chung; người làm UI/biểu đồ dựng bản mẫu song song, không chờ nghiệp vụ hoàn thiện.
- Chỉ tác giả sửa file của mình. Người 1 điều phối; tác giả từng phần cùng nối và sửa phần của mình. Không tạo lớp cùng tên ở package khác để né phụ thuộc.
- Mỗi ngày trước 21:00 có PR nhỏ chạy được và cập nhật Issue: đã làm / còn thiếu / đang bị chặn bởi ai. Vướng quá 2 giờ phải báo để hỗ trợ, không chờ sang hôm sau.
- Dữ liệu mẫu/mock chỉ dùng kiểm tra độc lập; bản ghép ngày 19/10 phải gọi nghiệp vụ thật. Mock không được coi là chức năng đã hoàn thành.
- Mỗi người gửi mô tả OOP, ảnh, ca test/kết quả, commit và ghi chép AI cho người 2 (báo cáo), người 3 (UML), người 6 (bảng test), người 9 (slide) trước 18:00 ngày 19/10; cập nhật phần sửa đến 18:00 ngày 20/10.
- Done khi đã ghép và chạy đúng, có test thực tế.

## Người 1 — Cửa sổ chính và tích hợp

Issue #3. Nhánh riêng theo nhiệm vụ; dùng tài khoản Git cá nhân.

### File sở hữu

- `src/vn/edu/ptit/oop/nhom04/ui/MainWindow.java`
- `src/vn/edu/ptit/oop/nhom04/ui/BankController.java`

### Hạn bàn giao

- Bản đầu: **17/10 12:00: cửa sổ chứa panel/bảng/biểu đồ mẫu, các nút gọi được callback.**
- Bản chức năng: **19/10 21:00: bản ghép đủ chức năng; 20/10 21:00: JAR và gói nộp.**
- Bản cuối chung: **20/10 21:00**; sáng 22/10 thuyết trình.

### Checklist

- [ ] Nhận bản phác UI đã được nhóm chốt ở Issue #1; dựng vùng danh sách/chi tiết và các tab trống, thống nhất điểm gắn thành phần với người 6/7/8/9.

- [ ] Menu/toolbar/status/icon/tooltip và bố cục chung; chỉ người 1 sửa MainWindow.
- [ ] Nhận panel/dialog/action từ người 6/7/8/9; từng tác giả cùng ghép và sửa phần mình.
- [ ] Đổi tài khoản/đăng xuất xóa quyền, dữ liệu cũ; hủy PIN không lộ chi tiết.
- [ ] Giao dịch thành công cập nhật danh sách, chi tiết, bảng, hai biểu đồ và status; lỗi giữ nguyên dữ liệu.
- [ ] Chỉ nối menu CSV vào action người 6 bàn giao; không tự viết exporter hoặc bộ chọn file thứ hai.
- [ ] Đóng gói JAR có thư viện/icon; người 9 thử trên máy trình chiếu, người 5 cùng kiểm tra nghiệp vụ.

Hồ sơ: **README, bảng đóng góp/Git và cấu trúc gói nộp**. Tự kiểm thử và cùng người 1 ghép phần mình.

## Người 2 — Model nền, dữ liệu mẫu và tổng hợp báo cáo

Issue #2. Nhánh riêng theo nhiệm vụ; dùng tài khoản Git cá nhân.

### File sở hữu

- `src/vn/edu/ptit/oop/nhom04/model/AccountType.java`
- `src/vn/edu/ptit/oop/nhom04/model/AccountSummary.java`
- `src/vn/edu/ptit/oop/nhom04/model/Customer.java`
- `src/vn/edu/ptit/oop/nhom04/model/Account.java`
- `src/vn/edu/ptit/oop/nhom04/util/DemoDataFactory.java`

### Hạn bàn giao

- Bản đầu: **17/10 12:00: Account/Customer biên dịch, constructor/getter và cơ chế cập nhật theo API v1.**
- Bản chức năng: **18/10 18:00: model nền + dữ liệu mẫu đầy đủ; 20/10 18:00: báo cáo bản cuối.**
- Bản cuối chung: **20/10 21:00**; sáng 22/10 thuyết trình.

### Checklist

- [ ] Customer có mã/tên/điện thoại; Account trừu tượng, balance private, không setBalance public.
- [ ] Chốt constructor, BigDecimal, lịch sử chỉ đọc và cơ chế cập nhật nguyên tử với người 3/4/5/6 ngay 16/10.
- [ ] Bàn giao dữ liệu 4–6 tài khoản, ít nhất 3 khách hàng, đủ 3 loại; tạo bằng API model khi các lớp con có.
- [ ] Dữ liệu lịch sử tạo qua nghiệp vụ, số dư khớp; phối hợp người 8 đăng ký PIN demo khác nhau, có số 0 đầu.
- [ ] Khung báo cáo và mục lục có ngay 17/10; nhận nội dung từng tác giả trước 19/10 18:00.
- [ ] Tổng hợp báo cáo 15–25 trang và phụ lục AI/Git từ nội dung nhóm; không viết thay phần của các bạn.

Hồ sơ: **Đầu mối báo cáo; viết phần Customer/Account và đóng gói**. Tự kiểm thử và cùng người 1 ghép phần mình.

## Người 3 — Quy tắc tiết kiệm, thanh toán và UML

Issue #4. Nhánh riêng theo nhiệm vụ; dùng tài khoản Git cá nhân.

### File sở hữu

- `src/vn/edu/ptit/oop/nhom04/model/SavingsAccount.java`
- `src/vn/edu/ptit/oop/nhom04/model/CheckingAccount.java`
- `src/vn/edu/ptit/oop/nhom04/exception/InsufficientFundsException.java`
- `src/vn/edu/ptit/oop/nhom04/exception/WithdrawalLimitExceededException.java`

### Hạn bàn giao

- Bản đầu: **17/10 12:00: chữ ký hai lớp con/exception, dùng Account đã thống nhất.**
- Bản chức năng: **18/10 18:00: đủ quy tắc và test; 20/10 18:00: UML khớp code cuối.**
- Bản cuối chung: **20/10 21:00**; sáng 22/10 thuyết trình.

### Checklist

- [ ] Savings tối đa 3 lần rút thành công/tháng, không vượt số dư; thao tác lỗi không tiêu lượt.
- [ ] Checking cho phép đến đúng số dư + thấu chi, vượt 1 đồng phải lỗi.
- [ ] Savings triển khai InterestBearing của người 4; dùng cùng kỳ mô phỏng, không tự tạo interface khác.
- [ ] Chốt thao tác kiểm tra/ghi nhận với người 5 để chuyển khoản lỗi không đổi số dư/lượt rút.
- [ ] Kiểm thử rút ở biên, lượt thứ 4, tháng mới, chuyển khoản từ Savings và rollback.
- [ ] Tổng hợp UML từ thông tin lớp mỗi tác giả gửi; người 4/5/8 kiểm tra phần interface/giao dịch/PIN.

Hồ sơ: **Đầu mối UML, viết kế thừa/đa hình và test hai loại tài khoản**. Tự kiểm thử và cùng người 1 ghép phần mình.

## Người 4 — Tài khoản kỳ hạn và toàn bộ xử lý cuối tháng

Issue #5. Nhánh riêng theo nhiệm vụ; dùng tài khoản Git cá nhân.

### File sở hữu

- `src/vn/edu/ptit/oop/nhom04/service/SimulationClock.java`
- `src/vn/edu/ptit/oop/nhom04/model/FixedTermAccount.java`
- `src/vn/edu/ptit/oop/nhom04/model/InterestBearing.java`
- `src/vn/edu/ptit/oop/nhom04/service/MonthlyInterestService.java`
- `src/vn/edu/ptit/oop/nhom04/exception/AccountNotMaturedException.java`

### Hạn bàn giao

- Bản đầu: **17/10 12:00: InterestBearing và API thời gian/kỳ mô phỏng cho người 3/5.**
- Bản chức năng: **18/10 18:00: kỳ hạn + lãi chạy độc lập; 19/10 21:00: ghép nút cuối tháng.**
- Bản cuối chung: **20/10 21:00**; sáng 22/10 thuyết trình.

### Checklist

- [ ] FixedTerm là lớp con nghiệp vụ thứ ba, có ngày đáo hạn và quy tắc rút rõ ràng.
- [ ] Tính lãi BigDecimal = số dư chốt kỳ × lãi suất năm / 12, làm tròn thống nhất; Checking không nhận lãi.
- [ ] MonthlyInterestService gọi qua InterestBearing; phối hợp Savings của người 3.
- [ ] Một thời gian mô phỏng chung; chống cộng trùng tài khoản/kỳ, reset lượt rút đúng tháng.
- [ ] Bank ghi khoản lãi và lịch sử nhất quán; tài khoản khóa vẫn nhận lãi theo quy tắc demo.
- [ ] Tự ghép callback cuối tháng cùng người 1 và cập nhật bảng/biểu đồ với người 6/9; ghi kết quả BI01–BI03.

Hồ sơ: **Viết phần kỳ hạn/interface/lãi, test và nội dung slide gửi người 2/9**. Tự kiểm thử và cùng người 1 ghép phần mình.

## Người 5 — Nghiệp vụ ngân hàng và chuyển khoản toàn vẹn

Issue #6. Nhánh riêng theo nhiệm vụ; dùng tài khoản Git cá nhân.

### File sở hữu

- `src/vn/edu/ptit/oop/nhom04/exception/InvalidTransferException.java`
- `src/vn/edu/ptit/oop/nhom04/service/Bank.java`
- `src/vn/edu/ptit/oop/nhom04/service/TransferService.java`
- `src/vn/edu/ptit/oop/nhom04/service/BankingOperations.java`
- `src/vn/edu/ptit/oop/nhom04/exception/InvalidAmountException.java`
- `src/vn/edu/ptit/oop/nhom04/exception/AccountNotFoundException.java`

### Hạn bàn giao

- Bản đầu: **17/10 12:00: interface nghiệp vụ, exception và cách lấy danh sách/lịch sử cho GUI.**
- Bản chức năng: **18/10 18:00: nạp/rút/chuyển chạy độc lập; 19/10 21:00: ghép với PIN và GUI.**
- Bản cuối chung: **20/10 21:00**; sáng 22/10 thuyết trình.

### Checklist

- [ ] Quản lý tài khoản theo mã, không ghi đè khi trùng; kiểm tra số tiền dương và tài khoản tồn tại.
- [ ] Chuyển khoản kiểm tra toàn bộ trước khi commit; lỗi giữ nguyên số dư, lịch sử, bộ đếm hai bên.
- [ ] Dùng chung quy tắc withdraw của lớp con, không viết công thức riêng trong Bank/GUI.
- [ ] Gọi PinService người 8 để kiểm tra quyền cho chi tiết/lịch sử/nạp/rút/chuyển ra; không chỉ ẩn nút.
- [ ] Tài khoản nhận không cần PIN, tài khoản khóa vẫn nhận chuyển vào và lãi; không tự mở khóa.
- [ ] Ghi lịch sử qua Transaction người 6; tự ghép nghiệp vụ với người 1/7/8 và kiểm thử cả đường thành công/lỗi.

Hồ sơ: **Viết luồng chuyển khoản, test nguyên tử; rà nghiệp vụ bản JAR**. Tự kiểm thử và cùng người 1 ghép phần mình.

## Người 6 — Lịch sử và xuất CSV trọn luồng

Issue #7. Nhánh riêng theo nhiệm vụ; dùng tài khoản Git cá nhân.

### File sở hữu

- `src/vn/edu/ptit/oop/nhom04/model/TransactionDirection.java`
- `src/vn/edu/ptit/oop/nhom04/model/Transaction.java`
- `src/vn/edu/ptit/oop/nhom04/model/TransactionType.java`
- `src/vn/edu/ptit/oop/nhom04/ui/TransactionTableModel.java`
- `src/vn/edu/ptit/oop/nhom04/util/CsvStatementExporter.java`
- `src/vn/edu/ptit/oop/nhom04/ui/CsvExportAction.java`

### Hạn bàn giao

- Bản đầu: **17/10 12:00: cấu trúc Transaction/getter và mẫu lịch sử cho người 2/5/9.**
- Bản chức năng: **18/10 18:00: bảng + CSV chạy độc lập; 19/10 21:00: ghép menu và dữ liệu thật.**
- Bản cuối chung: **20/10 21:00**; sáng 22/10 thuyết trình.

### Checklist

- [ ] Transaction bất biến: mã, loại tài khoản/giao dịch, số tiền, thời gian, số dư sau; chuyển vào/ra có liên kết rõ ràng.
- [ ] TransactionTableModel nhận lịch sử chỉ đọc, cập nhật đúng tài khoản và tiếng Việt/VND.
- [ ] Tự làm trọn CSV: CsvExportAction mở JFileChooser, hỏi ghi đè, gọi exporter, thông báo kết quả/lỗi; người 1 chỉ gắn action vào menu.
- [ ] Xuất đúng tài khoản đang xác thực; UTF-8, header thống nhất, quote/escape đúng; hủy không ghi, rỗng chỉ header.
- [ ] Kiểm thử BC01–BC05; đối chiếu bảng, CSV, số dư và số liệu người 9.
- [ ] Tập hợp kết quả test do từng tác giả gửi, không kiểm thử thay cả nhóm; đánh dấu rõ test chưa chạy.

Hồ sơ: **Đầu mối bảng kiểm thử; viết immutable/composition và CSV**. Tự kiểm thử và cùng người 1 ghép phần mình.

## Người 7 — UI danh sách/chi tiết tài khoản, dialog giao dịch và About

Issue #8. Nhánh riêng theo nhiệm vụ; dùng tài khoản Git cá nhân.

### File sở hữu

- `src/vn/edu/ptit/oop/nhom04/ui/AccountListRenderer.java`
- `src/vn/edu/ptit/oop/nhom04/ui/panels/AccountDetailsPanel.java`
- `src/vn/edu/ptit/oop/nhom04/ui/dialogs/TransactionDialog.java`
- `src/vn/edu/ptit/oop/nhom04/ui/dialogs/AboutDialog.java`

### Hạn bàn giao

- Bản đầu: **17/10 18:00: renderer/panel/dialog chạy với dữ liệu mẫu và callback giả.**
- Bản chức năng: **18/10 18:00: UI đủ trường/thông báo; 19/10 21:00: tự ghép với người 1/5.**
- Bản cuối chung: **20/10 21:00**; sáng 22/10 thuyết trình.

### Checklist

- [ ] Viết AccountListRenderer và AccountDetailsPanel cho danh sách và chi tiết tài khoản.
- [ ] Hiển thị chủ sở hữu/số tài khoản/số dư; Savings lãi/lượt còn lại, Checking thấu chi/khả dụng, FixedTerm đáo hạn.
- [ ] TransactionDialog dùng chung nạp/rút/chuyển, kiểm tra dữ liệu, xác nhận nguồn/đích/số tiền và hiển thị lỗi.
- [ ] AboutDialog có đủ thông tin nhóm 9 người; phối hợp LoginDialog của người 8.
- [ ] UI chỉ gọi callback/nghiệp vụ, không sửa số dư hoặc tự tính lãi; dữ liệu giả chỉ ở bản thử, thay bằng dữ liệu thật khi ghép.
- [ ] Sau hủy PIN/đổi tài khoản phải xóa nội dung cũ; cùng người 1/8 kiểm thử luồng; kiểm tra icon/VND/tiếng Việt.

Hồ sơ: **Viết phần giao diện và ảnh/test; không tổng hợp slide/báo cáo**. Tự kiểm thử và cùng người 1 ghép phần mình.

## Người 8 — PIN và khóa tài khoản trọn luồng

Issue #9. Nhánh riêng theo nhiệm vụ; dùng tài khoản Git cá nhân.

### File sở hữu

- `src/vn/edu/ptit/oop/nhom04/exception/AuthenticationRequiredException.java`
- `src/vn/edu/ptit/oop/nhom04/model/PinCredential.java`
- `src/vn/edu/ptit/oop/nhom04/service/PinService.java`
- `src/vn/edu/ptit/oop/nhom04/exception/AccountLockedException.java`
- `src/vn/edu/ptit/oop/nhom04/ui/dialogs/LoginDialog.java`

### Hạn bàn giao

- Bản đầu: **17/10 12:00: API PIN/quyền/khóa cho người 1/2/5; 17/10 18:00: dialog với service mẫu.**
- Bản chức năng: **18/10 18:00: PIN + dialog chạy độc lập; 19/10 21:00: ghép với Bank và chọn tài khoản.**
- Bản cuối chung: **20/10 21:00**; sáng 22/10 thuyết trình.

### Checklist

- [ ] Viết LoginDialog và nối PinService; phối hợp người 7 để hiển thị tài khoản sau khi xác thực.
- [ ] Tự làm trọn PinCredential + PinService + LoginDialog, PIN demo 6 chữ số có số 0 đầu, JPasswordField, băm kèm salt, không log.
- [ ] Sai liên tiếp lần thứ ba khóa; đúng trước khi khóa reset số lần sai, đúng sau khóa vẫn bị từ chối.
- [ ] Hủy dialog không tính sai; đóng/mở hoặc đổi tài khoản không reset khóa/bộ đếm; đổi/đăng xuất xóa quyền cũ.
- [ ] Cung cấp API kiểm tra quyền cho Bank người 5; bảo vệ chi tiết/lịch sử/CSV và thao tác chủ động. Khóa vẫn nhận tiền/lãi.
- [ ] Ghép toàn bộ luồng với người 1/5/7; kiểm thử BP01–BP09; ghi hạn chế trạng thái chỉ trong phiên chạy.

Hồ sơ: **Viết phần PIN/khóa, ảnh và test gửi người 2/9**. Tự kiểm thử và cùng người 1 ghép phần mình.

## Người 9 — Biểu đồ, thống kê và tổng hợp slide

Issue #10. Nhánh riêng theo nhiệm vụ; dùng tài khoản Git cá nhân.

### File sở hữu

- `src/vn/edu/ptit/oop/nhom04/util/ChartHelper.java`
- `src/vn/edu/ptit/oop/nhom04/service/StatisticsService.java`

### Hạn bàn giao

- Bản đầu: **17/10 18:00: hai biểu đồ chạy bằng danh sách Transaction mẫu người 6 đã chốt.**
- Bản chức năng: **18/10 18:00: dataset đúng; 19/10 21:00: ghép dữ liệu thật; 20/10 18:00: slide/video.**
- Bản cuối chung: **20/10 21:00**; sáng 22/10 thuyết trình.

### Checklist

- [ ] Phác UI chung trong 30–60 phút đầu, đăng ảnh Issue #1; nhóm xác nhận bản chốt để người 1 dựng khung. Sau đó tiếp tục biểu đồ; không chờ nghiệp vụ xong mới làm bản mẫu.

- [ ] Hai JFreeChart khác loại: đường biến động số dư, cột tổng nạp/rút theo ngày, đủ nhãn/đơn vị/tiêu đề.
- [ ] Biểu đồ cột chỉ tính DEPOSIT/WITHDRAW; biểu đồ số dư gồm mọi giao dịch, có điểm đầu kỳ và giữ đủ các giao dịch trùng timestamp.
- [ ] Tự bàn giao cấu hình JFreeChart và ghép panel/dataset cùng người 1; không yêu cầu người 1 viết lại biểu đồ.
- [ ] Đổi tài khoản/hủy PIN xóa dữ liệu cũ; cập nhật sau giao dịch/cộng lãi, đối chiếu CSV người 6.
- [ ] Tổng hợp slide 10–12 trang từ nội dung từng người; không viết thay phần giải thích của tác giả.
- [ ] Quay video dự phòng ≤3 phút và thử JAR trên máy trình chiếu; báo lỗi để chủ phần sửa, hỗ trợ người 1 đóng gói.

Hồ sơ: **Đầu mối slide/video; viết phần thống kê/biểu đồ**. Tự kiểm thử và cùng người 1 ghép phần mình.
