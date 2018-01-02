package com.tsungweiho.intelligentpowersaving.constants;

import java.util.Arrays;
import java.util.List;

/**
 * Interface for PubNub API usage
 * <p>
 * This interface is used to store all constants used to make PubNub API calls.
 *
 * @author Tsung Wei Ho
 * @version 0220.2017
 * @since 1.0.0
 */
public interface PubNubAPIConstants {
    // Project: Intelligent Power Saving
    // Pubnub keys
    String PUBNUB_PUBLISH_KEY = "pub-c-7ba4f492-6b0b-4aa3-a625-87167eca2214";
    String PUBNUB_SUBSCRIBE_KEY = "sub-c-bee2108c-ef52-11e7-acf8-26f7716e5467";

    // PubNub channels
    String CHANNEL_VERSION = "1";

    enum ActiveChannels {
        EVENT("event_channel_" + CHANNEL_VERSION),
        MESSAGE("message_channel_" + CHANNEL_VERSION),
        EVENT_DELETED("event_channel_" + CHANNEL_VERSION + "_deleted"),
        MESSAGE_DELETED("message_channel_" + CHANNEL_VERSION + "_deleted");

        private final String text;

        /**
         * @param text the String text to set to enum items
         */
        ActiveChannels(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    List<String> EVENT_CHANNEL_SET = Arrays.asList(ActiveChannels.EVENT.toString(), ActiveChannels.EVENT_DELETED.toString());
    List<String> MESSAGE_CHANNEL_SET = Arrays.asList(ActiveChannels.MESSAGE.toString(), ActiveChannels.MESSAGE_DELETED.toString());

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
