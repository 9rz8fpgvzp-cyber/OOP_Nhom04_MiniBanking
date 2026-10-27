package vn.edu.ptit.oop.nhom04.service;

import java.math.BigDecimal;
import java.time.*;
import java.nio.file.*;
import java.util.*;
import vn.edu.ptit.oop.nhom04.model.*;
import vn.edu.ptit.oop.nhom04.service.*;
import vn.edu.ptit.oop.nhom04.repository.*;
import vn.edu.ptit.oop.nhom04.exception.*;
import vn.edu.ptit.oop.nhom04.util.CsvStatementExporter;

public class BankIntegrationTest {
    static int checks;
    static BigDecimal money(long v){return BigDecimal.valueOf(v);}
    static final LocalDateTime OPEN=LocalDateTime.of(2026,10,1,9,0);
    static final Instant START=Instant.parse("2026-10-21T02:00:00Z");
    static final ZoneId ZONE=ZoneId.of("Asia/Ho_Chi_Minh");
    static void check(boolean value,String name){checks++;if(!value)throw new AssertionError(name);}
    static void rejects(Class<? extends Throwable> type,Runnable r){checks++;try{r.run();}catch(Throwable t){if(type.isInstance(t))return;throw new AssertionError(t);}throw new AssertionError("Expected "+type.getSimpleName());}
    static PinService pins(){PinService p=new PinService(Clock.fixed(START,ZONE));p.registerPin("A","001234".toCharArray());p.registerPin("B","234567".toCharArray());return p;}
    static void lock(PinService p,String id){for(int i=0;i<3;i++)try{p.authenticate(id,"999999".toCharArray());}catch(AccountLockedException e){}}
    static Bank bank(List<Account> accounts,PinService p,SimulationClock c){return new Bank(new InMemoryAccountRepository(accounts),p,c,new TransferService(),new MonthlyInterestService());}
    static class FailTarget extends CheckingAccount {
        boolean fail;
        FailTarget(Customer c){super("BT",c,money(1000),OPEN,money(0));}
        @Override public AccountType getType(){if(fail)throw new IllegalStateException("Lỗi lúc ghi giao dịch đích");return super.getType();}
    }
    static class FailInterest extends SavingsAccount {
        boolean fail=true;
        FailInterest(Customer c){super("BI",c,money(20000),OPEN,new BigDecimal("0.06"));}
        @Override public BigDecimal calculateMonthlyInterest(){if(fail)throw new IllegalStateException("Lỗi cộng lãi tài khoản thứ hai");return super.calculateMonthlyInterest();}
    }
    public static void main(String[] args)throws Exception{
        Customer a=new Customer("A","An","0900000001"),b=new Customer("B","Bình","0900000002");
        SavingsAccount sa=new SavingsAccount("AS",a,money(10000),OPEN,new BigDecimal("0.06"));
        CheckingAccount ca=new CheckingAccount("AC",a,money(10000),OPEN,money(1000));
        SavingsAccount sb=new SavingsAccount("BS",b,money(20000),OPEN,new BigDecimal("0.06"));
        PinService p=pins();SimulationClock c=new SimulationClock(START,ZONE);Bank bank=bank(List.of(sa,ca,sb),p,c);
        check(bank.getCurrentCustomer().isEmpty(),"No customer before login");check(bank.listAccounts().isEmpty(),"No accounts before login");
        check(p.authenticate("A","001234".toCharArray()),"Login A");
        check(bank.getCurrentCustomer().orElseThrow()==a,"Customer reference");
        check(bank.listAccounts().stream().map(AccountSummary::accountNumber).toList().equals(List.of("AS","AC")),"Only owned accounts and order");
        rejects(UnsupportedOperationException.class,()->bank.listAccounts().clear());
        check(bank.getAccount(" AS ")==sa && bank.getAccount("AC")==ca,"Switch same-owner accounts without PIN");
        for(String id:Arrays.asList(null,"","999"))rejects(AccountNotFoundException.class,()->bank.getAccount(id));
        BigDecimal before=sb.getBalance();int historyBefore=sb.getTransactions().size();
        rejects(AuthenticationRequiredException.class,()->bank.getAccount("BS"));
        rejects(AuthenticationRequiredException.class,()->bank.getTransactions("BS"));
        rejects(AuthenticationRequiredException.class,()->bank.deposit("BS",money(1)));
        rejects(AuthenticationRequiredException.class,()->bank.withdraw("BS",money(1)));
        rejects(AuthenticationRequiredException.class,()->bank.transfer("BS","AS",money(1)));
        check(sb.getBalance().equals(before)&&sb.getTransactions().size()==historyBefore,"Denied operations preserve target");
        for(BigDecimal amount:Arrays.asList(null,money(0),money(-1),new BigDecimal("0.5"))) {
            rejects(InvalidAmountException.class,()->bank.deposit("AC",amount));
            rejects(InvalidAmountException.class,()->bank.withdraw("AC",amount));
            rejects(InvalidAmountException.class,()->bank.transfer("AC","AS",amount));
        }
        bank.deposit("AC",new BigDecimal("1.00"));check(ca.getBalance().compareTo(money(10001))==0,"Whole VND with trailing zero accepted");
        bank.withdraw("AC",money(1));
        List<Transaction> snapshot=bank.getTransactions("AC");rejects(UnsupportedOperationException.class,snapshot::clear);
        bank.deposit("AC",money(1));check(snapshot.size()==2,"History snapshot stable");
        Path csv=Files.createTempFile("minibank-pr48-", ".csv");
        try{int rows=new CsvStatementExporter().export(csv,"AC",bank.getTransactions("AC"));check(rows==3,"Own CSV history permitted");
            String data=Files.readString(csv);rejects(AuthenticationRequiredException.class,()->{
                try{new CsvStatementExporter().export(csv,"BS",bank.getTransactions("BS"));}catch(java.io.IOException e){throw new RuntimeException(e);}});
            check(Files.readString(csv).equals(data),"Other-owner export denied before file changes");
        }finally{Files.delete(csv);}
        p.logout();check(bank.listAccounts().isEmpty()&&bank.getCurrentCustomer().isEmpty(),"Logout clears results");
        rejects(AuthenticationRequiredException.class,()->bank.getTransactions("AC"));
        lock(p,"B");check(p.isLocked("B"),"B locked");p.authenticate("A","001234".toCharArray());
        List<Transaction> pair=bank.transfer("AC","BS",money(100));
        check(pair.size()==2&&pair.get(0).getDirection()==TransactionDirection.OUT&&pair.get(1).getDirection()==TransactionDirection.IN,"Transfer order");
        check(pair.get(0).getReferenceId().equals(pair.get(1).getReferenceId())&&pair.get(0).getTimestamp().equals(pair.get(1).getTimestamp()),"Transfer linked records");
        p.logout();List<Transaction> interest=bank.simulateMonthEnd();check(interest.size()==2,"All savings get interest including locked B");
        check(LocalDateTime.now(c).equals(LocalDateTime.of(2026,11,1,0,0)),"First day of next month");
        for(Transaction t:interest)check(t.getTimestamp().equals(LocalDateTime.of(2026,10,31,23,59,59)),"Closing timestamp");

        PinService rollbackPins=pins();rollbackPins.authenticate("A","001234".toCharArray());
        SavingsAccount source=new SavingsAccount("AS",a,money(10000),OPEN,new BigDecimal("0.06"));FailTarget target=new FailTarget(b);
        Bank rb=bank(List.of(source,target),rollbackPins,new SimulationClock(START,ZONE));target.fail=true;
        rejects(IllegalStateException.class,()->rb.transfer("AS","BT",money(100)));
        check(source.getBalance().compareTo(money(10000))==0&&target.getBalance().compareTo(money(1000))==0,"Both balances restored after mid-transfer failure");
        check(source.getTransactions().isEmpty()&&target.getTransactions().isEmpty(),"Both histories restored");
        check(source.getRemainingWithdrawals(YearMonth.of(2026,10))==3,"Savings allowance restored");

        SavingsAccount first=new SavingsAccount("AI",a,money(10000),OPEN,new BigDecimal("0.06"));FailInterest second=new FailInterest(b);
        SimulationClock mc=new SimulationClock(START,ZONE);Bank mb=bank(List.of(first,second),pins(),mc);
        rejects(IllegalStateException.class,mb::simulateMonthEnd);check(mc.instant().equals(START),"No clock advance on partial interest failure");
        check(first.getTransactions().size()==1,"Earlier account interest committed once");second.fail=false;mb.simulateMonthEnd();
        check(first.getTransactions().size()==1&&second.getTransactions().size()==1,"Retry avoids duplicate interest");
        check(LocalDateTime.now(mc).equals(LocalDateTime.of(2026,11,1,0,0)),"Retry advances month");
        PinService lockedPins=pins();Bank lockedBank=bank(List.of(sa),lockedPins,new SimulationClock(START,ZONE));lock(lockedPins,"A");
        rejects(AccountLockedException.class,()->lockedBank.deposit("AS",money(1)));
        rejects(AccountLockedException.class,()->lockedBank.withdraw("AS",money(1)));
        rejects(AccountLockedException.class,()->lockedBank.getAccount("AS"));
        rejects(AccountLockedException.class,()->lockedBank.getTransactions("AS"));
        lockedPins.authenticate("B","234567".toCharArray());
        rejects(AuthenticationRequiredException.class,()->lockedBank.getAccount("AS"));
        rejects(AuthenticationRequiredException.class,()->lockedBank.withdraw("AS",money(1)));
        System.out.println("BankIntegrationTest: PASS ("+checks+" checks)");
    }
}
