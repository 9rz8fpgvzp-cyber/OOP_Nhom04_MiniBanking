# Repository trong bộ nhớ

`AccountRepository` khai báo tìm theo STK, chủ sở hữu và lấy danh sách nội bộ.
`InMemoryAccountRepository` giữ LinkedHashMap, duy trì thứ tự và cùng đối tượng Account.
Danh sách trả về không sửa được. Bank kiểm tra quyền trước khi đưa dữ liệu cho GUI.
Dữ liệu chỉ nằm trong RAM; đóng app rồi mở lại sẽ tạo dữ liệu mẫu mới.
