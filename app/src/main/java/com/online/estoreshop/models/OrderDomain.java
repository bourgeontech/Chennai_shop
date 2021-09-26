package com.online.estoreshop.models;

public class OrderDomain {

    String shop_id;

    public String getCustomer_Order_Id() {
        return customer_Order_Id;
    }

    public void setCustomer_Order_Id(String customer_Order_Id) {
        this.customer_Order_Id = customer_Order_Id;
    }

    String customer_Order_Id;
    String customer_id;
    String order_id;
    String order_date;
    String order_time;
    String customer_name;
    String phone;
    String address;
    String landmark;
    String payable_amount;
    String delivery_status;

    public OrderDomain(String shop_id, String customer_id, String order_id, String order_date, String order_time, String customer_name, String phone,
                       String address, String landmark, String payable_amount, String delivery_status, String customer_Order_Id) {
        this.shop_id = shop_id;
        this.customer_id = customer_id;
        this.order_id = order_id;
        this.order_date = order_date;
        this.order_time = order_time;
        this.customer_name = customer_name;
        this.phone = phone;
        this.address = address;
        this.landmark = landmark;
        this.payable_amount = payable_amount;
        this.delivery_status = delivery_status;
        this.customer_Order_Id= customer_Order_Id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public String getCustomer_id() {
        return customer_id;
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

    public String getLandmark() {
        return landmark;
    }

    public String getPayable_amount() {
        return payable_amount;
    }

    public String getDelivery_status() {
        return delivery_status;
    }
}
