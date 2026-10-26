package vn.edu.ptit.oop.nhom04.test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import vn.edu.ptit.oop.nhom04.exception.*;
import vn.edu.ptit.oop.nhom04.model.*;
import vn.edu.ptit.oop.nhom04.repository.InMemoryAccountRepository;
import vn.edu.ptit.oop.nhom04.service.*;

public class BankV2Test {
    static int pass = 0, fail = 0;

    static void check(String name, boolean ok) {
        System.out.println((ok ? "  [PASS] " : "  [FAIL] ") + name);
        if (ok) pass++; else fail++;
    }

    static boolean throwsEx(Class<? extends Throwable> type, Runnable r) {
        try { r.run(); return false; }
        catch (Throwable t) { return type.isInstance(t); }
    }

    static BigDecimal vnd(long v) { return BigDecimal.valueOf(v); }

    public static void main(String[] args) {
        SimulationClock clock = new SimulationClock(
                Instant.parse("2026-10-16T02:00:00Z"), ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime opened = LocalDateTime.of(2026, 10, 1, 9, 0);

        Customer a = new Customer("KH001", "An", "0900000001");
        Customer b = new Customer("KH002", "Binh", "0900000002");
        Account accA = new SavingsAccount("100001", a, vnd(5_000_000), opened, new BigDecimal("0.06"));
        Account accB = new CheckingAccount("200001", b, vnd(1_000_000), opened, vnd(500_000));

        PinService pins = new PinService(Clock.systemUTC());
        pins.registerPin("KH001", "012345".toCharArray());
        pins.registerPin("KH002", "654321".toCharArray());

        Bank bank = new Bank(new InMemoryAccountRepository(List.of(accA, accB)), pins, clock,
                new TransferService(), new MonthlyInterestService());

        System.out.println("1. Chua dang nhap:");
        check("getCurrentCustomer rong", bank.getCurrentCustomer().isEmpty());
        check("listAccounts rong", bank.listAccounts().isEmpty());
        check("getAccount bi chan", throwsEx(AuthenticationRequiredException.class,
                () -> bank.getAccount("100001")));
        check("deposit bi chan", throwsEx(AuthenticationRequiredException.class,
                () -> bank.deposit("100001", vnd(1000))));

        for (int i = 0; i < 3; i++) {
            try { pins.authenticate("KH002", "000000".toCharArray()); }
            catch (AccountLockedException ignored) { }
        }
        check("KH002 da bi khoa", pins.isLocked("KH002"));
        check("dang nhap KH001 dung PIN so 0 dau", pins.authenticate("KH001", "012345".toCharArray()));

        System.out.println("2. Da dang nhap KH001:");
        check("getCurrentCustomer la KH001",
                bank.getCurrentCustomer().map(Customer::getCustomerId).orElse("").equals("KH001"));
        check("chi thay STK cua minh",
                bank.listAccounts().size() == 1 && bank.listAccounts().get(0).accountNumber().equals("100001"));

        System.out.println("3. Truy cap STK nguoi khac:");
        BigDecimal balB = accB.getBalance();
        int histB = accB.getTransactions().size();
        check("xem bi chan", throwsEx(AuthenticationRequiredException.class, () -> bank.getAccount("200001")));
        check("lich su bi chan", throwsEx(AuthenticationRequiredException.class, () -> bank.getTransactions("200001")));
        check("nap bi chan", throwsEx(AuthenticationRequiredException.class, () -> bank.deposit("200001", vnd(1000))));
        check("rut bi chan", throwsEx(AuthenticationRequiredException.class, () -> bank.withdraw("200001", vnd(1000))));
        check("chuyen tu nguon nguoi khac bi chan", throwsEx(AuthenticationRequiredException.class,
                () -> bank.transfer("200001", "100001", vnd(1000))));
        check("so du/lich su KH002 khong doi",
                accB.getBalance().compareTo(balB) == 0 && accB.getTransactions().size() == histB);

        System.out.println("4. Chuyen khoan:");
        List<Transaction> tx = bank.transfer("100001", "200001", vnd(1_000_000));
        check("chuyen sang chu khac (dang khoa) thanh cong", tx.size() == 2);
        check("nguon giam 1.000.000", accA.getBalance().compareTo(vnd(4_000_000)) == 0);
        check("dich tang 1.000.000", accB.getBalance().compareTo(vnd(2_000_000)) == 0);
        check("hai ban ghi cung referenceId", tx.get(0).getReferenceId().equals(tx.get(1).getReferenceId()));

        BigDecimal balA = accA.getBalance();
        int histA = accA.getTransactions().size();
        check("dich khong ton tai", throwsEx(AccountNotFoundException.class,
                () -> bank.transfer("100001", "999999", vnd(1000))));
        check("cung STK", throwsEx(InvalidTransferException.class,
                () -> bank.transfer("100001", "100001", vnd(1000))));
        check("tien 0", throwsEx(InvalidAmountException.class, () -> bank.transfer("100001", "200001", vnd(0))));
        check("tien am", throwsEx(InvalidAmountException.class, () -> bank.transfer("100001", "200001", vnd(-5))));
        check("tien co phan le", throwsEx(InvalidAmountException.class,
                () -> bank.transfer("100001", "200001", new BigDecimal("1000.5"))));
        check("nguon khong doi sau cac loi tren",
                accA.getBalance().compareTo(balA) == 0 && accA.getTransactions().size() == histA);

        System.out.println("5. Cuoi thang khong can PIN cua KH002 (dang khoa):");
        check("simulateMonthEnd chay duoc", !bank.simulateMonthEnd().isEmpty());

        System.out.println("\nKET QUA: " + pass + " dat, " + fail + " loi");
    }
}