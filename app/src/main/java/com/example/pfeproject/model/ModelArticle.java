package com.example.pfeproject.model;

public class ModelArticle {
    private String id,article_label,article_price,article_point;
    private int article_image;

    public ModelArticle(String article_label, String article_price, String article_point, int article_image) {
        this.article_label = article_label;
        this.article_price = article_price;
        this.article_point = article_point;
        this.article_image = article_image;
    }

    public ModelArticle(String id, String article_label, String article_price, String article_point, int article_image) {
        this.id = id;
        this.article_label = article_label;
        this.article_price = article_price;
        this.article_point = article_point;
        this.article_image = article_image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArticle_label() {
        return article_label;
    }

    public void setArticle_label(String article_label) {
        this.article_label = article_label;
    }

    public String getArticle_price() {
        return article_price;
    }

    public void setArticle_price(String article_price) {
        this.article_price = article_price;
    }

    public String getArticle_point() {
        return article_point;
    }

    public void setArticle_point(String article_point) {
        this.article_point = article_point;
    }

    public int getArticle_image() {
        return article_image;
    }

    public void setArticle_image(int article_image) {
        this.article_image = article_image;
    }
}
