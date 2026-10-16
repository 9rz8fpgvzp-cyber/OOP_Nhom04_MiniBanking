# Sơ đồ tổng hợp luồng MiniBank — nhóm 04

Đọc sơ đồ 1 để hiểu ứng dụng, sơ đồ 2 để biết phần mình nối vào đâu, sơ đồ 3 để hiểu chuyển khoản toàn vẹn. **Số người trong ô là số nhiệm vụ, không phải số Issue.** Đây là thiết kế bàn giao, chưa xác nhận code đã triển khai. Phạm vi: Java Swing, một máy, dữ liệu giả lập trong bộ nhớ.

## 1. Người dùng thao tác như thế nào?

```mermaid
flowchart TD
    START([Mở MiniBank]) --> INIT["Người 1 + 2: khởi tạo cửa sổ và dữ liệu mẫu"]
    INIT --> LIST["Người 7: danh sách tài khoản"]
    LIST --> PICK[Chọn tài khoản và xóa quyền / dữ liệu đang xem]
    PICK --> LOGIN["Người 8: LoginDialog gọi PinService"]
    LOGIN --> CANCEL{Bấm Hủy?}
    CANCEL -->|Có| LIST
    CANCEL -->|Không| LOCK{Tài khoản đã khóa?}
    LOCK -->|Có| DENY[Thông báo khóa, không mở dữ liệu]
    LOCK -->|Không| PIN{PIN đúng?}
    PIN -->|Không| COUNT[Tăng số lần sai liên tiếp]
    COUNT --> THIRD{Đã sai 3 lần?}
    THIRD -->|Có| SETLOCK[Khóa tài khoản]
    SETLOCK --> DENY
    THIRD -->|Chưa| LOGIN
    DENY --> LIST
    PIN -->|Đúng| AUTH[Reset số lần sai, ghi nhận tài khoản được xác thực]
    AUTH --> VIEW["Người 7 / 6 / 9: chi tiết, lịch sử và biểu đồ"]
    VIEW --> ACTION{Chọn thao tác}
    ACTION -->|Nạp / Rút / Chuyển| FORM["Người 7: TransactionDialog"]
    FORM --> BANK["Người 5: Bank kiểm tra quyền và giao dịch"]
    BANK --> RULE["Người 2 / 3 / 4: áp dụng quy tắc loại tài khoản"]
    RULE --> VALID{Đủ điều kiện?}
    VALID -->|Không| ERROR[Báo lỗi, giữ nguyên trạng thái giao dịch]
    ERROR --> VIEW
    VALID -->|Có| COMMIT["Người 5 + 6: cập nhật toàn vẹn và ghi Transaction"]
    COMMIT --> REFRESH["Người 1 điều phối làm mới chi tiết, bảng, biểu đồ"]
    REFRESH --> VIEW
    ACTION -->|Xuất CSV| CSV["Người 6: kiểm tra quyền, chọn file, xác nhận ghi đè và xuất"]
    CSV --> VIEW
    ACTION -->|Đổi tài khoản / Đăng xuất| CLEAR[Xóa quyền và dữ liệu cũ]
    CLEAR --> LIST
```

- Hủy nhập PIN không tính là nhập sai. Tài khoản đã khóa không mở được bằng PIN đúng. Đóng/mở dialog không đặt lại số lần sai hoặc khóa.
- GUI chỉ nhập và hiển thị. **Bank/PinService vẫn kiểm tra quyền ở tầng nghiệp vụ**, kể cả khi gọi trực tiếp, không chỉ dựa vào trạng thái nút.
- Chuyển tiền chỉ xác thực tài khoản gửi; tài khoản nhận không cần PIN và vẫn nhận tiền khi bị khóa theo quy tắc mô phỏng của nhóm.
- Menu Help → About do người 7 làm, người 1 nối vào cửa sổ; đây là luồng xem thông tin nhóm, không thay đổi tài khoản.
- Luồng mô phỏng cuối tháng là tác vụ hệ thống của bản demo, xem phía dưới; không phải giao dịch rút tiền của tài khoản đang chọn.

## Chuẩn bị giao diện chung

```mermaid
flowchart LR
    D["Người 9: phác UI 30–60 phút"] --> G["Nhóm chốt ảnh trong Issue 1"]
    G --> F["Người 1: dựng khung Swing và tab trống"]
    F --> P["Người 6 / 7 / 8 / 9: cùng ghép thành phần của mình"]
```

Người 7 chỉ làm danh sách/chi tiết tài khoản, dialog giao dịch và About. Lịch sử/CSV thuộc người 6, PIN người 8, biểu đồ người 9. Không đổi API hoặc hạn chức năng. Xem [bản bàn giao](BAN_GIAO.md) để biết nội dung bản phác và cách chốt.

## 2. Chín người nối phần của mình vào đâu?

Mũi tên chỉ hướng **gọi hoặc sử dụng dữ liệu**, không có nghĩa phải đợi người trước viết xong. Chốt API trước, dùng dữ liệu mẫu để dựng UI/biểu đồ song song.

```mermaid
flowchart TB
    P1["Người 1 - MainWindow / BankController<br/>Cửa sổ chung và điều phối cập nhật"]
    P7["Người 7 - UI tài khoản và giao dịch<br/>Renderer / DetailsPanel / TransactionDialog / About"]
    P8["Người 8 - PIN trọn luồng<br/>LoginDialog / PinService / PinCredential"]
    P5["Người 5 - Bank / TransferService<br/>Kiểm tra và thực hiện nạp, rút, chuyển"]
    P2["Người 2 - Customer / Account / DemoDataFactory<br/>Nền dữ liệu và tài khoản"]
    P3["Người 3 - SavingsAccount / CheckingAccount<br/>Giới hạn rút và thấu chi"]
    P4["Người 4 - FixedTerm / InterestBearing / MonthlyInterestService<br/>Kỳ hạn và cuối tháng"]
    P6["Người 6 - Transaction / TableModel / CSV<br/>Lịch sử, chọn file và xuất sao kê"]
    P9["Người 9 - StatisticsService / ChartHelper<br/>Hai biểu đồ JFreeChart"]
    P1 -->|Mở và ghép thành phần| P7
    P1 -->|Chọn tài khoản, xác thực| P8
    P1 -->|Gọi nghiệp vụ| P5
    P1 -->|Hiển thị lịch sử và gọi xuất CSV| P6
    P1 -->|Tạo và cập nhật biểu đồ| P9
    P1 -->|Gọi mô phỏng cuối tháng| P4
    P7 -->|Gửi yêu cầu qua controller| P1
    P5 -->|Kiểm tra quyền| P8
    P5 -->|Quản lý tài khoản| P2
    P5 -->|Gọi quy tắc rút đa hình| P3
    P5 -->|Quy tắc kỳ hạn| P4
    P3 -->|Kế thừa Account| P2
    P4 -->|Kế thừa Account và xử lý lãi| P2
    P3 -->|Triển khai InterestBearing| P4
    P5 -->|Tạo bản ghi giao dịch| P6
    P4 -->|Nhờ Bank ghi nhận lãi| P5
    P9 -->|Đọc lịch sử chỉ đọc| P6
```

### Điểm bàn giao để không bị chờ

| Ai bàn giao? | Giao cho ai? | Phải chốt trước |
| --- | --- | --- |
| 2 | 3, 4, 5, 6, 7 | Constructor/getter Account, kiểu tiền, cách cập nhật số dư và lịch sử |
| 6 | 2, 5, 9 | Cấu trúc Transaction và dữ liệu lịch sử mẫu |
| 4 | 3, 5, 9 | InterestBearing, thời gian mô phỏng và kỳ tính lãi |
| 8 | 1, 2, 5 | API xác thực/quyền/khóa; kết quả LoginDialog |
| 5 | 1, 7 | API danh sách/chi tiết/nạp/rút/chuyển/lịch sử và lỗi trả về |
| 7 | 1 | Panel/dialog + callback, tự cùng ghép |
| 6 | 1 | TableModel + action CSV đã có chọn file/ghi đè |
| 9 | 1 | Chart/panel + cách cập nhật dataset, cấu hình JFreeChart |

**Hôm nay 16/10:** nhận và triển khai chữ ký/kiểu dữ liệu theo API v1 tại [API.md](API.md). **Trước 12:00 17/10:** có khung dùng chung biên dịch. **Chiều 17/10:** UI và biểu đồ có bản mẫu, không chờ nghiệp vụ đầy đủ. Không tự tạo Account/Transaction giả cùng tên ở package khác; mock ở bản thử phải được ghi rõ và thay bằng nghiệp vụ thật trước khi chốt.

## 3. Chuyển khoản phải cập nhật nguyên tử thế nào?

```mermaid
flowchart TD
    REQ[Nhận yêu cầu: nguồn, đích, số tiền] --> PRE["Bank / TransferService: kiểm tra quyền nguồn,<br/>tài khoản, số tiền và điều kiện nhận"]
    PRE --> CHECK["Áp dụng quy tắc nguồn:<br/>số dư, lượt rút, thấu chi hoặc đáo hạn"]
    CHECK --> OK{Tất cả điều kiện đạt?}
    OK -->|Không| FAIL[Báo lỗi; không thay đổi số dư, lịch sử, lượt rút]
    OK -->|Có| PREP["Chuẩn bị số dư mới và hai bản ghi liên kết;<br/>chưa sửa trạng thái thật"]
    PREP --> READY{Chuẩn bị thành công?}
    READY -->|Không| FAIL
    READY -->|Có| APPLY["Áp dụng như một thao tác:<br/>hai số dư + hai lịch sử + lượt rút của nguồn"]
    APPLY --> DONE{Áp dụng thành công?}
    DONE -->|Có| SUCCESS[Báo thành công và cập nhật giao diện]
    DONE -->|Không| RESTORE[Khôi phục đầy đủ trạng thái trước giao dịch]
    RESTORE --> FAIL
```

Người 2/3/5/6 phải thống nhất phần kiểm tra, chuẩn bị và áp dụng. Nếu đoạn áp dụng vẫn có thể lỗi, phải có cơ chế khôi phục cả số dư, lịch sử và lượt rút; không chỉ hoàn tiền nguồn bằng một giao dịch nạp mới. Tạo thử lỗi giữa chừng ở bản kiểm thử để chứng minh không cập nhật một nửa. Không cần thêm database cho phạm vi một máy này.

## 4. Mô phỏng cuối tháng

```mermaid
flowchart LR
    BTN["Người 1: nút cuối tháng"] --> MONTH["Người 4: chốt kỳ và thời gian mô phỏng"]
    MONTH --> INTEREST["Gọi InterestBearing trên tài khoản hưởng lãi;<br/>kiểm tra chưa cộng kỳ đó"]
    INTEREST --> BANK["Người 5 + 6: ghi nhận lãi và lịch sử nhất quán"]
    BANK --> NEXT["Người 3 + 4: chuyển kỳ mô phỏng,<br/>reset lượt rút đúng tháng"]
    NEXT --> UI["Người 1 / 7 / 6 / 9:<br/>cập nhật chi tiết, lịch sử, hai biểu đồ"]
```

CheckingAccount không hưởng lãi. Tài khoản khóa vẫn được cộng lãi theo quy tắc demo. Không cộng trùng cùng tài khoản/kỳ; không cho số dư đã đổi mà lịch sử bị thiếu. Nếu một tài khoản xử lý lỗi, báo kết quả rõ ràng, không hiển thị thành công toàn bộ; lần thử lại phải bỏ qua tài khoản/kỳ đã thành công. Thứ tự chốt kỳ → cộng lãi → chuyển kỳ phải được người 3/4/5 thống nhất để không lệch giới hạn rút và timestamp.

## 5. Ai tổng hợp hồ sơ?

Mỗi người tự viết mô tả phần mình, ảnh, UML lớp liên quan, kết quả test và ghi chép Git/AI. Người 2 tổng hợp báo cáo, người 3 tổng hợp UML, người 6 tổng hợp bảng test, người 9 tổng hợp slide/video. Người 1 rà README và gói nộp.

Mốc: nội dung hồ sơ trước 18:00 ngày 19/10; ghép đủ chức năng tối 19/10; chốt toàn bộ 21:00 ngày 20/10; tập ngày 21/10; thuyết trình sáng 22/10. Chi tiết file, Issue và hạn từng người: [BAN_GIAO.md](BAN_GIAO.md). Quy tắc PIN/CSV/lãi và mã kiểm thử: [BONUS.md](BONUS.md).
