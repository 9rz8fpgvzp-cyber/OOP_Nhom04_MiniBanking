# Package ui

Package Java: `vn.edu.ptit.oop.nhom04.ui`.

| Nhiệm vụ | File phụ trách |
| --- | --- |
| 1 | MainWindow.java (đã có bản khung), BankController.java (tạo mới) |
| 6 | TransactionTableModel.java (tạo mới) |
| 7 | AccountListRenderer.java (tạo mới) |
| 6 | CsvExportAction.java (chọn file/ghi CSV) |

Dialog nằm trong package ui.dialogs, panel chi tiết trong ui.panels; xem README của hai thư mục con. Người 1 sửa bố cục MainWindow và kết nối các phần; các thành viên còn lại bàn giao thành phần qua API thống nhất.

GUI gọi Bank/BankingOperations, không tự tính hoặc sửa số dư. Khi giao dịch thành công, người 1 điều phối cập nhật danh sách, chi tiết, bảng lịch sử, biểu đồ và status bar.

BONUS — người 1 tích hợp LoginDialog/PinService khi chọn tài khoản, xóa trạng thái xác thực khi đổi tài khoản, nối menu/nút xuất CSV vào CsvExportAction do người 6 bàn giao. Nghiệp vụ vẫn kiểm tra xác thực và khóa ở Bank/PinService, không chỉ ẩn hoặc vô hiệu hóa nút trên GUI. Xem docs/BONUS.md.

> Phân công hiện hành: [BAN_GIAO.md](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/blob/main/docs/BAN_GIAO.md). Mốc chốt 20/10/2026, thuyết trình sáng 22/10. Theo bản này nếu mô tả cũ có khác.
