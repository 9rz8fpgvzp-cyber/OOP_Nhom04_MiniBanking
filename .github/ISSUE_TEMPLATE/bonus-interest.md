---
name: 'BONUS B03 — InterestBearing'
about: 'Hoàn thiện interface hưởng lãi, sử dụng đa hình và kiểm tra kỳ lãi.'
title: '[BONUS B03] Hoàn thiện InterestBearing và kiểm thử cộng lãi'
---

Chủ trì: người 4. Phối hợp: người 3 áp dụng vào SavingsAccount; người 9 kiểm tra dữ liệu biểu đồ; người 5 ghi giao dịch lãi.

Hạn: ngày 3 nối model, ngày 4 ghép, ngày 5 kiểm thử. Chọn Assignees bằng username thật và thêm Project MiniBank - oop.

Tham khảo: [Quy tắc và test BONUS](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/blob/main/docs/BONUS.md), [phân công tổng #1](https://github.com/9rz8fpgvzp-cyber/OOP_Nhom04_MiniBanking/issues/1).

File: model/InterestBearing.java, model/SavingsAccount.java, model/FixedTermAccount.java và service/MonthlyInterestService.java dưới src/vn/edu/ptit/oop/nhom04/.

Phần này đã nằm trong nhiệm vụ lãi hiện tại: hoàn thiện và ghi nhận chung, không viết interface hay công thức lãi thứ hai.

- [ ] Chốt chữ ký InterestBearing, SavingsAccount cài đặt và các loại hưởng lãi theo thiết kế nhóm.
- [ ] Gọi tính lãi qua tham chiếu interface; không tính lại trong GUI hoặc switch theo tên loại.
- [ ] BigDecimal, công thức mô phỏng lãi suất năm / 12 và làm tròn nhất quán.
- [ ] Không cộng trùng một tài khoản/cùng kỳ; CheckingAccount không nhận lãi.
- [ ] Bank ghi nhận giao dịch lãi, bảng và biểu đồ cập nhật đúng.
- [ ] Chạy BI01–BI03 và các ca tháng/lãi bắt buộc; ghi kết quả thực tế.
- [ ] UML có interface và lớp cài đặt; mỗi người giải thích được nơi gọi qua interface khi bảo vệ.

Done khi đã dùng interface thực tế, ghép bản chung và kiểm thử đạt. Cộng lãi cuối tháng vẫn là chức năng bắt buộc của bước 7.
