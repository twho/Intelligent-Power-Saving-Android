package com.tsungweiho.intelligentpowersaving.objects;

import java.util.ArrayList;

/**
 * Object class to store FIXIT event information
 * <p>
 * This class is used to store FIXIT event information used in EventFragment
 *
 * @author Tsung Wei Ho
 * @version 1229.2017
 * @since 1.0.0
 */
public class Event {
    private String uniqueId;
    private String detail;
    private String position;
    private String poster;
    private String image;
    private String time;
    private String posterImg;
    private String isFixed;

    /**
     * Event constructor
     *
     * @param uniqueId  the uniqueId of the event
     * @param detail    the content information of the event
     * @param position  the position of the event in the format of [latitude],[longitude]
     * @param image     the image url resource of the event
     * @param poster    the name of the person that posts the event
     * @param posterImg the uid of the person that posts the event
     * @param time      the happening time of the event
     * @param isFixed   the status of the event
     */
    public Event(String uniqueId, String detail, String position, String image, String poster, String posterImg, String time, String isFixed) {
        this.uniqueId = uniqueId;
        this.detail = detail;
        this.position = position;
        this.image = image;
        this.poster = poster;
        this.posterImg = posterImg;
        this.time = time;
        this.isFixed = isFixed;
    }

    /**
     * Event constructor
     *
     * @param eventArray the arrayList of string that contains information of an event
     */
    public Event(ArrayList<String> eventArray) {
        this.uniqueId = eventArray.get(0);
        this.detail = eventArray.get(1);
        this.position = eventArray.get(2);
        this.image = eventArray.get(3);
        this.poster = eventArray.get(4);
        this.posterImg = eventArray.get(5);
        this.time = eventArray.get(6);
        this.isFixed = eventArray.get(7);
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

    public String getPosterImg() {
        return posterImg;
    }

    public void setPosterImg(String posterImg) {
        this.posterImg = posterImg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIsFixed() {
        return isFixed;
    }

    public void setIsFixed(String ifFixed) {
        this.isFixed = ifFixed;
    }
}
