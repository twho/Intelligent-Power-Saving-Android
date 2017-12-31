package com.tsungweiho.intelligentpowersaving.constants;

/**
 * Interface for PubNub API usage
 *
 * This interface is used to store all constants used to make PubNub API calls.
 *
 * @author Tsung Wei Ho
 * @version 0220.2017
 * @since 1.0.0
 */
public interface PubNubAPIConstants {
    // Project: Intelligent Power Saving
    // Pubnub keys
    String PUBNUB_PUBLISH_KEY = "pub-c-dca997e6-cdc6-44eb-929d-d6cc56a56a1f";
    String PUBNUB_SUBSCRIBE_KEY = "sub-c-1061b83c-f6e2-11e6-ac91-02ee2ddab7fe";

    // PubNub channels
    String CHANNEL_VERSION = "10";
    String EVENT_CHANNEL = "event_channel_" + CHANNEL_VERSION;
    String EVENT_CHANNEL_DELETED = "event_channel_" + CHANNEL_VERSION + "_deleted";
    String MESSAGE_CHANNEL = "message_channel_" + CHANNEL_VERSION;
    String MESSAGE_CHANNEL_DELETED = "message_channel_" + CHANNEL_VERSION + "_deleted";

    // Event Params
    String EVENT_UNID = "uniqueId";
    String EVENT_DETAIL = "detail";
    String EVENT_POS = "position";
    String EVENT_IMG = "image";
    String EVENT_POSTER = "poster";
    String EVENT_POSTERIMG = "posterImg";
    String EVENT_TIME = "time";
    String EVENT_IF_FIXED = "isFixed";

    // Message from web Params
    String FROM_WEB_MESSAGE_SEPARATOR = "IPSFROMWEB";
    int FROM_WEB_MESSAGE_UNID = 1;
    int FROM_WEB_MESSAGE_TITLE = 2;
    int FROM_WEB_MESSAGE_CONTENT = 3;
    int FROM_WEB_MESSAGE_SENDER = 4;
    int FROM_WEB_MESSAGE_INBOX_LABEL = 5;

    // Message Inbox Label
    String MESSAGE_LABEL_ANNOUNCEMENT = "announcement";
    String MESSAGE_LABEL_WARNING = "warning";
    String MESSAGE_LABEL_EMERGENCY = "emergency";

    // Message Params
    String MESSAGE_UNID = "uniqueId";
    String MESSAGE_TITLE = "title";
    String MESSAGE_CONTENT = "content";
    String MESSAGE_SENDER = "sender";
    String MESSAGE_TIME = "time";
    String MESSAGE_INBOX_LABEL = "inboxLabel";
}
