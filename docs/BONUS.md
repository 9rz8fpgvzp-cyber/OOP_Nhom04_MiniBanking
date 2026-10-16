# Công việc BONUS — MiniBank nhóm 04

Cập nhật phạm vi theo quyết định của nhóm ngày 16/10/2026: thực hiện BONUS cùng các chức năng bắt buộc, vẫn chạy một máy và hoàn thiện trong một tuần. Tài liệu này mô tả **công việc cần triển khai**, không xác nhận đã có chức năng hay đã chạy các ca kiểm thử.

Thẻ đề nêu: LoginDialog nhập PIN, sai 3 lần khóa tài khoản; interface InterestBearing; xuất sao kê CSV. Công thức lãi, định dạng PIN, phạm vi khóa và cách CSV trình bày dưới đây là quy tắc mô phỏng nhóm chọn để làm thống nhất, không phải quy định ngân hàng ngoài đời.

## 1. Phân công và file

Các đường dẫn tính từ `src/vn/edu/ptit/oop/nhom04/`.

| Phần | Chủ trì | Phối hợp | File cần tạo / sửa |
| --- | --- | --- | --- |
| B01 — trạng thái PIN, kiểm tra PIN và khóa | Người 8 | Người 2 tạo dữ liệu mẫu, người 5 kiểm tra trong nghiệp vụ | Tạo `model/PinCredential.java`, `service/PinService.java`, `exception/AccountLockedException.java` |
| B01 — giao diện PIN | Người 7 | Người 1 nối vào luồng chọn tài khoản | Tạo `ui/dialogs/LoginDialog.java`; sửa `ui/BankController.java`, `ui/MainWindow.java` |
| B02 — sao kê CSV | Người 6 | Người 1 làm nút/menu, người 9 đối chiếu số liệu | Tạo `util/CsvStatementExporter.java`; dùng dữ liệu `Transaction`; không tạo model giao dịch thứ hai |
| B03 — InterestBearing | Người 4 | Người 3 áp dụng vào SavingsAccount; người 9 kiểm tra biểu đồ | Dùng `model/InterestBearing.java`, `model/SavingsAccount.java`, `model/FixedTermAccount.java`, `service/MonthlyInterestService.java` đã có trong phân công |

Nhánh đề xuất: `feat/pin-auth` (người 8), `feat/login-dialog` (người 7), `feat/csv-statement` (người 6). Người 1 tích hợp trên nhánh của mình sau khi nhận API. B03 tiếp tục phần lãi đã giao cho người 4, không tạo thêm bản InterestBearing ở package khác.

Người 8 có thêm PIN nên người 2 hỗ trợ tạo dữ liệu và test trạng thái; người 6 tập hợp test bonus, người 7 bổ sung slide, người 9 hỗ trợ ảnh/video. Người tổng hợp báo cáo nhận mô tả và kết quả kiểm tra từ tác giả từng phần.

## 2. B01 — PIN và khóa tài khoản

### Quy tắc thống nhất

1. Mỗi tài khoản có PIN riêng. Dùng PIN demo **6 chữ số**, giữ dạng chuỗi/char[] để không mất số 0 đầu. Không dùng PIN thật của bất kỳ ai.
2. DemoDataFactory tạo PIN demo và đăng ký vào một PinService dùng chung trong phiên chạy. Lưu băm PIN kèm salt riêng, không giữ PIN dạng rõ trong model hoặc log.
3. PinCredential giữ thông tin xác thực; PinService quản lý kiểm tra PIN, số lần sai liên tiếp và trạng thái khóa theo số tài khoản. GUI không được tự đặt lại bộ đếm hoặc mở khóa.
4. Chọn tài khoản thì mở LoginDialog. Chỉ cho xem chi tiết, lịch sử, biểu đồ, xuất CSV hoặc thao tác chủ động trên tài khoản đó sau khi xác thực thành công.
5. Một lần bấm xác nhận PIN sai của tài khoản tồn tại được tính một lần sai; lần thứ ba khóa tài khoản. Hủy dialog không tính thêm lần sai. Đăng nhập đúng trước khi bị khóa reset bộ đếm sai về 0.
6. Tài khoản đã khóa vẫn bị từ chối khi nhập PIN đúng. Đóng/mở dialog, chọn lại tài khoản hoặc đăng xuất không làm hết khóa.
7. Đổi sang tài khoản khác hoặc đăng xuất phải xóa trạng thái xác thực cũ. Không dùng PIN của tài khoản A để thao tác tài khoản B.
8. Bank/PinService kiểm tra quyền và khóa ở tầng nghiệp vụ cho nạp, rút, chuyển tiền ra và lấy sao kê/chi tiết; không chỉ dựa vào việc GUI ẩn nút. Tài khoản nhận không cần nhập PIN.
9. Quy tắc mô phỏng chọn cho khóa: khóa truy cập và các thao tác chủ động của chủ tài khoản; tài khoản khóa vẫn có thể nhận chuyển khoản vào và được hệ thống cộng lãi. Người 5 kiểm tra quy tắc này nhất quán, không trừ tiền nguồn trước khi kiểm tra đủ các điều kiện giao dịch.
10. Toàn bộ dữ liệu đang ở bộ nhớ. Khóa và số lần sai giữ trong **phiên chạy ứng dụng**, khởi động lại sẽ nạp lại dữ liệu demo. Đây là hạn chế công khai trong báo cáo; không bổ sung chức năng lưu/phục hồi chỉ để giữ trạng thái PIN.

### Giao diện và dữ liệu bàn giao

- LoginDialog dùng JPasswordField; có Xác nhận/Hủy, thông báo còn bao nhiêu lần và thông báo khóa rõ ràng. Không hiển thị PIN trong danh sách hoặc chi tiết tài khoản.
- Người 8 và người 5 chốt API đăng ký PIN, xác thực, kiểm tra tài khoản đang xác thực và kiểm tra khóa trước khi người 7/1 nối UI.
- Người 2 chuẩn bị ít nhất hai tài khoản PIN khác nhau và một PIN demo có số 0 đầu để kiểm tra.
- Người 1 đảm bảo hủy dialog không để lộ lại dữ liệu tài khoản trước đó, đổi tài khoản không giữ quyền của tài khoản cũ.

## 3. B02 — xuất sao kê CSV

- Người 6 viết CsvStatementExporter nhận số tài khoản và bản chụp lịch sử chỉ đọc; không sửa số dư hoặc Transaction.
- Người 1 thêm menu/nút **Xuất sao kê CSV**, dùng JFileChooser chọn nơi lưu. Xuất tài khoản đang được xác thực, không xuất tài khoản khác bằng cách đổi tham số.
- Xuất toàn bộ lịch sử hiện có của tài khoản được chọn. Lọc theo ngày là mở rộng sau, chưa bắt buộc cho bonus này.
- Cột thống nhất: `accountNumber,transactionId,type,direction,amount,timestamp,balanceAfter`. `direction` phân biệt IN/OUT cho TRANSFER; các giao dịch khác có thể để trống vì đã có type.
- UTF-8, có hàng tiêu đề, dùng dấu phẩy; giá trị có dấu phẩy/ngoặc kép/xuống dòng phải được quote/escape đúng. Số tiền dùng dạng số thập phân không phân nhóm, thời điểm dùng ISO-8601 thống nhất với lịch sử.
- Nếu ghi ra file đã tồn tại phải hỏi xác nhận trước khi ghi đè. Hủy chọn file không tạo file; lỗi ghi file được thông báo thân thiện.
- Tài khoản chưa có giao dịch xuất file chỉ có tiêu đề. Thông báo số dòng giao dịch đã xuất, không tính hàng tiêu đề.
- Đối chiếu cả bản ghi chuyển vào/ra và khoản lãi. File CSV không phải cơ chế khôi phục dữ liệu ứng dụng.

Tham khảo định dạng CSV: [RFC 4180](https://www.rfc-editor.org/rfc/rfc4180).

## 4. B03 — InterestBearing

- Giữ một interface `model.InterestBearing`, có phương thức tính lãi theo quy tắc đã thống nhất; người 4 chốt chữ ký phương thức với người 3.
- SavingsAccount cài đặt interface. FixedTermAccount cũng có thể cài đặt theo thiết kế hiện tại của nhóm; CheckingAccount không hưởng lãi trong bản mô phỏng này.
- MonthlyInterestService gọi hành vi qua tham chiếu InterestBearing; không viết lại công thức riêng trong GUI và biểu đồ, không dùng switch tên loại để tính lãi.
- Công thức mô phỏng: số dư chốt kỳ × lãi suất năm / 12, dùng BigDecimal và quy tắc làm tròn về đồng đã thống nhất. Bank ghi nhận khoản lãi thành giao dịch DEPOSIT với nội dung kỳ lãi nếu model có trường mô tả.
- Không cộng lại cho cùng một tài khoản/cùng kỳ. Tháng mô phỏng dùng chung cho cộng lãi, giới hạn rút và timestamp.
- Cập nhật bảng lịch sử và hai biểu đồ sau khi cộng lãi. Nút mô phỏng cuối tháng vốn thuộc phần bắt buộc; việc dùng interface là phần thiết kế có thể trình bày cho bonus.

## 5. Mốc bàn giao trong tuần

| Mốc | Kết quả |
| --- | --- |
| Chốt API ngay khi nhận việc | PIN theo tài khoản, phạm vi khóa, dữ liệu Transaction/chiều chuyển, cột CSV và API lãi được thống nhất |
| Ngày 3 | PinService và exporter chạy được với dữ liệu mẫu; InterestBearing nối được với model |
| Ngày 4 | Ghép LoginDialog, quyền thao tác, nút CSV và cộng lãi vào bản chung |
| Ngày 5 | Kiểm thử bắt buộc + bonus, sửa lỗi, thử JAR và CSV từ bản đóng gói |
| Ngày 6 | Cập nhật UML, báo cáo, slide, bảng hoàn thành bonus và tập demo |
| Ngày 7 | Tập phản biện, rà bản nộp và sửa lỗi chặn demo nếu có |

Hạn cụ thể theo mốc tuần nhóm đã chốt, không tính một tuần mới riêng cho bonus. Không mở thêm tính năng ngoài B01–B03 sau mốc ghép ngày 4. Không đánh dấu Done nếu làm bonus nhưng gây hỏng bước 1–7.

## 6. Các ca kiểm thử bổ sung

Các ca dưới đây là **kế hoạch kiểm thử, chưa có kết quả thực tế**. Người thực hiện ghi dữ liệu, kết quả, người kiểm tra và Đạt/Không đạt vào báo cáo.

| Mã | Tình huống | Kết quả mong đợi |
| --- | --- | --- |
| BP01 | Nhập PIN đúng, gồm PIN có số 0 đầu | Vào đúng tài khoản; dữ liệu không thuộc tài khoản khác |
| BP02 | Sai 1 lần, sai 2 lần | Báo còn 2 rồi 1 lần; chưa khóa |
| BP03 | Sai lần thứ ba, sau đó nhập đúng | Tài khoản khóa; PIN đúng cũng không mở được |
| BP04 | Sai 2 lần → đúng → sai 1 lần | Đúng reset bộ đếm; lần sai sau tính là lần 1 |
| BP05 | Đóng dialog, mở lại hoặc đổi tài khoản rồi quay lại | Không reset số lần sai/khóa của tài khoản cũ |
| BP06 | Đăng nhập A rồi chọn B hoặc gọi nghiệp vụ cho B | Không được dùng quyền của A cho B; B cần xác thực |
| BP07 | Hủy LoginDialog | Không mở chi tiết/lịch sử/biểu đồ/CSV; không tính thêm lần sai |
| BP08 | Gọi trực tiếp nạp/rút/chuyển ra khi chưa xác thực hoặc bị khóa | Nghiệp vụ từ chối; số dư, lịch sử, bộ đếm rút không đổi |
| BP09 | Chuyển vào tài khoản khóa hoặc cộng lãi cho nó | Theo quy tắc mô phỏng đã chọn: vẫn nhận tiền/lãi; không tự mở khóa |
| BC01 | Xuất tài khoản có nạp/rút/chuyển/lãi | Đủ hàng, đúng cột và số dư sau từng giao dịch |
| BC02 | Xuất tài khoản chưa có giao dịch | File chỉ có hàng tiêu đề, thông báo 0 giao dịch |
| BC03 | Dữ liệu có dấu phẩy, ngoặc kép hoặc xuống dòng | Không vỡ cột, mở/đọc lại giữ đúng dữ liệu |
| BC04 | Hủy lưu, lỗi quyền ghi hoặc từ chối ghi đè | Không ghi ngoài ý muốn, có thông báo khi lỗi, dữ liệu ứng dụng không đổi |
| BC05 | Xuất tài khoản khác với tài khoản đang xác thực | Từ chối, không tạo sao kê trái quyền |
| BI01 | Cộng lãi qua interface cho nhiều loại hưởng lãi | Đúng công thức; CheckingAccount không nhận lãi |
| BI02 | Xử lý lại cùng kỳ và xử lý kỳ kế tiếp | Cùng kỳ không cộng trùng; kỳ tiếp theo tính đúng |
| BI03 | Cộng lãi thành công | Lịch sử và biểu đồ khớp số dư mới |

## 7. Tạo Issue và liên kết công việc

Ba mẫu trong `.github/ISSUE_TEMPLATE/` đã được soạn để mở biểu mẫu điền sẵn. **Mẫu không tự tạo Issue hoặc tự gán thành viên**.

1. Mở liên kết B01/B02/B03 trong README gốc hoặc Issues → New issue, chọn mẫu tương ứng.
2. Chọn Assignees là username thật của người chủ trì; thêm Project **MiniBank - oop**, để Todo.
3. Bấm Create, rồi liên kết vào Issue #1 bằng Add existing sub-issue nếu muốn theo dõi chung.
4. Gắn link các Issue nhiệm vụ cũ có liên quan; cập nhật In progress/In review/Done theo quy trình chung.

Không tạo một Issue mới nếu nhóm đã có Issue cùng nội dung; có thể bổ sung checklist từ mẫu vào Issue hiện có. Trong báo cáo chỉ ghi hoàn thành bonus khi chức năng đã chạy và có kiểm thử đạt; điểm bonus tối đa theo thang của thầy, không tự cộng điểm dự kiến.
