package com.example.pfeproject.model;

public class ModelCategorie {
    int catId;
    String catLabel;
    int catImage;

    public ModelCategorie(int catId, String catLabel) {
        this.catId = catId;
        this.catLabel = catLabel;
    }

    public ModelCategorie(int catId, String catLabel, int catImage) {
        this.catId = catId;
        this.catLabel = catLabel;
        this.catImage = catImage;
    }

    public int getCatImage() {
        return catImage;
    }

    public void setCatImage(int catImage) {
        this.catImage = catImage;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getCatLabel() {
        return catLabel;
    }

    public void setCatLabel(String catLabel) {
        this.catLabel = catLabel;
    }
}
