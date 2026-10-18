package vn.edu.ptit.oop.nhom04.test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import vn.edu.ptit.oop.nhom04.exception.AccountLockedException;
import vn.edu.ptit.oop.nhom04.exception.AccountNotFoundException;
import vn.edu.ptit.oop.nhom04.exception.AuthenticationRequiredException;
import vn.edu.ptit.oop.nhom04.exception.InsufficientFundsException;
import vn.edu.ptit.oop.nhom04.exception.InvalidAmountException;
import vn.edu.ptit.oop.nhom04.exception.InvalidTransferException;
import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.AccountSummary;
import vn.edu.ptit.oop.nhom04.model.CheckingAccount;
import vn.edu.ptit.oop.nhom04.model.Customer;
import vn.edu.ptit.oop.nhom04.model.SavingsAccount;
import vn.edu.ptit.oop.nhom04.model.Transaction;
import vn.edu.ptit.oop.nhom04.model.TransactionDirection;
import vn.edu.ptit.oop.nhom04.model.TransactionType;
import vn.edu.ptit.oop.nhom04.service.Bank;
import vn.edu.ptit.oop.nhom04.service.MonthlyInterestService;
import vn.edu.ptit.oop.nhom04.service.PinService;
import vn.edu.ptit.oop.nhom04.service.SimulationClock;
import vn.edu.ptit.oop.nhom04.service.TransferService;

/**
 * Bộ kiểm thử toàn diện nghiệp vụ Bank và TransferService (Người 5).
 */
public class BankTest {

    private static int passedTests = 0;
    private static int totalTests = 0;

    private static void assertTrue(boolean condition, String testName) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName);
        }
    }

    private static void assertEquals(Object expected, Object actual, String testName) {
        totalTests++;
        if ((expected == null && actual == null) || (expected != null && expected.equals(actual))) {
            passedTests++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU KIỂM THỬ NGHIỆP VỤ BANK & TRANSFERSERVICE (NGƯỜI 5) ===");

        testBankInitialization();
        testAuthenticationRequirements();
        testDepositOperations();
        testWithdrawOperations();
        testAtomicTransfer();
        testSimulationMonthEnd();

        System.out.println("===================================================================");
        System.out.println("KẾT QUẢ: " + passedTests + "/" + totalTests + " kiểm thử thành công!");
        if (passedTests == totalTests) {
            System.out.println(">>> TẤT CẢ CÁC CA KIỂM THỬ ĐÃ ĐẠT CHUẨN ĐẶC TẢ! <<<");
        } else {
            System.exit(1);
        }
    }

    private static void testBankInitialization() {
        System.out.println("\n1. Kiểm thử khởi tạo Bank:");
        Customer c1 = new Customer("C01", "Nguyen Van A", "0901234567");
        LocalDateTime now = LocalDateTime.now();
        Account a1 = new SavingsAccount("SA01", c1, new BigDecimal("1000000"), now, new BigDecimal("0.06"));
        Account a2 = new SavingsAccount("SA01", c1, new BigDecimal("500000"), now, new BigDecimal("0.06"));

        SimulationClock clock = new SimulationClock(Instant.now(), ZoneId.systemDefault());
        PinService pins = new PinService();
        TransferService transfers = new TransferService();
        MonthlyInterestService interestService = new MonthlyInterestService();

        boolean duplicateCaught = false;
        try {
            new Bank(List.of(a1, a2), pins, clock, transfers, interestService);
        } catch (IllegalArgumentException e) {
            duplicateCaught = true;
        }
        assertTrue(duplicateCaught, "Chặn tạo Bank khi trùng số tài khoản");

        Bank bank = new Bank(List.of(a1), pins, clock, transfers, interestService);
        List<AccountSummary> summaries = bank.listAccounts();
        assertEquals(1, summaries.size(), "listAccounts trả đúng số lượng tài khoản");
        assertEquals("SA01", summaries.get(0).accountNumber(), "listAccounts trả đúng số tài khoản");
    }

    private static void testAuthenticationRequirements() {
        System.out.println("\n2. Kiểm thử bảo vệ xác thực PIN (Người 5 gọi PinService):");
        Customer c1 = new Customer("C01", "Nguyen Van A", "0901234567");
        LocalDateTime now = LocalDateTime.now();
        Account a1 = new SavingsAccount("SA01", c1, new BigDecimal("1000000"), now, new BigDecimal("0.06"));

        SimulationClock clock = new SimulationClock(Instant.now(), ZoneId.systemDefault());
        PinService pins = new PinService();
        pins.registerPin("SA01", "123456".toCharArray());

        TransferService transfers = new TransferService();
        MonthlyInterestService interestService = new MonthlyInterestService();
        Bank bank = new Bank(List.of(a1), pins, clock, transfers, interestService);

        boolean authCaught = false;
        try {
            bank.getAccount("SA01");
        } catch (AuthenticationRequiredException e) {
            authCaught = true;
        }
        assertTrue(authCaught, "getAccount từ chối khi chưa đăng nhập PIN");

        authCaught = false;
        try {
            bank.deposit("SA01", new BigDecimal("50000"));
        } catch (AuthenticationRequiredException e) {
            authCaught = true;
        }
        assertTrue(authCaught, "deposit từ chối khi chưa đăng nhập PIN");

        // Đăng nhập đúng PIN
        pins.authenticate("SA01", "123456".toCharArray());
        assertEquals("SA01", bank.getAccount("SA01").getAccountNumber(), "getAccount thành công sau khi xác thực PIN");
        pins.logout();
    }

    private static void testDepositOperations() {
        System.out.println("\n3. Kiểm thử nghiệp vụ nạp tiền (Deposit):");
        Customer c1 = new Customer("C01", "Nguyen Van A", "0901234567");
        LocalDateTime now = LocalDateTime.now();
        Account a1 = new SavingsAccount("SA02", c1, new BigDecimal("500000"), now, new BigDecimal("0.06"));

        SimulationClock clock = new SimulationClock(Instant.now(), ZoneId.systemDefault());
        PinService pins = new PinService();
        pins.registerPin("SA02", "111111".toCharArray());
        pins.authenticate("SA02", "111111".toCharArray());

        Bank bank = new Bank(List.of(a1), pins, clock, new TransferService(), new MonthlyInterestService());

        // Kiểm tra tiền lẻ bị từ chối
        boolean invalidAmountCaught = false;
        try {
            bank.deposit("SA02", new BigDecimal("1000.5"));
        } catch (InvalidAmountException e) {
            invalidAmountCaught = true;
        }
        assertTrue(invalidAmountCaught, "deposit từ chối tiền có phần lẻ");

        // Nạp tiền hợp lệ
        Transaction tx = bank.deposit("SA02", new BigDecimal("200000"));
        assertEquals(new BigDecimal("700000"), a1.getBalance(), "Số dư sau nạp tăng chính xác");
        assertEquals(TransactionType.DEPOSIT, tx.getType(), "Loại giao dịch là DEPOSIT");
        assertEquals(new BigDecimal("200000"), tx.getAmount(), "Số tiền giao dịch khớp");
        pins.logout();
    }

    private static void testWithdrawOperations() {
        System.out.println("\n4. Kiểm thử nghiệp vụ rút tiền (Withdraw):");
        Customer c1 = new Customer("C01", "Nguyen Van A", "0901234567");
        LocalDateTime now = LocalDateTime.now();
        Account a1 = new SavingsAccount("SA03", c1, new BigDecimal("300000"), now, new BigDecimal("0.06"));

        SimulationClock clock = new SimulationClock(Instant.now(), ZoneId.systemDefault());
        PinService pins = new PinService();
        pins.registerPin("SA03", "222222".toCharArray());
        pins.authenticate("SA03", "222222".toCharArray());

        Bank bank = new Bank(List.of(a1), pins, clock, new TransferService(), new MonthlyInterestService());

        // Rút quá số dư
        boolean insufficientCaught = false;
        try {
            bank.withdraw("SA03", new BigDecimal("500000"));
        } catch (InsufficientFundsException e) {
            insufficientCaught = true;
        }
        assertTrue(insufficientCaught, "Rút quá số dư bị ném InsufficientFundsException");

        // Rút hợp lệ
        Transaction tx = bank.withdraw("SA03", new BigDecimal("100000"));
        assertEquals(new BigDecimal("200000"), a1.getBalance(), "Số dư sau rút giảm chính xác");
        assertEquals(TransactionType.WITHDRAW, tx.getType(), "Loại giao dịch là WITHDRAW");
        pins.logout();
    }

    private static void testAtomicTransfer() {
        System.out.println("\n5. Kiểm thử chuyển khoản toàn vẹn (Atomic Transfer):");
        Customer c1 = new Customer("C01", "Nguyen Van A", "0901234567");
        Customer c2 = new Customer("C02", "Tran Thi B", "0987654321");
        LocalDateTime now = LocalDateTime.now();

        Account src = new SavingsAccount("SRC01", c1, new BigDecimal("1000000"), now, new BigDecimal("0.06"));
        Account tgt = new CheckingAccount("TGT01", c2, new BigDecimal("200000"), now, new BigDecimal("500000"));

        SimulationClock clock = new SimulationClock(Instant.now(), ZoneId.systemDefault());
        PinService pins = new PinService();
        pins.registerPin("SRC01", "333333".toCharArray());
        pins.registerPin("TGT01", "444444".toCharArray());

        Bank bank = new Bank(List.of(src, tgt), pins, clock, new TransferService(), new MonthlyInterestService());

        // Chưa đăng nhập nguồn
        boolean authCaught = false;
        try {
            bank.transfer("SRC01", "TGT01", new BigDecimal("100000"));
        } catch (AuthenticationRequiredException e) {
            authCaught = true;
        }
        assertTrue(authCaught, "Chuyển khoản từ chối nếu nguồn chưa xác thực PIN");

        pins.authenticate("SRC01", "333333".toCharArray());

        // Chuyển cho chính mình
        boolean sameCaught = false;
        try {
            bank.transfer("SRC01", "SRC01", new BigDecimal("100000"));
        } catch (InvalidTransferException e) {
            sameCaught = true;
        }
        assertTrue(sameCaught, "Chuyển cho chính mình bị từ chối");

        // Chuyển vượt quá số dư nguồn
        boolean overCaught = false;
        try {
            bank.transfer("SRC01", "TGT01", new BigDecimal("2000000"));
        } catch (InsufficientFundsException e) {
            overCaught = true;
        }
        assertTrue(overCaught, "Chuyển vượt quá số dư bị từ chối");
        assertEquals(new BigDecimal("1000000"), src.getBalance(), "Toàn vẹn: Số dư nguồn giữ nguyên");
        assertEquals(new BigDecimal("200000"), tgt.getBalance(), "Toàn vẹn: Số dư đích giữ nguyên");
        assertEquals(0, src.getTransactions().size(), "Toàn vẹn: Lịch sử nguồn không có giao dịch dở dang");
        assertEquals(0, tgt.getTransactions().size(), "Toàn vẹn: Lịch sử đích không có giao dịch dở dang");

        // Chuyển khoản hợp lệ 300,000 VND
        List<Transaction> txs = bank.transfer("SRC01", "TGT01", new BigDecimal("300000"));
        assertEquals(2, txs.size(), "Trả về đúng 2 giao dịch [OUT nguồn, IN đích]");
        assertEquals(new BigDecimal("700000"), src.getBalance(), "Số dư nguồn sau chuyển giảm còn 700,000");
        assertEquals(new BigDecimal("500000"), tgt.getBalance(), "Số dư đích sau chuyển tăng lên 500,000");

        Transaction txOut = txs.get(0);
        Transaction txIn = txs.get(1);
        assertEquals(TransactionDirection.OUT, txOut.getDirection(), "Giao dịch 1 là hướng OUT");
        assertEquals(TransactionDirection.IN, txIn.getDirection(), "Giao dịch 2 là hướng IN");
        assertEquals(txOut.getReferenceId(), txIn.getReferenceId(), "Hai giao dịch dùng chung referenceId");
        pins.logout();
    }

    private static void testSimulationMonthEnd() {
        System.out.println("\n6. Kiểm thử mô phỏng cuối tháng (Simulate Month End):");
        Customer c1 = new Customer("C01", "Nguyen Van A", "0901234567");
        LocalDateTime now = LocalDateTime.now();
        // 1,200,000 với 6%/năm -> 1,200,000 * 0.06 / 12 = 6,000 VND lãi
        SavingsAccount sa = new SavingsAccount("SA05", c1, new BigDecimal("1200000"), now, new BigDecimal("0.06"));

        SimulationClock clock = new SimulationClock(Instant.now(), ZoneId.systemDefault());
        PinService pins = new PinService();
        pins.registerPin("SA05", "555555".toCharArray());

        Bank bank = new Bank(List.of(sa), pins, clock, new TransferService(), new MonthlyInterestService());

        List<Transaction> interestTxs = bank.simulateMonthEnd();
        assertEquals(1, interestTxs.size(), "Tạo đúng 1 giao dịch lãi cho tài khoản tiết kiệm");
        assertEquals(new BigDecimal("6000"), interestTxs.get(0).getAmount(), "Số tiền lãi chính xác là 6,000 VND");
        assertEquals(new BigDecimal("1206000"), sa.getBalance(), "Số dư sau cộng lãi chính xác");
        assertTrue(interestTxs.get(0).getDescription().startsWith("INTEREST:"), "Mô tả giao dịch bắt đầu bằng INTEREST:yyyy-MM");
    }
}
