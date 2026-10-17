
package vn.edu.ptit.oop.nhom04.service;

import java.time.YearMonth;

public class TestSimulationClock {

    public static void main(String[] args) {

        SimulationClock clock =
            new SimulationClock(YearMonth.of(2026, 10));

        System.out.println(
            "Thang ban dau: " + clock.getCurrentMonth()
        );

        clock.nextMonth();

        System.out.println(
            "Sau lan 1: " + clock.getCurrentMonth()
        );

        clock.nextMonth();

        System.out.println(
            "Sau lan 2: " + clock.getCurrentMonth()
        );

        clock.nextMonth();

        System.out.println(
            "Sau lan 3: " + clock.getCurrentMonth()
        );
    }
}
