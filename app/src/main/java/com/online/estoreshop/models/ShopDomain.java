package com.online.estoreshop.models;

public class ShopDomain {

    String shopname;
    String rating;
    String location;

    public ShopDomain(String shopname, String rating, String location) {
        this.shopname = shopname;
        this.rating = rating;
        this.location = location;
    }

    public String getShopname() {
        return shopname;
    }

    public String getRating() {
        return rating;
    }

    public String getLocation() {
        return location;
    }
}
