package com.online.estoreshop.models;

public class NotificationDomain {

    String order_id;
    String order_date;
    String order_time;
    String customer_name;
    String phone;
    String address;

    public NotificationDomain(String order_id, String order_date, String order_time, String customer_name, String phone, String address) {
        this.order_id = order_id;
        this.order_date = order_date;
        this.order_time = order_time;
        this.customer_name = customer_name;
        this.phone = phone;
        this.address = address;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getOrder_date() {
        return order_date;
    }

    public String getOrder_time() {
        return order_time;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}
