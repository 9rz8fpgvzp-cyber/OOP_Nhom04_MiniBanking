package vn.edu.ptit.oop.nhom04.repository;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.*;
import vn.edu.ptit.oop.nhom04.model.*;
import vn.edu.ptit.oop.nhom04.repository.*;

public class AccountRepositoryTest {
    static int checks;
    static final LocalDateTime OPEN=LocalDateTime.of(2026,10,1,9,0);
    static void check(boolean value,String name){checks++;if(!value)throw new AssertionError(name);}
    static void rejects(Class<? extends Throwable> type,Runnable r){checks++;try{r.run();}catch(Throwable t){if(type.isInstance(t))return;throw new AssertionError(t);}throw new AssertionError("Expected "+type.getSimpleName());}
    static Account account(String number,Customer owner){return new CheckingAccount(number,owner,new BigDecimal("1000"),OPEN,BigDecimal.ZERO);}
    public static void main(String[] args){
        Customer a=new Customer("KH001","An","0900000001"),b=new Customer("KH002","Bình","0900000002");
        Account a1=account("100001",a),b1=account("100002",b),a2=account("100003",a);
        List<Account> input=new ArrayList<>(List.of(a1,b1,a2));
        AccountRepository repository=new InMemoryAccountRepository(input);
        check(repository.findAll().equals(List.of(a1,b1,a2)),"stable bootstrap order");
        check(repository.findByCustomerId("KH001").equals(List.of(a1,a2)),"multiple accounts per owner in original order");
        check(repository.findByCustomerId("KH002").equals(List.of(b1)),"other owner correct");
        check(repository.findByCustomerId(" KH001 ").equals(List.of(a1,a2)),"customer lookup trims input");
        check(repository.findByNumber("100001").orElseThrow()==a1,"same reference by number");
        check(repository.findByNumber(" 100001 ").orElseThrow()==a1,"number lookup trims input");
        for(String unknown:Arrays.asList(null,"","  ","999999"))check(repository.findByNumber(unknown).isEmpty(),"unknown account empty");
        for(String unknown:Arrays.asList(null,"","  ","KH999","kh001"))check(repository.findByCustomerId(unknown).isEmpty(),"unknown/blank/case-sensitive customer empty");
        check(repository.findAll().size()==3,"unknown lookups create no data");
        rejects(UnsupportedOperationException.class,()->repository.findAll().clear());
        rejects(UnsupportedOperationException.class,()->repository.findAll().set(0,b1));
        rejects(UnsupportedOperationException.class,()->repository.findByCustomerId("KH001").add(b1));
        rejects(UnsupportedOperationException.class,()->repository.findByCustomerId("KH001").remove(0));
        input.clear();input.add(account("999999",a));
        check(repository.findAll().equals(List.of(a1,b1,a2)),"input list mutation does not alter repository");
        check(repository.findByNumber("999999").isEmpty(),"later input additions ignored");
        List<Account> all=repository.findAll(),owned=repository.findByCustomerId("KH001");
        a1.deposit(new BigDecimal("100"),OPEN.plusDays(1));
        check(repository.findByNumber("100001").orElseThrow().getBalance().compareTo(new BigDecimal("1100"))==0,"live balance by number");
        check(all.get(0).getBalance().compareTo(new BigDecimal("1100"))==0,"same live account in all snapshot");
        check(owned.get(0).getTransactions().size()==1,"same live history in owner snapshot");
        check(repository.findByCustomerId("KH001").get(0)==a1,"owner lookup does not clone models");
        rejects(NullPointerException.class,()->new InMemoryAccountRepository(null));
        rejects(IllegalArgumentException.class,()->new InMemoryAccountRepository(Arrays.asList(a1,null)));
        rejects(IllegalArgumentException.class,()->new InMemoryAccountRepository(List.of(a1,a1)));
        rejects(IllegalArgumentException.class,()->new InMemoryAccountRepository(List.of(a1,account(" 100001 ",b))));
        AccountRepository empty=new InMemoryAccountRepository(List.of());
        check(empty.findAll().isEmpty(),"empty bootstrap accepted");
        check(empty.findByNumber("100001").isEmpty(),"empty repository number lookup");
        check(empty.findByCustomerId("KH001").isEmpty(),"empty repository customer lookup");
        check(new InMemoryAccountRepository(List.of(a1,a2)).findByCustomerId("KH001").size()==2,"shared owner allowed");
        System.out.println("AccountRepositoryTest: PASS ("+checks+" checks)");
    }
}
