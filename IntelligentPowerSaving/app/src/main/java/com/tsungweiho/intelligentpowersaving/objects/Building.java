package com.tsungweiho.intelligentpowersaving.objects;

/**
 * Created by MichaelHo on 2015/4/14.
 */
public class Building {
    private String name;
    private String detail;
    private String efficiency;
    private String consumption;
    private String imageUrl;
    private String ifFollow;

    public Building(String name, String detail, String efficiency, String consumption, String imageUrl, String ifFollow) {
        this.name = name;
        this.detail = detail;
        this.efficiency = efficiency;
        this.consumption = consumption;
        this.imageUrl = imageUrl;
        this.ifFollow = ifFollow;
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

    public String getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(String efficiency) {
        this.efficiency = efficiency;
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIfFollow() {
        return ifFollow;
    }

    public void setIfFollow(String ifFollow) {
        this.ifFollow = ifFollow;
    }

}
