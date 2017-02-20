package com.tsungweiho.intelligentpowersaving.constants;

/**
 * Created by Tsung Wei Ho on 2/20/2017.
 */

public interface PubNubAPIConstants {
    // Project: Intelligent Power Saving
    // Pubnub keys
    String PUBNUB_PUBLISH = "pub-c-dca997e6-cdc6-44eb-929d-d6cc56a56a1f";
    String PUBNUB_SUBSCRIBE = "sub-c-1061b83c-f6e2-11e6-ac91-02ee2ddab7fe";

    //PubNub channels
    String CHANNEL_VERSION = "1";
    String EVENT_CHANNEL = "event_channel_" + CHANNEL_VERSION;
    String EVENT_CHANNEL_DELETED = "event_channel_" + CHANNEL_VERSION + "_deleted";
    String MESSAGE_CHANNEL = "message_channel_" + CHANNEL_VERSION;
    String MESSAGE_CHANNEL_DELETED = "message_channel_" + CHANNEL_VERSION + "_deleted";
}
