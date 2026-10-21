package vn.edu.ptit.oop.nhom04.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.CheckingAccount;
import vn.edu.ptit.oop.nhom04.model.Customer;
import vn.edu.ptit.oop.nhom04.model.FixedTermAccount;
import vn.edu.ptit.oop.nhom04.model.SavingsAccount;

public class AccountDetailsPanel extends JPanel {
    // Thông tin khách hàng
    private JLabel lblCustomerId = new JLabel("Mã KH: --");
    private JLabel lblCustomerName = new JLabel("Họ tên: --");
    private JLabel lblCustomerPhone = new JLabel("Số điện thoại: --");

    // Thông tin tài khoản
    private JLabel lblAccNum = new JLabel("Số TK: --");
    private JLabel lblType = new JLabel("Loại TK: --");
    private JLabel lblBalance = new JLabel("Số dư: --");
    private JLabel lblExtra1 = new JLabel("");
    private JLabel lblExtra2 = new JLabel("");

    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AccountDetailsPanel() {
        setLayout(new GridLayout(8, 1, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Chi tiết khách hàng & tài khoản"));

        add(lblCustomerId);
        add(lblCustomerName);
        add(lblCustomerPhone);
        add(lblAccNum);
        add(lblType);
        add(lblBalance);
        add(lblExtra1);
        add(lblExtra2);

        lblExtra1.setVisible(false);
        lblExtra2.setVisible(false);
    }

    /**
     * Cập nhật thông tin Khách hàng đã đăng nhập (API v2)
     */
    public void setCustomer(Customer customer) {
        if (customer == null) {
            clear();
            return;
        }

        String id = customer.getId();
        String name = customer.getFullName();
        String phone = customer.getPhoneNumber();

        lblCustomerId.setText("Mã KH: " + (id != null ? id : "--"));
        lblCustomerName.setText("Họ tên: " + (name != null ? name : "--"));
        lblCustomerPhone.setText("Số điện thoại: " + (phone != null ? phone : "--"));
    }

    /**
     * Cập nhật thông tin Tài khoản đang chọn (API v2)
     */
    public void setAccount(Account account, YearMonth currentMonth) {
        if (account == null) {
            clearAccount();
            return;
        }

        lblAccNum.setText("Số TK: " + account.getAccountNumber());
        lblType.setText("Loại TK: " + getAccountTypeNameVi(account.getType()));
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
            
            String formattedDate = "--";
            if (fixedTerm.getMaturityDate() != null) {
                formattedDate = fixedTerm.getMaturityDate().format(DATE_FORMATTER);
            }
            lblExtra2.setText("Ngày đáo hạn: " + formattedDate);
            lblExtra1.setVisible(true);
            lblExtra2.setVisible(true);
        } else {
            lblExtra1.setVisible(false);
            lblExtra2.setVisible(false);
        }
    }

    private String getAccountTypeNameVi(Object type) {
        if (type == null) return "--";
        String name = type.toString().toUpperCase();
        if (name.contains("SAVINGS")) return "Tiết kiệm";
        if (name.contains("CHECKING")) return "Thanh toán";
        if (name.contains("FIXED") || name.contains("TERM")) return "Tiền gửi có kỳ hạn";
        return type.toString();
    }

    /**
     * Chỉ xóa phần thông tin tài khoản (Giữ nguyên thông tin khách hàng)
     */
    public void clearAccount() {
        lblAccNum.setText("Số TK: --");
        lblType.setText("Loại TK: --");
        lblBalance.setText("Số dư: --");
        lblExtra1.setText("");
        lblExtra2.setText("");
        lblExtra1.setVisible(false);
        lblExtra2.setVisible(false);
    }

    /**
     * Xóa sạch toàn bộ thông tin khi đăng xuất
     */
    public void clear() {
        lblCustomerId.setText("Mã KH: --");
        lblCustomerName.setText("Họ tên: --");
        lblCustomerPhone.setText("Số điện thoại: --");
        clearAccount();
    }
}