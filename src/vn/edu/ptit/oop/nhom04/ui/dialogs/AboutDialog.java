package vn.edu.ptit.oop.nhom04.ui.dialogs;

import javax.swing.*;
import java.awt.*;

public class AboutDialog extends JDialog {
    public AboutDialog(Frame owner) {
        super(owner, "Giới thiệu ứng dụng - MiniBank", true);
        setLayout(new BorderLayout(10, 10));

        JTextArea txtInfo = new JTextArea(
            "BÀI TẬP LỚN LẬP TRÌNH HƯỚNG ĐỐI TƯỢNG (JAVA SWING)\n" +
            "Ứng dụng Ngân hàng Giả lập MiniBank — Nhóm 04\n\n" +
            "Danh sách thành viên:\n" +
            "1. Phạm Đình Doanh - Leader / Integrator\n" +
            "2. Tiến Đạt - Model & Báo cáo\n" +
            "3. Võ Thành Phát - Savings / Checking / UML\n" +
            "4. Bảo An - FixedTerm / End-of-month\n" +
            "5. Nguyễn Quang Minh - Core Banking Service\n" +
            "6. Tăng Khánh Long - History & CSV Exporter\n" +
            "7. Quang Huy - UI Account, Dialogs & About\n" +
            "8. Nguyễn Thắng - PIN & Lock Account\n" +
            "9. Trọng Thắng - Chart & Slide"
        );
        txtInfo.setEditable(false);
        txtInfo.setMargin(new Insets(10, 10, 10, 10));

        add(new JScrollPane(txtInfo), BorderLayout.CENTER);
        setSize(420, 300);
        setLocationRelativeTo(owner);
    }
}