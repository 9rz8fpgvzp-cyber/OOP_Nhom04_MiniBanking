---
name: 'BONUS B02 — Xuất sao kê CSV'
about: 'Xuất lịch sử tài khoản đang xác thực ra CSV, đúng định dạng và dữ liệu.'
title: '[BONUS B02] Xuất sao kê tài khoản ra CSV'
---

Chủ trì: người 6. Phối hợp: người 6 làm CsvExportAction/JFileChooser; người 1 chỉ nối menu/nút; người 9 đối chiếu thống kê.

Hạn: API 17/10 12:00, chức năng 18/10 18:00, ghép 19/10 21:00, bản cuối 20/10 21:00. Chọn Assignees bằng username thật và thêm Project MiniBank - oop.

Tham khảo: [Quy tắc và test BONUS](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/blob/main/docs/BONUS.md), [phân công tổng #1](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/1).

File mới: `src/vn/edu/ptit/oop/nhom04/util/CsvStatementExporter.java`.
File phối hợp: MainWindow, BankController và API lấy lịch sử có kiểm tra quyền. Dùng chung Transaction, không tạo bản model khác.

- [ ] Có nút/menu Xuất sao kê CSV cho tài khoản đang xác thực.
- [ ] Người 6 tạo `ui/CsvExportAction.java`, dùng JFileChooser, xác nhận ghi đè, xử lý hủy và lỗi ghi file.
- [ ] CSV UTF-8 có header; cột accountNumber, transactionId, type, direction, amount, timestamp, balanceAfter.
- [ ] Quote/escape đúng dấu phẩy, ngoặc kép và xuống dòng; số tiền không có dấu phân nhóm.
- [ ] Dữ liệu có cả chuyển vào/ra và lãi, khớp lịch sử tài khoản; không sửa số dư hoặc lịch sử.
- [ ] Tài khoản chưa có giao dịch xuất header và báo 0 giao dịch.
- [ ] Từ chối xuất tài khoản không được xác thực.
- [ ] Chạy BC01–BC05, ghi kết quả và chụp ảnh/file demo để bổ sung báo cáo.

Done khi đã ghép bản chung, xuất/đọc lại đúng dữ liệu và có kiểm thử đạt. CSV chỉ là sao kê, không phải lưu/phục hồi ứng dụng.

> Phân công hiện hành: [BAN_GIAO.md](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/blob/main/docs/BAN_GIAO.md). Mốc chốt 20/10/2026, thuyết trình sáng 22/10. Theo bản này nếu mô tả cũ có khác.
