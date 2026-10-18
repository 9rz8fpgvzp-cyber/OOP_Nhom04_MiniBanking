package vn.edu.ptit.oop.nhom04.model;

import java.math.BigDecimal;

/**
 * Interface cho các tài khoản sinh lãi hàng tháng.
 * Thuộc sở hữu của Người 4 theo API v1.
 */
public interface InterestBearing {
    BigDecimal calculateMonthlyInterest();
}
