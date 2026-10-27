# Luồng ứng dụng API v2

```mermaid
flowchart TD
    A[Mở ứng dụng] --> B[Mã khách hàng và PIN]
    B -->|Hủy hoặc đóng| W[Màn hình chào trống]
    W -->|Đăng nhập| B
    B -->|Sai PIN| C[Giảm lượt thử của khách hàng]
    C -->|Sai lần thứ ba| L[Khóa 30 giây theo đồng hồ thật]
    L -->|Đổi khách hàng| B
    L -->|Hết hạn, chưa tự đăng nhập| B
    C -->|Còn lượt| B
    B -->|PIN đúng và không khóa| D[Hồ sơ và STK của chính khách hàng]
    D --> E[Chọn STK không nhập lại PIN]
    E --> F[Bank kiểm tra phiên và chủ sở hữu]
    F --> G[Nạp, rút, chuyển, lịch sử, CSV và biểu đồ]
    G --> E
    D --> H[Cuối tháng: cộng lãi toàn hệ thống]
    H --> D
    D -->|Đăng xuất| B
```

Đăng xuất xóa cả danh sách STK, hồ sơ, lựa chọn, lịch sử và biểu đồ trước khi mở login.
Bank là cổng kiểm tra quyền, không chỉ dựa vào trạng thái nút giao diện. Chuyển khoản chỉ
xác thực chủ nguồn; người nhận có thể khác chủ và đang khóa đăng nhập. Đồng hồ mô phỏng
không ảnh hưởng hạn khóa PIN. Đóng app xóa dữ liệu trong RAM; mở lại nạp mẫu mới.

Chữ ký API: [API.md](API.md). Cách thao tác và PIN giả: [DEMO.md](DEMO.md).
