package com.online.estoreshop.models;

public class ProductDomain {

    String product_id;
    String shop_id;
    String category_id;
    String product_name;
    String orginal_price;
    String special_price;
    String discount;
    String unit;
    String product_image;
    String stock_availability;
    String major_id;

    public ProductDomain(String product_id, String shop_id, String category_id, String product_name, String orginal_price, String special_price, String discount, String unit, String product_image, String stock_availability) {
        this.product_id = product_id;
        this.shop_id = shop_id;
        this.category_id = category_id;
        this.product_name = product_name;
        this.orginal_price = orginal_price;
        this.special_price = special_price;
        this.discount = discount;
        this.unit = unit;
        this.product_image = product_image;
        this.stock_availability = stock_availability;
    }

    public ProductDomain(String product_id, String shop_id, String category_id, String product_name, String orginal_price, String special_price, String discount, String unit, String product_image, String stock_availability, String major_id) {
        this.product_id = product_id;
        this.shop_id = shop_id;
        this.category_id = category_id;
        this.product_name = product_name;
        this.orginal_price = orginal_price;
        this.special_price = special_price;
        this.discount = discount;
        this.unit = unit;
        this.product_image = product_image;
        this.stock_availability = stock_availability;
        this.major_id = major_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getProduct_name() {
        return product_name;
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

    public String getUnit() {
        return unit;
    }

    public String getProduct_image() {
        return product_image;
    }

    public String getStock_availability() {
        return stock_availability;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setOrginal_price(String orginal_price) {
        this.orginal_price = orginal_price;
    }

    public void setSpecial_price(String special_price) {
        this.special_price = special_price;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public void setStock_availability(String stock_availability) {
        this.stock_availability = stock_availability;
    }

    public String getMajor_id() {
        return major_id;
    }
}
