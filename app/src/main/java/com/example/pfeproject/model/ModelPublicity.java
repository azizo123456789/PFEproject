package com.example.pfeproject.model;

import java.util.ArrayList;

public class ModelPublicity {
    int catId;
    String EseName;
    ArrayList<String> pubList;

    public ModelPublicity(String eseName, ArrayList<String> pubList) {
        EseName = eseName;
        this.pubList = pubList;
    }

    public ModelPublicity(int catId, String eseName, ArrayList<String> pubList) {
        this.catId = catId;
        EseName = eseName;
        this.pubList = pubList;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getEseName() {
        return EseName;
    }

    public void setEseName(String eseName) {
        EseName = eseName;
    }

    public ArrayList<String> getPubList() {
        return pubList;
    }

    public void setPubList(ArrayList<String> pubList) {
        this.pubList = pubList;
    }
}
