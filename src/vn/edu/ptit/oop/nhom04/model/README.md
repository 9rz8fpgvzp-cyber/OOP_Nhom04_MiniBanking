# Package model

Package Java: `vn.edu.ptit.oop.nhom04.model`.

| Nhiệm vụ | File cần tạo |
| --- | --- |
| 2 | Customer.java, Account.java (abstract) |
| 3 | SavingsAccount.java, CheckingAccount.java |
| 4 | FixedTermAccount.java, InterestBearing.java (interface) |
| 6 | Transaction.java, TransactionType.java (enum) |
| 8 — bonus | PinCredential.java (PIN băm + salt, không giữ PIN dạng rõ) |

Mỗi người tạo đúng file phần mình bằng New → Class/Interface/Enum trong Eclipse, trên nhánh riêng. Các lớp con dùng chung Account; lịch sử dùng chung Transaction. Chốt chữ ký phương thức và constructor trong Issue trước khi viết phần phụ thuộc.

Quy tắc nghiệp vụ không phụ thuộc Swing. Các file trong bảng là công việc cần thực hiện, chưa có mã triển khai.

Chi tiết BONUS: xem mục Công việc BONUS và docs/BONUS.md ở README gốc. Công việc mới đang chờ triển khai.
