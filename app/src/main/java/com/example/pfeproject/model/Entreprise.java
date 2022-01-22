package com.example.pfeproject.model;

public class Entreprise implements Comparable<Entreprise>{
    private String id,imageLink,name;
    private Publicity[] publicities;
    private Category category;
    private Product[] products;

    // "products": [],  "requests": [],


    public Entreprise(String id, String imageLink, String name) {
        this.id = id;
        this.imageLink = imageLink;
        this.name = name;
    }

    public Entreprise(String id, String imageLink, String name, Publicity[] publicities, Category category, Product[] products) {
        this.id = id;
        this.imageLink = imageLink;
        this.name = name;
        this.publicities = publicities;
        this.category = category;
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Publicity[] getPublicities() {
        return publicities;
    }

    public void setPublicities(Publicity[] publicities) {
        this.publicities = publicities;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    @Override
    public int compareTo(Entreprise o) {
        return this.getName().compareTo(o.name);
    }

    /*
    "id": 10,
            "email": "entreprise1@niw.com",
            "password": "$2a$10$H/CC7.07ioUN4ZUd9SXAI.r4Q71dghm0scU.9uE.5CKDPQlU/KwKW",
            "phone": "21212121",
            "role": "entreprise",
            "imageLink": null,
            "category": {
                "id": 1,
                "name": "Clothses"
            },
            "publicities": [
                {
                    "id": 1,
                    "videoLink": "http://127.0.0.1:8081/downloadFile/karasna.mp4",
                    "description": "pub1",
                    "pointToEarn": 5000,
                    "pointEarned": []
                }
            ],
            "products": [],
            "requests": [],
            "name": "entreprise1",
            "adress": "3 rue"
     */
}
