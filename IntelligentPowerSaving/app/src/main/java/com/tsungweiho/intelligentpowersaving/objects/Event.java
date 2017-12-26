package com.tsungweiho.intelligentpowersaving.objects;

import java.util.ArrayList;

/**
 * Created by Tsung Wei Ho on 2015/5/23.
 */
public class Event {
    private String uniqueId;
    private String detail;
    private String position;
    private String poster;
    private String image;
    private String time;
    private String isFixed;

    public Event(String uniqueId, String detail, String position, String image, String poster, String time, String isFixed) {
        this.uniqueId = uniqueId;
        this.detail = detail;
        this.position = position;
        this.image = image;
        this.poster = poster;
        this.time = time;
        this.isFixed = isFixed;
    }

    public Event(ArrayList<String> eventArray) {
        this.uniqueId = eventArray.get(0);
        this.detail = eventArray.get(1);
        this.position = eventArray.get(2);
        this.image = eventArray.get(3);
        this.poster = eventArray.get(4);
        this.time = eventArray.get(5);
        this.isFixed = eventArray.get(6);
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String posX) {
        this.position = position;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String[] pos) {
        this.time = time;
    }

    public String getIfFixed() {
        return isFixed;
    }

    public void setIfFixed(String ifFixed) {
        this.isFixed = ifFixed;
    }
}
