package vn.edu.ptit.oop.nhom04.ui.dialogs;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import vn.edu.ptit.oop.nhom04.service.PinService;
import vn.edu.ptit.oop.nhom04.exception.AccountLockedException;
import vn.edu.ptit.oop.nhom04.exception.AccountNotFoundException;

public class LoginDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final PinService pinService;

    private final JTextField userField = new JTextField(16);
    private final JPasswordField pinField = new JPasswordField(16);

    private final JLabel messageLabel = new JLabel("Nhập mã khách hàng và PIN gồm 6 chữ số.");

    private final JButton loginButton = new JButton("Đăng nhập");
    private final Timer countdown;

    private boolean authenticated = false;

    private boolean showingLockMessage = false;

    public LoginDialog(Window owner, PinService pinService) {
        super(owner, "Đăng nhập khách hàng", Dialog.ModalityType.APPLICATION_MODAL);

        this.pinService = Objects.requireNonNull(pinService);
        countdown = new Timer(250, e -> updateLockStatus());

        initUI();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(540, 220);
        setResizable(false);
        setLocationRelativeTo(owner);

        onUserChanged();
    }

    private void initUI() {
        JPanel fields = new JPanel(new GridLayout(2, 2, 8, 8));

        fields.add(new JLabel("Mã khách hàng:"));
        fields.add(userField);

        fields.add(new JLabel("PIN:"));
        fields.add(pinField);

        JPanel center = new JPanel(new BorderLayout(8, 12));
        center.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        center.add(fields, BorderLayout.CENTER);
        center.add(messageLabel, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Hủy");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelButton);
        buttons.add(loginButton);

        add(buttons, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> attemptLogin());
        cancelButton.addActionListener(e -> dispose());

        getRootPane().setDefaultButton(loginButton);

        userField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onUserChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onUserChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onUserChanged();
            }
        });
    }

    private String getCustomerId() {
        return userField.getText().trim();
    }

    private void onUserChanged() {
        pinField.setText("");
        showingLockMessage = false;

        messageLabel.setText("Nhập mã khách hàng và PIN gồm 6 chữ số.");

        updateLockStatus();
    }

    private void updateLockStatus() {
        String customerId = getCustomerId();

        pinField.setEnabled(true);
        loginButton.setEnabled(!customerId.isEmpty());

        long seconds = 0;

        try {
            if (!customerId.isEmpty()) {
                seconds = pinService.getRemainingLockSeconds(customerId);
            }
        } catch (AccountNotFoundException ex) {
            // Chỉ báo khách hàng không tồn tại khi nhấn Đăng nhập.
        }

        if (seconds > 0) {
            showingLockMessage = true;

            messageLabel.setText("Khách hàng bị khóa đăng nhập. Còn " + seconds + " giây.");

            return;
        }

        if (showingLockMessage) {
            showingLockMessage = false;

            messageLabel.setText("Đã hết khóa. Bạn có thể bấm Đăng nhập.");
        }
    }

    private void attemptLogin() {
        String customerId = getCustomerId();
        char[] pin = pinField.getPassword();

        try {
            boolean success = pinService.authenticate(customerId, pin);

            if (success) {
                authenticated = true;
                dispose();
            } else {
                int remaining = pinService.getRemainingAttempts(customerId);

                messageLabel.setText( "Sai PIN. Còn " + remaining + " lần thử.");

                pinField.requestFocusInWindow();
            }

        } catch (AccountLockedException ex) {
            updateLockStatus();
            pinField.requestFocusInWindow();

        } catch (AccountNotFoundException | IllegalArgumentException ex) {
            messageLabel.setText(ex.getMessage());

        } finally {
            Arrays.fill(pin, '\0');
            pinField.setText("");
        }
    }

    @Override
    public void dispose() {
        countdown.stop();
        pinField.setText("");
        super.dispose();
    }

    public boolean showDialog() {
        authenticated = false;
        pinService.logout();

        onUserChanged();
        countdown.start();

        try {
            setVisible(true);
            return authenticated;

        } finally {
            countdown.stop();
            pinField.setText("");
        }
    }
}