package com.tsungweiho.intelligentpowersaving.objects;

/**
 * Object class to store message information
 * <p>
 * This class is used to store message information used in InboxFragment, MessageFragment and ReportFragment
 *
 * @author Tsung Wei Ho
 * @version 0104.2018
 * @since 1.0.0
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
     * @param uniqueId   the uniqueId of the message
     * @param title      the title of the message
     * @param content    the content of the message
     * @param sender     the sender of the message in the format of [sender],[senderUid]
     * @param senderImg  the image url of the sender
     * @param time       the timestamp of the message
     * @param inboxLabel inbox label stores information in the format of [un/read],[star],[inbox],[messageImgUrl]
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

    /**
     * Convert to a long string that matches web version
     *
     * @return the string encoded with specified separator
     */
    public String toEncodedString() {
        // Separator in encoding
        String TAG = "IPSFROMWEB";

        return TAG + time + TAG + title + TAG + content + TAG + sender + TAG + senderImg + TAG + inboxLabel + TAG;
    }
}
