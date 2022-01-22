package com.example.pfeproject.model;

public class ModelUserPoint {
    String id,socName,point;

    public ModelUserPoint(String id, String socName, String point) {
        this.id = id;
        this.socName = socName;
        this.point = point;
    }

    public ModelUserPoint(String socName, String point) {
        this.socName = socName;
        this.point = point;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSocName() {
        return socName;
    }

    public void setSocName(String socName) {
        this.socName = socName;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
