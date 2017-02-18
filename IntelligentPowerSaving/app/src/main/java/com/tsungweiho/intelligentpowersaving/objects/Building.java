package com.tsungweiho.intelligentpowersaving.objects;

/**
 * Created by MichaelHo on 2015/4/14.
 */
public class Building {
    private String name;
    private String detail;
    private String consumption;
    private String imageUrl;

    public Building(String name, String detail, String consumption, String imageUrl) {
        this.name = name;
        this.detail = detail;
        this.consumption = consumption;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getConsumption() {
        return consumption;
    }

    public void setConsumption(String consumption) {
        this.consumption = consumption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String image) {
        this.imageUrl = imageUrl;
    }

}
