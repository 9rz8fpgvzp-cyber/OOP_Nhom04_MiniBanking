# Package ui.dialogs

Package Java: `vn.edu.ptit.oop.nhom04.ui.dialogs`.

**Nhiệm vụ 7:** tạo TransactionDialog.java, AboutDialog.java và LoginDialog.java (bonus), kế thừa JDialog.

TransactionDialog hỗ trợ nạp/rút/chuyển, kiểm tra nhập liệu, gọi API nghiệp vụ và hiển thị lỗi qua JOptionPane. AboutDialog hiển thị thông tin đề tài và đủ 9 thành viên. Thống nhất constructor và cách báo thành công với người 1 trước khi ghép.

Tạo các lớp trên nhánh riêng; LoginDialog có thể dùng nhánh feat/login-dialog sau khi cập nhật main. Không tạo main riêng hoặc đặt logic tính số dư trong dialog. Chưa có mã triển khai cho các dialog.

LoginDialog dùng JPasswordField, gọi PinService của người 8, hiển thị số lần còn lại và trạng thái khóa. Hủy dialog không mở chi tiết hoặc giao dịch tài khoản. Xem docs/BONUS.md để thống nhất luồng với người 1.
