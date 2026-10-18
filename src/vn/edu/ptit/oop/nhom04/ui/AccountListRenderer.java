package vn.edu.ptit.oop.nhom04.ui;

import javax.swing.*;
import java.awt.*;
import vn.edu.ptit.oop.nhom04.model.AccountSummary;

public class AccountListRenderer extends JPanel implements ListCellRenderer<AccountSummary> {
    private JLabel lblAccountNumber = new JLabel();
    private JLabel lblAccountType = new JLabel();
    private JLabel lblPinStatus = new JLabel();

    public AccountListRenderer() {
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        lblAccountNumber.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        lblAccountType.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        lblPinStatus.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(lblPinStatus);
        rightPanel.add(lblAccountType);

        add(lblAccountNumber, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends AccountSummary> list, 
            AccountSummary value, 
            int index, 
            boolean isSelected, 
            boolean cellHasFocus) {

        if (value != null) {
            lblAccountNumber.setText("STK: " + value.accountNumber());
            lblAccountType.setText(value.type() != null ? value.type().toString() : "");
            
            if (value.isPinSet()) {
                lblPinStatus.setText("🔒"); 
            } else {
                lblPinStatus.setText("🔓"); 
            }
        } else {
            lblAccountNumber.setText("");
            lblAccountType.setText("");
            lblPinStatus.setText("");
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