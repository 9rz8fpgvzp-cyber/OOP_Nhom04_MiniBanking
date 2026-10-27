# Các chức năng bổ sung theo API v2

Tài liệu nghiệm thu đang dùng: [API.md](API.md), [DEMO.md](DEMO.md), [TESTING.md](TESTING.md).

## PIN và khóa

- User là customerId; một PIN cho nhiều STK cùng chủ. Chỉ có một phiên khách hàng.
- PIN gồm sáu chữ số ASCII, giữ số 0 đầu, lưu PBKDF2 + salt ngẫu nhiên.
- Sai ba lần khóa đăng nhập 30 giây thật; đúng PIN trong lúc khóa vẫn bị từ chối.
- Đếm ngược bằng Swing Timer; đổi user/đóng dialog/logout không xóa khóa.
- Hết hạn có lại ba lượt, phải xác thực lại. Cuối tháng mô phỏng không làm hết khóa.
- Phiên, dữ liệu và khóa chỉ trong RAM; khởi động app mới nạp lại dữ liệu mẫu.

## Sao kê CSV

- Chọn STK của khách hàng đã đăng nhập, Bank kiểm tra quyền trước chọn file và trước ghi.
- Hủy không ghi, file có sẵn được hỏi ghi đè. Header UTF-8/CRLF giữ theo API.
- Exporter kiểm tra các bản ghi đều thuộc STK yêu cầu. Không có nhập/khôi phục CSV vào app.

## InterestBearing và chốt tháng

- Savings/FixedTerm triển khai InterestBearing, lãi tính bằng BigDecimal và làm tròn về đồng.
- Hệ thống cộng lãi mọi tài khoản hưởng lãi, kể cả chủ đang khóa; GUI chỉ cho chốt khi có phiên.
- Account giữ kỳ đã xét, thử lại sau lỗi một phần không cộng lãi trùng. Đồng hồ chỉ sang tháng mới
  khi đợt hoàn tất. GUI làm mới cả phần lãi đã thành công trước lỗi.
- Transaction giữ INTEREST:yyyy-MM nội bộ; bảng hiện “Cộng lãi tháng MM/yyyy”.
