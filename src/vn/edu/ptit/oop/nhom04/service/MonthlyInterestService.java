package vn.edu.ptit.oop.nhom04.service;
import java.time.*;
import java.util.*;
import vn.edu.ptit.oop.nhom04.model.*;

public class MonthlyInterestService {
    public MonthlyInterestService() {}
    public List<Transaction> applyMonthEnd(List<Account> accounts, YearMonth closingMonth, LocalDateTime closingTime) {
        Objects.requireNonNull(closingMonth); Objects.requireNonNull(closingTime);
        if (!YearMonth.from(closingTime).equals(closingMonth)) throw new IllegalArgumentException("Chưa đến kỳ chốt");
        List<Account> snapshot = List.copyOf(accounts);
        List<Transaction> result = new ArrayList<>();
        for (Account account : snapshot) {
            //Kiem tra tai khoan co ho tro co tinh lai khong
            if (account instanceof InterestBearing bearing)
                account.creditInterest(bearing.calculateMonthlyInterest(), closingMonth, closingTime).ifPresent(result::add);
        }
        //Tra ket qua danh sach tai khoan
        return List.copyOf(result);
    }
}
