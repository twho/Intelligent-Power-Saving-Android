package com.tsungweiho.intelligentpowersaving.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.EventDBHelper;
import com.tsungweiho.intelligentpowersaving.databases.MessageDBHelper;
import com.tsungweiho.intelligentpowersaving.fragments.EventFragment;
import com.tsungweiho.intelligentpowersaving.fragments.InboxFragment;
import com.tsungweiho.intelligentpowersaving.objects.Event;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.tools.NotificationHelper;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Tsung Wei Ho on 3/2/2017.
 */

public class MainService extends Service implements PubNubAPIConstants, FragmentTags, DBConstants {
    private String TAG = "MainService";

    // Functions
    private Context context;
    private MainServiceListener mainServiceListener;
    private FragmentManager fm;
    private EventDBHelper eventDBHelper;
    private MessageDBHelper messageDBHelper;
    private TimeUtils timeUtils;

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

        if (null != MainActivity.getContext()) {
            fm = ((MainActivity) MainActivity.getContext()).getSupportFragmentManager();
        }

        eventDBHelper = new EventDBHelper(context);
        messageDBHelper = new MessageDBHelper(context);
        timeUtils = TimeUtils.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Set PubNub Listeners
        PubNub pubnub = MainActivity.getPubNub();
        pubnub.addListener(mainServiceListener);

        return Service.START_STICKY;
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
                    // Save message to eventDB
                    insertDataToDB(jObject);
                    // Save message in messageDB
                    convertEventToMessage(jObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (message.getChannel().equalsIgnoreCase(MESSAGE_CHANNEL) || message.getChannel().equalsIgnoreCase(MESSAGE_CHANNEL_DELETED)) {
                if (message.getMessage().toString().contains(FROM_WEB_MESSAGE_SEPARATOR)) {
                    String strMessage = message.getMessage().toString();
                    String uniqueId = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_UNID];
                    if (!messageDBHelper.checkIfExist(uniqueId)) {
                        String title = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_TITLE];
                        String content = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_CONTENT];
                        String sender = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_SENDER];
                        String time = timeUtils.getTimeByMillies(strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_UNID]);
                        String inboxLabel = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_INBOX_LABEL];
                        Message newMessage = new Message(uniqueId, title, content, sender, time, inboxLabel);
                        messageDBHelper.insertDB(newMessage);

                        InboxFragment inboxFragment = setUpInboxFragment();
                        inboxFragment.refreshViewingInboxOnUiThread();
                    }
                }
            }
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {

        }
    }

    private void insertDataToDB(JSONObject jObject) {
        Event event = getEventByJSONObj(jObject);
        if (!eventDBHelper.checkIfExist(event.getUniqueId())) {
            eventDBHelper.insertDB(event);
            if (EventFragment.ifFragmentActive) {
                EventFragment eventFragment = (EventFragment) fm.findFragmentByTag(EVENT_FRAGMENT);
                if (null == eventFragment)
                    eventFragment = new EventFragment();

                eventFragment.setAllMarkerOnUiThread();
            }
        }
    }

    private Event getEventByJSONObj(JSONObject jsonObject) {
        Event event = null;
        try {
            String uniqueId = jsonObject.getString(EVENT_UNID);
            String detail = jsonObject.getString(EVENT_DETAIL);
            String position = jsonObject.getString(EVENT_POS);
            String image = jsonObject.getString(EVENT_IMG);
            String poster = jsonObject.getString(EVENT_POSTER);
            String time = jsonObject.getString(EVENT_TIME);
            String ifFixed = jsonObject.getString(EVENT_IF_FIXED);
            event = new Event(uniqueId, detail, position, image, poster, time, ifFixed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }

    private InboxFragment setUpInboxFragment() {
        InboxFragment inboxFragment = (InboxFragment) fm.findFragmentByTag(INBOX_FRAGMENT);
        if (null == inboxFragment)
            inboxFragment = new InboxFragment();

        return inboxFragment;
    }

    private void convertEventToMessage(JSONObject jsonObject) {
        try {
            String uniqueId = jsonObject.getString(EVENT_UNID);
            String detail = jsonObject.getString(EVENT_DETAIL);
            String address = getAddressByPosition(jsonObject.getString(EVENT_POS));
            String poster = jsonObject.getString(EVENT_POSTER);
            String time = jsonObject.getString(EVENT_TIME);

            if (!messageDBHelper.checkIfExist(uniqueId)) {
                Message message = new Message(uniqueId, detail, getString(R.string.event_reported_around) + " " + address, poster, time, getString(R.string.label_default) + uniqueId);
                messageDBHelper.insertDB(message);

                if (InboxFragment.ifFragmentActive) {
                    InboxFragment inboxFragment = setUpInboxFragment();
                    inboxFragment.refreshViewingFromService();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getAddressByPosition(String position) {
        String address = "---";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(Double.valueOf(position.split(SEPARATOR_MSG_LABEL)[0]),
                    Double.valueOf(position.split(SEPARATOR_MSG_LABEL)[1]), 1);
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Clean memory
        messageDBHelper.closeDB();
        eventDBHelper.closeDB();
    }
}
