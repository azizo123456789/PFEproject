package com.example.pfeproject.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Panier {
    public static int id ;
    private String entreprise_id,entreprise_name;
    private Product[] products;

    public Panier( String entreprise_id, Product[] products) {
        this.entreprise_id = entreprise_id;
        this.products = products;
        id++ ;
    }

    public Panier(String entreprise_id, String entreprise_name, Product[] products) {
        this.entreprise_id = entreprise_id;
        this.entreprise_name = entreprise_name;
        this.products = products;
    }

    public Panier() {
        id ++;
    }

    public String getEntreprise_id() {
        return entreprise_id;
    }

    public void setEntreprise_id(String entreprise_id) {
        this.entreprise_id = entreprise_id;
    }

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    public String getEntreprise_name() {
        return entreprise_name;
    }

    public void setEntreprise_name(String entreprise_name) {
        this.entreprise_name = entreprise_name;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
