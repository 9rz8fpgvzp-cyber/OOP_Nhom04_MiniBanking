package vn.edu.ptit.oop.nhom04.ui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import vn.edu.ptit.oop.nhom04.model.Transaction;
import vn.edu.ptit.oop.nhom04.service.BankingOperations;
import vn.edu.ptit.oop.nhom04.util.CsvStatementExporter;

public class CsvExportAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    private static final String TITLE = "Xuất sao kê CSV";

    private final Window owner;
    private final BankingOperations bank;
    private final Supplier<Optional<String>> selectedAccount;
    private final Consumer<String> showStatus;
    private final CsvStatementExporter exporter = new CsvStatementExporter();

    public CsvExportAction(Window owner, BankingOperations bank,
                           Supplier<Optional<String>> selectedAccount,
                           Consumer<String> showStatus) {
        super(TITLE);
        putValue(SHORT_DESCRIPTION, "Xuất lịch sử giao dịch của tài khoản đang chọn ra file CSV");
        this.owner = owner;
        this.bank = bank;
        this.selectedAccount = selectedAccount;
        this.showStatus = showStatus;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Optional<String> selected = selectedAccount.get();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(owner,
                    "Hãy đăng nhập và chọn tài khoản trước khi xuất sao kê.",
                    TITLE, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String accountNumber = selected.get();


        try {
            bank.getTransactions(accountNumber);
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(TITLE);
        chooser.setFileFilter(new FileNameExtensionFilter("Tệp CSV (*.csv)", "csv"));
        chooser.setSelectedFile(new File("saoke_" + accountNumber + ".csv"));
        if (chooser.showSaveDialog(owner) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = ensureCsvExtension(chooser.getSelectedFile());

        if (file.exists()) {
            int choice = JOptionPane.showConfirmDialog(owner,
                    "File \"" + file.getName() + "\" đã tồn tại. Ghi đè?",
                    TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            List<Transaction> history = bank.getTransactions(accountNumber);
            int rows = exporter.export(file.toPath(), accountNumber, history);
            String message = "Đã xuất " + rows + " giao dịch ra " + file.getName();
            showStatus.accept(message);
            JOptionPane.showMessageDialog(owner, message, TITLE, JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            showError("Không ghi được file: " + ex.getMessage());
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private static File ensureCsvExtension(File file) {
        if (file.getName().toLowerCase().endsWith(".csv")) {
            return file;
        }
        return new File(file.getParentFile(), file.getName() + ".csv");
    }

    private void showError(String message) {
        showStatus.accept("Xuất CSV thất bại");
        JOptionPane.showMessageDialog(owner, message, TITLE, JOptionPane.ERROR_MESSAGE);
    }
}
