package com.example.pfeproject.model;

import java.util.ArrayList;

public class Command implements Comparable<Command>{
    String id,status;
    double price;
    ArrayList<TotalPoint> totalPointsPerEntreprise;
    int cmdTotalPoint;

    /*
            "id": 1,
            "price": 77000.0,
            "status": "waiting"
     */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ArrayList<TotalPoint> getTotalPointsPerEntreprise() {
        return totalPointsPerEntreprise;
    }

    public void setTotalPointsPerEntreprise(ArrayList<TotalPoint> totalPointsPerEntreprise) {
        this.totalPointsPerEntreprise = totalPointsPerEntreprise;
    }

    public int getCmdTotalPoint() {
        return cmdTotalPoint;
    }

    public void setCmdTotalPoint(int cmdTotalPoint) {
        this.cmdTotalPoint = cmdTotalPoint;
    }

    @Override
    public int compareTo(Command o) {
        return this.getId().compareTo(o.getId());
    }
}
