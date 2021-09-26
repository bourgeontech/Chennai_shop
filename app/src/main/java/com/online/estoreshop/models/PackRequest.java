package com.online.estoreshop.models;

public class PackRequest {
    String product_id;
    String unit;
    String quantity;
    String description;
    String orginal_price;
    String special_price;
    String discount;

    public PackRequest(String product_id, String unit, String quantity, String description, String orginal_price, String special_price, String discount) {
        this.product_id = product_id;
        this.unit = unit;
        this.quantity = quantity;
        this.description = description;
        this.orginal_price = orginal_price;
        this.special_price = special_price;
        this.discount = discount;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getUnit() {
        return unit;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getOrginal_price() {
        return orginal_price;
    }

    public String getSpecial_price() {
        return special_price;
    }

    public String getDiscount() {
        return discount;
    }

    @Override
    public String toString() {
        return "PackRequest{" +
                "product_id='" + product_id + '\'' +
                ", unit='" + unit + '\'' +
                ", quantity='" + quantity + '\'' +
                ", description='" + description + '\'' +
                ", orginal_price='" + orginal_price + '\'' +
                ", special_price='" + special_price + '\'' +
                ", discount='" + discount + '\'' +
                '}';
    }
}
