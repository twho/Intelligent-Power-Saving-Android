package com.tsungweiho.intelligentpowersaving.objects;

/**
 * Created by MichaelHo on 2015/6/1.
 */

public class Message {

    private String uniqueId;
    private String title;
    private String content;
    private String sender;
    private String senderImg;
    private String inboxLabel;
    private String time;

    /**
     * Message constructor
     *
     * @param uniqueId the uniqueId of the message
     * @param title
     * @param content
     * @param sender the sender of the message in the format of sender,senderUid
     * @param senderImg the image url of the sender
     * @param time the timestamp of the message
     * @param inboxLabel inbox label stores information in the format fo un/read,star,inbox,messageImgUrl
     */
    public Message(String uniqueId, String title, String content, String sender, String senderImg, String time, String inboxLabel) {
        this.uniqueId = uniqueId;
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.senderImg = senderImg;
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

    public String getSenderImg() {
        return senderImg;
    }

    public void setSenderImg(String senderImg) {
        this.senderImg = senderImg;
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

    public String toEncodedString(){
        // Separator in encoding
        String SEPARATOR = "IPSFROMWEB";

        StringBuilder strBuilder = new StringBuilder(SEPARATOR);
        strBuilder.append(time);
        strBuilder.append(SEPARATOR);
        strBuilder.append(title);
        strBuilder.append(SEPARATOR);
        strBuilder.append(content);
        strBuilder.append(SEPARATOR);
        strBuilder.append(sender);
        strBuilder.append(SEPARATOR);
        strBuilder.append(senderImg);
        strBuilder.append(SEPARATOR);
        strBuilder.append(inboxLabel);
        strBuilder.append(SEPARATOR);

        return strBuilder.toString();
    }
}
