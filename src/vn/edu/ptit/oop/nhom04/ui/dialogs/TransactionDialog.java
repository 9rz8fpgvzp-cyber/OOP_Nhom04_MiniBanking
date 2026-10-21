package vn.edu.ptit.oop.nhom04.ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

import vn.edu.ptit.oop.nhom04.model.TransactionType;
import vn.edu.ptit.oop.nhom04.service.BankingOperations;

public class TransactionDialog extends JDialog {
    private final BankingOperations bank;
    private final TransactionType operation;
    private final String sourceNumber;
    private final Runnable afterSuccess;

    private JTextField txtTargetAccount;
    private JTextField txtAmount;
    private JButton btnConfirm;
    private JButton btnCancel;

    public TransactionDialog(Window owner, BankingOperations bank, TransactionType operation, String sourceNumber, Runnable afterSuccess) {
        super(owner, getTitleByOperation(operation), ModalityType.APPLICATION_MODAL);
        this.bank = bank;
        this.operation = operation;
        this.sourceNumber = sourceNumber;
        this.afterSuccess = afterSuccess;

        initUI();
    }

    private static String getTitleByOperation(TransactionType type) {
        if (type == TransactionType.DEPOSIT) return "Nạp tiền";
        if (type == TransactionType.WITHDRAW) return "Rút tiền";
        if (type == TransactionType.TRANSFER) return "Chuyển khoản";
        return "Giao dịch";
    }

    private void initUI() {
        boolean isTransfer = (operation == TransactionType.TRANSFER);
        setLayout(new GridLayout(isTransfer ? 4 : 3, 2, 10, 10));

        add(new JLabel("Tài khoản nguồn:"));
        JTextField txtSource = new JTextField(sourceNumber != null ? sourceNumber : "");
        txtSource.setEditable(false);
        add(txtSource);

        if (isTransfer) {
            add(new JLabel("Tài khoản đích:"));
            txtTargetAccount = new JTextField();
            add(txtTargetAccount);
        }

        add(new JLabel("Số tiền (VND):"));
        txtAmount = new JTextField();
        add(txtAmount);

        btnConfirm = new JButton("Xác nhận");
        btnCancel = new JButton("Hủy");
        add(btnConfirm);
        add(btnCancel);

        btnConfirm.addActionListener(e -> handleConfirm());
        btnCancel.addActionListener(e -> dispose()); // Hủy chỉ đóng Dialog, không gọi nghiệp vụ/callback

        pack();
        setResizable(false);
    }

    private void handleConfirm() {
        if (bank == null) {
            JOptionPane.showMessageDialog(this, "Hệ thống ngân hàng chưa sẵn sàng!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String amountStr = txtAmount.getText().trim();
        if (amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String targetNumber = null;
        if (operation == TransactionType.TRANSFER) {
            if (txtTargetAccount == null) {
                JOptionPane.showMessageDialog(this, "Lỗi ô nhập tài khoản đích!", "Lỗi UI", JOptionPane.ERROR_MESSAGE);
                return;
            }
            targetNumber = txtTargetAccount.getText().trim();
            if (targetNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tài khoản đích!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Bước 1: Thực hiện giao dịch ngân hàng
        try {
            if (operation == TransactionType.DEPOSIT) {
                bank.deposit(sourceNumber, amount);
            } else if (operation == TransactionType.WITHDRAW) {
                bank.withdraw(sourceNumber, amount);
            } else if (operation == TransactionType.TRANSFER) {
                bank.transfer(sourceNumber, targetNumber, amount);
            } else {
                JOptionPane.showMessageDialog(this, "Loại giao dịch không hỗ trợ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Giao dịch thất bại: " + ex.getMessage(), "Lỗi giao dịch", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Bước 2: Thông báo thành công và đóng Dialog trước
        JOptionPane.showMessageDialog(this, "Giao dịch thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        dispose();

        // Bước 3: Cập nhật UI (Nằm ngoài try-catch nghiệp vụ)
        if (afterSuccess != null) {
            try {
                afterSuccess.run();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void showDialog() {
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}