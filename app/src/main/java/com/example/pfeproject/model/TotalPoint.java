package com.example.pfeproject.model;

public class TotalPoint {
    String id, idEntreprise , id_client, totalpoints ;
    int restpoints = 0;
    double totalprice;
    int earnedPoints = 0;
    Entreprise entreprise;

    public TotalPoint() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdEntreprise() {
        return idEntreprise;
    }

    public void setIdEntreprise(String idEntreprise) {
        this.idEntreprise = idEntreprise;
    }

    public String getTotalpoints() {
        return totalpoints;
    }

    public void setTotalpoints(String totalpoints) {
        this.totalpoints = totalpoints;
    }

    public String getId_client() {
        return id_client;
    }

    public void setId_client(String id_client) {
        this.id_client = id_client;
    }

    public int getRestpoints() {
        return restpoints;
    }

    public void setRestpoints(int restpoints) {
        this.restpoints = restpoints;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public int getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(int earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    public String getId() {
        return id;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

}
