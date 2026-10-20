package vn.edu.ptit.oop.nhom04.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import vn.edu.ptit.oop.nhom04.exception.InvalidAmountException;
import vn.edu.ptit.oop.nhom04.exception.InvalidTransferException;
import vn.edu.ptit.oop.nhom04.model.Account;
import vn.edu.ptit.oop.nhom04.model.Transaction;

/**
 * Dịch vụ chuyển khoản toàn vẹn (atomic transfer) giữa hai tài khoản.
 * Chỉ dùng nội bộ sau khi Bank đã kiểm tra quyền và xác thực tài khoản nguồn.
 * Thuộc sở hữu của Người 5.
 */
public class TransferService {

    public TransferService() {
    }

    /**
     * Thực hiện chuyển khoản toàn vẹn giữa hai tài khoản.
     *
     * @param source Tài khoản nguồn
     * @param target Tài khoản đích
     * @param amount Số tiền chuyển (VND nguyên dương)
     * @param time   Thời điểm giao dịch lấy từ SimulationClock
     * @return Danh sách gồm đúng 2 Transaction [OUT của nguồn, IN của đích]
     */
    public List<Transaction> transfer(Account source, Account target,
                                      BigDecimal amount, LocalDateTime time) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Tài khoản nguồn và đích không được null");
        }
        if (time == null) {
            throw new IllegalArgumentException("Thời điểm giao dịch không được null");
        }
        if (source.getAccountNumber().equals(target.getAccountNumber())) {
            throw new InvalidTransferException("Tài khoản nguồn và đích không được trùng nhau");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.stripTrailingZeros().scale() > 0) {
            throw new InvalidAmountException("Số tiền chuyển phải là số nguyên đồng lớn hơn 0");
        }

        // Gọi cơ chế chuyển khoản nguyên tử trên Account (do Người 2 phối hợp Người 5 triển khai)
        return source.transferTo(target, amount, time);
    }
}
