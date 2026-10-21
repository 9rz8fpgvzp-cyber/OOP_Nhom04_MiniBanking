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

## Kiểm tra dữ liệu mẫu

Chạy từ thư mục gốc dự án, dùng được trong Bash và Fish:

```sh
mkdir -p /tmp/minibank-demo-test
rg --files src -g '*.java' > /tmp/minibank-demo-sources.txt
javac --release 17 -encoding UTF-8 -cp "lib/*" -d /tmp/minibank-demo-test @/tmp/minibank-demo-sources.txt
javac --release 17 -encoding UTF-8 -cp "/tmp/minibank-demo-test:lib/*" -d /tmp/minibank-demo-test tests/vn/edu/ptit/oop/nhom04/util/DemoDataFactoryTest.java
java -cp "/tmp/minibank-demo-test:lib/*" vn.edu.ptit.oop.nhom04.util.DemoDataFactoryTest
```
