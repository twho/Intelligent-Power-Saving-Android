package com.ibplan.michaelho.com.ibplan.michaelho.objects;

/**
 * Created by Administrator on 2015/5/23.
 */
public class Event {
    private String name;
    private String department;
    private String location;
    private String posX;
    private String posY;
    private String event;
    private byte[] image;
    private String time;

    public Event() {

    }

    public Event(String name, String department, String location, String posX, String posY, String event, byte[] image, String time) {
        this.name = name;
        this.department = department;
        this.location = location;
        this.posX = posX;
        this.posY = posY;
        this.event = event;
        this.image = image;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPosX() {
        return posX;
    }

    public void setPosX(String posX) {
        this.posX = posX;
    }

    public String getPosY() {
        return posY;
    }

    public void setPosY(String posY) {
        this.posY = posY;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String[] pos) {
        this.time = time;
    }
}
