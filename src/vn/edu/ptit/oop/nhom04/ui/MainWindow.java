package vn.edu.ptit.oop.nhom04.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import vn.edu.ptit.oop.nhom04.common.BaseWindow;
import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.AccountSummary;
import vn.edu.ptit.oop.nhom04.model.Transaction;
import vn.edu.ptit.oop.nhom04.model.TransactionType;
import vn.edu.ptit.oop.nhom04.ui.dialogs.AboutDialog;
import vn.edu.ptit.oop.nhom04.ui.panels.AccountDetailsPanel;

/** Giữ bố cục và hiển thị dữ liệu; mọi nghiệp vụ được chuyển cho controller. */
public class MainWindow extends BaseWindow {
    private static final long serialVersionUID = 1L;

    // BaseWindow gọi initUI trong constructor: khởi tạo các thành phần tại initUI.
    private BankController controller;
    private DefaultListModel<AccountSummary> accountChoices;
    private JList<AccountSummary> accountList;
    private AccountDetailsPanel detailsPanel;
    private TransactionTableModel historyModel;
    private JPanel chartContainer;
    private JTabbedPane tabs;
    private Action depositAction;
    private Action withdrawAction;
    private Action transferAction;
    private Action logoutAction;
    private Action refreshAction;
    private Action monthEndAction;
    private Action csvAction;
    private JButton csvButton;
    private JMenuItem csvMenuItem;
    private boolean updatingSelection;

    public MainWindow() {
        super("MiniBank — Nhóm 04", 1100, 700);
    }

    @Override
    protected void initUI() {
<<<<<<< HEAD
        JLabel titleLabel = new JLabel("MiniBank — Nhóm 04",
                SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        add(titleLabel, BorderLayout.CENTER);
        setStatus("Sẵn sàng");
        
=======
        setMinimumSize(new Dimension(900, 600));
        depositAction = action("Nạp tiền", () -> controller.openTransaction(TransactionType.DEPOSIT));
        withdrawAction = action("Rút tiền", () -> controller.openTransaction(TransactionType.WITHDRAW));
        transferAction = action("Chuyển khoản", () -> controller.openTransaction(TransactionType.TRANSFER));
        logoutAction = action("Đăng xuất", () -> controller.logout());
        refreshAction = action("Làm mới", () -> controller.refreshSelectedAccount());
        monthEndAction = action("Cuối tháng", () -> controller.simulateMonthEnd());
        monthEndAction.putValue(Action.SHORT_DESCRIPTION,
                "Cộng lãi toàn hệ thống và chuyển sang ngày 1 tháng kế tiếp");

        toolBar.add(new JButton(depositAction));
        toolBar.add(new JButton(withdrawAction));
        toolBar.add(new JButton(transferAction));
        csvButton = new JButton("Xuất CSV");
        csvButton.setEnabled(false);
        toolBar.add(csvButton);
        toolBar.addSeparator();
        toolBar.add(new JButton(monthEndAction));
        toolBar.add(new JButton(logoutAction));

        fileMenu.setText("Tệp");
        fileMenu.getItem(0).setText("Thoát");
        fileMenu.insert(new JMenuItem(depositAction), 0);
        fileMenu.insert(new JMenuItem(withdrawAction), 1);
        fileMenu.insert(new JMenuItem(transferAction), 2);
        csvMenuItem = new JMenuItem("Xuất CSV");
        csvMenuItem.setEnabled(false);
        fileMenu.insert(csvMenuItem, 3);
        fileMenu.insert(new JMenuItem(logoutAction), 4);
        fileMenu.insertSeparator(5);
        viewMenu.setText("Xem");
        viewMenu.add(new JMenuItem(refreshAction));
        viewMenu.add(new JMenuItem(monthEndAction));
        helpMenu.setText("Trợ giúp");
        helpMenu.getItem(0).setText("Giới thiệu...");

        accountChoices = new DefaultListModel<>();
        accountList = new JList<>(accountChoices);
        accountList.setName("accountList");
        accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountList.setCellRenderer(new AccountListRenderer());
        accountList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && !updatingSelection && controller != null) {
                controller.selectAccount(getSelectedAccountNumber().orElse(null));
            }
        });
        JPanel accountsPanel = new JPanel(new BorderLayout(5, 5));
        accountsPanel.setBorder(BorderFactory.createTitledBorder("Danh sách tài khoản"));
        accountsPanel.add(new JScrollPane(accountList), BorderLayout.CENTER);
        accountsPanel.add(new JLabel("Chọn tài khoản để nhập PIN"), BorderLayout.SOUTH);
        accountsPanel.setMinimumSize(new Dimension(220, 100));

        detailsPanel = new AccountDetailsPanel();
        historyModel = new TransactionTableModel();
        JTable historyTable = new JTable(historyModel);
        historyTable.setName("historyTable");
        historyTable.setFillsViewportHeight(true);
        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {155, 105, 65, 130, 145, 300};
        for (int i = 0; i < widths.length; i++) {
            historyTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        chartContainer = new JPanel(new GridLayout(1, 2, 10, 10));
        chartContainer.setName("chartContainer");
        tabs = new JTabbedPane();
        tabs.addTab("Lịch sử", new JScrollPane(historyTable));
        tabs.addTab("Biểu đồ", chartContainer);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, detailsPanel, tabs);
        rightSplit.setResizeWeight(0.35);
        rightSplit.setDividerLocation(220);
        rightSplit.setMinimumSize(new Dimension(450, 300));
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, accountsPanel, rightSplit);
        mainSplit.setResizeWeight(0.25);
        mainSplit.setDividerLocation(260);
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(mainSplit, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
        clearAccountContent();
        showStatus("Sẵn sàng");
    }

    private Action action(String name, Runnable handler) {
        Action action = new AbstractAction(name) {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                if (controller != null) {
                    handler.run();
                }
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION, name);
        action.setEnabled(false);
        return action;
    }

    public void bindController(BankController controller) {
        this.controller = Objects.requireNonNull(controller, "controller");
        monthEndAction.setEnabled(true);
    }

    public void setAccountChoices(List<AccountSummary> accounts) {
        updatingSelection = true;
        try {
            accountChoices.clear();
            accountChoices.addAll(List.copyOf(accounts));
            accountList.clearSelection();
        } finally {
            updatingSelection = false;
        }
    }

    public Optional<String> getSelectedAccountNumber() {
        AccountSummary selected = accountList.getSelectedValue();
        return selected == null ? Optional.empty() : Optional.of(selected.accountNumber());
    }

    public void showAccount(Account account, YearMonth currentMonth) {
        detailsPanel.setAccount(account, currentMonth);
    }

    public void showTransactions(List<Transaction> transactions) {
        historyModel.setTransactions(transactions);
    }

    public void showCharts(JPanel balanceChart, JPanel depositWithdrawalChart) {
        chartContainer.removeAll();
        chartContainer.add(balanceChart);
        chartContainer.add(depositWithdrawalChart);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    public void clearAccountContent() {
        detailsPanel.clear();
        historyModel.clear();
        clearCharts();
        tabs.setSelectedIndex(0);
        setAccountActionsEnabled(false);
    }

    public void showStatus(String message) {
        setStatus(message);
    }

    @Override
    protected void showAbout() {
        new AboutDialog(this).showDialog();
    }

    void setCsvExportAction(Action action) {
        csvAction = Objects.requireNonNull(action, "csvAction");
        csvAction.setEnabled(false);
        csvButton.setAction(csvAction);
        csvButton.setText("Xuất CSV");
        csvMenuItem.setAction(csvAction);
    }

    void setAccountActionsEnabled(boolean enabled) {
        depositAction.setEnabled(enabled);
        withdrawAction.setEnabled(enabled);
        transferAction.setEnabled(enabled);
        logoutAction.setEnabled(enabled);
        refreshAction.setEnabled(enabled);
        if (csvAction != null) {
            csvAction.setEnabled(enabled);
        }
    }

    void clearAccountSelection() {
        updatingSelection = true;
        try {
            accountList.clearSelection();
        } finally {
            updatingSelection = false;
        }
    }

    void enableSessionRecovery() {
        refreshAction.setEnabled(true);
        logoutAction.setEnabled(true);
    }

    void clearCharts() {
        chartContainer.removeAll();
        chartContainer.add(new JLabel("Chọn và xác thực tài khoản để xem biểu đồ",
                SwingConstants.CENTER));
        chartContainer.revalidate();
        chartContainer.repaint();
>>>>>>> main
    }
}
