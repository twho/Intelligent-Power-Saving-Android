package com.tsungweiho.intelligentpowersaving.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.tools.NotificationHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tsung Wei Ho on 3/2/2017.
 */

public class MainService extends Service implements PubNubAPIConstants {
    private String TAG = "MainService";

    // Functions
    private MainServiceListener mainServiceListener;
    private NotificationHelper notificationHelper;
    private FragmentManager fm;

    // PubNub listener
    private PubNub pubnub;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        mainServiceListener = new MainServiceListener();
        fm = ((MainActivity) MainActivity.getContext()).getSupportFragmentManager();

        // Set PubNub Listeners
        pubnub = MainActivity.getPubNub();
        pubnub.addListener(mainServiceListener);
    }

    private class MainServiceListener extends SubscribeCallback {

        @Override
        public void status(PubNub pubnub, PNStatus status) {
            if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                // TODO connectivity
            }
        }

        @Override
        public void message(PubNub pubnub, PNMessageResult message) {
            if (message.getChannel().equalsIgnoreCase(EVENT_CHANNEL) || message.getChannel().equalsIgnoreCase(EVENT_CHANNEL_DELETED)) {
                try {
                    JSONObject jObject = new JSONObject(message.getMessage().toString());
                    insertDataToDB(jObject);
                    setAllMarkerOnUiThread();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (message.getChannel().equalsIgnoreCase(MESSAGE_CHANNEL) || message.getChannel().equalsIgnoreCase(MESSAGE_CHANNEL_DELETED)) {

            }
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {

        }
    }
}
