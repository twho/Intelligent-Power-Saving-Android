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
import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.ListAdapterConstants;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.MessageDBHelper;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;
import com.tsungweiho.intelligentpowersaving.adapters.DrawerListAdapter;
import com.tsungweiho.intelligentpowersaving.adapters.MessageListAdapter;
import com.tsungweiho.intelligentpowersaving.tools.PubNubHelper;
import com.tsungweiho.intelligentpowersaving.utils.SharedPrefsUtils;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtils;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;

/**
 * Fragment for user to receive important messages from admin
 * <p>
 * This fragment is the user interface that user can receive messages and can use as mailbox
 *
 * @author Tsung Wei Ho
 * @version 0101.2018
 * @since 1.0.0
 */
public class InboxFragment extends Fragment implements ListAdapterConstants, PubNubAPIConstants, DBConstants {
    // UI Views
    private View view;
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
    private ImageUtils imageUtils;
    private AnimUtils animUtils;
    private MessageDBHelper msgDBHelper;
    private InboxFragmentListener inboxFragmentListener;
    private Thread uiThread;
    private MessageListAdapter msgListAdapter;
    private ArrayList<Message> msgList;
    private String currentBox;
    private int REFRESH_DELAY = 5000;
    private boolean isSelectedRead;
    private boolean isUnreadShown;

    public static boolean isActive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inbox, container, false);

        context = IPowerSaving.getContext();

        init();

        return view;
    }

    /**
     * Init functions and UIs in InboxFragment
     */
    private void init() {
        inboxFragmentListener = new InboxFragmentListener();
        msgDBHelper = new MessageDBHelper(context);

        // Singleton classes
        animUtils = AnimUtils.getInstance();
        imageUtils = ImageUtils.getInstance();

        isUnreadShown = false;

        findViews();

        setListeners();
    }

    /**
     * Link views in layout file to view controller, no need to cast views since compile with SDK 26
     */
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
                PubNubHelper.getInstance().getChannelHistory(IPowerSaving.getPubNub(), ActiveChannels.MESSAGE, new PubNubHelper.OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(boolean isSuccessful) {
                        pullToRefreshView.setRefreshing(false);
                    }
                });

                // Set refresh timeout 5 sec
                pullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
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

    /**
     * Set all listeners in InboxFragment
     */
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

        isActive = true;

        // use the data already saved
        MyAccountInfo myAccountInfo = SharedPrefsUtils.getInstance().getMyAccountInfo();
        imageUtils.setRoundedCornerImageViewFromUrl(myAccountInfo.getImageUrl(), ivDrawerPic, imageUtils.IMG_CIRCULAR);
        tvMail.setText(myAccountInfo.getEmail());

        // init inbox
        currentBox = SharedPrefsUtils.getInstance().getPreferenceString(SharedPrefsUtils.getInstance().CURRENT_INBOX, DBConstants.LABEL_MSG_INBOX);
        msgList = msgDBHelper.getMessageListByLabel(currentBox);
        msgListAdapter = new MessageListAdapter(context, msgList, InboxMode.VIEW);

        lvMessages.setAdapter(msgListAdapter);
        animUtils.fadeinToVisible(lvMessages, animUtils.MID_ANIM_DURATION);

        tvTitle.setText(currentBox.substring(0, 1).toUpperCase() + currentBox.substring(1));
        tvNoMail.setVisibility(msgList.size() == 0 ? View.VISIBLE : ViewGroup.GONE);
    }

    /**
     * Refresh inbox listView on viewing mode
     *
     * @param messageList the msgList to be displayed in inbox
     */
    private void refreshViewingInbox(ArrayList<Message> messageList) {
        this.msgList = messageList;

        switchTopBar(InboxMode.VIEW);
        msgListAdapter.setMode(InboxMode.VIEW);
        msgListAdapter.setMsgList(messageList);

        tvNoMail.setVisibility(messageList.size() == 0 ? View.VISIBLE : ViewGroup.GONE);
    }

    /**
     * Setup inbox listView on editing mode
     *
     * @param messageList the msgList to be displayed in inbox
     */
    public void initEditingInbox(ArrayList<Message> messageList) {
        this.msgList = messageList;
        switchTopBar(InboxMode.EDIT);
    }

    /**
     * Star or unstar the inbox mail
     *
     * @param position the position of the mail in the list
     * @param isStar   the boolean indicate star or unstar action
     */
    public void markMailStar(int position, boolean isStar) {
        msgDBHelper.starMailByLabel(msgList.get(position), isStar ? LABEL_MSG_STAR : LABEL_MSG_UNSTAR);
        this.msgList = msgDBHelper.getMessageListByLabel(currentBox);
    }

    /**
     * Change top un/read button according to selected mail items
     *
     * @param isSelectedRead the boolean indicate read or unread
     */
    public void setIsSelectedRead(boolean isSelectedRead) {
        this.isSelectedRead = isSelectedRead;
        if (!currentBox.equalsIgnoreCase(LABEL_MSG_TRASH))
            ibInboxFunction.setImageDrawable(context.getResources().getDrawable(isSelectedRead ? R.mipmap.ic_unread : R.mipmap.ic_read));
    }

    /**
     * Switch top function bar based on InboxMode
     *
     * @param mode the current InboxMode
     */
    private void switchTopBar(InboxMode mode) {
        // Mailbox functions, top bar buttons are padding 15%
        switch (mode) {
            case VIEW:
                llEditing.setVisibility(View.GONE);
                btnUnread.setVisibility(View.VISIBLE);
                animUtils.rotateToIcon(ibOptions, R.mipmap.ic_options);
                break;
            case EDIT:
                llEditing.setVisibility(View.VISIBLE);
                btnUnread.setVisibility(View.GONE);
                animUtils.rotateToIcon(ibOptions, R.mipmap.ic_back);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        isActive = false;

        // Clean memory
        msgDBHelper.closeDB();

        // save which box the user is using
        SharedPrefsUtils.getInstance().savePreferenceString(SharedPrefsUtils.getInstance().CURRENT_INBOX, currentBox);
    }

    /**
     * All listeners used in InboxFragment
     */
    private class InboxFragmentListener implements View.OnClickListener, AdapterView.OnItemClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_inbox_ib_options:
                    // Change functions based on current mode
                    switch (msgListAdapter.getMode()) {
                        case VIEW:
                            drawer.openDrawer(Gravity.LEFT);
                            break;
                        case EDIT:
                            refreshViewingInbox(msgDBHelper.getMessageListByLabel(currentBox));
                            break;
                    }
                    break;
                case R.id.fragment_inbox_ib_delete:
                    // If the mail is in trash box, the delete button will permanently delete the mail
                    int count = 0;
                    for (int i = msgListAdapter.getMsgSelectedList().size() - 1; i >= 0; i--) {
                        if (currentBox.equalsIgnoreCase(LABEL_MSG_TRASH) && msgListAdapter.getMsgSelectedList().get(i)) {
                            msgDBHelper.deleteByUniqueId(msgList.get(i).getUniqueId());
                        } else {
                            if (msgListAdapter.getMsgSelectedList().get(i))
                                count += msgDBHelper.moveDirByLabel(msgList.get(i), LABEL_MSG_TRASH);
                        }
                    }

                    if (!currentBox.equalsIgnoreCase(LABEL_MSG_TRASH))
                        Toast.makeText(context, getString(R.string.delete_caption1) + " " + count + " " + getString(R.string.delete_caption2), Toast.LENGTH_SHORT).show();

                    refreshViewingInbox(msgDBHelper.getMessageListByLabel(currentBox));
                    switchTopBar(InboxMode.VIEW);
                    break;
                case R.id.fragment_inbox_ib_inbox_function:
                    for (int i = msgListAdapter.getMsgSelectedList().size() - 1; i >= 0; i--) {
                        if (msgListAdapter.getMsgSelectedList().get(i)) {
                            if (currentBox.equalsIgnoreCase(LABEL_MSG_TRASH)) // Check which mailbox user is currently in, trash box is different from the others
                                msgDBHelper.moveDirByLabel(msgList.get(i), LABEL_MSG_INBOX);
                            else // Check if mail is read or unread
                                msgDBHelper.markMailByLabel(msgList.get(i), isSelectedRead ? LABEL_MSG_UNREAD : LABEL_MSG_READ);
                        }
                    }

                    refreshViewingInbox(msgDBHelper.getMessageListByLabel(currentBox));
                    switchTopBar(InboxMode.VIEW);
                    break;
                case R.id.fragment_inbox_btn_unread:
                    btnUnread.setText(isUnreadShown ? getString(R.string.unread) : getString(R.string.all_mails));
                    refreshViewingInbox(isUnreadShown ? msgDBHelper.getMessageListByLabel(currentBox) : msgDBHelper.getUnreadMessageListInBox(msgList));

                    // Alternate the flag
                    isUnreadShown = !isUnreadShown;
                    break;
                case R.id.fragment_inbox_fab_write:
                    ((MainActivity) getActivity()).setReportFragment();
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
                            // TODO function to be developed
                            break;
                        case 3:
                            tvTitle.setText(getString(R.string.trash));
                            currentBox = LABEL_MSG_TRASH;
                            break;
                    }

                    // If the mailbox is trash, the button is changed to be move back
                    ibInboxFunction.setImageDrawable(context.getResources().getDrawable(currentBox.equalsIgnoreCase(LABEL_MSG_TRASH) ? R.mipmap.ic_move_back : R.mipmap.ic_unread));

                    refreshViewingInbox(msgDBHelper.getMessageListByLabel(currentBox));
                    animUtils.fadeinToVisible(lvMessages, animUtils.MID_ANIM_DURATION);

                    super.onDrawerClosed(drawerView);
                }
            });
            drawer.closeDrawer(llDrawer);
        }
    }

    /**
     * Refresh inbox on viewing mode
     */
    public void refreshViewingInboxOnUiThread() {
        if (null != uiThread)
            uiThread.interrupt();

        uiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshViewingInbox(msgDBHelper.getMessageListByLabel(currentBox));
                    }
                });
            }
        });
        uiThread.start();
    }
}