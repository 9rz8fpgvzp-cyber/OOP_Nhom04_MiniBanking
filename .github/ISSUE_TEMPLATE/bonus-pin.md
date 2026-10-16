---
name: 'BONUS B01 — PIN và khóa tài khoản'
about: 'Đăng nhập PIN theo tài khoản, khóa sau 3 lần sai liên tiếp và tích hợp GUI/nghiệp vụ.'
title: '[BONUS B01] PIN và khóa tài khoản sau 3 lần sai'
---

Chủ trì: người 8 làm cả PinService và LoginDialog. Phối hợp: người 7 làm panel tài khoản; người 1 nối UI; người 5 kiểm tra nghiệp vụ; người 2 chuẩn bị dữ liệu mẫu.

Hạn: API 17/10 12:00, chức năng 18/10 18:00, ghép 19/10 21:00, chốt bản cuối 20/10 21:00. Chọn Assignees bằng username thật và thêm Project MiniBank - oop.

Tham khảo: [Quy tắc và test BONUS](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/blob/main/docs/BONUS.md), [phân công tổng #1](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/1).

File cần tạo:
- `src/vn/edu/ptit/oop/nhom04/model/PinCredential.java` — người 8.
- `src/vn/edu/ptit/oop/nhom04/service/PinService.java` — người 8.
- `src/vn/edu/ptit/oop/nhom04/exception/AccountLockedException.java` — người 8.
- `src/vn/edu/ptit/oop/nhom04/ui/dialogs/LoginDialog.java` — người 8.
- Phối hợp sửa Bank, BankController, MainWindow và DemoDataFactory; không tự sửa file người khác mà chưa thống nhất.

- [ ] Chốt API xác thực, PIN demo 6 chữ số và phạm vi khóa theo docs/BONUS.md.
- [ ] PIN lưu dạng băm kèm salt, nhập bằng JPasswordField, không log PIN.
- [ ] Sai 3 lần liên tiếp khóa đúng tài khoản; đúng trước khi khóa reset số lần sai.
- [ ] Đóng/mở dialog và đổi tài khoản không xóa bộ đếm/khóa.
- [ ] Đổi tài khoản/đăng xuất xóa quyền cũ; hủy dialog không cho xem/thao tác tài khoản.
- [ ] Kiểm tra quyền và khóa trong nghiệp vụ, không chỉ vô hiệu hóa nút GUI.
- [ ] Khóa chủ động vẫn cho nhận chuyển khoản vào và cộng lãi theo quy tắc mô phỏng.
- [ ] Chạy và ghi kết quả BP01–BP09, kiểm tra không làm hỏng các giao dịch bắt buộc.
- [ ] Cập nhật UML, báo cáo và slide; ghi rõ trạng thái trong bộ nhớ chỉ giữ trong phiên chạy.

Done khi đã ghép vào bản chung, chạy đúng và có kết quả kiểm thử thực tế. Đây là đầu việc cần triển khai, chưa phải chức năng đã hoàn thành.

> Phân công hiện hành: [BAN_GIAO.md](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/blob/main/docs/BAN_GIAO.md). Mốc chốt 20/10/2026, thuyết trình sáng 22/10. Theo bản này nếu mô tả cũ có khác.
