package com.online.estoreshop.models;

public class CategoryDomain {

    String cid;
    String category_id;
    String category_title;
    String category_description;
    String category_image;

    public CategoryDomain(String category_id, String category_title, String category_description, String category_image) {
        this.category_id = category_id;
        this.category_title = category_title;
        this.category_description = category_description;
        this.category_image = category_image;
    }

    public CategoryDomain(String cid, String category_id, String category_title, String category_description, String category_image) {
        this.cid = cid;
        this.category_id = category_id;
        this.category_title = category_title;
        this.category_description = category_description;
        this.category_image = category_image;
    }

    public String getCid() {
        return cid;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getCategory_title() {
        return category_title;
    }

    public String getCategory_description() {
        return category_description;
    }

    public String getCategory_image() {
        return category_image;
    }
}
