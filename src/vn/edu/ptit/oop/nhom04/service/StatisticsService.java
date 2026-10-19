package vn.edu.ptit.oop.nhom04.service;

import vn.edu.ptit.oop.nhom04.model.Transaction;
import vn.edu.ptit.oop.nhom04.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatisticsService {

    public StatisticsService() {
    }

    public BigDecimal totalDeposits(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.DEPOSIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalWithdrawals(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.WITHDRAW)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<LocalDate, BigDecimal> depositsByDay(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<LocalDate, BigDecimal> map = new TreeMap<>();
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.DEPOSIT) {
                LocalDate date = t.getTimestamp().toLocalDate();
                map.merge(date, t.getAmount(), BigDecimal::add);
            }
        }
        return map;
    }

    public Map<LocalDate, BigDecimal> withdrawalsByDay(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<LocalDate, BigDecimal> map = new TreeMap<>();
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.WITHDRAW) {
                LocalDate date = t.getTimestamp().toLocalDate();
                map.merge(date, t.getAmount(), BigDecimal::add);
            }
        }
        return map;
    }
}