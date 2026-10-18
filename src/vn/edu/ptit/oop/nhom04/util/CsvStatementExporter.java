package vn.edu.ptit.oop.nhom04.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

import vn.edu.ptit.oop.nhom04.model.Transaction;
import vn.edu.ptit.oop.nhom04.model.TransactionDirection;

public class CsvStatementExporter {
    private static final String HEADER =
            "accountNumber,transactionId,type,direction,amount,timestamp,balanceAfter";
    private static final String NEW_LINE = "\r\n";

    public CsvStatementExporter() {
    }

   
    public int export(Path path, String accountNumber, List<Transaction> transactions)
            throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Đường dẫn file không được để trống.");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Số tài khoản không được rỗng.");
        }
        if (transactions == null) {
            throw new IllegalArgumentException("Danh sách giao dịch không được null.");
        }
        
        for (Transaction t : transactions) {
            if (t == null) {
                throw new IllegalArgumentException("Danh sách có giao dịch null.");
            }
            if (!accountNumber.equals(t.getAccountNumber())) {
                throw new IllegalArgumentException(
                        "Giao dịch " + t.getTransactionId()
                        + " không thuộc tài khoản " + accountNumber + ".");
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(HEADER);
            writer.write(NEW_LINE);
            for (Transaction t : transactions) {
                String direction = t.getDirection() == TransactionDirection.NONE
                        ? "" : t.getDirection().name();
                String line = String.join(",",
                        escape(t.getAccountNumber()),
                        escape(t.getTransactionId()),
                        escape(t.getType().name()),
                        escape(direction),
                        escape(t.getAmount().toPlainString()),
                        escape(t.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)),
                        escape(t.getBalanceAfter().toPlainString()));
                writer.write(line);
                writer.write(NEW_LINE);
            }
        }
        return transactions.size();
    }

    static String escape(String value) {
        if (value == null) {
            return "";
        }
        boolean needsQuote = value.contains(",") || value.contains("\"")
                || value.contains("\n") || value.contains("\r");
        if (!needsQuote) {
            return value;
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}