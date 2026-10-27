# Chạy và demo MiniBank — API v2

Yêu cầu JDK 17 trở lên. Dữ liệu/PIN bên dưới hoàn toàn giả lập, chỉ dùng cho bài tập.
Đồng hồ mô phỏng khởi đầu **21/10/2026 09:00 (Asia/Ho_Chi_Minh)**; khóa PIN dùng đồng hồ thật,
không đổi khi bấm Cuối tháng.

## Chạy

Trong Eclipse: Import project có sẵn, chạy `vn.edu.ptit.oop.nhom04.Main`.
Thư mục `src/resources` được copy cùng nguồn để nạp icon qua classpath.

Trong terminal (Linux/macOS hoặc Git Bash có JDK):

```bash
./scripts/build.sh
java -jar dist/MiniBank.jar
```

Khi chuyển sang máy khác, copy **cả thư mục `dist`**, giữ `lib/jfreechart-1.5.4.jar`
cạnh `MiniBank.jar`. Không cần Eclipse để chạy. Windows có thể dùng Git Bash để build;
JAR đã build chạy bằng lệnh `java -jar` trong PowerShell/CMD bình thường.

## Đăng nhập khách hàng

Mở app hiện dialog **Mã khách hàng + PIN**; Hủy/X về màn hình chào trống.
Không nhập STK vào ô mã khách hàng. Không hiện PIN trên giao diện ứng dụng.

| Mã khách hàng | Họ tên | PIN demo | STK được thấy |
| --- | --- | --- | --- |
| KH001 | Nguyễn Văn An | `001234` | 100001, 100002 |
| KH002 | Trần Thị Bình | `234567` | 100003, 100004 |
| KH003 | Lê Minh Chi | `456789` | 100005, 100006 |

Đúng PIN mới hiện mã/tên/điện thoại và danh sách STK của khách hàng đó.
Chọn STK để xem chi tiết, lịch sử, biểu đồ; đổi giữa hai STK cùng chủ không hỏi lại PIN.
Sai liên tiếp ba lần khóa **khách hàng** 30 giây, có đếm ngược. PIN đúng trong lúc khóa vẫn
bị từ chối. Hủy/mở lại dialog hoặc đổi user không xóa khóa. Hết khóa phải nhập PIN đúng lại.
Đăng xuất xóa mọi nội dung riêng tư và mở lại dialog để chọn khách hàng khác.

## Dữ liệu ngay sau khi mở app

| STK | Chủ | Loại | Số dư VND | Điều kiện |
| --- | --- | --- | ---: | --- |
| 100001 | KH001 | Tiết kiệm | 11.000.000 | Lãi 6%/năm; đã rút một lần trong tháng 10/2026 |
| 100002 | KH001 | Thanh toán | -1.500.000 | Thấu chi 5.000.000; khả dụng 3.500.000 |
| 100003 | KH002 | Thanh toán | 6.000.000 | Thấu chi 2.000.000 |
| 100004 | KH002 | Tiết kiệm | 12.000.000 | Lãi 6%/năm |
| 100005 | KH003 | Tiền gửi có kỳ hạn | 50.000.000 | Lãi 8%/năm; đáo hạn 21/01/2027 |
| 100006 | KH003 | Tiền gửi có kỳ hạn | 20.000.000 | Lãi 7%/năm; đáo hạn 21/10/2026 |

Tổng ban đầu **97.500.000 VND**. Ba STK 100004/100005/100006 chưa có giao dịch mẫu;
bảng lịch sử rỗng và xuất CSV chỉ có header là hợp lệ. Biểu đồ số dư vẫn có điểm mở tài khoản.
Mọi dữ liệu được tạo qua model, không sửa số dư trực tiếp để tạo lịch sử giả.

## Luồng demo ngắn

1. Đăng nhập `KH001` / `001234`; kiểm tra chỉ có hai STK của An và đủ thông tin khách hàng.
2. Chọn `100001`, nạp `100000`: số dư tăng từ 11.000.000 lên 11.100.000,
   thêm một bản ghi, biểu đồ cập nhật. Nhập số nguyên đồng, không gõ dấu phân cách hàng nghìn.
3. Thử số tiền `abc`, `0`, `-100`, `1000.5`: bị từ chối, số dư không đổi.
4. Đổi `100002` không nhập PIN lại. Chuyển `100000` đến `100003` (khác chủ, không nằm trong danh sách).
   Nguồn giảm còn -1.600.000, đích tăng lên 6.100.000; mỗi bên một bản ghi cùng referenceId.
5. Chọn tab Biểu đồ: đường số dư có cả chuyển khoản; cột nạp/rút chỉ tính DEPOSIT/WITHDRAW.
6. Xuất CSV: chọn đường dẫn, kiểm tra header + số dòng bằng lịch sử đang thấy.
   Hủy không ghi file; có file sẵn thì hỏi ghi đè. CSV giữ enum/ISO phục vụ đọc dữ liệu.
7. Đăng xuất, đăng nhập KH002: không còn thông tin/lịch sử của An; số dư 100003 là 6.100.000.
8. Đăng nhập KH003: rút từ 100005 trước 21/01/2027 bị chặn bằng thông báo ngày có dấu;
   100006 đã đáo hạn nên được rút nếu đủ số dư.
9. Bấm Cuối tháng khi đã đăng nhập: xử lý mọi tài khoản hưởng lãi, thời gian chuyển sang
   **01/11/2026 00:00**. Có thể bấm khi chưa chọn STK. Nếu chưa giao dịch sau khi mở app,
   lãi tháng 10 là 565.000 VND, tổng sau lãi 98.065.000 VND.
10. Đăng xuất, nhập sai PIN của một khách hàng ba lần: thấy khóa 30 giây; đổi sang khách hàng khác vẫn đăng nhập được.

## Kiểm thử và phạm vi lưu trữ

```bash
./scripts/test.sh        # nghiệp vụ, dữ liệu mẫu, PIN, repository, quyền và khôi phục giao dịch
./scripts/test.sh --gui  # thêm Swing/dialog/CSV thật; cần màn hình đồ họa
```

Chi tiết kết quả trong [TESTING.md](TESTING.md). Test khóa dùng Clock điều khiển được,
không chờ thật 30 giây. Mở lại ứng dụng tạo lại dữ liệu và khóa/PIN từ đầu; không có database
hoặc lưu phiên. CSV đã xuất vẫn nằm trên ổ đĩa nhưng không được tự nhập vào app.
