package vn.edu.ptit.oop.nhom04.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;

import vn.edu.ptit.oop.nhom04.common.BaseWindow;

public class MainWindow extends BaseWindow {
    private static final long serialVersionUID = 1L;

    public MainWindow() {
        super("MiniBank — Nhóm 04", 1100, 700);
    }

    @Override
    protected void initUI() {
        // BaseWindow đã tạo menu, toolbar và status bar.
        createToolbarButtons();

        // Bên trái: danh sách tài khoản, hiện đang trống.
        JPanel accountListPanel = new JPanel(new BorderLayout());
        accountListPanel.setBorder(
            BorderFactory.createTitledBorder("Danh sách tài khoản")
        );
        accountListPanel.add(
            new JScrollPane(new JList<String>()),
            BorderLayout.CENTER
        );
        accountListPanel.setMinimumSize(new Dimension(220, 100));

        // Bên phải, phía trên: vị trí gắn AccountDetailsPanel.
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(
            BorderFactory.createTitledBorder("Chi tiết tài khoản")
        );
        detailsPanel.add(
            new JLabel("Chọn tài khoản để xem thông tin",
                       SwingConstants.CENTER),
            BorderLayout.CENTER
        );

        // Bên phải, phía dưới: lịch sử và biểu đồ.
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Lịch sử", createPlaceholder(
            "Vị trí gắn bảng lịch sử của người 6"
        ));
        tabs.addTab("Biểu đồ", createPlaceholder(
            "Vị trí gắn hai biểu đồ của người 9"
        ));

        // Chia vùng bên phải thành phần trên và dưới.
        JSplitPane rightSplit = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT, detailsPanel, tabs
        );
        rightSplit.setResizeWeight(0.35);
        rightSplit.setDividerLocation(200);

        // Chia toàn bộ nội dung thành trái và phải.
        JSplitPane mainSplit = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, accountListPanel, rightSplit
        );
        mainSplit.setResizeWeight(0.25);
        mainSplit.setDividerLocation(260);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
        content.add(mainSplit, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
        setMinimumSize(new Dimension(900, 600));
        setStatus("Đã dựng bố cục — chưa kết nối nghiệp vụ");
    }

    private void createToolbarButtons() {
        String[] names = {
            "Nạp tiền", "Rút tiền", "Chuyển khoản",
            "Xuất CSV", "Cuối tháng", "Đăng xuất"
        };

        for (String name : names) {
            JButton button = new JButton(name);
            button.setToolTipText(name);
            button.setFocusable(false);

            // Bật sau khi đã nối chức năng.
            button.setEnabled(false);
            toolBar.add(button);
        }
    }

    private JPanel createPlaceholder(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(
            new JLabel(text, SwingConstants.CENTER),
            BorderLayout.CENTER
        );
        return panel;
    }
}