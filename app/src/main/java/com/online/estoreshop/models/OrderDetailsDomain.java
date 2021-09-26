package com.online.estoreshop.models;

public class OrderDetailsDomain {

    String shop_id;
    String order_id;
    String category_id;
    String product_id;
    String quantity;
    String price;
    String product_name;
    String special_price;
    String unit;
    String product_image;

    public OrderDetailsDomain(String shop_id, String order_id, String category_id, String product_id, String quantity, String price, String product_name, String special_price, String unit, String product_image) {
        this.shop_id = shop_id;
        this.order_id = order_id;
        this.category_id = category_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.price = price;
        this.product_name = product_name;
        this.special_price = special_price;
        this.unit = unit;
        this.product_image = product_image;
    }

    public String getShop_id() {
        return shop_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getProduct_id() {
        return product_id;
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

    public String getSpecial_price() {
        return special_price;
    }

    public String getUnit() {
        return unit;
    }

    public String getProduct_image() {
        return product_image;
    }
}
