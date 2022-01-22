package com.example.pfeproject.model;

import java.util.ArrayList;

public class ModelStore {
    int catId;
    String storeName;
    ArrayList<ModelArticle> articles;

    public ModelStore(int catId, String storeName, ArrayList<ModelArticle> articles) {
        this.catId = catId;
        this.storeName = storeName;
        this.articles = articles;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public ArrayList<ModelArticle> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<ModelArticle> articles) {
        this.articles = articles;
    }
}
