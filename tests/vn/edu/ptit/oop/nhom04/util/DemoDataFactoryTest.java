package vn.edu.ptit.oop.nhom04.util;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import vn.edu.ptit.oop.nhom04.model.*;
import vn.edu.ptit.oop.nhom04.service.*;
import vn.edu.ptit.oop.nhom04.util.DemoDataFactory;
import vn.edu.ptit.oop.nhom04.exception.*;

public class DemoDataFactoryTest {
    static int checks;
    static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-21T02:00:00Z"), ZoneId.of("Asia/Ho_Chi_Minh"));
    static void check(boolean b, String m) { checks++; if (!b) throw new AssertionError(m); }
    static void rejects(Class<? extends Throwable> type, Runnable r) {
        checks++; try { r.run(); } catch (Throwable t) { if (type.isInstance(t)) return; throw new AssertionError(t); }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
    static class Recording extends PinService {
        List<String> ids = new ArrayList<>(); List<char[]> buffers = new ArrayList<>(); boolean fail;
        Recording() { super(CLOCK); }
        public synchronized void registerPin(String id, char[] pin) {
            ids.add(id); buffers.add(pin); if (fail) throw new IllegalStateException(); super.registerPin(id,pin);
        }
    }
    public static void main(String[] args) {
        for (Instant instant : List.of(CLOCK.instant(), Instant.parse("2027-01-01T00:00:00Z"), Instant.parse("2024-03-01T00:00:00Z"))) {
            Clock c = Clock.fixed(instant, CLOCK.getZone());
            List<Account> accounts = DemoDataFactory.createAccounts(c);
            check(accounts.size()==6,"6 accounts");
            check(accounts.stream().map(a->a.getOwner().getCustomerId()).distinct().count()==3,"3 customers");
            check(accounts.stream().map(Account::getType).distinct().count()==3,"3 types");
            check(accounts.stream().map(Account::getAccountNumber).distinct().count()==6,"unique numbers");
            boolean matured=false, unmatured=false;
            for(Account a:accounts) {
                BigDecimal balance=a.getOpeningBalance(); LocalDateTime previous=a.getOpenedAt();
                for(Transaction t:a.getTransactions()) {
                    check(!t.getTimestamp().isBefore(previous) && !t.getTimestamp().isAfter(LocalDateTime.now(c)),"time ordering");
                    check(t.getAccountNumber().equals(a.getAccountNumber()),"history owner");
                    balance=t.getType()==TransactionType.WITHDRAW||t.getDirection()==TransactionDirection.OUT ? balance.subtract(t.getAmount()):balance.add(t.getAmount());
                    check(balance.compareTo(t.getBalanceAfter())==0,"balanceAfter"); previous=t.getTimestamp();
                }
                check(balance.compareTo(a.getBalance())==0,"final balance");
                if(a instanceof FixedTermAccount f) {
                    matured |= !f.getMaturityDate().isAfter(LocalDate.now(c)); unmatured |= f.getMaturityDate().isAfter(LocalDate.now(c));
                }
            }
            check(matured&&unmatured,"both maturity cases");
            check(accounts.stream().map(Account::getBalance).reduce(BigDecimal.ZERO,BigDecimal::add).compareTo(new BigDecimal("97500000"))==0,"new total");
            rejects(UnsupportedOperationException.class,accounts::clear);
        }
        List<Account> accounts=DemoDataFactory.createAccounts(CLOCK);
        Recording pins=new Recording(); DemoDataFactory.registerDemoPins(pins,accounts);
        check(pins.ids.equals(List.of("KH001","KH002","KH003")),"one registration per customer");
        check(pins.getAuthenticatedCustomerId().isEmpty(),"no automatic login");
        for(char[] buffer:pins.buffers) for(char v:buffer) check(v=='\0',"PIN buffer wiped");
        Map<String,String> known=Map.of("KH001","001234","KH002","234567","KH003","456789");
        for(var e:known.entrySet()) check(pins.authenticate(e.getKey(),e.getValue().toCharArray()),"customer PIN matches");
        for(Account a:accounts) rejects(AccountNotFoundException.class,()->pins.authenticate(a.getAccountNumber(),"001234".toCharArray()));
        Recording invalid=new Recording();
        rejects(NullPointerException.class,()->DemoDataFactory.registerDemoPins(null,accounts));
        rejects(NullPointerException.class,()->DemoDataFactory.registerDemoPins(invalid,null));
        rejects(IllegalArgumentException.class,()->DemoDataFactory.registerDemoPins(invalid,Arrays.asList(accounts.get(0),null)));
        rejects(IllegalArgumentException.class,()->DemoDataFactory.registerDemoPins(invalid,List.of(accounts.get(0),accounts.get(0))));
        Customer conflict=new Customer("KH001","Tên khác","0900000000");
        Account conflictAccount=new CheckingAccount("999998",conflict,BigDecimal.ZERO,LocalDateTime.now(CLOCK),BigDecimal.ZERO);
        rejects(IllegalArgumentException.class,()->DemoDataFactory.registerDemoPins(invalid,List.of(accounts.get(0),conflictAccount)));
        Account unknown=new CheckingAccount("999999",new Customer("KH999","Khác","0900000000"),BigDecimal.ZERO,LocalDateTime.now(CLOCK),BigDecimal.ZERO);
        rejects(IllegalArgumentException.class,()->DemoDataFactory.registerDemoPins(invalid,List.of(accounts.get(0),unknown)));
        check(invalid.ids.isEmpty(),"invalid input rejected before any registration");
        Recording failing=new Recording(); failing.fail=true;
        rejects(IllegalStateException.class,()->DemoDataFactory.registerDemoPins(failing,accounts));
        for(char[] buffer:failing.buffers) for(char v:buffer) check(v=='\0',"buffer wiped on service failure");
        rejects(IllegalArgumentException.class,()->DemoDataFactory.registerDemoPins(pins,accounts));
        System.out.println("DemoDataFactoryTest: PASS ("+checks+" checks)");

    }
}
