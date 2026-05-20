package com.example.alphachat.model;

import java.io.Serializable;

public class Mechina implements Serializable {

    private String name;
    private String region;
    private String gender;
    private String type;
    private String link;
    private String image;

    public Mechina() {
    }

    public Mechina(String name, String region, String gender, String type, String link, String image) {
        this.name = name;
        this.region = region;
        this.gender = gender;
        this.type = type;
        this.link = link;
        this.image = image;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
