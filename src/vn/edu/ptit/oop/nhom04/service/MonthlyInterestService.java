package vn.edu.ptit.oop.nhom04.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.InterestBearing;
import vn.edu.ptit.oop.nhom04.model.Transaction;

/**
 * Xử lý tính và cộng lãi cuối tháng cho các tài khoản sinh lãi.
 * Thuộc sở hữu của Người 4 theo API v1.
 */
public class MonthlyInterestService {

    public MonthlyInterestService() {
    }

    /**
     * Áp dụng tính lãi cuối tháng cho danh sách tài khoản.
     *
     * @param accounts     Danh sách các tài khoản trong hệ thống
     * @param closingMonth Tháng chốt lãi
     * @param closingTime  Thời điểm chốt (23:59:59 ngày cuối tháng)
     * @return Danh sách các giao dịch lãi đã tạo thành công
     */
    public List<Transaction> applyMonthEnd(List<Account> accounts,
                                          YearMonth closingMonth,
                                          LocalDateTime closingTime) {
        if (accounts == null || closingMonth == null || closingTime == null) {
            throw new IllegalArgumentException("Các tham số không được null");
        }

        List<Transaction> createdTransactions = new ArrayList<>();
        for (Account account : accounts) {
            if (account instanceof InterestBearing interestBearing) {
                BigDecimal monthlyInterest = interestBearing.calculateMonthlyInterest();
                if (monthlyInterest != null && monthlyInterest.compareTo(BigDecimal.ZERO) >= 0) {
                    Optional<Transaction> txOpt = account.creditInterest(monthlyInterest, closingMonth, closingTime);
                    txOpt.ifPresent(createdTransactions::add);
                }
            }
        }
        return createdTransactions;
    }
}
