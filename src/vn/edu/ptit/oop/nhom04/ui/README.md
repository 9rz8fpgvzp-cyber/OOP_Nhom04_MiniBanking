# Package ui

Package Java: `vn.edu.ptit.oop.nhom04.ui`.

| Nhiệm vụ | File phụ trách |
| --- | --- |
| 1 | MainWindow.java (đã có bản khung), BankController.java (tạo mới) |
| 6 | TransactionTableModel.java (tạo mới) |
| 8 | AccountListRenderer.java (tạo mới) |

Dialog nằm trong package ui.dialogs, panel chi tiết trong ui.panels; xem README của hai thư mục con. Người 1 sửa bố cục MainWindow và kết nối các phần; các thành viên còn lại bàn giao thành phần qua API thống nhất.

GUI gọi Bank/BankingOperations, không tự tính hoặc sửa số dư. Khi giao dịch thành công, người 1 điều phối cập nhật danh sách, chi tiết, bảng lịch sử, biểu đồ và status bar.
