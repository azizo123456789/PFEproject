package com.example.pfeproject.model;

public class Category implements Comparable<Category>{
    private String id,name;
    private Entreprise[] entreprises;

    public Category() {
    }

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
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

    public Entreprise[] getEntreprises() {
        return entreprises;
    }

    public void setEntreprises(Entreprise[] entreprises) {
        this.entreprises = entreprises;
    }

    @Override
    public int compareTo(Category o) {
        return this.getId().compareTo(o.getId());
    }
}
