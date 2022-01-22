package com.example.pfeproject.model;

public class Point {
    String id , idEntreprise , totalpoints;
    Entreprise entreprise;

    public String getId() {
        return id;
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

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    /*
     "id": 1,
            "idEntreprise": 11,
            "totalpoints": 15000
     */
}
