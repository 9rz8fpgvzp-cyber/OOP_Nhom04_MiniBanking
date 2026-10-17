package vn.edu.ptit.oop.nhom04.model;

public class Customer {
    private final String customerId;
    private final String fullName;
    private final String phoneNumber;

    public Customer(String customerId, String fullName, String phoneNumber){
        if(customerId == null || customerId.isBlank()){
            throw new IllegalArgumentException("Mã khách hàng không được để trống");
        }
        if(fullName == null || fullName.isBlank()){
            throw new IllegalArgumentException("Tên khách hàng không được để trống");
        }
        if(phoneNumber == null || phoneNumber.isBlank()){
            throw new IllegalArgumentException("Số điện thoại khách hàng không được để trống");
        }
        this.customerId = customerId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }
    public String getCustomerId(){
        return customerId;
    }
    public String getFullName(){
        return fullName;
    }
    public String getPhoneNumber(){
        return phoneNumber;
    }
}
