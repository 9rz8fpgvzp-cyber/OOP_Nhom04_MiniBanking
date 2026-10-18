package vn.edu.ptit.oop.nhom04.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.util.Locale;

import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.CheckingAccount;
import vn.edu.ptit.oop.nhom04.model.FixedTermAccount;
import vn.edu.ptit.oop.nhom04.model.SavingsAccount;

public class AccountDetailsPanel extends JPanel {
    private JLabel lblOwner = new JLabel("Chủ sở hữu: --");
    private JLabel lblAccNum = new JLabel("Số TK: --");
    private JLabel lblType = new JLabel("Loại TK: --");
    private JLabel lblBalance = new JLabel("Số dư: 0 VND");
    private JLabel lblExtra1 = new JLabel("");
    private JLabel lblExtra2 = new JLabel("");

    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public AccountDetailsPanel() {
        setLayout(new GridLayout(6, 1, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Chi tiết tài khoản"));
        add(lblOwner);
        add(lblAccNum);
        add(lblType);
        add(lblBalance);
        add(lblExtra1);
        add(lblExtra2);
    }

    public void setAccount(Account account, YearMonth currentMonth) {
        if (account == null) {
            clear();
            return;
        }

        lblOwner.setText("Chủ sở hữu: " + (account.getOwner() != null ? account.getOwner().getFullName() : "--"));
        lblAccNum.setText("Số TK: " + account.getAccountNumber());
        lblType.setText("Loại TK: " + account.getType());
        lblBalance.setText("Số dư: " + CURRENCY_FORMATTER.format(account.getBalance()));

        if (account instanceof SavingsAccount) {
            SavingsAccount savings = (SavingsAccount) account;
            lblExtra1.setText("Lãi suất: " + savings.getAnnualInterestRate().multiply(new BigDecimal("100")) + "%/năm");
            lblExtra2.setText("Lượt rút còn lại (" + (currentMonth != null ? currentMonth : "") + "): " 
                              + (currentMonth != null ? savings.getRemainingWithdrawals(currentMonth) : "--"));
            lblExtra1.setVisible(true);
            lblExtra2.setVisible(true);
        } else if (account instanceof CheckingAccount) {
            CheckingAccount checking = (CheckingAccount) account;
            lblExtra1.setText("Hạn mức thấu chi: " + CURRENCY_FORMATTER.format(checking.getOverdraftLimit()));
            lblExtra2.setText("Số dư khả dụng: " + CURRENCY_FORMATTER.format(checking.getAvailableBalance()));
            lblExtra1.setVisible(true);
            lblExtra2.setVisible(true);
        } else if (account instanceof FixedTermAccount) {
            FixedTermAccount fixedTerm = (FixedTermAccount) account;
            lblExtra1.setText("Lãi suất: " + fixedTerm.getAnnualInterestRate().multiply(new BigDecimal("100")) + "%/năm");
            lblExtra2.setText("Ngày đáo hạn: " + fixedTerm.getMaturityDate());
            lblExtra1.setVisible(true);
            lblExtra2.setVisible(true);
        } else {
            lblExtra1.setVisible(false);
            lblExtra2.setVisible(false);
        }
    }

    public void clear() {
        lblOwner.setText("Chủ sở hữu: --");
        lblAccNum.setText("Số TK: --");
        lblType.setText("Loại TK: --");
        lblBalance.setText("Số dư: 0 VND");
        lblExtra1.setText("");
        lblExtra2.setText("");
        lblExtra1.setVisible(false);
        lblExtra2.setVisible(false);
    }
}