
package vn.edu.ptit.oop.nhom04.ui.dialogs;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.*;

import vn.edu.ptit.oop.nhom04.service.PinService;
import vn.edu.ptit.oop.nhom04.exception.AccountLockedException;

public class LoginDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private final PinService pinService;

    private final JPasswordField pinField =new JPasswordField(12);

    private final JLabel messageLabel =new JLabel("Nhập PIN gồm 6 chữ số.");

    private final JButton loginButton =new JButton("Đăng nhập");

    private boolean authenticated = false;

    public LoginDialog(Window owner,String accountNumber,PinService pinService) {

        super(owner, "Xác thực PIN", Dialog.ModalityType.APPLICATION_MODAL);

        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Số tài khoản không hợp lệ.");
        }

        this.accountNumber = accountNumber;
        this.pinService = Objects.requireNonNull(pinService);

        initUI();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(390, 220);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel center = new JPanel(new GridLayout(3, 1, 5, 8));

        center.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        center.add(new JLabel("Tài khoản: " + accountNumber));

        center.add(pinField);
        center.add(messageLabel);

        add(center, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Hủy");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttons.add(cancelButton);
        buttons.add(loginButton);

        add(buttons, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> attemptLogin());

        cancelButton.addActionListener(e -> {
            pinField.setText("");
            dispose();
        });

        getRootPane().setDefaultButton(loginButton);
    }

    private void attemptLogin() {
        char[] pin = pinField.getPassword();

        try {
            boolean success = pinService.authenticate(accountNumber, pin);

            if (success) {
                authenticated = true;
                dispose();
            } else {
                int remaining = pinService.getRemainingAttempts(accountNumber);

                messageLabel.setText("Sai PIN. Còn " + remaining + " lần thử.");

                pinField.requestFocusInWindow();
            }

        } catch (AccountLockedException ex) {
            messageLabel.setText("Tài khoản đã bị khóa.");

            loginButton.setEnabled(false);
            pinField.setEnabled(false);

            JOptionPane.showMessageDialog(this,ex.getMessage(),"Tài khoản bị khóa",JOptionPane.ERROR_MESSAGE);

        } catch (RuntimeException ex) {messageLabel.setText("Không thể xác thực.");

            JOptionPane.showMessageDialog(this,ex.getMessage(),"Lỗi xác thực",JOptionPane.ERROR_MESSAGE);

        } finally {
        	Arrays.fill(pin, '\0');
            pinField.setText("");
        }
    }

    public boolean showDialog() {
        authenticated = false;
        setVisible(true);
        pinField.setText("");
        return authenticated;
    }
}
