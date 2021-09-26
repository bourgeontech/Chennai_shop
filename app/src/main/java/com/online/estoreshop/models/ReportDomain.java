package com.online.estoreshop.models;

public class ReportDomain {

    String report_date;
    String customer_name;
    String category_title;
    String sub_category_title;
    String quantity;
    String price;
    String product_name;
    String unit;

    public ReportDomain(String report_date, String customer_name, String category_title, String sub_category_title, String quantity, String price, String product_name, String unit) {
        this.report_date = report_date;
        this.customer_name = customer_name;
        this.category_title = category_title;
        this.sub_category_title = sub_category_title;
        this.quantity = quantity;
        this.price = price;
        this.product_name = product_name;
        this.unit = unit;
    }

    public String getReport_date() {
        return report_date;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public String getCategory_title() {
        return category_title;
    }

    public String getSub_category_title() {
        return sub_category_title;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getUnit() {
        return unit;
    }
}
