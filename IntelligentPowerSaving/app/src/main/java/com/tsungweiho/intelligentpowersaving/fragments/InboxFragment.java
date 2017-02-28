package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.DrawerListConstants;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.MessageDBHelper;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;
import com.tsungweiho.intelligentpowersaving.tools.DrawerListAdapter;
import com.tsungweiho.intelligentpowersaving.tools.MessageListAdapter;
import com.tsungweiho.intelligentpowersaving.tools.SharedPreferencesManager;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtilities;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtilities;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/18.
 */

public class InboxFragment extends Fragment implements DrawerListConstants, PubNubAPIConstants, DBConstants {

    // Message Fragment View
    private View view;

    // UI Views
    private DrawerListAdapter drawerListAdapter;
    private FrameLayout flTopBar;
    private DrawerLayout drawer;
    private LinearLayout llDrawer, llEditing;
    private TextView tvTitle, tvMail;
    private ListView navList, lvMessages;
    private ImageButton ibOptions, ibDelete, ibInboxFunction;
    private Button btnUnread;
    private ImageView ivDrawerPic;

    // Functions
    private Context context;
    private ImageUtilities imageUtilities;
    private AnimUtilities animUtilities;
    private TimeUtilities timeUtilities;
    private MessageDBHelper messageDBHelper;
    private InboxFragmentListener inboxFragmentListener;
    private SharedPreferencesManager sharedPreferencesManager;
    private Thread uiThread;

    // Mailbox functions, topbar buttons are padding 15%
    private MessageListAdapter messageListAdapter;
    private ArrayList<Message> messageList;
    private ArrayList<Boolean> messageSelectedList;
    private int MODE_VIEWING = 0;
    private int MODE_EDITING = 1;
    private String currentBox;
    private Boolean ifSelectedRead;

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
        timeUtilities = new TimeUtilities(context);
        imageUtilities = MainActivity.getImageUtilities();
        sharedPreferencesManager = new SharedPreferencesManager(context);

        // Add PubNub Listeners
        pubnub = MainActivity.getPubNub();
        pubnub.addListener(inboxFragmentListener);

        drawerListAdapter = new DrawerListAdapter(context, MESSAGE_DRAWER, MESSAGE_DRAWER_IMG);
        drawer = (DrawerLayout) view.findViewById(R.id.fragment_inbox_drawer_layout);
        navList = (ListView) view.findViewById(R.id.fragment_inbox_drawer_lv);
        navList.setAdapter(drawerListAdapter);
        lvMessages = (ListView) view.findViewById(R.id.fragment_inbox_lv_message);
        tvMail = (TextView) view.findViewById(R.id.fragment_inbox_drawer_tv_mail);
        tvTitle = (TextView) view.findViewById(R.id.fragment_inbox_tv_title);
        tvTitle.setText(context.getString(R.string.inbox));
        ibOptions = (ImageButton) view.findViewById(R.id.fragment_inbox_ib_options);
        ibInboxFunction = (ImageButton) view.findViewById(R.id.fragment_inbox_ib_inbox_function);
        ibDelete = (ImageButton) view.findViewById(R.id.fragment_inbox_ib_delete);
        btnUnread = (Button) view.findViewById(R.id.fragment_inbox_btn_unread);
        flTopBar = (FrameLayout) view.findViewById(R.id.fragment_inbox_frame_layout);
        llDrawer = (LinearLayout) view.findViewById(R.id.fragment_inbox_ll_drawer);
        llEditing = (LinearLayout) view.findViewById(R.id.fragment_inbox_layout_editing);
        ivDrawerPic = (ImageView) view.findViewById(R.id.fragment_inbox_drawer_iv);

        setAllListeners();
    }

    private void setAllListeners() {
        ibOptions.setOnClickListener(inboxFragmentListener);
        navList.setOnItemClickListener(inboxFragmentListener);
        ibDelete.setOnClickListener(inboxFragmentListener);
        ibInboxFunction.setOnClickListener(inboxFragmentListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        // use the data already saved
        currentBox = sharedPreferencesManager.getCurrentMessagebox();
        messageList = messageDBHelper.getMessageListByLabel(currentBox);
        MyAccountInfo myAccountInfo = sharedPreferencesManager.getMyAccountInfo();
        ivDrawerPic.setImageBitmap(imageUtilities.getRoundedCroppedBitmap(imageUtilities.decodeBase64ToBitmap(myAccountInfo.getImageUrl())));
        tvMail.setText(myAccountInfo.getEmail());

        // init inbox
        messageList = messageDBHelper.getMessageListByLabel(currentBox);
        messageListAdapter = new MessageListAdapter(context, messageList, messageSelectedList, MODE_VIEWING);
        initInbox(messageList);
        lvMessages.setAdapter(messageListAdapter);
        animUtilities.setlvAnimToVisible(lvMessages);
        tvTitle.setText(currentBox.substring(0, 1).toUpperCase() + currentBox.substring(1));
    }

    private void initInbox(ArrayList<Message> messageList) {
        if (null != messageSelectedList)
            messageSelectedList = null;
        messageSelectedList = new ArrayList<Boolean>(Arrays.asList(new Boolean[messageList.size()]));
        Collections.fill(messageSelectedList, Boolean.FALSE);

        // Data set change
        messageListAdapter.setMessageList(messageList);
        messageListAdapter.setSelectedList(messageSelectedList);
    }

    private void refreshViewingInbox(ArrayList<Message> messageList) {
        this.messageList = messageList;
        initInbox(messageList);
        switchTopBar(MODE_VIEWING);
        messageListAdapter.setMode(MODE_VIEWING);
        messageListAdapter.notifyDataSetChanged();
    }

    // Be executed only When firstly start editing mode
    public void initEditingInbox(int initSelectedPosition, ArrayList<Message> messageList) {
        this.messageList = messageList;
        initInbox(messageList);
        switchTopBar(MODE_EDITING);
        messageSelectedList.set(initSelectedPosition, Boolean.TRUE);
        messageListAdapter.setMode(MODE_EDITING);
        messageListAdapter.setSelectedList(messageSelectedList);
        messageListAdapter.notifyDataSetChanged();
        setIfSelectedRead(messageList.get(initSelectedPosition).getInboxLabel().split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(LABEL_MSG_READ));
    }

    public void setIndexSelected(int selectedPosition, Boolean ifSelected) {
        messageSelectedList.set(selectedPosition, ifSelected);
        messageListAdapter.setSelectedList(messageSelectedList);
        messageListAdapter.notifyDataSetChanged();
        setIfSelectedRead(messageList.get(selectedPosition).getInboxLabel().split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(LABEL_MSG_READ));
    }

    private void setIfSelectedRead(Boolean ifSelectedRead) {
        this.ifSelectedRead = ifSelectedRead;
        if (!currentBox.equalsIgnoreCase(LABEL_MSG_TRASH)) {
            if (ifSelectedRead) {
                ibInboxFunction.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_read));
            } else {
                ibInboxFunction.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_unread));
            }
        }
    }

    private void switchTopBar(int mode) {
        if (mode == MODE_EDITING) {
            llEditing.setVisibility(View.VISIBLE);
            btnUnread.setVisibility(View.GONE);
            animUtilities.rotateToIcon(ibOptions, R.mipmap.ic_back);
        } else if (mode == MODE_VIEWING) {
            llEditing.setVisibility(View.GONE);
            btnUnread.setVisibility(View.VISIBLE);
            animUtilities.rotateToIcon(ibOptions, R.mipmap.ic_options);
        }
    }

    private void switchBoxFunctions(String currentBox) {
        // The buttons in trash box is different from the other two
        if (currentBox.equalsIgnoreCase(LABEL_MSG_TRASH)) {
            ibInboxFunction.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_move_back));
        } else {
            ibInboxFunction.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_unread));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        messageDBHelper.closeDB();

        // save which box the user is using
        sharedPreferencesManager.saveCurrentMessageBox(currentBox);
    }

    private class InboxFragmentListener extends SubscribeCallback implements View.OnClickListener, AdapterView.OnItemClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_inbox_ib_options:
                    if (messageListAdapter.getMode() == MODE_VIEWING) {
                        drawer.openDrawer(Gravity.LEFT);
                    } else if (messageListAdapter.getMode() == MODE_EDITING) {
                        refreshViewingInbox(messageDBHelper.getMessageListByLabel(currentBox));
                    }
                    break;
                case R.id.fragment_inbox_ib_delete:
                    if (currentBox.equalsIgnoreCase(LABEL_MSG_TRASH)) {
                        // If in trash box, the delete button will permanently delete the mail
                        for (int i = messageSelectedList.size() - 1; i >= 0; i--) {
                            if (messageSelectedList.get(i)) {
                                messageDBHelper.deleteByUniqueId(messageList.get(i).getUniqueId());
                            }
                        }
                    } else {
                        // Remember to reverse the order back
                        int count = 0;
                        for (int i = messageSelectedList.size() - 1; i >= 0; i--) {
                            if (messageSelectedList.get(i)) {
                                messageDBHelper.moveToBoxByLabel(messageList.get(i), LABEL_MSG_TRASH);
                                count++;
                            }
                        }
                        makeDeleteToast(count);
                    }
                    refreshViewingInbox(messageDBHelper.getMessageListByLabel(currentBox));
                    switchTopBar(MODE_VIEWING);
                    break;
                case R.id.fragment_inbox_ib_inbox_function:
                    for (int i = messageSelectedList.size() - 1; i >= 0; i--) {
                        if (messageSelectedList.get(i)) {
                            // Check which mailbox user is currently in, trash box is different from the others
                            if (currentBox.equalsIgnoreCase(LABEL_MSG_TRASH)) {
                                messageDBHelper.moveToBoxByLabel(messageList.get(i), LABEL_MSG_INBOX);
                            } else {
                                // Check if mail is read or unread
                                if (ifSelectedRead) {
                                    messageDBHelper.markMailByLabel(messageList.get(i), LABEL_MSG_UNREAD);
                                } else {
                                    messageDBHelper.markMailByLabel(messageList.get(i), LABEL_MSG_READ);
                                }
                            }
                        }
                    }
                    refreshViewingInbox(messageDBHelper.getMessageListByLabel(currentBox));
                    switchTopBar(MODE_VIEWING);
                    break;
                case R.id.fragment_inbox_btn_unread:
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
                            tvTitle.setText(getString(R.string.inbox));
                            currentBox = LABEL_MSG_INBOX;
                            break;
                        case 1:
                            tvTitle.setText(getString(R.string.starred));
                            currentBox = LABEL_MSG_STAR;
                            break;
                        case 2:
                            tvTitle.setText(getString(R.string.trash));
                            currentBox = LABEL_MSG_TRASH;
                            break;
                    }
                    switchBoxFunctions(currentBox);
                    refreshViewingInbox(messageDBHelper.getMessageListByLabel(currentBox));
                    animUtilities.setlvAnimToVisible(lvMessages);
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
                        refreshViewingInboxOnUiThread();
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
                refreshViewingInbox(messageDBHelper.getMessageListByLabel(currentBox));
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

    private void refreshViewingInboxOnUiThread() {
        if (null != uiThread)
            uiThread.interrupt();

        uiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshViewingInbox(messageDBHelper.getMessageListByLabel(currentBox));
                    }
                });
            }
        });
        uiThread.start();
    }

    private void makeDeleteToast(int count) {
        Toast.makeText(context, getString(R.string.delete_caption1) + " " + count + " " + getString(R.string.delete_caption2), Toast.LENGTH_SHORT).show();
    }
}