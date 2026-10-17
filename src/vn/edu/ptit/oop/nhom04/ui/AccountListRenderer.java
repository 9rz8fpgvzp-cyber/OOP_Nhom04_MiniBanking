package vn.edu.ptit.oop.nhom04.ui;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class AccountListRenderer extends JPanel implements ListCellRenderer<Object> {
    private JLabel lblAccountInfo = new JLabel();
    private JLabel lblBalance = new JLabel();

    public AccountListRenderer() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(lblAccountInfo, BorderLayout.CENTER);
        add(lblBalance, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Sau này khi ghép code sẽ cast value về AccountSummary
        if (value != null) {
            lblAccountInfo.setText(value.toString());
            lblBalance.setText("0 VND"); // Định dạng tiền tệ VND
        } else {
            lblAccountInfo.setText("");
            lblBalance.setText("");
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}