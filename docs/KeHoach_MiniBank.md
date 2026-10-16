# Kế hoạch MiniBank — nhóm 9 người, hoàn thành trong 1 tuần

Ngày lập: 14/10/2026. Cập nhật: 16/10/2026. Trạng thái: đã có bản khung; các chức năng tiếp theo đang chờ triển khai.

Nguồn yêu cầu: tài liệu `Java_OOP_ThuyetTrinh_10Nhom.pdf` trong thư mục môn học, đặc biệt mục 3–5, thẻ Mini Banking trang 11, mục 7–9 và checklist trang 27. Các quy tắc bổ sung được ghi rõ là quyết định thiết kế của nhóm, không phải yêu cầu nguyên văn của giảng viên.

Nhóm 04 có 9 thành viên, làm ứng dụng trên một máy. Chốt bản cuối trước 21:00 ngày 20/10/2026, thuyết trình sáng 22/10. Tên và username của các thành viên được bổ sung khi nhận Issue.

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

## 6. Lịch bàn giao hiện hành — 16/10 đến sáng 22/10/2026

| Mốc (giờ Việt Nam) | Kết quả bắt buộc |
| --- | --- |
| Hôm nay, thứ Sáu 16/10 — bàn giao trước 23:00 | Nhận 9 nhiệm vụ, chạy được khung, tạo nhánh; chốt API và người sở hữu file |
| Thứ Bảy 17/10 — 12:00 / 18:00 | API/lớp dùng chung biên dịch / giao diện và biểu đồ mẫu chạy độc lập; ghép nạp/rút bản đầu tối cùng ngày |
| Chủ nhật 18/10 — 18:00 / 21:00 | Từng phần đủ nghiệp vụ và test riêng / ghép chuyển khoản, lịch sử, cộng lãi và PIN; CSV/biểu đồ có bản chạy |
| Thứ Hai 19/10 — 18:00 / 21:00 | Nhận nội dung hồ sơ / ghép đủ bắt buộc + bonus, JAR thử; dừng thêm chức năng |
| Thứ Ba 20/10 — 18:00 / 21:00 | Chốt báo cáo/UML/slide/test / đóng băng source, JAR, CSV, video và gói nộp; sửa hết lỗi chặn demo |
| Thứ Tư 21/10 | Tập 2 lượt đủ 9 người, demo ≤10 phút, phản biện; kiểm tra máy trình chiếu và bản dự phòng |
| Sáng thứ Năm 22/10 | Thuyết trình; dùng bản đã kiểm tra, không đổi tính năng sát giờ |

Mốc 20/10 là hạn nội bộ để hoàn thiện trước buổi thuyết trình khoảng 1–2 ngày. Nếu hạn nộp chính thức của thầy sớm hơn thì theo hạn sớm hơn.

## 7. Phân công cho 9 người

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

Chi tiết file, điều kiện bàn giao và cách làm song song xem [BAN_GIAO.md](BAN_GIAO.md). Triển khai đúng chữ ký đã chốt trong [API v1](API.md). PIN giao trọn người 8; UI tài khoản/giao dịch giao người 7; CSV/chọn file giao trọn người 6. Mọi tác giả cùng ghép và kiểm thử phần mình với người 1.

### Chuẩn bị UI chung trước khi code

1. **Người 9** dành 30–60 phút đầu để phác bố cục UI (giấy hoặc công cụ quen dùng), đăng ảnh vào [Issue #1](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/1). Người 7 hỗ trợ rà trường nhập nếu cần. Sau đó, người 9 làm phần biểu đồ.
2. **Cả nhóm xem và chốt bản phác**: danh sách tài khoản, chi tiết, tab Lịch sử, tab Biểu đồ, vị trí nút nạp/rút/chuyển/CSV/cuối tháng, dialog PIN/giao dịch/About, thông báo lỗi. Thống nhất font, định dạng VND và kích thước cơ bản; chưa cần thiết kế cầu kỳ. Bản phác chỉ được coi là đã chốt khi có xác nhận trong Issue #1.
3. **Người 1** dựng khung Swing theo bản phác đã chốt, có các vùng/tab trống và điểm gắn thành phần. Chỉ người 1 sửa bố cục MainWindow; khung có bản chạy trước 12:00 ngày 17/10. Trong lúc chờ chốt, các bạn vẫn làm model, nghiệp vụ và thử thành phần độc lập.
4. **Từng tác giả cùng người 1 ghép phần mình**: người 6 bàn giao bảng lịch sử/action CSV; người 7 bàn giao danh sách/chi tiết/dialog giao dịch/About; người 8 bàn giao PIN; người 9 bàn giao biểu đồ. Người 2–5 hỗ trợ nối nghiệp vụ và sửa lỗi phần mình.

**Phạm vi người 7:** UI danh sách/chi tiết tài khoản, dialog nạp–rút–chuyển dùng chung và About; không làm UI lịch sử, CSV, PIN hoặc biểu đồ.

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

Nội dung demo và giải thích phải phối hợp để tổng không quá 10 phút; tập bằng đồng hồ ngày 20 và 21/10. Cả 9 người tập trả lời ngẫu nhiên, không chỉ học phần mình nói.

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
