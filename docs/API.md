# API chung — phải chốt trong ngày 16/10/2026

Đây là phiếu bàn giao cần các tác giả điền, **chưa phải API đã triển khai**. Người 1 điều phối, các người sở hữu phương thức chốt chữ ký/constructor thật trong PR đầu trước 12:00 ngày 17/10. Không tự tạo lớp thay thế khác package. Thay đổi sau khi chốt phải báo người đang dùng.

| Chủ trì | Cần chốt với ai | Nội dung phải ghi trong PR đầu |
| --- | --- | --- |
| 2 | 3/4/5/6/7 | Constructor Customer/Account; số tài khoản, getter; BigDecimal; cơ chế kiểm tra rồi commit, quyền cập nhật số dư/lịch sử |
| 6 | 2/5/9 | Constructor/getter Transaction; loại/chiều chuyển; mã liên kết hai phía; thời gian và số dư đầu kỳ; danh sách chỉ đọc |
| 5 | 1/2/3/4/6/7/8 | BankingOperations: danh sách/chi tiết, deposit/withdraw/transfer, lịch sử; tham số, trả về và exception; kiểm tra quyền/khóa; điểm commit nguyên tử |
| 4 | 2/3/5/9 | InterestBearing; MonthlyInterestService; nguồn thời gian mô phỏng và kỳ lãi; ai đổi kỳ/reset lượt rút, tránh cộng trùng |
| 8 | 1/2/5/7 | Đăng ký PIN demo, xác thực, số lần còn lại, khóa, tài khoản đang xác thực, đăng xuất, kiểm tra quyền; constructor LoginDialog và kết quả Hủy/Thành công |
| 7 | 1/5/8 | Constructor panel/dialog, cập nhật Account, callback chọn tài khoản/giao dịch thành công; tác giả tự ghép thành phần |
| 6 | 1/5/8 | CsvExportAction nhận cửa sổ cha, tài khoản đã xác thực và nguồn lịch sử; chọn file/ghi đè; báo kết quả/hủy/lỗi |
| 9 | 1/6 | Tạo/cập nhật hai chart từ dữ liệu chỉ đọc, cấu hình JFreeChart, xóa dataset khi đổi/hủy tài khoản |

Mỗi API cần ghi đủ: tên lớp/phương thức, tham số và kiểu, trả về, exception, điều kiện trước/sau, ví dụ gọi. Constructor dùng chung cũng phải chốt. Có thể viết trong PR rồi chép chữ ký đã thống nhất về tài liệu này.

## Quy ước dữ liệu

- Tiền BigDecimal, đơn vị VND; từ chối số tiền không hợp lệ, chốt xử lý phần lẻ và làm tròn một lần cho cả nhóm.
- Một thời gian mô phỏng chung do phần cuối tháng quản lý; thống nhất kiểu thời gian với Transaction và biểu đồ. Không trộn đồng hồ thật và kỳ mô phỏng.
- Account không phụ thuộc Swing; GUI không sửa số dư. Lịch sử không cho sửa trạng thái nội bộ.
- Transfer thất bại giữ nguyên số dư/lịch sử/lượt rút cả hai phía. Người 2/3/5 phải thống nhất cơ chế này trước khi viết hoàn chỉnh.
- PinService có một instance chung trong phiên chạy, Bank kiểm tra quyền thực tế. Quy tắc PIN/CSV/lãi xem BONUS.md.
- UI dùng dữ liệu mẫu/callback giả trong bản thử riêng để không chờ. PR phải ghi rõ mock, không đưa hành vi giả thành bản nộp.

## Mốc kiểm tra chốt API

- [ ] Người 2, 4, 5, 6, 8 đã đưa chữ ký/constructor vào PR hoặc cập nhật ở đây.
- [ ] Người dùng API xác nhận gọi được, các lớp dùng chung biên dịch trên Java 17.
- [ ] Người 1 ghép khung; không có lớp cùng tên khác package hoặc phương thức giả báo thành công.
