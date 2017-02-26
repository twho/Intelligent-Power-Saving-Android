package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DrawerListConstants;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.MessageDBHelper;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.tools.DrawerListAdapter;
import com.tsungweiho.intelligentpowersaving.tools.MessageListAdapter;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtilities;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtilities;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/18.
 */

public class InboxFragment extends Fragment implements DrawerListConstants, PubNubAPIConstants {

    // Message Fragment View
    private View view;

    // UI Views
    private DrawerListAdapter drawerListAdapter;
    private FrameLayout flTopBar;
    private DrawerLayout drawer;
    private LinearLayout llDrawer;
    private TextView tvTitle;
    private ListView navList, lvMessages;
    private ImageButton ibOptions;

    // Functions
    private Context context;
    private AnimUtilities animUtilities;
    private ImageUtilities imageUtilities;
    private TimeUtilities timeUtilities;
    private MessageDBHelper messageDBHelper;
    private InboxFragmentListener inboxFragmentListener;
    private MessageListAdapter messageListAdapter;
    private ArrayList<Message> messageList;
    private Thread uiThread;

    // PubNub
    private PubNub pubnub = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inbox, container, false);
        context = MainActivity.getContext();
        init();
        return view;
    }

    private void init() {
        inboxFragmentListener = new InboxFragmentListener();
        messageDBHelper = new MessageDBHelper(context);
        animUtilities = new AnimUtilities(context);
        imageUtilities = new ImageUtilities(context);
        timeUtilities = new TimeUtilities(context);

        // Add PubNub Listeners
        pubnub = MainActivity.getPubNub();
        pubnub.addListener(inboxFragmentListener);

        drawerListAdapter = new DrawerListAdapter(context, MESSAGE_DRAWER, MESSAGE_DRAWER_IMG);
        drawer = (DrawerLayout) view.findViewById(R.id.fragment_inbox_drawer_layout);
        navList = (ListView) view.findViewById(R.id.fragment_inbox_drawer_lv);
        navList.setAdapter(drawerListAdapter);
        lvMessages = (ListView) view.findViewById(R.id.fragment_inbox_lv_message);
        tvTitle = (TextView) view.findViewById(R.id.fragment_inbox_tv_title);
        tvTitle.setText(context.getString(R.string.inbox));
        ibOptions = (ImageButton) view.findViewById(R.id.fragment_inbox_ib_options);
        flTopBar = (FrameLayout) view.findViewById(R.id.fragment_inbox_frame_layout);
        llDrawer = (LinearLayout) view.findViewById(R.id.fragment_inbox_ll_drawer);

        refreshInbox();
        setAllListeners();
    }

    private void refreshInbox() {
        messageList = messageDBHelper.getAllMessageList();
        messageListAdapter = new MessageListAdapter(context, messageList);
        lvMessages.setAdapter(messageListAdapter);
    }

    private void setAllListeners() {
        ibOptions.setOnClickListener(inboxFragmentListener);
        navList.setOnItemClickListener(inboxFragmentListener);
    }

    private class InboxFragmentListener extends SubscribeCallback implements View.OnClickListener, AdapterView.OnItemClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_inbox_ib_options:
                    drawer.openDrawer(Gravity.LEFT);
                    break;
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
            drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerClosed(View drawerView) {
                    switch (pos) {
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                    }
                    super.onDrawerClosed(drawerView);
                }
            });
            drawer.closeDrawer(llDrawer);
        }

        @Override
        public void status(PubNub pubnub, PNStatus status) {

        }

        @Override
        public void message(PubNub pubnub, PNMessageResult message) {
            // deal with event channel
            if (message.getChannel().equalsIgnoreCase(EVENT_CHANNEL) || message.getChannel().equalsIgnoreCase(EVENT_CHANNEL_DELETED)) {
                try {
                    JSONObject jObject = new JSONObject(message.getMessage().toString());
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
                        String time = timeUtilities.getTimeByMillies(strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_UNID]);
                        String inboxLabel = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_INBOX_LABEL];
                        Message newMessage = new Message(uniqueId, title, content, sender, time, inboxLabel);
                        messageDBHelper.insertDB(newMessage);
                        refreshMessageOnUiThread();
                    }
                }
            }
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {

        }
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
                refreshInbox();
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
            addresses = geocoder.getFromLocation(Double.valueOf(position.split(",")[0]), Double.valueOf(position.split(",")[1]), 1);
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private void refreshMessageOnUiThread() {
        if (null != uiThread)
            uiThread.interrupt();

        uiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshInbox();
                    }
                });
            }
        });
        uiThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        messageDBHelper.closeDB();
    }
}