package com.online.estoreshop.models;

public class UserDomain {

    String shopname;
    String ownername;
    String phone;
    String address;
    String pincode;
    String landmark;
    String password;

    public UserDomain(String shopname, String ownername, String phone, String address, String pincode, String landmark, String password) {
        this.shopname = shopname;
        this.ownername = ownername;
        this.phone = phone;
        this.address = address;
        this.pincode = pincode;
        this.landmark = landmark;
        this.password = password;
    }

    public String getShopname() {
        return shopname;
    }

    public String getOwnername() {
        return ownername;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getPincode() {
        return pincode;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getPassword() {
        return password;
    }
}
