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
        try {
            String amountStr = txtAmount.getText().trim();
            if (amountStr.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập số tiền!");
            }

            BigDecimal amount = new BigDecimal(amountStr);

            if (bank != null) {
                if (operation == TransactionType.DEPOSIT) {
                    bank.deposit(sourceNumber, amount);
                } else if (operation == TransactionType.WITHDRAW) {
                    bank.withdraw(sourceNumber, amount);
                } else if (operation == TransactionType.TRANSFER) {
                    String targetNumber = (String) cbTargetAccount.getSelectedItem();
                    if (targetNumber == null || targetNumber.isEmpty()) {
                        throw new IllegalArgumentException("Vui lòng chọn tài khoản đích!");
                    }
                    bank.transfer(sourceNumber, targetNumber, amount);
                } else {
                    throw new IllegalStateException("Loại giao dịch không hỗ trợ!");
                }
            }

            JOptionPane.showMessageDialog(this, "Giao dịch thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
            if (afterSuccess != null) {
                afterSuccess.run();
            }
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi giao dịch", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showDialog() {
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}