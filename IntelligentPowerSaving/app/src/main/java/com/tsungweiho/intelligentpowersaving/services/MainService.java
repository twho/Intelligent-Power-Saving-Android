package com.tsungweiho.intelligentpowersaving.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.EventDBHelper;
import com.tsungweiho.intelligentpowersaving.databases.MessageDBHelper;
import com.tsungweiho.intelligentpowersaving.fragments.EventFragment;
import com.tsungweiho.intelligentpowersaving.fragments.InboxFragment;
import com.tsungweiho.intelligentpowersaving.objects.Event;
import com.tsungweiho.intelligentpowersaving.tools.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Service in background to subscribe to PubNub channels
 *
 * This service is always running in background to receive pushed messages once user open the app.
 *
 * @author Tsung Wei Ho
 * @version 0302.2017
 * @since 1.0.0
 */
public class MainService extends Service implements PubNubAPIConstants, FragmentTags {
    private String TAG = "MainService";

    // Functions
    private Context context;
    private MainServiceListener mainServiceListener;
    private FragmentManager fm;
    private EventDBHelper eventDBHelper;
    private MessageDBHelper messageDBHelper;
    private JsonParser jsonParser;
    private static PubNub pubnub = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = MainService.this;

        init();
    }

    private void init() {
        mainServiceListener = new MainServiceListener();

        if (null != MainActivity.getContext())
            fm = ((MainActivity) MainActivity.getContext()).getSupportFragmentManager();

        eventDBHelper = new EventDBHelper(context);
        messageDBHelper = new MessageDBHelper(context);
        jsonParser = JsonParser.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Set PubNub Listeners
        if (null != MainActivity.getContext()) {
            pubnub = MainActivity.getPubNub();
            pubnub.addListener(mainServiceListener);
        }

        getEventChannelHistory();

        return Service.START_STICKY;
    }

    private void getEventChannelHistory() {
        pubnub.history().channel(EVENT_CHANNEL).count(100)
                .async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(PNHistoryResult result, PNStatus status) {
                        try {
                            if (null != result) {
                                eventDBHelper.deleteAllDB();
                                for (int index = 0; index < result.getMessages().size(); index++) {
                                    JSONObject jObject = new JSONObject(String.valueOf(result.getMessages().get(index).getEntry()));
                                    Event event = JsonParser.getInstance().getEventByJSONObj(jObject);

                                    if (!eventDBHelper.isExist(event.getUniqueId()))
                                        eventDBHelper.insertDB(event);

                                    if (!messageDBHelper.isExist(event.getUniqueId()))
                                        messageDBHelper.insertDB(JsonParser.getInstance().convertEventToMessage(jObject));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private class MainServiceListener extends SubscribeCallback {

        @Override
        public void status(PubNub pubnub, PNStatus status) {
            switch (status.getCategory()) {
                case PNTimeoutCategory:
                    // TODO connectivity
                    break;
                case PNDisconnectedCategory:
                    // TODO connectivity
                    break;
                case PNUnknownCategory:
                    break;
            }
        }

        @Override
        public void message(PubNub pubnub, PNMessageResult message) {
            if (message.getChannel().equalsIgnoreCase(EVENT_CHANNEL) || message.getChannel().equalsIgnoreCase(EVENT_CHANNEL_DELETED)) {
                try {
                    JSONObject jObject = new JSONObject(message.getMessage().toString());

                    // Save message to eventDB
                    insertDataToDB(jObject);

                    // Save message in messageDB
                    String uniqueId = jObject.getString(EVENT_UNID);
                    if (!messageDBHelper.isExist(uniqueId)) {
                        messageDBHelper.insertDB(jsonParser.convertEventToMessage(jObject));

                        if (InboxFragment.isActive)
                            setUpInboxFragment().refreshViewingFromService();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (message.getChannel().equalsIgnoreCase(MESSAGE_CHANNEL) || message.getChannel().equalsIgnoreCase(MESSAGE_CHANNEL_DELETED)) {
                if (message.getMessage().toString().contains(FROM_WEB_MESSAGE_SEPARATOR)) {
                    String strMessage = message.getMessage().toString();
                    String uniqueId = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_UNID];

                    if (!messageDBHelper.isExist(uniqueId)) {
                        messageDBHelper.insertDB(jsonParser.getMessageByString(strMessage));

                        if (InboxFragment.isActive)
                            setUpInboxFragment().refreshViewingInboxOnUiThread();
                    }
                }
            }
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {

        }
    }

    private void insertDataToDB(JSONObject jObject) {
        Event event = jsonParser.getEventByJSONObj(jObject);

        if (!eventDBHelper.isExist(event.getUniqueId())) {
            eventDBHelper.insertDB(event);
            if (EventFragment.isActive) {
                EventFragment eventFragment = (EventFragment) fm.findFragmentByTag(EVENT_FRAGMENT);
                if (null == eventFragment)
                    eventFragment = new EventFragment();

                eventFragment.setMarkersOnUiThread();
            }
        }
    }

    private InboxFragment setUpInboxFragment() {
        InboxFragment inboxFragment = (InboxFragment) fm.findFragmentByTag(INBOX_FRAGMENT);
        if (null == inboxFragment)
            inboxFragment = new InboxFragment();

        return inboxFragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Clean memory
        messageDBHelper.closeDB();
        eventDBHelper.closeDB();
    }
}
