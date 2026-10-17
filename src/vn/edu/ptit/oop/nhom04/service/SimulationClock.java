
package vn.edu.ptit.oop.nhom04.service;

import java.time.YearMonth;
import java.util.Objects;

public class SimulationClock {

    // Tháng và năm đang được mô phỏng
    private YearMonth currentMonth;

    // Khởi tạo thời gian mô phỏng
    public SimulationClock(YearMonth startMonth) {
        this.currentMonth = Objects.requireNonNull(
            startMonth,
            "Thang bat dau khong duoc null"
        );
    }

    // Lấy tháng/năm hiện tại
    public YearMonth getCurrentMonth() {
        return currentMonth;
    }

    // Lấy số tháng hiện tại
    public int getMonth() {
        return currentMonth.getMonthValue();
    }

    // Lấy năm hiện tại
    public int getYear() {
        return currentMonth.getYear();
    }

    // Chuyển sang tháng tiếp theo
    public void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
    }
}
