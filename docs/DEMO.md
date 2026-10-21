# Dữ liệu mẫu MiniBank

`DemoDataFactory` tạo 6 tài khoản cho 3 khách hàng giả lập, đủ Savings,
Checking và FixedTerm. Dữ liệu được nạp lại mỗi lần khởi động ứng dụng.

## Tài khoản và PIN mẫu

Đây là PIN giả để thuyết trình. PIN chỉ xuất hiện trong hướng dẫn và phần
khởi tạo dữ liệu; không đưa PIN lên danh sách tài khoản, panel chi tiết hoặc log.

| Số tài khoản | Chủ tài khoản | Loại | Số dư sau khi nạp mẫu (VND) | PIN mẫu |
| --- | --- | --- | ---: | --- |
| 100001 | Nguyễn Văn An | Savings, lãi 6%/năm | 10.500.000 | `001234` |
| 100002 | Nguyễn Văn An | Checking, thấu chi 2.000.000 | -500.000 | `123456` |
| 100003 | Trần Thị Bình | Savings, lãi 5%/năm | 8.250.000 | `234567` |
| 100004 | Trần Thị Bình | FixedTerm, lãi 7%/năm | 21.000.000 | `345678` |
| 100005 | Lê Minh Chi | Checking, thấu chi 1.000.000 | 3.250.000 | `456789` |
| 100006 | Lê Minh Chi | FixedTerm, lãi 6,5%/năm | 14.500.000 | `567890` |

- Mỗi tài khoản được mở trước thời điểm mô phỏng 3 tháng.
- Tài khoản `100004` đáo hạn sau ngày mô phỏng hiện tại 3 tháng; chưa được rút/chuyển ra.
- Tài khoản `100006` đã đáo hạn trước ngày mô phỏng hiện tại 7 ngày; được rút/chuyển ra nếu đủ tiền.
- Lịch sử nằm trong 6 ngày trước thời điểm mô phỏng và được tạo bằng các phương thức
  nạp/rút/chuyển của model. Tổng số dư sau khi nạp mẫu là **57.000.000 VND**.
- Lượt rút Savings được tính theo tháng của lịch sử. Nếu đồng hồ khởi động vào đầu tháng,
  một số giao dịch mẫu thuộc tháng trước và không tiêu lượt của tháng mới.
- Khách hàng, điện thoại và tiền đều là dữ liệu giả lập.

## Khởi tạo khi ghép ứng dụng

`Main` đã thực hiện các bước khởi tạo dưới đây trên Swing EDT và gọi
`BankController.start()`. `MainWindow` giữ bố cục, controller gọi nghiệp vụ qua
`BankingOperations`, dùng chung `PinService` và đồng hồ mô phỏng.

```java
List<Account> accounts = DemoDataFactory.createAccounts(clock);
PinService pins = new PinService();
DemoDataFactory.registerDemoPins(pins, accounts);
Bank bank = new Bank(accounts, pins, clock,
        new TransferService(), new MonthlyInterestService());
```

`clock` là đồng hồ mô phỏng dùng chung. Ví dụ mốc khởi động để tập demo:

```java
SimulationClock clock = new SimulationClock(
        Instant.parse("2026-10-21T02:00:00Z"), ZoneId.of("Asia/Ho_Chi_Minh"));
```

Mốc trên tương ứng 09:00 ngày 21/10/2026 tại Việt Nam. Gọi factory và đăng ký
PIN một lần trước khi mở UI. Chưa có tài khoản nào được tự xác thực; các thao tác
của người dùng sau đó phải qua Bank.

## Chạy ứng dụng đã ghép

Trong Eclipse: mở `Main.java` → **Run As → Java Application**. Thư viện
`lib/jfreechart-1.5.4.jar` đã có trong `.classpath`.

Hoặc dùng terminal từ thư mục gốc dự án (Linux, Bash/Fish; cần JDK 17 trở lên):

```sh
mkdir -p bin
rg --files src -g '*.java' > /tmp/minibank-sources.txt
javac --release 17 -encoding UTF-8 -cp "lib/*" -d bin @/tmp/minibank-sources.txt
java -cp "bin:lib/*" vn.edu.ptit.oop.nhom04.Main
```

1. Chọn `100001`, nhập PIN `001234`. Chỉ sau PIN đúng mới hiện chi tiết/lịch sử.
2. Thử nạp tiền; bảng lịch sử, số dư và biểu đồ được cập nhật sau thành công.
3. Thử rút vượt số dư: lỗi giữ nguyên dữ liệu. Chuyển khoản chọn đích trong dialog;
   nhập số tiền nguyên đồng, ví dụ `100000`, không gõ dấu phân cách hàng nghìn.
4. Chọn tab **Biểu đồ** để xem biến động số dư và tổng nạp/rút theo ngày.
5. **Xuất CSV** qua toolbar hoặc menu **Tệp**, chọn vị trí lưu; xác nhận nếu ghi đè.
6. **Cuối tháng** cộng lãi toàn hệ thống và chuyển đồng hồ đến ngày 1 tháng sau.
   Nút này dùng được cả khi chưa đăng nhập. Nếu lỗi một phần, dữ liệu đã thành công
   vẫn được làm mới, kỳ chưa chuyển để có thể thử lại mà không cộng trùng.
7. Chọn tài khoản khác sẽ xóa quyền/nội dung cũ trước khi hỏi PIN. Hủy hoặc đăng xuất
   để trống chi tiết, lịch sử và biểu đồ. Nhập sai 3 lần khóa tài khoản trong phiên chạy.

Nếu cập nhật giao diện lỗi sau giao dịch, thông báo yêu cầu **không nhập lại giao dịch**;
dùng **Xem → Làm mới** hoặc đăng xuất. Lỗi hiển thị không hoàn tác giao dịch tại Bank.
Khởi động lại ứng dụng nạp lại toàn bộ dữ liệu mẫu, không giữ thay đổi của phiên trước.

## Kiểm tra controller và bản ghép

Cần màn hình đồ họa vì bài kiểm tra mở dialog Swing thật rồi tự đóng. Chạy từ
thư mục dự án, sau khi biên dịch ứng dụng như trên:

```sh
javac --release 17 -encoding UTF-8 -cp "bin:lib/*" -d bin tests/vn/edu/ptit/oop/nhom04/ui/BankControllerTest.java
java -cp "bin:lib/*" vn.edu.ptit.oop.nhom04.ui.BankControllerTest
```

Kiểm tra phiên PIN, hủy/đổi/khóa tài khoản, gắn menu/toolbar CSV, nạp/rút/chuyển qua
Bank thật, callback refresh, lỗi biểu đồ sau giao dịch, cuối tháng thành công/một phần
và khởi động bằng `Main`. Phần điều phối dùng dialog thay thế để gây lỗi có kiểm soát;
phần kiểm tra UI mở LoginDialog, TransactionDialog và JFileChooser thật, xuất CSV thật.
Ảnh bản chạy được lưu tại `/tmp/minibank-main-window.png` và `/tmp/minibank-main-charts.png`.

## Kiểm tra dữ liệu mẫu

Chạy từ thư mục gốc dự án, dùng được trong Bash và Fish:

```sh
mkdir -p /tmp/minibank-demo-test
rg --files src -g '*.java' > /tmp/minibank-demo-sources.txt
javac --release 17 -encoding UTF-8 -cp "lib/*" -d /tmp/minibank-demo-test @/tmp/minibank-demo-sources.txt
javac --release 17 -encoding UTF-8 -cp "/tmp/minibank-demo-test:lib/*" -d /tmp/minibank-demo-test tests/vn/edu/ptit/oop/nhom04/util/DemoDataFactoryTest.java
java -cp "/tmp/minibank-demo-test:lib/*" vn.edu.ptit.oop.nhom04.util.DemoDataFactoryTest
```
