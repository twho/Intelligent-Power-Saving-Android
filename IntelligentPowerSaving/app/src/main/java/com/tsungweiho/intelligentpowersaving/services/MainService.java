package com.tsungweiho.intelligentpowersaving.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.tools.JsonParser;
import com.tsungweiho.intelligentpowersaving.tools.PubNubHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Service in background to subscribe to PubNub channels
 * <p>
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
    private PubNubHelper pubNubHelper;
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

    /**
     * Init functions needed in MainService
     */
    private void init() {
        mainServiceListener = new MainServiceListener();

        if (null != MainActivity.getContext())
            fm = ((MainActivity) MainActivity.getContext()).getSupportFragmentManager();

        eventDBHelper = new EventDBHelper(context);
        messageDBHelper = new MessageDBHelper(context);

        // Singleton class
        jsonParser = JsonParser.getInstance();
        pubNubHelper = PubNubHelper.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Set PubNub Listeners
        if (null != MainActivity.getContext()) {
            pubnub = MainActivity.getPubNub();
            pubnub.addListener(mainServiceListener);
        }

        getEventChannelHistory();
        getMessageChannelHistory();

        return Service.START_STICKY;
    }

    private void getEventChannelHistory() {
        pubNubHelper.getChannelHistory(pubnub, ActiveChannels.EVENT, new PubNubHelper.OnTaskCompleted() {
            @Override
            public void onTaskCompleted() {
                if (EventFragment.isActive)
                    ((EventFragment) setUpFragment(MainFragment.EVENT)).setMarkersOnUiThread();
            }
        });
    }

    private void getMessageChannelHistory() {
        pubNubHelper.getChannelHistory(pubnub, ActiveChannels.MESSAGE, new PubNubHelper.OnTaskCompleted() {
            @Override
            public void onTaskCompleted() {
                // Do tasks related to UI
                if (InboxFragment.isActive)
                    ((InboxFragment) setUpFragment(MainFragment.INBOX)).refreshViewingFromService();
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
            if (message.getChannel().equalsIgnoreCase(ActiveChannels.EVENT.toString())
                    || message.getChannel().equalsIgnoreCase(ActiveChannels.EVENT_DELETED.toString())) {
                try {
                    JSONObject jObject = new JSONObject(message.getMessage().toString());

                    // Save message to eventDB
                    Event event = jsonParser.getEventByJSONObj(jObject);
                    if (!eventDBHelper.isExist(event.getUniqueId())){
                        eventDBHelper.insertDB(event);

                        if (EventFragment.isActive)
                            ((EventFragment) setUpFragment(MainFragment.EVENT)).setMarkersOnUiThread();
                    }


                    // Save message in messageDB
                    if (!messageDBHelper.isExist(event.getUniqueId())) {
                        messageDBHelper.insertDB(jsonParser.convertEventToMessage(jObject));

                        if (InboxFragment.isActive)
                            ((InboxFragment) setUpFragment(MainFragment.INBOX)).refreshViewingFromService();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (message.getChannel().equalsIgnoreCase(ActiveChannels.MESSAGE.toString())
                    || message.getChannel().equalsIgnoreCase(ActiveChannels.MESSAGE_DELETED.toString())) {
                if (message.getMessage().toString().contains(FROM_WEB_MESSAGE_SEPARATOR)) {
                    String strMessage = message.getMessage().toString();
                    String uniqueId = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_UNID];

                    if (!messageDBHelper.isExist(uniqueId))
                        messageDBHelper.insertDB(JsonParser.getInstance().getMessageByString(strMessage));

                    if (InboxFragment.isActive)
                        ((InboxFragment) setUpFragment(MainFragment.INBOX)).refreshViewingInboxOnUiThread();
                }
            }
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {

        }
    }

    private Fragment setUpFragment(MainFragment fragment) {
        Fragment existingFragment = fm.findFragmentByTag(MainFragment.INBOX.toString());

        switch (fragment) {
            case INBOX:
                if (null == existingFragment)
                    return new InboxFragment();
                break;
            case EVENT:
                if (null == existingFragment)
                    return new EventFragment();
                break;
        }

        return existingFragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Clean memory
        messageDBHelper.closeDB();
        eventDBHelper.closeDB();
    }
}
