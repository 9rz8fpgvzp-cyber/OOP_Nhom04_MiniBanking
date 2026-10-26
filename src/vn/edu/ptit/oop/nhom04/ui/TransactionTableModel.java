package vn.edu.ptit.oop.nhom04.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

import javax.swing.table.AbstractTableModel;

import vn.edu.ptit.oop.nhom04.model.Transaction;
import vn.edu.ptit.oop.nhom04.model.TransactionDirection;
import vn.edu.ptit.oop.nhom04.model.TransactionType;

public class TransactionTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNS =
            {"Thời gian", "Loại", "Chiều", "Số tiền (VND)", "Số dư sau (VND)", "Mô tả"};
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MM/yyyy");
    private static final String INTEREST_PREFIX = "INTEREST:";

    private List<Transaction> transactions = List.of();

    public TransactionTableModel() {
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = (transactions == null) ? List.of() : List.copyOf(transactions);
        fireTableDataChanged();
    }

    public void clear() {
        transactions = List.of();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return transactions.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Transaction t = transactions.get(rowIndex);
        switch (columnIndex) {
            case 0: return t.getTimestamp().format(TIME_FORMAT);
            case 1: return typeLabel(t.getType());
            case 2: return directionLabel(t.getDirection());
            case 3: return formatMoney(t.getAmount());
            case 4: return formatMoney(t.getBalanceAfter());
            case 5: return descriptionLabel(t);
            default: return "";
        }
    }

    private static String typeLabel(TransactionType type) {
        switch (type) {
            case DEPOSIT: return "Nạp tiền";
            case WITHDRAW: return "Rút tiền";
            case TRANSFER: return "Chuyển khoản";
            case INTEREST: return "Cộng lãi";
            default: return type.name();
        }
    }

    private static String directionLabel(TransactionDirection direction) {
        switch (direction) {
            case IN: return "Vào";
            case OUT: return "Ra";
            default: return "";
        }
    }


    private static String descriptionLabel(Transaction t) {
        String description = t.getDescription();
        if (t.getType() == TransactionType.INTEREST && description.startsWith(INTEREST_PREFIX)) {
            try {
                YearMonth period = YearMonth.parse(description.substring(INTEREST_PREFIX.length()));
                return "Cộng lãi tháng " + period.format(MONTH_FORMAT);
            } catch (DateTimeParseException e) {
                return description; 
            }
        }
        return description;
    }

    private static String formatMoney(BigDecimal value) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(
                Locale.forLanguageTag("vi-VN"));
        return new DecimalFormat("#,##0", symbols).format(value);
    }
}