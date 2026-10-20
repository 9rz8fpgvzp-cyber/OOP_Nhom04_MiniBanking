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

import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.Transaction;
import vn.edu.ptit.oop.nhom04.service.StatisticsService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ChartHelper {

    private final StatisticsService statistics;

    // Bảng màu Solid Flat đồng bộ
    private static final Color COLOR_CORAL    = Color.decode("#C94B4B");
    private static final Color COLOR_TEAL     = Color.decode("#3B7B7A");
    private static final Color COLOR_CHARCOAL = Color.decode("#414643");

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ChartHelper(StatisticsService statistics) {
        this.statistics = statistics;
    }

    /**
     * Biểu đồ đường biến động số dư:
     * - Thêm điểm openingBalance / openedAt
     * - Các balanceAfter theo thứ tự danh sách
     * - Không dùng Map/TreeMap làm key để tránh mất các giao dịch trùng thời điểm
     */
    public JPanel createBalanceChart(Account account, List<Transaction> transactions) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (account != null) {
            String openLabel = "Mở TK";
            if (account.getOpenedAt() != null) {
                openLabel = account.getOpenedAt().format(DATE_FMT) + " (Mở TK)";
            }
            BigDecimal openBal = account.getOpeningBalance() != null ? account.getOpeningBalance() : BigDecimal.ZERO;
            dataset.addValue(openBal, "Số dư", openLabel);
        }

        if (transactions != null && !transactions.isEmpty()) {
            int seq = 1;
            for (Transaction t : transactions) {
                // Tạo nhãn dạng: "15/10 14:30 [#1]" đảm bảo duy nhất, không ghi đè khi trùng timestamp
                String timestampStr = (t.getTimestamp() != null) ? t.getTimestamp().format(TIME_FMT) : "N/A";
                String xLabel = timestampStr + " [#" + (seq++) + "]";
                BigDecimal balAfter = t.getBalanceAfter() != null ? t.getBalanceAfter() : BigDecimal.ZERO;

                dataset.addValue(balAfter, "Số dư", xLabel);
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Biến Động Số Dư (VND)",
                "Thời gian",
                "Số dư",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
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

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setBackground(Color.WHITE);
        return chartPanel;
    }

    /**
     * Biểu đồ cột nạp / rút:
     * - Gom theo ngày (LocalDate)
     * - Chỉ lấy DEPOSIT và WITHDRAW
     * - Dữ liệu rỗng vẫn hiển thị biểu đồ trắng, không sinh exception
     */
    public JPanel createDepositWithdrawalChart(List<Transaction> transactions) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<LocalDate, BigDecimal> depMap = statistics.depositsByDay(transactions);
        Map<LocalDate, BigDecimal> withMap = statistics.withdrawalsByDay(transactions);

        // Hợp nhất toàn bộ các ngày có phát sinh giao dịch
        Set<LocalDate> allDates = new TreeSet<>();
        allDates.addAll(depMap.keySet());
        allDates.addAll(withMap.keySet());

        for (LocalDate day : allDates) {
            String dayLabel = day.format(DATE_FMT);
            BigDecimal depVal = depMap.getOrDefault(day, BigDecimal.ZERO);
            BigDecimal withVal = withMap.getOrDefault(day, BigDecimal.ZERO);

            dataset.addValue(depVal, "Nạp tiền", dayLabel);
            dataset.addValue(withVal, "Rút tiền", dayLabel);
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Tổng Nạp / Rút Theo Ngày",
                "Ngày",
                "Số tiền (VND)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        barChart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(COLOR_CHARCOAL);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);

        renderer.setSeriesPaint(0, COLOR_TEAL);  // Cột Nạp tiền
        renderer.setSeriesPaint(1, COLOR_CORAL); // Cột Rút tiền

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setBackground(Color.WHITE);
        return chartPanel;
    }
}



// 
public static void main(String[] args) {
SwingUtilities.invokeLater(() -> {
    JFrame frame = new JFrame("Kiểm Thử Biểu Đồ - Người 9");
    frame.setSize(1100, 550);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new GridLayout(1, 2, 12, 12));

    // Dữ liệu giả lập test thử
    vn.edu.ptit.oop.nhom04.model.Account sampleAcc = 
        new vn.edu.ptit.oop.nhom04.model.Account(new java.math.BigDecimal("10000000"), java.time.LocalDateTime.now().minusDays(10));
    
    java.util.List<vn.edu.ptit.oop.nhom04.model.Transaction> list = new java.util.ArrayList<>();
    list.add(new vn.edu.ptit.oop.nhom04.model.Transaction(
        vn.edu.ptit.oop.nhom04.model.TransactionType.DEPOSIT, 
        new java.math.BigDecimal("5000000"), 
        java.time.LocalDateTime.now().minusDays(5), 
        new java.math.BigDecimal("15000000")));
    list.add(new vn.edu.ptit.oop.nhom04.model.Transaction(
        vn.edu.ptit.oop.nhom04.model.TransactionType.WITHDRAW, 
        new java.math.BigDecimal("3000000"), 
        java.time.LocalDateTime.now().minusDays(2), 
        new java.math.BigDecimal("12000000")));

    vn.edu.ptit.oop.nhom04.service.StatisticsService stats = new vn.edu.ptit.oop.nhom04.service.StatisticsService();
    ChartHelper helper = new ChartHelper(stats);

    frame.add(helper.createBalanceChart(sampleAcc, list));
    frame.add(helper.createDepositWithdrawalChart(list));
    frame.setVisible(true);
});
}

} // 