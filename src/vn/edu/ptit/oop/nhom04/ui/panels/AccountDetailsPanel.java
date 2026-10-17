package vn.edu.ptit.oop.nhom04.ui.panels;

import javax.swing.*;
import java.awt.*;

public class AccountDetailsPanel extends JPanel {
    private JLabel lblOwner = new JLabel("Chủ sở hữu: --");
    private JLabel lblAccNum = new JLabel("Số tài khoản: --");
    private JLabel lblBalance = new JLabel("Số dư: 0 VND");
    private JLabel lblSpecificInfo = new JLabel("Thông tin đặc thù: --");

    public AccountDetailsPanel() {
        setLayout(new GridLayout(4, 1, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Chi tiết tài khoản"));
        add(lblOwner);
        add(lblAccNum);
        add(lblBalance);
        add(lblSpecificInfo);
    }

    public void updateData(String owner, String accNum, String balance, String specificInfo) {
        lblOwner.setText("Chủ sở hữu: " + owner);
        lblAccNum.setText("Số tài khoản: " + accNum);
        lblBalance.setText("Số dư: " + balance + " VND");
        lblSpecificInfo.setText(specificInfo);
    }
}