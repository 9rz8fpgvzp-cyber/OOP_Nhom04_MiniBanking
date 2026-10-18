package vn.edu.ptit.oop.nhom04.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Đồng hồ mô phỏng dùng chung cho toàn bộ hệ thống.
 * Thuộc sở hữu của Người 4 theo API v1.
 */
public class SimulationClock extends Clock {
    private Instant currentInstant;
    private final ZoneId zone;

    public SimulationClock(Instant initialInstant, ZoneId zone) {
        if (initialInstant == null || zone == null) {
            throw new IllegalArgumentException("initialInstant và zone không được null");
        }
        this.currentInstant = initialInstant;
        this.zone = zone;
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        if (this.zone.equals(zone)) {
            return this;
        }
        return new SimulationClock(this.currentInstant, zone);
    }

    @Override
    public Instant instant() {
        return currentInstant;
    }

    /**
     * Chuyển thời gian sang 00:00:00 ngày đầu tiên của tháng tiếp theo.
     */
    public void advanceToNextMonth() {
        ZonedDateTime zdt = currentInstant.atZone(zone);
        ZonedDateTime nextMonthStart = zdt.plusMonths(1)
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        this.currentInstant = nextMonthStart.toInstant();
    }
}
