package com.tsungweiho.intelligentpowersaving.objects;

import java.util.ArrayList;

/**
 * Created by Tsung Wei Ho on 2015/5/23.
 */
public class Event {
    private String uniqueId;
    private String detail;
    private String position;
    private String posterImg;
    private String image;
    private String time;
    private String ifFixed;

    public Event(String uniqueId, String detail, String position, String image, String posterImg, String time, String ifFixed) {
        this.uniqueId = uniqueId;
        this.detail = detail;
        this.position = position;
        this.image = image;
        this.posterImg = posterImg;
        this.time = time;
        this.ifFixed = ifFixed;
    }

    public Event(ArrayList<String> eventArray) {
        this.uniqueId = eventArray.get(0);
        this.detail = eventArray.get(1);
        this.position = eventArray.get(2);
        this.image = eventArray.get(3);
        this.posterImg = eventArray.get(4);
        this.time = eventArray.get(5);
        this.ifFixed = eventArray.get(6);
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

    public String getPosterImg() {
        return posterImg;
    }

    public void setPosterImg(String posterImg) {
        this.posterImg = posterImg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String[] pos) {
        this.time = time;
    }

    public String getIfFixed() {
        return ifFixed;
    }

    public void setIfFixed(String ifFixed) {
        this.ifFixed = ifFixed;
    }
}
