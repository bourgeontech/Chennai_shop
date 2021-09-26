package com.online.estoreshop.models;

public class PaymentsDomain {
    String payable_amount;
    String order_id;
    String payment_status;
    String payment_method;
    String raz_payment_id;

    public PaymentsDomain(String order_id, String payable_amount, String payment_status, String payment_method, String raz_payment_id){
        this.order_id = order_id;
        this.payable_amount = payable_amount;
        this.payment_status = payment_status;
        this.payment_method = payment_method;
        this.raz_payment_id = raz_payment_id;
    }
    public String getOrder_id() {return order_id;}
    public String getPayable_amount() { return payable_amount; }
    public String getPayment_status() { return payment_status; }
    public String getPayment_method() { return payment_method; }
    public String getRaz_payment_id() { return raz_payment_id; }
}
