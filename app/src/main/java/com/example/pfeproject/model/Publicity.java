package com.example.pfeproject.model;

import java.util.Objects;

public class Publicity implements Comparable<Publicity> {
    private String id,videoLink,description;
    private int pointToEarn;
    // pointEarned -> "pointEarned": []

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPointToEarn() {
        return pointToEarn;
    }

    public void setPointToEarn(int pointToEarn) {
        this.pointToEarn = pointToEarn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Publicity publicity = (Publicity) o;
        return Objects.equals(id, publicity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Publicity o) {
        return this.getId().compareTo(o.getId());
    }
}
