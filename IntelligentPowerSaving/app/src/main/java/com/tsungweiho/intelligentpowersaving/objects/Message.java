package com.tsungweiho.intelligentpowersaving.objects;

/**
 * Created by MichaelHo on 2015/6/1.
 */

public class Message {

    private String uniqueId;
    private String title;
    private String content;
    private String sender;
    private String inboxLabel;
    private String time;

    // inbox label stores information (un/read,star,inbox,eventId)
    public Message(String uniqueId, String title, String content, String sender, String time, String inboxLabel) {
        this.uniqueId = uniqueId;
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.time = time;
        this.inboxLabel = inboxLabel;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getInboxLabel() {
        return inboxLabel;
    }

    public void setInboxLabel(String inboxLabel) {
        this.inboxLabel = inboxLabel;
    }
}
