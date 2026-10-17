package vn.edu.ptit.oop.nhom04.service;

import java.time.*;
import java.util.Objects;

public final class SimulationClock extends Clock {

    private final State state;
    private final ZoneId zone;

    private static final class State {
        private Instant instant;

        State(Instant instant) {
            this.instant = instant;
        }
    }

    // Tạo đồng hồ với thời gian thiết bị
    public SimulationClock() {
        this(Instant.now(), ZoneId.systemDefault());
    }

    // Tạo đồng hồ với thời gian tùy chỉnh
    public SimulationClock(Instant instant, ZoneId zone) {
        this(new State(Objects.requireNonNull(instant)), zone);
    }

    private SimulationClock(State state, ZoneId zone) {
        this.state = state;
        this.zone = Objects.requireNonNull(zone);
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new SimulationClock(state, zone);
    }

    @Override
    public Instant instant() {
        synchronized (state) {
            return state.instant;
        }
    }

    // Cộng thêm 1 tháng
    public void advanceToNextMonth() {
        synchronized (state) {
            state.instant = state.instant
                    .atZone(zone)
                    .plusMonths(1)
                    .toInstant();
        }
    }
}