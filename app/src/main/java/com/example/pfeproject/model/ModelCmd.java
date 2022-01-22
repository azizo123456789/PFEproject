package com.example.pfeproject.model;

public class ModelCmd {
    private String id,date,point,etat;

    public ModelCmd(String date, String point, String etat) {
        this.date = date;
        this.point = point;
        this.etat = etat;
    }

    public ModelCmd(String id, String date, String point, String etat) {
        this.id = id;
        this.date = date;
        this.point = point;
        this.etat = etat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
}
