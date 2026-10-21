package vn.edu.ptit.oop.nhom04.ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

import vn.edu.ptit.oop.nhom04.model.AccountSummary;
import vn.edu.ptit.oop.nhom04.model.TransactionType;
import vn.edu.ptit.oop.nhom04.service.BankingOperations;

public class TransactionDialog extends JDialog {
    private final BankingOperations bank;
    private final TransactionType operation;
    private final String sourceNumber;
    private final Runnable afterSuccess;

    private JComboBox<String> cbTargetAccount;
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
            cbTargetAccount = new JComboBox<String>();
            populateTargetAccounts();
            add(cbTargetAccount);
        }

        add(new JLabel("Số tiền (VND):"));
        txtAmount = new JTextField();
        add(txtAmount);

        btnConfirm = new JButton("Xác nhận");
        btnCancel = new JButton("Hủy");
        add(btnConfirm);
        add(btnCancel);

        btnConfirm.addActionListener(e -> handleConfirm());
        btnCancel.addActionListener(e -> dispose());

        pack();
        setResizable(false);
    }

    private void populateTargetAccounts() {
        if (bank == null) return;
        List<AccountSummary> summaries = bank.listAccounts();
        if (summaries != null) {
            for (AccountSummary acc : summaries) {
                if (sourceNumber == null || !acc.accountNumber().equals(sourceNumber)) {
                    cbTargetAccount.addItem(acc.accountNumber());
                }
            }
        }
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

        try {
            if (operation == TransactionType.DEPOSIT) {
                bank.deposit(sourceNumber, amount);
            } else if (operation == TransactionType.WITHDRAW) {
                bank.withdraw(sourceNumber, amount);
            } else if (operation == TransactionType.TRANSFER) {
                String targetNumber = (String) cbTargetAccount.getSelectedItem();
                if (targetNumber == null || targetNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản đích!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                bank.transfer(sourceNumber, targetNumber, amount);
            } else {
                JOptionPane.showMessageDialog(this, "Loại giao dịch không hỗ trợ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Giao dịch thất bại: " + ex.getMessage(), "Lỗi giao dịch", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Giao dịch thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        dispose();

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