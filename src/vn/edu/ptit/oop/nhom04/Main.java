package vn.edu.ptit.oop.nhom04;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import javax.swing.SwingUtilities;
import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.service.Bank;
import vn.edu.ptit.oop.nhom04.service.MonthlyInterestService;
import vn.edu.ptit.oop.nhom04.service.PinService;
import vn.edu.ptit.oop.nhom04.service.SimulationClock;
import vn.edu.ptit.oop.nhom04.service.StatisticsService;
import vn.edu.ptit.oop.nhom04.service.TransferService;
import vn.edu.ptit.oop.nhom04.ui.BankController;
import vn.edu.ptit.oop.nhom04.ui.MainWindow;
import vn.edu.ptit.oop.nhom04.util.ChartHelper;
import vn.edu.ptit.oop.nhom04.util.DemoDataFactory;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulationClock clock = new SimulationClock(
                    Instant.parse("2026-10-21T02:00:00Z"), ZoneId.of("Asia/Ho_Chi_Minh"));
            PinService pins = new PinService();
            List<Account> accounts = DemoDataFactory.createAccounts(clock);
            DemoDataFactory.registerDemoPins(pins, accounts);
            Bank bank = new Bank(accounts, pins, clock,
                    new TransferService(), new MonthlyInterestService());
            MainWindow window = new MainWindow();
            BankController controller = new BankController(window, bank, pins, clock,
                    new ChartHelper(new StatisticsService()));
            controller.start();
            window.setVisible(true);
        });
    }
}
