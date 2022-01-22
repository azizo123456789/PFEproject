package com.example.pfeproject.model;

public class Product implements Comparable<Product>{
    private String id,name,description , imageLink;
    private double price,discountPriceperPoints;
    private int discountPoints;
    private String entreprise_id,entreprise_name;

    public Product(String id, String name, double price, double discountPriceperPoints, int discountPoints, String imageLink) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.discountPriceperPoints = discountPriceperPoints;
        this.discountPoints = discountPoints;
        this.imageLink = imageLink;
    }

    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountPriceperPoints() {
        return discountPriceperPoints;
    }

    public void setDiscountPriceperPoints(double discountPriceperPoints) {
        this.discountPriceperPoints = discountPriceperPoints;
    }

    public int getDiscountPoints() {
        return discountPoints;
    }

    public void setDiscountPoints(int discountPoints) {
        this.discountPoints = discountPoints;
    }

    public String getEntreprise_id() {
        return entreprise_id;
    }

    public void setEntreprise_id(String entreprise_id) {
        this.entreprise_id = entreprise_id;
    }

    public String getEntreprise_name() {
        return entreprise_name;
    }

    public void setEntreprise_name(String entreprise_name) {
        this.entreprise_name = entreprise_name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    @Override
    public int compareTo(Product o) {
        return this.getId().compareTo(o.getId());
    }
    /*
    "id": 1,
    "name": "pull",
    "price": 87000.0,
    "description": "pull pour homme",
    "discountPoints": 1000,
    "discountPriceperPoints": 10000.0,
    "commands": null,
     */
}
