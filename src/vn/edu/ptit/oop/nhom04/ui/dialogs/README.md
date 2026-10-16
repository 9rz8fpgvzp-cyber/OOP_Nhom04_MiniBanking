# Package ui.dialogs

Package Java: `vn.edu.ptit.oop.nhom04.ui.dialogs`.

- Người 7: `TransactionDialog.java`, `AboutDialog.java`; đồng thời làm renderer/panel tài khoản ở ui và ui.panels.
- Người 8: `LoginDialog.java` cùng PinService/PinCredential; sở hữu trọn luồng PIN, không tách dialog cho người 7.

Dialog kế thừa JDialog, không có main riêng. TransactionDialog nạp/rút/chuyển qua API nghiệp vụ, không tự sửa số dư. About có thông tin 9 người. LoginDialog dùng JPasswordField, gọi PinService, xử lý Xác nhận/Hủy, số lần còn lại và khóa.

Chốt constructor/callback trong ngày 16/10; bản mẫu 17/10, chức năng 18/10, tự ghép cùng người 1 trước 21:00 19/10. Xem `docs/BAN_GIAO.md`, `docs/API.md`, `docs/BONUS.md` ở gốc repository. Đây là file hướng dẫn, các lớp chưa được triển khai.

> Phân công hiện hành: [BAN_GIAO.md](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/blob/main/docs/BAN_GIAO.md). Mốc chốt 20/10/2026, thuyết trình sáng 22/10. Theo bản này nếu mô tả cũ có khác.
