package vn.edu.ptit.oop.nhom04.util;

import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.Transaction;
import vn.edu.ptit.oop.nhom04.model.TransactionType;
import vn.edu.ptit.oop.nhom04.service.StatisticsService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChartHelperTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Kiểm Thử Biểu Đồ - Người 9");
            frame.setSize(1100, 550);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new GridLayout(1, 2, 12, 12));

            // Dữ liệu giả lập có ngày tháng năm
            Account sampleAcc = new Account(new BigDecimal("10000000"), LocalDateTime.now().minusDays(10));
            List<Transaction> list = new ArrayList<>();
            list.add(new Transaction(TransactionType.DEPOSIT, new BigDecimal("5000000"), LocalDateTime.now().minusDays(5), new BigDecimal("15000000")));
            list.add(new Transaction(TransactionType.WITHDRAW, new BigDecimal("3000000"), LocalDateTime.now().minusDays(2), new BigDecimal("12000000")));
            list.add(new Transaction(TransactionType.DEPOSIT, new BigDecimal("8000000"), LocalDateTime.now().minusDays(1), new BigDecimal("20000000")));

            StatisticsService stats = new StatisticsService();
            ChartHelper helper = new ChartHelper(stats);

            frame.add(helper.createBalanceChart(sampleAcc, list));
            frame.add(helper.createDepositWithdrawalChart(list));
            frame.setVisible(true);
        });
    }
}