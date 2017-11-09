package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.EventDBHelper;
import com.tsungweiho.intelligentpowersaving.databases.MessageDBHelper;
import com.tsungweiho.intelligentpowersaving.databinding.FragmentMessageBinding;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/18.
 */

public class MessageFragment extends Fragment implements FragmentTags, DBConstants, PubNubAPIConstants {
    private String TAG = "MessageFragment";

    // Settings Fragment View
    private View view;

    // UI views
    private ImageButton ibBack, ibDelete, ibRead, ibStar;
    private ImageView ivSender;
    private static FrameLayout imgLayout;
    private static ProgressBar pbImg;

    // Functions
    private FragmentMessageBinding binding;
    private FragmentManager fm;
    private Context context;
    private ArrayList<String> messageInfo;
    private int position;

    private Message currentMessage;
    private String currentBox;
    private MessageDBHelper messageDBHelper;
    private MessageFragmentListener messageFragmentListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);
        view = binding.getRoot();

        context = MainActivity.getContext();
        this.messageInfo = this.getArguments().getStringArrayList(MESSAGE_FRAGMENT_KEY);
        init();
        return view;
    }

    private void init() {
        messageDBHelper = new MessageDBHelper(context);
        messageFragmentListener = new MessageFragmentListener();

        // Mark message as read when getting in the message screen
        currentMessage = messageDBHelper.getMessageByUnId(messageInfo.get(0));
        messageDBHelper.markMailByLabel(currentMessage, LABEL_MSG_READ);
        this.position = Integer.parseInt(messageInfo.get(1));
        this.currentBox = currentMessage.getInboxLabel().split(SEPARATOR_MSG_LABEL)[2];
        binding.setMessage(currentMessage);

        findViews();

        setImageViewByLabel(currentMessage.getInboxLabel().split(SEPARATOR_MSG_LABEL)[3], ivSender);
        setAllListeners();
    }

    private void findViews() {
        ibBack = (ImageButton) view.findViewById(R.id.fragment_message_ib_back);
        ibDelete = (ImageButton) view.findViewById(R.id.fragment_message_ib_delete);
        ibRead = (ImageButton) view.findViewById(R.id.fragment_message_ib_read);
        ibStar = (ImageButton) view.findViewById(R.id.fragment_home_ib_following);
        ivSender = (ImageView) view.findViewById(R.id.fragment_message_iv_sender);
        imgLayout = (FrameLayout) view.findViewById(R.id.fragment_message_layout_img);
        pbImg = (ProgressBar) view.findViewById(R.id.fragment_message_pb_img);
    }

    @BindingAdapter({"bind:inboxLabel"})
    public static void loadImage(final ImageView imageView, final String inboxLabel) {
        if (inboxLabel.split(SEPARATOR_MSG_LABEL)[3].matches(".*\\d+.*")) {
            imgLayout.setVisibility(View.VISIBLE);
            EventDBHelper eventDBHelper = new EventDBHelper(MainActivity.getContext());

            String url = eventDBHelper.getEventByUnId(inboxLabel.split(SEPARATOR_MSG_LABEL)[3]).getImage();
            ImageUtils.getInstance().setImageViewFromUrl(url, imageView, pbImg);
        } else {
            imgLayout.setVisibility(View.GONE);
        }
    }

    @BindingAdapter({"bind:star"})
    public static void loadStar(final ImageButton imageButton, final String inboxLabel) {
        if (inboxLabel.split(SEPARATOR_MSG_LABEL)[1].equalsIgnoreCase(LABEL_MSG_STAR)) {
            imageButton.setImageDrawable(MainActivity.getContext().getResources().getDrawable(R.mipmap.ic_follow));
        } else {
            imageButton.setImageDrawable(MainActivity.getContext().getResources().getDrawable(R.mipmap.ic_unfollow));
        }
    }

    @BindingAdapter({"bind:time"})
    public static void loadTime(final TextView textView, final String time) {
        if (time.split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(TimeUtils.getInstance().getDate()))
            textView.setText(time.split(SEPARATOR_MSG_LABEL)[1]);
        else {
            textView.setText(time.split(SEPARATOR_MSG_LABEL)[0]);
        }
    }

    private void setAllListeners() {
        ibBack.setOnClickListener(messageFragmentListener);
        ibDelete.setOnClickListener(messageFragmentListener);
        ibRead.setOnClickListener(messageFragmentListener);
        ibStar.setOnClickListener(messageFragmentListener);
    }

    private void setImageViewByLabel(String label, ImageView imageView) {
        Drawable drawable;
        switch (label) {
            case MESSAGE_LABEL_ANNOUNCEMENT:
                drawable = context.getResources().getDrawable(R.mipmap.ic_label_announcement);
                imageView.setBackground(context.getResources().getDrawable(R.drawable.background_circle_teal));
                break;
            case MESSAGE_LABEL_WARNING:
                drawable = context.getResources().getDrawable(R.mipmap.ic_label_warning);
                imageView.setBackground(context.getResources().getDrawable(R.drawable.background_circle_yellow));
                break;
            case MESSAGE_LABEL_EMERGENCY:
                drawable = context.getResources().getDrawable(R.mipmap.ic_label_emergency);
                imageView.setBackground(context.getResources().getDrawable(R.drawable.background_circle_red));
                break;
            default:
                drawable = context.getResources().getDrawable(R.mipmap.ic_label_event);
                imageView.setBackground(context.getResources().getDrawable(R.drawable.background_circle_lightred));
                break;
        }
        imageView.setImageDrawable(drawable);
    }

    private class MessageFragmentListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_message_ib_back:
                    ((MainActivity) MainActivity.getContext()).setFragment(INBOX_FRAGMENT);
                    break;
                case R.id.fragment_message_ib_delete:
                    messageDBHelper.moveToBoxByLabel(currentMessage, LABEL_MSG_TRASH);
                    if (position + 1 < messageDBHelper.getMessageListByLabel(currentBox).size()) {
                        currentMessage = messageDBHelper.getMessageListByLabel(currentBox).get(position + 1);
                        setImageViewByLabel(currentMessage.getInboxLabel().split(SEPARATOR_MSG_LABEL)[3], ivSender);
                        binding.setMessage(currentMessage);
                        position = position + 1;
                    } else {
                        ((MainActivity) MainActivity.getContext()).setFragment(INBOX_FRAGMENT);
                    }
                    break;
                case R.id.fragment_message_ib_read:
                    messageDBHelper.markMailByLabel(currentMessage, LABEL_MSG_UNREAD);
                    ((MainActivity) MainActivity.getContext()).setFragment(INBOX_FRAGMENT);
                    break;
                case R.id.fragment_home_ib_following:
                    currentMessage = messageDBHelper.getMessageByUnId(messageInfo.get(0));
                    if (currentMessage.getInboxLabel().split(SEPARATOR_MSG_LABEL)[1].equalsIgnoreCase(LABEL_MSG_STAR)) {
                        ibStar.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_unfollow));
                        messageDBHelper.starMailByLabel(currentMessage, LABEL_MSG_UNSTAR);
                    } else {
                        ibStar.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_follow));
                        messageDBHelper.starMailByLabel(currentMessage, LABEL_MSG_STAR);
                    }

                    break;
            }
        }
    }
}
