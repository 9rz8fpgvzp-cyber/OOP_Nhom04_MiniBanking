package vn.edu.ptit.oop.nhom04.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ChartHelper extends JFrame {

    // Bộ mã màu Solid Flat
    private static final Color COLOR_CORAL    = Color.decode("#C94B4B");
    private static final Color COLOR_CREAM    = Color.decode("#EAE3C3");
    private static final Color COLOR_SAGE     = Color.decode("#9BB6A1");
    private static final Color COLOR_TEAL     = Color.decode("#3B7B7A");
    private static final Color COLOR_CHARCOAL = Color.decode("#414643");

    // Các thành phần giao diện cần cập nhật khi đổi tài khoản
    private JLabel lblBalanceVal;
    private JLabel lblAccountTypeVal;
    private JLabel lblRuleVal;
    private JLabel lblStatusVal;
    private JLabel lblFooterStatus;
    private JTable transactionTable;

    public ChartHelper() {
        setTitle("Hệ thống Quản lý tài khoản Ngân Hàng");
        setSize(1240, 750);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Menu Bar
        setJMenuBar(createMenuBar());

        // 2. Không gian làm việc chính
        JPanel mainContentPanel = new JPanel(new BorderLayout(15, 0));
        mainContentPanel.setBackground(new Color(246, 244, 238));
        mainContentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainContentPanel.add(createLeftPanel(), BorderLayout.WEST);
        mainContentPanel.add(createRightPanel(), BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER);

        // 3. Status Bar đáy màn hình
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    // --- MENU BAR (TIẾNG VIỆT) ---
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JMenu menuSys = new JMenu("Hệ thống");
        JMenu menuTrans = new JMenu("Giao dịch");
        JMenu menuHelp = new JMenu("Trợ giúp");

        menuSys.setForeground(COLOR_CHARCOAL);
        menuTrans.setForeground(COLOR_CHARCOAL);
        menuHelp.setForeground(COLOR_CHARCOAL);

        JMenuItem itemAccountList = new JMenuItem("Danh sách tài khoản");
        itemAccountList.addActionListener(e -> openAccountListDialog());
        menuSys.add(itemAccountList);

        JMenuItem itemExit = new JMenuItem("Thoát");
        itemExit.addActionListener(e -> System.exit(0));
        menuSys.addSeparator();
        menuSys.add(itemExit);

        JMenuItem itemDeposit = new JMenuItem("Nạp tiền...");
        JMenuItem itemWithdraw = new JMenuItem("Rút tiền...");
        JMenuItem itemTransfer = new JMenuItem("Chuyển khoản...");
        JMenuItem itemSimulate = new JMenuItem("Mô phỏng cuối tháng...");
        menuTrans.add(itemDeposit);
        menuTrans.add(itemWithdraw);
        menuTrans.add(itemTransfer);
        menuTrans.addSeparator();
        menuTrans.add(itemSimulate);

        JMenuItem itemAbout = new JMenuItem("Giới thiệu");
        itemAbout.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Hệ thống Quản lý Tài khoản Ngân Hàng\nĐồ án Lập trình Hướng đối tượng Java Swing",
                "Giới thiệu", JOptionPane.INFORMATION_MESSAGE));
        menuHelp.add(itemAbout);

        menuBar.add(menuSys);
        menuBar.add(menuTrans);
        menuBar.add(menuHelp);
        return menuBar;
    }

    // --- CỘT TRÁI: THẺ THÔNG TIN & LƯỚI 6 NÚT BẤM ---
    public JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(450, 0));
        leftPanel.setOpaque(false);

        // Khung thẻ thông tin
        JPanel cardContainer = new JPanel();
        cardContainer.setLayout(new BoxLayout(cardContainer, BoxLayout.Y_AXIS));
        cardContainer.setBackground(COLOR_CREAM);
        cardContainer.setBorder(new EmptyBorder(4, 4, 4, 4));
        cardContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Banner xanh Teal
        JPanel bannerPanel = new JPanel();
        bannerPanel.setBackground(COLOR_SAGE);
        bannerPanel.setPreferredSize(new Dimension(330, 48));
        bannerPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 48));
        JLabel lblTitle = new JLabel("TÀI KHOẢN HIỆN TẠI");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        bannerPanel.add(lblTitle);
        cardContainer.add(bannerPanel);
        cardContainer.add(Box.createVerticalStrut(8));

        // Nội dung chi tiết
        JPanel detailsBox = new JPanel(new GridLayout(4, 2, 6, 10));
        detailsBox.setBackground(COLOR_CREAM);
        detailsBox.setBorder(new EmptyBorder(12, 12, 12, 12));

        detailsBox.add(createLabelKey("Loại tài khoản:"));
        lblAccountTypeVal = new JLabel("Tiết kiệm (Savings)");
        lblAccountTypeVal.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblAccountTypeVal.setForeground(COLOR_CHARCOAL);
        detailsBox.add(lblAccountTypeVal);

        detailsBox.add(createLabelKey("Số dư khả dụng:"));
        lblBalanceVal = new JLabel("25,400,000 VND");
        lblBalanceVal.setForeground(COLOR_TEAL);
        lblBalanceVal.setFont(new Font("SansSerif", Font.BOLD, 14));
        detailsBox.add(lblBalanceVal);

        detailsBox.add(createLabelKey("Quy tắc riêng:"));
        lblRuleVal = new JLabel("Lãi: 5.5% | Rút: 1/3 lần");
        lblRuleVal.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblRuleVal.setForeground(COLOR_CHARCOAL);
        detailsBox.add(lblRuleVal);

        detailsBox.add(createLabelKey("Trạng thái:"));
        lblStatusVal = new JLabel("● Đang hoạt động");
        lblStatusVal.setForeground(COLOR_TEAL);
        lblStatusVal.setFont(new Font("SansSerif", Font.BOLD, 12));
        detailsBox.add(lblStatusVal);

        cardContainer.add(detailsBox);
        leftPanel.add(cardContainer);
        leftPanel.add(Box.createVerticalStrut(15));

        // Lưới 6 nút chức năng (2 cột x 3 dòng)
        JPanel actionGrid = new JPanel(new GridLayout(3, 2, 8, 8));
        actionGrid.setOpaque(false);
        actionGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnDeposit  = createSolidButton("Nạp tiền", "👛", COLOR_SAGE, COLOR_CHARCOAL);
        JButton btnWithdraw = createSolidButton("Rút tiền", "🏧", COLOR_SAGE, COLOR_CHARCOAL);
        JButton btnTransfer = createSolidButton("Chuyển khoản", "🔁", COLOR_SAGE, COLOR_CHARCOAL);
        JButton btnSimulate = createSolidButton("Mô phỏng", "📊", COLOR_SAGE, COLOR_CHARCOAL);
        JButton btnRefresh  = createSolidButton("Làm mới", "🔄", COLOR_SAGE, COLOR_CHARCOAL);
        JButton btnSwitch   = createSolidButton("Đổi tài khoản", "👥", COLOR_SAGE, COLOR_CHARCOAL);

        // Gắn sự kiện click mở danh sách tài khoản
        btnSwitch.addActionListener(e -> openAccountListDialog());

        actionGrid.add(btnDeposit);
        actionGrid.add(btnWithdraw);
        actionGrid.add(btnTransfer);
        actionGrid.add(btnSimulate);
        actionGrid.add(btnRefresh);
        actionGrid.add(btnSwitch);

        leftPanel.add(actionGrid);
        return leftPanel;
    }

    private JLabel createLabelKey(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(COLOR_CHARCOAL);
        return lbl;
    }

    // Phương thức tạo nút nhận đủ 4 tham số, tránh lỗi biên dịch
    private JButton createSolidButton(String text, String iconSymbol, Color bgColor, Color fgColor) {
    	String htmlText = "<html><center><span style='font-family:Segoe UI Emoji; font-size:16px;'>" 
                + iconSymbol + "</span><br><b>" + text + "</b></center></html>";


        JButton btn = new JButton(htmlText);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(bgColor.darker(), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 52));
        btn.setMargin(new Insets(2, 4, 2, 4));
        btn.setToolTipText("Thao tác " + text);
        return btn;
    }

    // --- CỘT PHẢI: BẢNG LỊCH SỬ & 2 BIỂU ĐỒ JFREECHART ---
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setOpaque(false);

        // Bảng lịch sử giao dịch bất biến
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(new TitledBorder(new LineBorder(COLOR_SAGE, 1), "Lịch sử giao dịch bất biến",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 12), COLOR_CHARCOAL));

        String[] cols = {"Mã GD", "Thời gian", "Loại GD", "Số tiền (VND)", "Số dư sau GD"};
        Object[][] data = {
            {"TX-01", "2026-10-01 09:15", "NẠP TIỀN", "+10,000,000", "10,000,000"},
            {"TX-02", "2026-10-05 14:20", "NẠP TIỀN", "+20,000,000", "30,000,000"},
            {"TX-03", "2026-10-10 11:00", "RÚT TIỀN", "-5,000,000", "25,000,000"},
            {"TX-04", "2026-10-15 16:45", "CỘNG LÃI", "+400,000", "25,400,000"}
        };
        transactionTable = new JTable(new DefaultTableModel(data, cols));
        transactionTable.setRowHeight(26);
        transactionTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        transactionTable.setForeground(COLOR_CHARCOAL);

        // Cố định nền Charcoal và chữ trắng cho Header bảng
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(COLOR_CHARCOAL);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setFont(new Font("SansSerif", Font.BOLD, 12));
        headerRenderer.setPreferredSize(new Dimension(0, 30));
        transactionTable.getTableHeader().setDefaultRenderer(headerRenderer);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        transactionTable.getColumnModel().getColumn(0).setCellRenderer(center);
        transactionTable.getColumnModel().getColumn(1).setCellRenderer(center);
        transactionTable.getColumnModel().getColumn(2).setCellRenderer(center);

        JScrollPane scrollTable = new JScrollPane(transactionTable);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        tableContainer.add(scrollTable, BorderLayout.CENTER);
        tableContainer.setPreferredSize(new Dimension(0, 190));

        // 2 Biểu đồ JFreeChart
        JPanel chartsContainer = new JPanel(new GridLayout(1, 2, 12, 0));
        chartsContainer.setOpaque(false);
        chartsContainer.setBorder(new TitledBorder(new LineBorder(COLOR_SAGE, 1), "Thống kê trực quan",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 12), COLOR_CHARCOAL));

        chartsContainer.add(createSolidLineChartPanel());
        chartsContainer.add(createSolidBarChartPanel());

        rightPanel.add(tableContainer, BorderLayout.NORTH);
        rightPanel.add(chartsContainer, BorderLayout.CENTER);
        return rightPanel;
    }

    // Biểu đồ đường biến động số dư
    private ChartPanel createSolidLineChartPanel() {
        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
        lineDataset.addValue(0, "Số dư", "01/10");
        lineDataset.addValue(10000000, "Số dư", "05/10");
        lineDataset.addValue(30000000, "Số dư", "10/10");
        lineDataset.addValue(25000000, "Số dư", "15/10");
        lineDataset.addValue(25400000, "Số dư", "17/10");

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Biến Động Số Dư (VND)", "Thời gian", "Số dư",
                lineDataset, PlotOrientation.VERTICAL, false, true, false
        );
        lineChart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(COLOR_CHARCOAL);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, COLOR_TEAL);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        plot.setRenderer(renderer);

        return new ChartPanel(lineChart);
    }

    // Biểu đồ cột tổng nạp / rút theo ngày
    private ChartPanel createSolidBarChartPanel() {
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        barDataset.addValue(10000000, "Nạp tiền", "01/10");
        barDataset.addValue(0, "Rút tiền", "01/10");
        barDataset.addValue(0, "Lãi suất", "01/10");

        barDataset.addValue(20000000, "Nạp tiền", "05/10");
        barDataset.addValue(0, "Rút tiền", "05/10");
        barDataset.addValue(0, "Lãi suất", "05/10");

        barDataset.addValue(0, "Nạp tiền", "10/10");
        barDataset.addValue(5000000, "Rút tiền", "10/10");
        barDataset.addValue(0, "Lãi suất", "10/10");

        barDataset.addValue(0, "Nạp tiền", "15/10");
        barDataset.addValue(0, "Rút tiền", "15/10");
        barDataset.addValue(400000, "Lãi suất", "15/10");

        JFreeChart barChart = ChartFactory.createBarChart(
                "Tổng Nạp / Rút Theo Ngày", "Ngày", "Số tiền (VND)",
                barDataset, PlotOrientation.VERTICAL, true, true, false
        );
        barChart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(COLOR_CHARCOAL);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);

        renderer.setSeriesPaint(0, COLOR_TEAL);
        renderer.setSeriesPaint(1, COLOR_CORAL);
        renderer.setSeriesPaint(2, COLOR_SAGE);

        return new ChartPanel(barChart);
    }

    // --- STATUS BAR ---
    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                new EmptyBorder(6, 15, 6, 15)
        ));

        lblFooterStatus = new JLabel("Hệ thống sẵn sàng | Ngày mô phỏng: 17/10/2026");
        lblFooterStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblFooterStatus.setForeground(COLOR_CHARCOAL);

        JLabel lblUser = new JLabel("Phiên: Quản trị viên");
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblUser.setForeground(COLOR_CHARCOAL);

        statusPanel.add(lblFooterStatus, BorderLayout.WEST);
        statusPanel.add(lblUser, BorderLayout.EAST);
        return statusPanel;
    }

    // Xử lý mở dialog danh sách tài khoản và cập nhật dữ liệu
    private void openAccountListDialog() {
        AccountListDialog dialog = new AccountListDialog(this);
        dialog.setVisible(true);

        AccountRecord selected = dialog.getSelectedAccount();
        if (selected != null) {
            lblAccountTypeVal.setText(selected.type);
            lblBalanceVal.setText(selected.balance);
            lblRuleVal.setText(selected.rule);
            lblFooterStatus.setText("Đang làm việc với: " + selected.accNumber + " (" + selected.owner + ")");
        }
    }

    // --- HỘP THOẠI DANH SÁCH TÀI KHOẢN (LỒNG TRONG FILE) ---
    public static class AccountListDialog extends JDialog {
        private JTable tblAccounts;
        private AccountRecord selectedAccount = null;

        public AccountListDialog(JFrame parent) {
            super(parent, "Danh Sách Tài Khoản Hệ Thống", true);
            setSize(680, 360);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout(10, 10));

            String[] cols = {"Số TK", "Chủ tài khoản", "Loại tài khoản", "Số dư (VND)", "Ràng buộc"};
            Object[][] data = {
                {"TK-1001", "Nguyễn Văn A", "Tiết kiệm (Savings)", "25,400,000", "Lãi 5.5% | Tối đa 3 lần rút"},
                {"TK-1002", "Trần Thị B", "Vãng lai (Checking)", "12,800,000", "Hạn mức thấu chi: 5,000,000"},
                {"TK-1003", "Lê Văn C", "Kỳ hạn (Fixed Term)", "50,000,000", "Đáo hạn: 31/12/2026"}
            };

            tblAccounts = new JTable(new DefaultTableModel(data, cols));
            tblAccounts.setRowHeight(26);
            tblAccounts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            add(new JScrollPane(tblAccounts), BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnSelect = new JButton("Chọn tài khoản này");
            JButton btnCancel = new JButton("Đóng");

            btnSelect.addActionListener(e -> {
                int row = tblAccounts.getSelectedRow();
                if (row != -1) {
                    selectedAccount = new AccountRecord(
                        (String) tblAccounts.getValueAt(row, 0),
                        (String) tblAccounts.getValueAt(row, 1),
                        (String) tblAccounts.getValueAt(row, 2),
                        (String) tblAccounts.getValueAt(row, 3) + " VND",
                        (String) tblAccounts.getValueAt(row, 4)
                    );
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng click chọn 1 tài khoản trong bảng!");
                }
            });

            btnCancel.addActionListener(e -> dispose());
            bottomPanel.add(btnSelect);
            bottomPanel.add(btnCancel);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        public AccountRecord getSelectedAccount() {
            return selectedAccount;
        }
    }

    // Lớp DTO đơn giản lưu trữ thông tin dòng được chọn
    public static class AccountRecord {
        public String accNumber;
        public String owner;
        public String type;
        public String balance;
        public String rule;

        public AccountRecord(String accNumber, String owner, String type, String balance, String rule) {
            this.accNumber = accNumber;
            this.owner = owner;
            this.type = type;
            this.balance = balance;
            this.rule = rule;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new ChartHelper().setVisible(true);
        });
    }
}