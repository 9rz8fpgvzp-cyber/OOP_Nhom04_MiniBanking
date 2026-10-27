# Bản tích hợp khách hàng v2

Nhánh: `feat/customer-login-integration`, đã ghép main đến commit `13d8f73` sau PR #47.
Chưa tự đẩy lên GitHub hoặc merge vào main trong quá trình tạo nhánh.

## Nguồn code thành viên

- PIN: PR #46, các commit `ebd135a` và `6ba8fb9`; giữ commit tác giả, dùng bản sửa mới nhất
  về Clock/chữ ký PinCredential, chuẩn hóa customerId và kiểm tra khóa trong matches.
- Bank: PR #48, các commit `324eb95` và `266943a`; giữ tác giả, đã có phân biệt chủ STK khóa
  với chưa đăng nhập. Test thành viên được bổ sung exit lỗi để lệnh kiểm thử thất bại nếu có ca FAIL.
- Dữ liệu mẫu: PR #51, commit `e4c6021`; giữ tác giả và bộ 6 STK/3 khách hàng mới, cập nhật hướng dẫn/test.
- UI: đã ghép PR #47 được merge trên main, gồm bản sửa `c4895c8` về getter customerId,
  cập nhật hồ sơ trong setAccount và tháng MM/yyyy. Khi giải quyết xung đột, giữ nút xác nhận
  bị vô hiệu hóa sau giao dịch thành công, cảnh báo riêng khi callback lỗi và xóa cả phần
  tài khoản khi xóa khách hàng.
- Người 1: nối Main/MainWindow/BankController, welcome/login/logout, quyền nút, icon,
  script build/test, gói JAR và tài liệu demo.

Các PR chưa merge trên GitHub vẫn do nhóm tự quyết định. Bản tích hợp đã chứa các thay đổi
trên; khi ghép vào main cần tránh thay lại bằng phiên bản cũ của cùng file.

## Các chỗ gọi API đã cập nhật

- `Main`: khởi tạo Bank bằng `InMemoryAccountRepository`, hiện MainWindow trước khi mở login.
- `BankController`: gọi `LoginDialog(window, pins)`; đăng nhập bằng customerId trước khi tải STK,
  kiểm tra phiên khách hàng và không yêu cầu nhập PIN lại khi đổi STK cùng chủ.
- `MainWindow`: có màn hình chào, nút đăng nhập/đăng xuất, hiển thị hồ sơ khách hàng và xóa dữ liệu
  riêng tư khi kết thúc phiên. Cần ghép cả ba file này cùng các service API v2.

## Chạy và xác nhận

Xem [DEMO.md](DEMO.md) để lấy tài khoản/PIN mẫu và lệnh chạy.
Kết quả thực thi các bộ test trong [TESTING.md](TESTING.md).
Dữ liệu giữ trong bộ nhớ, khởi động lại reset; CSV chỉ xuất sao kê.
