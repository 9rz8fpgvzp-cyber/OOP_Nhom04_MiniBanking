# Bản tích hợp khách hàng v2

Nhánh: `feat/customer-login-integration`, bắt đầu từ main sau các PR #49, #45, #44.
Chưa tự đẩy lên GitHub hoặc merge vào main trong quá trình tạo nhánh.

## Nguồn code thành viên

- PIN: PR #46, commit `ebd135a`; giữ commit tác giả, bổ sung Clock/chữ ký PinCredential,
  chuẩn hóa customerId và kiểm tra khóa trong matches theo API đã chốt.
- Bank: PR #48, các commit `324eb95` và `266943a`; giữ tác giả, đã có phân biệt chủ STK khóa
  với chưa đăng nhập. Test thành viên được bổ sung exit lỗi để lệnh kiểm thử thất bại nếu có ca FAIL.
- Dữ liệu mẫu: PR #51, commit `e4c6021`; giữ tác giả và bộ 6 STK/3 khách hàng mới, cập nhật hướng dẫn/test.
- UI: lấy AccountListRenderer, AccountDetailsPanel, TransactionDialog ở PR #47 (`14885d0`).
  Không lấy MainWindow chứa dấu xung đột. Sửa getter customerId, cập nhật hồ sơ trong setAccount,
  tháng MM/yyyy và thông báo riêng nếu callback sau thành công gặp lỗi.
- Người 1: nối Main/MainWindow/BankController, welcome/login/logout, quyền nút, icon,
  script build/test, gói JAR và tài liệu demo.

Các PR chưa merge trên GitHub vẫn do nhóm tự quyết định. Bản tích hợp đã chứa các thay đổi
trên; khi ghép vào main cần tránh thay lại bằng phiên bản cũ của cùng file.

## Chạy và xác nhận

Xem [DEMO.md](DEMO.md) để lấy tài khoản/PIN mẫu và lệnh chạy.
Kết quả thực thi các bộ test trong [TESTING.md](TESTING.md).
Dữ liệu giữ trong bộ nhớ, khởi động lại reset; CSV chỉ xuất sao kê.
