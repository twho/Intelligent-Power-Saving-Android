package com.tsungweiho.intelligentpowersaving.tools;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtilities;

import java.util.ArrayList;

/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/18.
 */

public class MessageListAdapter extends BaseAdapter implements PubNubAPIConstants {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Message> messageList;
    private TimeUtilities timeUtilities;

    public MessageListAdapter(Context context, ArrayList<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
        timeUtilities = new TimeUtilities(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        layoutInflater = LayoutInflater.from(context);
        int newOrderPosition = getCount() - position - 1;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.obj_message_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.frameLayout = (FrameLayout) convertView.findViewById(R.id.obj_message_list_item_view);
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

        viewHolder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Message message = messageList.get(newOrderPosition);
        viewHolder.tvSender.setText(message.getSender());
        viewHolder.tvTitle.setText(message.getTitle());
        viewHolder.tvContent.setText(message.getContent());

        // Check if in the same day to determine how to show the time
        if (message.getTime().split(",")[0].equalsIgnoreCase(timeUtilities.getDate()))
            viewHolder.tvTime.setText(message.getTime().split(",")[1]);
        else {
            viewHolder.tvTime.setText(message.getTime().split(",")[0]);
        }

        Log.d("asdasd", message.getInboxLabel());
        setImageViewByLable(message.getInboxLabel().split(",")[2], viewHolder.imageView);

        return convertView;
    }

    private void setImageViewByLable(String label, ImageView imageView) {
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

    private class ViewHolder {
        FrameLayout frameLayout;
        ImageView imageView;
        TextView tvSender;
        TextView tvTitle;
        TextView tvContent;
        TextView tvTime;
        ImageButton ibStar;
    }
}
