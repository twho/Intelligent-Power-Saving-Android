package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
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
import com.tsungweiho.intelligentpowersaving.utils.AnimUtils;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtils;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Created by Michael Ho on 2015/4/15.
 * Updated by Michael Ho on 2017/12/22.
 */

public class InboxFragment extends Fragment implements DrawerListConstants, PubNubAPIConstants, DBConstants {

    // Message Fragment View
    private View view;

    // UI Views
    private DrawerLayout drawer;
    private LinearLayout llDrawer, llEditing;
    private TextView tvTitle, tvMail, tvNoMail;
    private ListView navList, lvMessages;
    private PullToRefreshView pullToRefreshView;
    private ImageButton ibOptions, ibDelete, ibInboxFunction;
    private Button btnUnread;
    private ImageView ivDrawerPic;
    private FloatingActionButton fabWrite;

    // Functions
    private Context context;
    private ImageUtils imageUtilities;
    private AnimUtils animUtils;
    private MessageDBHelper messageDBHelper;
    private InboxFragmentListener inboxFragmentListener;
    private Thread uiThread;

    // Mailbox functions, top bar buttons are padding 15%
    private MessageListAdapter messageListAdapter;
    private ArrayList<Message> messageList;
    private ArrayList<Boolean> messageSelectedList;
    private int MODE_VIEWING = 0;
    private int MODE_EDITING = 1;
    private int REFRESH_DELAY = 2000;
    private String currentBox;
    private Boolean ifSelectedRead;
    private boolean ifShowUnread;
    public static boolean ifFragmentActive;

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

        // Singleton classes
        animUtils = AnimUtils.getInstance();
        imageUtilities = ImageUtils.getInstance();

        ifShowUnread = false;

        findViews();

        setListeners();
    }

    // Compile with SDK 26, no need to cast views
    private void findViews() {
        DrawerListAdapter drawerListAdapter = new DrawerListAdapter(context, MESSAGE_DRAWER, MESSAGE_DRAWER_IMG);
        drawer = view.findViewById(R.id.fragment_inbox_drawer_layout);
        navList = view.findViewById(R.id.fragment_inbox_drawer_lv);
        navList.setAdapter(drawerListAdapter);

        lvMessages = view.findViewById(R.id.fragment_inbox_lv_message);
        pullToRefreshView = view.findViewById(R.id.fragment_inbox_pulltorefresh);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Get pubnub channel history
                        pullToRefreshView.setRefreshing(false);
                    }
                }, REFRESH_DELAY);
            }
        });

        tvMail = view.findViewById(R.id.fragment_inbox_drawer_tv_mail);
        tvNoMail = view.findViewById(R.id.fragmnet_inbox_tv_no_mail);
        tvTitle = view.findViewById(R.id.fragment_inbox_tv_title);
        tvTitle.setText(context.getString(R.string.inbox));

        ibOptions = view.findViewById(R.id.fragment_inbox_ib_options);
        ibInboxFunction = view.findViewById(R.id.fragment_inbox_ib_inbox_function);
        ibDelete = view.findViewById(R.id.fragment_inbox_ib_delete);

        btnUnread = view.findViewById(R.id.fragment_inbox_btn_unread);

        llDrawer = view.findViewById(R.id.fragment_inbox_ll_drawer);
        llEditing = view.findViewById(R.id.fragment_inbox_layout_editing);

        ivDrawerPic = view.findViewById(R.id.fragment_inbox_drawer_iv);

        fabWrite = view.findViewById(R.id.fragment_inbox_fab_write);
    }

    private void setListeners() {
        ibOptions.setOnClickListener(inboxFragmentListener);
        navList.setOnItemClickListener(inboxFragmentListener);
        ibDelete.setOnClickListener(inboxFragmentListener);
        ibInboxFunction.setOnClickListener(inboxFragmentListener);
        btnUnread.setOnClickListener(inboxFragmentListener);
        fabWrite.setOnClickListener(inboxFragmentListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        ifFragmentActive = true;

        // use the data already saved
        MyAccountInfo myAccountInfo = SharedPreferencesManager.getInstance().getMyAccountInfo();
        ivDrawerPic.setImageBitmap(imageUtilities.getRoundedCroppedBitmap(imageUtilities.decodeBase64ToBitmap(myAccountInfo.getImageUrl())));
        tvMail.setText(myAccountInfo.getEmail());

        // init inbox
        currentBox = SharedPreferencesManager.getInstance().getCurrentMessagebox();
        messageList = messageDBHelper.getMessageListByLabel(currentBox);
        messageListAdapter = new MessageListAdapter(context, messageList, messageSelectedList, MODE_VIEWING);
        initInbox(messageList);

        lvMessages.setAdapter(messageListAdapter);
        animUtils.setlvAnimToVisible(lvMessages);

        tvTitle.setText(currentBox.substring(0, 1).toUpperCase() + currentBox.substring(1));
        tvNoMail.setVisibility(messageList.size() == 0 ? View.VISIBLE : ViewGroup.GONE);
    }

    // Initialize message list view
    // Should be executed every time loading the message list
    private void initInbox(ArrayList<Message> messageList) {
        if (null != messageSelectedList)
            messageSelectedList = null;
        messageSelectedList = new ArrayList<>(Arrays.asList(new Boolean[messageList.size()]));
        Collections.fill(messageSelectedList, Boolean.FALSE);

        // Data set change
        messageListAdapter.setMessageList(messageList);
        messageListAdapter.setSelectedList(messageSelectedList);
    }

    // Refresh message list view
    // Should be executed every time users have action on the message list
    private void refreshViewingInbox(ArrayList<Message> messageList) {
        this.messageList = messageList;
        initInbox(messageList);
        switchTopBar(MODE_VIEWING);
        messageListAdapter.setMode(MODE_VIEWING);

        tvNoMail.setVisibility(messageList.size() == 0 ? View.VISIBLE : ViewGroup.GONE);
    }

    // Be executed only When firstly start editing mode
    public void initEditingInbox(int initSelectedPosition, ArrayList<Message> messageList) {
        this.messageList = messageList;
        initInbox(messageList);
        switchTopBar(MODE_EDITING);
        messageSelectedList.set(initSelectedPosition, Boolean.TRUE);
        messageListAdapter.setMode(MODE_EDITING);
        messageListAdapter.setSelectedList(messageSelectedList);
        setIfSelectedRead(messageList.get(initSelectedPosition).getInboxLabel().split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(LABEL_MSG_READ));
    }

    // Be executed When users selected another mail item
    public void setIndexSelected(int selectedPosition, Boolean ifSelected) {
        messageSelectedList.set(selectedPosition, ifSelected);
        messageListAdapter.setSelectedList(messageSelectedList);
        setIfSelectedRead(messageList.get(selectedPosition).getInboxLabel().split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(LABEL_MSG_READ));
    }

    public void markMailStar(int position, boolean ifStar) {
        String strStar = ifStar ? LABEL_MSG_STAR : LABEL_MSG_UNSTAR;

        messageDBHelper.starMailByLabel(messageList.get(position), strStar);
        messageList = messageDBHelper.getMessageListByLabel(currentBox);
        refreshViewingInbox(messageList);
    }

    // Change top un/read button according to selected mail items
    private void setIfSelectedRead(Boolean ifSelectedRead) {
        this.ifSelectedRead = ifSelectedRead;
        if (!currentBox.equalsIgnoreCase(LABEL_MSG_TRASH)) {
            ibInboxFunction.setImageDrawable(context.getResources().getDrawable(ifSelectedRead ? R.mipmap.ic_read : R.mipmap.ic_unread));
        }
    }

    private void switchTopBar(int mode) {
        if (mode == MODE_EDITING) {
            llEditing.setVisibility(View.VISIBLE);
            btnUnread.setVisibility(View.GONE);
            animUtils.rotateToIcon(ibOptions, R.mipmap.ic_back);
        } else if (mode == MODE_VIEWING) {
            llEditing.setVisibility(View.GONE);
            btnUnread.setVisibility(View.VISIBLE);
            animUtils.rotateToIcon(ibOptions, R.mipmap.ic_options);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        ifFragmentActive = false;

        // Clean memory
        messageDBHelper.closeDB();

        // save which box the user is using
        SharedPreferencesManager.getInstance().saveCurrentMessageBox(currentBox);
    }

    private class InboxFragmentListener implements View.OnClickListener, AdapterView.OnItemClickListener {

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
                                messageDBHelper.markMailByLabel(messageList.get(i), ifSelectedRead ? LABEL_MSG_UNREAD : LABEL_MSG_READ);
                            }
                        }
                    }
                    refreshViewingInbox(messageDBHelper.getMessageListByLabel(currentBox));
                    switchTopBar(MODE_VIEWING);
                    break;
                case R.id.fragment_inbox_btn_unread:
                    btnUnread.setText(ifShowUnread ? getString(R.string.unread) : getString(R.string.all_mails));
                    refreshViewingInbox(ifShowUnread ? messageDBHelper.getMessageListByLabel(currentBox) : messageDBHelper.getUnreadMessageListInBox(messageList));
                    ifShowUnread = !ifShowUnread;
                    break;
                case R.id.fragment_inbox_fab_write:

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
                            break;
                        case 3:
                            tvTitle.setText(getString(R.string.trash));
                            currentBox = LABEL_MSG_TRASH;
                            break;
                    }

                    // If the mailbox is trash, the button is changed to be move back
                    ibInboxFunction.setImageDrawable(context.getResources().getDrawable(currentBox.equalsIgnoreCase(LABEL_MSG_TRASH) ? R.mipmap.ic_move_back : R.mipmap.ic_unread));

                    refreshViewingInbox(messageDBHelper.getMessageListByLabel(currentBox));
                    animUtils.setlvAnimToVisible(lvMessages);
                    super.onDrawerClosed(drawerView);
                }
            });
            drawer.closeDrawer(llDrawer);
        }
    }

    public void refreshViewingInboxOnUiThread() {
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

    public void refreshViewingFromService() {
        refreshViewingInbox(messageDBHelper.getMessageListByLabel(currentBox));
    }
}