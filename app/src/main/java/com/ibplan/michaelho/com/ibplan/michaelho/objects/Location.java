package com.ibplan.michaelho.com.ibplan.michaelho.objects;

/**
 * Created by MichaelHo on 2015/4/14.
 */
public class Location {
    private String name;
    private String detail;
    private byte[] image;

    public Location() {

    }

    public Location(String name, String detail, byte[] image) {
        this.name = name;
        this.detail = detail;
        this.image = image;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}
