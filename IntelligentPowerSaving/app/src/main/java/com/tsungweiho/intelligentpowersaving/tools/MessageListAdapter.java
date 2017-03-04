package com.tsungweiho.intelligentpowersaving.tools;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.fragments.InboxFragment;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtilities;

import java.util.ArrayList;

/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/26.
 */

public class MessageListAdapter extends BaseAdapter implements PubNubAPIConstants, DBConstants, FragmentTags {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Message> messageList;
    private ArrayList<Boolean> messageSelectedList;
    private int mode;

    // functions
    private TimeUtilities timeUtilities;
    private FragmentManager fm;
    private int MODE_VIEWING = 0;
    private int MODE_EDITING = 1;

    public MessageListAdapter(Context context, ArrayList<Message> messageList, ArrayList<Boolean> messageSelectedList, int mode) {
        this.context = context;
        this.messageList = messageList;
        this.messageSelectedList = messageSelectedList;
        this.mode = mode;
        timeUtilities = new TimeUtilities(context);
    }

    public void setMessageList(ArrayList<Message> messageList) {
        this.messageList = messageList;
    }

    public void setSelectedList(ArrayList<Boolean> messageSelectedList) {
        this.messageSelectedList = messageSelectedList;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        layoutInflater = LayoutInflater.from(context);
        final int newOrderPosition = getCount() - position - 1;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.obj_message_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.obj_message_list_item_view);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.obj_message_list_item_iv);
            viewHolder.tvSender = (TextView) convertView.findViewById(R.id.obj_message_list_item_tv_sender);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.obj_message_list_item_tv_title);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.obj_message_list_item_tv_content);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.obj_message_list_item_tv_time);
            viewHolder.ibStar = (ImageButton) convertView.findViewById(R.id.obj_message_list_item_ib);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArrayList<TextView> tvSet = new ArrayList<TextView>() {{
            add(viewHolder.tvTitle);
            add(viewHolder.tvContent);
            add(viewHolder.tvSender);
            add(viewHolder.tvTime);
        }};

        final Message message = messageList.get(newOrderPosition);
        viewHolder.tvSender.setText(message.getSender());
        viewHolder.tvTitle.setText(message.getTitle());
        viewHolder.tvContent.setText(message.getContent());

        // Set star icon
        if (message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[1].equalsIgnoreCase(LABEL_MSG_STAR)) {
            viewHolder.ibStar.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_follow));
        } else {
            viewHolder.ibStar.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_unfollow));
        }

        viewHolder.ibStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ifStar;
                if (message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[1].equalsIgnoreCase(LABEL_MSG_STAR)) {
                    viewHolder.ibStar.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_unfollow));
                    ifStar = false;
                } else {
                    viewHolder.ibStar.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_follow));
                    ifStar = true;
                }

                if (null == fm)
                    fm = ((MainActivity) MainActivity.getContext()).getSupportFragmentManager();

                InboxFragment inboxFragment = (InboxFragment) fm.findFragmentByTag(INBOX_FRAGMENT);
                inboxFragment.markMailStar(newOrderPosition, ifStar);
            }
        });

        // Check if in the same day to determine how to show the time
        if (message.getTime().split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(timeUtilities.getDate()))
            viewHolder.tvTime.setText(message.getTime().split(SEPARATOR_MSG_LABEL)[1]);
        else {
            viewHolder.tvTime.setText(message.getTime().split(SEPARATOR_MSG_LABEL)[0]);
        }

        // set read message as gray, unread message as white bold
        if (message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(LABEL_MSG_READ)) {
            for (int i = 0; i < tvSet.size(); i++) {
                tvSet.get(i).setTextColor(context.getResources().getColor(R.color.colorTint));
                tvSet.get(i).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
        } else {
            for (int i = 0; i < tvSet.size(); i++) {
                tvSet.get(i).setTextColor(context.getResources().getColor(R.color.white));
                tvSet.get(i).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        }

        // Viewing Mode
        if (mode == MODE_VIEWING) {
            viewHolder.imageView.setClickable(true);
            viewHolder.relativeLayout.setOnClickListener(null);

            // On long click: perform editing mode
            viewHolder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    initEditingMode(newOrderPosition);
                    return false;
                }
            });

            // On click on mail icon: perform editing mode
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initEditingMode(newOrderPosition);
                }
            });

            // On click: view mail details
            viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) MainActivity.getContext()).setMessageFragment(message, newOrderPosition);
                }
            });

            setImageViewByLabel(message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[3], viewHolder.imageView);
        } else if (mode == MODE_EDITING) {
            viewHolder.imageView.setClickable(false);
            viewHolder.relativeLayout.setOnLongClickListener(null);

            // On click in editing mode: select mail
            viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null == fm)
                        fm = ((MainActivity) MainActivity.getContext()).getSupportFragmentManager();

                    InboxFragment inboxFragment = (InboxFragment) fm.findFragmentByTag(INBOX_FRAGMENT);
                    if (messageSelectedList.get(newOrderPosition)) {
                        inboxFragment.setIndexSelected(newOrderPosition, Boolean.FALSE);
                    } else {
                        inboxFragment.setIndexSelected(newOrderPosition, Boolean.TRUE);
                    }
                }
            });

            setImageViewOnEditing(messageSelectedList.get(newOrderPosition), viewHolder.imageView);
        }

        return convertView;
    }

    private class ViewHolder {
        RelativeLayout relativeLayout;
        ImageView imageView;
        TextView tvSender;
        TextView tvTitle;
        TextView tvContent;
        TextView tvTime;
        ImageButton ibStar;
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

    private void initEditingMode(int position){
        if (null == fm)
            fm = ((MainActivity) MainActivity.getContext()).getSupportFragmentManager();

        InboxFragment inboxFragment = (InboxFragment) fm.findFragmentByTag(INBOX_FRAGMENT);
        inboxFragment.initEditingInbox(position, messageList);
    }

    private void setImageViewOnEditing(Boolean select, ImageView imageView) {
        imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_label_check));
        imageView.setBackground(context.getResources().getDrawable(R.drawable.background_circle_colorprimary));
        if (select) {
            imageView.setBackground(context.getResources().getDrawable(R.drawable.background_circle_colortint));
        }
    }
}
