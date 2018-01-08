package com.tsungweiho.intelligentpowersaving.tools;

import android.content.Context;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.EventDBHelper;
import com.tsungweiho.intelligentpowersaving.databases.MessageDBHelper;
import com.tsungweiho.intelligentpowersaving.objects.Event;

import org.json.JSONObject;

/**
 * Class for handling PubNub API functions
 * <p>
 * This singleton class is used to handle all PubNub API functions in the app
 *
 * @author Tsung Wei Ho
 * @version 0101.2018
 * @since 2.0.0
 */
public class PubNubHelper implements PubNubAPIConstants {

    private static final PubNubHelper instance = new PubNubHelper();

    /**
     * Get singleton class instance
     *
     * @return class instance
     */
    public static PubNubHelper getInstance() {
        return instance;
    }

    private PubNubHelper() {
    }

    /**
     * Get application context for animation use
     *
     * @return application context
     */
    private Context getContext() {
        return IPowerSaving.getContext();
    }

    /**
     * Initialize PubNub object
     *
     * @return the PubNub object to be used in app-wide
     */
    public PubNub initPubNub() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(PUBNUB_SUBSCRIBE_KEY);
        pnConfiguration.setPublishKey(PUBNUB_PUBLISH_KEY);
        pnConfiguration.setSecure(false);

        return new PubNub(pnConfiguration);
    }

    /**
     * Subscribe to PubNub channels
     *
     * @param pubNub   the PubNub object
     * @param channels the channels to subscribe
     */
    public void subscribeToChannels(PubNub pubNub, ActiveChannels channels) {
        switch (channels) {
            case EVENT:
                pubNub.subscribe().channels(EVENT_CHANNEL_SET).execute();
                break;
            case MESSAGE:
                pubNub.subscribe().channels(MESSAGE_CHANNEL_SET).execute();
                break;
        }
    }

    /**
     * Unsubscribe to PubNub channels
     *
     * @param pubNub   the PubNub object
     * @param channels the channels to unsubscribe
     */
    public void unsubscribeToChannels(PubNub pubNub, ActiveChannels channels) {
        switch (channels) {
            case EVENT:
                pubNub.unsubscribe().channels(EVENT_CHANNEL_SET).execute();
                break;
            case MESSAGE:
                pubNub.unsubscribe().channels(MESSAGE_CHANNEL_SET).execute();
                break;
        }
    }

    /**
     * Get the all messages in the channel
     *
     * @param pubNub            the PubNub object used in app-wide
     * @param channel           the channel to get messages from
     * @param completedCallback callback for UI changes
     */
    public void getChannelHistory(PubNub pubNub, ActiveChannels channel, final OnTaskCompleted completedCallback) {
        switch (channel) {
            case EVENT:
                pubNub.history().channel(ActiveChannels.EVENT.toString()).count(100).async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(PNHistoryResult result, PNStatus status) {
                        try {
                            if (null == result)
                                return;

                            MessageDBHelper messageDBHelper = new MessageDBHelper(getContext());
                            EventDBHelper eventDBHelper = new EventDBHelper(getContext());
                            eventDBHelper.deleteAllDB();
                            for (int index = 0; index < result.getMessages().size(); index++) {
                                JSONObject jObject = new JSONObject(String.valueOf(result.getMessages().get(index).getEntry()));
                                Event event = JsonParser.getInstance().getEventByJSONObj(jObject);

                                if (!eventDBHelper.isExist(event.getUniqueId()))
                                    eventDBHelper.insertDB(event);

                                if (!messageDBHelper.isExist(event.getUniqueId()))
                                    messageDBHelper.insertDB(JsonParser.getInstance().convertEventToMessage(jObject));
                            }

                            // Callback when tasks are completed
                            completedCallback.onTaskCompleted(!status.isError());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case MESSAGE:
                pubNub.history().channel(ActiveChannels.MESSAGE.toString()).count(100).async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(PNHistoryResult result, PNStatus status) {
                        try {
                            if (null == result)
                                return;

                            MessageDBHelper messageDBHelper = new MessageDBHelper(getContext());
                            for (int index = 0; index < result.getMessages().size(); index++) {
                                if (result.getMessages().get(index).toString().contains(FROM_WEB_MESSAGE_SEPARATOR)) {
                                    String strMessage = result.getMessages().get(index).toString();
                                    String uniqueId = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_UNID];

                                    if (!messageDBHelper.isExist(uniqueId))
                                        messageDBHelper.insertDB(JsonParser.getInstance().getMessageByString(strMessage));
                                }
                            }

                            // Callback when tasks are completed
                            completedCallback.onTaskCompleted(!status.isError());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
    }

    /**
     * Publish an event to channel
     *
     * @param pubNub            the PubNub object used in app-wide
     * @param obj               the event or message object to be published
     * @param completedCallback callback for UI changes
     */
    public void publishMessage(PubNub pubNub, ActiveChannels channel, Object obj, final OnTaskCompleted completedCallback) {
        pubNub.publish().message(obj).channel(channel.toString())
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        completedCallback.onTaskCompleted(!status.isError());
                    }
                });
    }

    /**
     * Task complete callback
     */
    public interface OnTaskCompleted {
        void onTaskCompleted(boolean isSuccessful);
    }
}
