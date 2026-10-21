package vn.edu.ptit.oop.nhom04.ui;

import javax.swing.*;
import java.awt.*;
import vn.edu.ptit.oop.nhom04.model.AccountSummary;

public class AccountListRenderer extends JPanel implements ListCellRenderer<AccountSummary> {
    private JLabel lblIcon = new JLabel();
    private JLabel lblAccountNumber = new JLabel();
    private JLabel lblAccountType = new JLabel();

    public AccountListRenderer() {
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        lblIcon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        lblAccountNumber.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        lblAccountType.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(lblIcon);
        leftPanel.add(lblAccountNumber);

        add(leftPanel, BorderLayout.WEST);
        add(lblAccountType, BorderLayout.EAST);
    }

    private String getIconForType(Object type) {
        if (type == null) return "📄";
        String name = type.toString().toUpperCase();
        if (name.contains("SAVINGS")) return "💰";
        if (name.contains("CHECKING")) return "💳";
        if (name.contains("FIXED") || name.contains("TERM")) return "⏳";
        return "📄";
    }

    private String getAccountTypeNameVi(Object type) {
        if (type == null) return "";
        String name = type.toString().toUpperCase();
        if (name.contains("SAVINGS")) return "Tiết kiệm";
        if (name.contains("CHECKING")) return "Thanh toán";
        if (name.contains("FIXED") || name.contains("TERM")) return "Tiền gửi có kỳ hạn";
        return type.toString();
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends AccountSummary> list, 
            AccountSummary value, 
            int index, 
            boolean isSelected, 
            boolean cellHasFocus) {

        if (value != null) {
            lblIcon.setText(getIconForType(value.type()));
            lblAccountNumber.setText("STK: " + value.accountNumber());
            lblAccountType.setText(getAccountTypeNameVi(value.type()));
        } else {
            lblIcon.setText("");
            lblAccountNumber.setText("");
            lblAccountType.setText("");
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            lblAccountNumber.setForeground(list.getSelectionForeground());
            lblAccountType.setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            lblAccountNumber.setForeground(list.getForeground());
            lblAccountType.setForeground(Color.GRAY);
        }
        return this;
    }
}