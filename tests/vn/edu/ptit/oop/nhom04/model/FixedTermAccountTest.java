package vn.edu.ptit.oop.nhom04.model;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import vn.edu.ptit.oop.nhom04.model.*;
import vn.edu.ptit.oop.nhom04.exception.*;

public class FixedTermAccountTest {
    static int checks;
    static final Customer CUSTOMER=new Customer("KH001","An","0900000001");
    static final LocalDateTime OPEN=LocalDateTime.of(2026,10,1,9,0);
    static final LocalDate MATURITY=LocalDate.of(2027,1,21);
    static BigDecimal v(long n){return BigDecimal.valueOf(n);}
    static FixedTermAccount account(){return new FixedTermAccount("FT001",CUSTOMER,v(12000000),OPEN,new BigDecimal("0.06"),MATURITY);}
    static void check(boolean b,String name){checks++;if(!b)throw new AssertionError(name);}
    static RuntimeException rejects(Class<? extends RuntimeException> type,Runnable action){checks++;try{action.run();}catch(RuntimeException ex){if(type.isInstance(ex))return ex;throw new AssertionError(ex);}throw new AssertionError("Expected "+type.getSimpleName());}
    public static void main(String[] args){
        FixedTermAccount a=account();Account target=new CheckingAccount("CK001",CUSTOMER,v(0),OPEN,v(0));
        check(a.getType()==AccountType.FIXED_TERM,"correct type");
        check(a.calculateMonthlyInterest().compareTo(v(60000))==0,"monthly interest");
        RuntimeException early=rejects(AccountNotMaturedException.class,()->a.withdraw(v(1000),MATURITY.atStartOfDay().minusNanos(1)));
        check(early.getMessage().equals("Tài khoản chưa đáo hạn. Chỉ được rút hoặc chuyển tiền từ ngày 21/01/2027"),"accented error and formatted date");
        rejects(AccountNotMaturedException.class,()->a.transferTo(target,v(1000),MATURITY.minusDays(1).atTime(12,0)));
        check(a.getBalance().compareTo(v(12000000))==0&&target.getBalance().signum()==0,"immature withdrawal and transfer preserve balances");
        check(a.getTransactions().isEmpty()&&target.getTransactions().isEmpty(),"immature operations preserve histories");
        a.deposit(v(1000),MATURITY.minusDays(1).atTime(12,0));check(a.getBalance().compareTo(v(12001000))==0,"immature account may receive deposit");
        Transaction withdraw=a.withdraw(v(1000),MATURITY.atStartOfDay());
        check(withdraw.getType()==TransactionType.WITHDRAW&&a.getBalance().compareTo(v(12000000))==0,"withdraw allowed exactly maturity midnight");
        java.util.List<Transaction> transfer=a.transferTo(target,v(2000),MATURITY.atTime(12,0));
        check(transfer.size()==2&&a.getBalance().compareTo(v(11998000))==0&&target.getBalance().compareTo(v(2000))==0,"mature transfer works");
        RuntimeException funds=rejects(InsufficientFundsException.class,()->a.withdraw(v(12000000),MATURITY.plusDays(1).atStartOfDay()));
        check(funds.getMessage().equals("Số dư không đủ để thực hiện giao dịch"),"accented insufficient funds message");
        for(BigDecimal amount:Arrays.asList(null,v(0),v(-1),new BigDecimal("0.5"))) {
            rejects(InvalidAmountException.class,()->a.withdraw(amount,MATURITY.atStartOfDay()));
            rejects(InvalidAmountException.class,()->a.transferTo(target,amount,MATURITY.atStartOfDay()));
        }
        rejects(NullPointerException.class,()->a.withdraw(v(1),null));
        BigDecimal before=a.getBalance();int history=a.getTransactions().size();
        a.withdraw(new BigDecimal("1000.00"),MATURITY.plusDays(1).atStartOfDay());
        check(a.getBalance().compareTo(before.subtract(v(1000)))==0&&a.getTransactions().size()==history+1,"whole money with trailing zeros and one history entry");
        RuntimeException missingRate=rejects(IllegalArgumentException.class,()->new FixedTermAccount("F",CUSTOMER,v(1),OPEN,null,MATURITY));
        check(missingRate.getMessage().equals("Lãi suất năm không được để trống"),"null rate message");
        RuntimeException negativeRate=rejects(IllegalArgumentException.class,()->new FixedTermAccount("F",CUSTOMER,v(1),OPEN,new BigDecimal("-0.01"),MATURITY));
        check(negativeRate.getMessage().equals("Lãi suất năm không được âm"),"negative rate message");
        rejects(NullPointerException.class,()->new FixedTermAccount("F",CUSTOMER,v(1),OPEN,BigDecimal.ZERO,null));
        rejects(IllegalArgumentException.class,()->new FixedTermAccount("F",CUSTOMER,v(1),OPEN,BigDecimal.ZERO,OPEN.toLocalDate().minusDays(1)));
        FixedTermAccount zero=new FixedTermAccount("F",CUSTOMER,v(1),OPEN,BigDecimal.ZERO,OPEN.toLocalDate());
        check(zero.calculateMonthlyInterest().signum()==0,"zero rate accepted");
        check(new FixedTermAccount("Z",CUSTOMER,v(0),OPEN,new BigDecimal("0.07"),MATURITY).calculateMonthlyInterest().signum()==0,"zero balance gives zero interest");
        FixedTermAccount tiny=new FixedTermAccount("T",CUSTOMER,v(100),OPEN,new BigDecimal("0.06"),MATURITY);
        check(tiny.calculateMonthlyInterest().compareTo(v(1))==0,"half-up whole VND interest");
        BigDecimal interest=a.calculateMonthlyInterest();LocalDateTime close=LocalDateTime.of(2026,10,31,23,59,59);
        check(a.creditInterest(interest,YearMonth.of(2026,10),close).isPresent(),"interest credited before maturity");
        check(a.creditInterest(interest,YearMonth.of(2026,10),close).isEmpty(),"same interest period not credited twice");
        System.out.println("FixedTermAccountTest: PASS ("+checks+" checks)");
    }
}
