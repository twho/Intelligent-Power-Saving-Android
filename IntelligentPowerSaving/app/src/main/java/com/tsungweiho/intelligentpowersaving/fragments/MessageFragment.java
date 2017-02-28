package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.MessageDBHelper;
import com.tsungweiho.intelligentpowersaving.databinding.FragmentMessageBinding;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtilities;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtilities;

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
    private ImageButton ibBack, ibDelete, ibRead;
    private ImageView ivSender;

    // Functions
    private FragmentMessageBinding binding;
    private Context context;
    private ArrayList<String> messageInfo;
    private int position;
    private Message currentMessage;
    private String currentBox;
    private static ImageUtilities imageUtilities;
    private static TimeUtilities timeUtilities;
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
        imageUtilities = new ImageUtilities(context);
        timeUtilities = new TimeUtilities(context);
        messageFragmentListener = new MessageFragmentListener();

        // Mark message as read when getting in the message screen
        currentMessage = messageDBHelper.getMessageByUnId(messageInfo.get(0));
        messageDBHelper.markMailByLabel(currentMessage, LABEL_MSG_READ);
        this.position = Integer.parseInt(messageInfo.get(1));
        this.currentBox = currentMessage.getInboxLabel().split(SEPARATOR_MSG_LABEL)[1];
        binding.setMessage(currentMessage);
        Log.d("asdasdas", currentMessage.getInboxLabel().split(SEPARATOR_MSG_LABEL) + "");
        Log.d("asdasdas", currentMessage.getInboxLabel().split(SEPARATOR_MSG_LABEL)[1]);

        ibBack = (ImageButton) view.findViewById(R.id.fragment_message_ib_back);
        ibDelete = (ImageButton) view.findViewById(R.id.fragment_message_ib_delete);
        ibRead = (ImageButton) view.findViewById(R.id.fragment_message_ib_read);
        ivSender = (ImageView) view.findViewById(R.id.fragment_message_iv_sender);

        setImageViewByLabel(currentMessage.getInboxLabel().split(SEPARATOR_MSG_LABEL)[2], ivSender);
        setAllListeners();
    }

    @BindingAdapter({"bind:inboxLabel"})
    public static void loadImage(final ImageView imageView, final String inboxLabel) {

    }

    @BindingAdapter({"bind:time"})
    public static void loadTime(final TextView textView, final String time) {
        if (time.split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(timeUtilities.getDate()))
            textView.setText(time.split(SEPARATOR_MSG_LABEL)[1]);
        else {
            textView.setText(time.split(SEPARATOR_MSG_LABEL)[0]);
        }
    }

    private void setAllListeners() {
        ibBack.setOnClickListener(messageFragmentListener);
        ibDelete.setOnClickListener(messageFragmentListener);
        ibRead.setOnClickListener(messageFragmentListener);
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
            }
        }
    }
}
