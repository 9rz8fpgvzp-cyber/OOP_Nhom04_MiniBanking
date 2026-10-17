package vn.edu.ptit.oop.nhom04.ui.dialogs;

import javax.swing.*;
import java.awt.*;

public class TransactionDialog extends JDialog {
    private JTextField txtTargetAccount = new JTextField(15);
    private JTextField txtAmount = new JTextField(15);
    private JButton btnSubmit = new JButton("Xác nhận");
    private JButton btnCancel = new JButton("Hủy");

    public TransactionDialog(Frame owner, String title, boolean isTransfer) {
        super(owner, title, true);
        setLayout(new GridLayout(isTransfer ? 3 : 2, 2, 10, 10));

        if (isTransfer) {
            add(new JLabel("Tài khoản nhận:"));
            add(txtTargetAccount);
        }

        add(new JLabel("Số tiền (VND):"));
        add(txtAmount);

        add(btnSubmit);
        add(btnCancel);

        btnSubmit.addActionListener(e -> handleTransaction());
        btnCancel.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);
    }

    private void handleTransaction() {
        String amountText = txtAmount.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Thao tác gửi yêu cầu thành công!");
        clearInputs();
        dispose();
    }

    public void clearInputs() {
        txtTargetAccount.setText("");
        txtAmount.setText("");
    }
}