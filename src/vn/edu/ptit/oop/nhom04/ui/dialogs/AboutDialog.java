package vn.edu.ptit.oop.nhom04.ui.dialogs;

import javax.swing.*;
import java.awt.*;

public class AboutDialog extends JDialog {

    public AboutDialog(Window owner) {
        super(owner, "Giới thiệu ứng dụng — MiniBank", ModalityType.APPLICATION_MODAL);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JTextArea txtInfo = new JTextArea(
            "BÀI TẬP LỚN LẬP TRÌNH HƯỚNG ĐỐI TƯỢNG (JAVA SWING)\n" +
            "Ứng dụng Ngân hàng Giả lập MiniBank — Nhóm 04\n\n" +
            "Thành viên thực hiện:\n" +
            "1. Phạm Đình Doanh — Leader / Integrator\n" +
            "2. Tiến Đạt — Model nền & Dữ liệu mẫu\n" +
            "3. Võ Thành Phát — Quy tắc Tiết kiệm, Thanh toán & UML\n" +
            "4. Bảo An — Tài khoản Kỳ hạn & Xử lý cuối tháng\n" +
            "5. Nguyễn Quang Minh — Nghiệp vụ Ngân hàng & Chuyển khoản\n" +
            "6. Tăng Khánh Long — Lịch sử & Xuất CSV\n" +
            "7. Quang Huy — UI Danh sách, Chi tiết & Dialog Giao dịch\n" +
            "8. Nguyễn Thắng — PIN & Khóa tài khoản\n" +
            "9. Trọng Thắng — Biểu đồ, Thống kê & Slide"
        );
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        txtInfo.setMargin(new Insets(10, 10, 10, 10));

        add(new JScrollPane(txtInfo), BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        JPanel panelSouth = new JPanel();
        panelSouth.add(btnClose);
        add(panelSouth, BorderLayout.SOUTH);

        setSize(450, 320);
        setResizable(false);
    }

    public void showDialog() {
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}