package vn.edu.ptit.oop.nhom04.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class StatisticsService {

    // Tính tổng số dư toàn hệ thống (dùng BigDecimal theo quy định nhóm)
    public BigDecimal calculateTotalBalance() {
        // TODO: Khi Người 5 xong Bank/Operations, thay bằng dữ liệu thật
        return new BigDecimal("5200000000"); 
    }

    // Đếm số lượng tài khoản theo từng loại
    public Map<String, Integer> countAccountsByType() {
        Map<String, Integer> counts = new HashMap<>();
        // Dữ liệu mẫu ban đầu để vẽ biểu đồ tròn
        counts.put("Tài khoản thanh toán", 65);
        counts.put("Tài khoản tiết kiệm", 35);
        return counts;
    }

    // Thống kê dòng tiền theo tháng (triệu VNĐ)
    public Map<String, Double> getMonthlyCashFlow() {
        Map<String, Double> flow = new HashMap<>();
        flow.put("Tháng 7", 120.0);
        flow.put("Tháng 8", 190.0);
        flow.put("Tháng 9", 150.0);
        flow.put("Tháng 10", 210.0);
        return flow;
    }

    // Đếm tổng số lượng tài khoản
    public int countTotalAccounts() {
        return 1250;
    }
}