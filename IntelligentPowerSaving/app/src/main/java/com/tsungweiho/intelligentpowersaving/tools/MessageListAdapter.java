package com.tsungweiho.intelligentpowersaving.tools;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tsungweiho.intelligentpowersaving.IntelligentPowerSaving;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databinding.ObjMessageListItemBinding;
import com.tsungweiho.intelligentpowersaving.fragments.InboxFragment;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Class in background to subscribe to PubNub channels
 *
 * This class is the user interface of mail overview in InboxFragment
 *
 * @author Tsung Wei Ho
 * @version 1228.2017
 * @since 1.0.0
 */
public class MessageListAdapter extends BaseAdapter implements PubNubAPIConstants, DBConstants, FragmentTags {
    private Context context;

    private ArrayList<Message> messageList;
    private ArrayList<Boolean> messageSelectedList;
    private int mode;

    // functions
    private FragmentManager fm;

    public MessageListAdapter(Context context, ArrayList<Message> messageList, ArrayList<Boolean> messageSelectedList, int mode) {
        this.context = context;
        this.messageList = messageList;
        this.messageSelectedList = messageSelectedList;
        this.mode = mode;
    }

    public void setMessageList(ArrayList<Message> messageList) {
        this.messageList = messageList;
    }

    public void setSelectedList(ArrayList<Boolean> messageSelectedList) {
        this.messageSelectedList = messageSelectedList;
        this.notifyDataSetChanged();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        this.notifyDataSetChanged();
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
        final int newOrderPosition = getCount() - position - 1;

        if (convertView == null) {
            ObjMessageListItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.obj_message_list_item, parent, false);

            viewHolder = new ViewHolder(itemBinding);
            convertView = viewHolder.view;

            viewHolder.imageView = convertView.findViewById(R.id.obj_message_list_item_iv);
            viewHolder.relativeLayout = convertView.findViewById(R.id.obj_message_list_item_view);
            viewHolder.imageView = convertView.findViewById(R.id.obj_message_list_item_iv);
            viewHolder.ibStar = convertView.findViewById(R.id.obj_message_list_item_ib);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Message message = messageList.get(newOrderPosition);
        viewHolder.binding.setMessage(message);

        int MODE_VIEWING = 0;
        int MODE_EDITING = 1;

        if (mode == MODE_VIEWING)
            setImageView(viewHolder.imageView, message.getSender(), message.getInboxLabel());

        // Set star icon
        boolean isStarred = message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[1].equalsIgnoreCase(LABEL_MSG_STAR);
        viewHolder.ibStar.setImageDrawable(context.getResources().getDrawable(isStarred ? R.mipmap.ic_follow : R.mipmap.ic_unfollow));

        viewHolder.ibStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isStarred = message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[1].equalsIgnoreCase(LABEL_MSG_STAR);
                viewHolder.ibStar.setImageDrawable(context.getResources().getDrawable(isStarred ? R.mipmap.ic_unfollow : R.mipmap.ic_follow));
                isStarred = !isStarred;

                if (null == fm)
                    fm = ((MainActivity) MainActivity.getContext()).getSupportFragmentManager();

                InboxFragment inboxFragment = (InboxFragment) fm.findFragmentByTag(INBOX_FRAGMENT);
                inboxFragment.markMailStar(newOrderPosition, isStarred);
            }
        });

        // Viewing Mode
        if (mode == MODE_VIEWING) {
            viewHolder.imageView.setClickable(true);
            viewHolder.relativeLayout.setOnClickListener(null);

            // On long click: perform editing mode
            viewHolder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    startEditingMode(newOrderPosition);
                    return false;
                }
            });

            // On click on mail icon: perform editing mode
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startEditingMode(newOrderPosition);
                }
            });

            // On click: view mail details
            viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) MainActivity.getContext()).setMessageFragment(message, newOrderPosition);
                }
            });

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
                    inboxFragment.setIndexSelected(newOrderPosition, !messageSelectedList.get(newOrderPosition));
                }
            });

            setImageViewOnEditing(messageSelectedList.get(newOrderPosition), viewHolder.imageView);
        }

        return convertView;
    }

    /**
     * All UI widgets in each message list item
     */
    private class ViewHolder {
        RelativeLayout relativeLayout;
        ImageView imageView;
        ImageButton ibStar;

        View view;
        ObjMessageListItemBinding binding;

        ViewHolder(ObjMessageListItemBinding binding) {
            this.view = binding.getRoot();
            this.binding = binding;
        }
    }

    /**
     * Set the icon of the mail sender or resource
     *
     * @param imageView  the icon of the mail sender or resource
     * @param inboxLabel the label of the mail
     */
    private void setImageView(ImageView imageView, String sender, String inboxLabel){
        Context context = IntelligentPowerSaving.getContext();
        String label = inboxLabel.split(SEPARATOR_MSG_LABEL)[3];

        switch (label) {
            case MESSAGE_LABEL_ANNOUNCEMENT:
                setAdminSenderIcon(imageView, R.mipmap.ic_label_announcement, R.drawable.background_circle_teal);
                break;
            case MESSAGE_LABEL_WARNING:
                setAdminSenderIcon(imageView, R.mipmap.ic_label_warning, R.drawable.background_circle_yellow);
                break;
            case MESSAGE_LABEL_EMERGENCY:
                setAdminSenderIcon(imageView, R.mipmap.ic_label_emergency, R.drawable.background_circle_red);
                break;
            default:
                loadImgFromFirebase(sender, label, imageView);
                imageView.setBackground(context.getResources().getDrawable(R.drawable.background_circle_lightred));
                break;
        }
    }

    private void loadImgFromFirebase(String poster, String imgUrl, final ImageView imageView){
        FirebaseManager.getInstance().downloadProfileImg(poster + "/" + imgUrl, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageUtils.getInstance().setRoundedCornerImageViewFromUrl(uri.toString(), imageView, ImageUtils.getInstance().IMG_TYPE_PROFILE);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setImageDrawable(IntelligentPowerSaving.getContext().getResources().getDrawable(R.mipmap.ic_preload_profile));
            }
        });
    }

    private void setAdminSenderIcon(ImageView imageView, int icon, int background){
        imageView.setImageDrawable(context.getResources().getDrawable(icon));
        imageView.setBackground(context.getResources().getDrawable(background));
    }

    @BindingAdapter({"bind:sender", "bind:inboxLabel"})
    public static void setSender(TextView textView, String sender, String inboxLabel) {
        setTextView(textView, sender, inboxLabel);
    }

    @BindingAdapter({"bind:title", "bind:inboxLabel"})
    public static void setTitle(TextView textView, String title, String inboxLabel) {
        setTextView(textView, title, inboxLabel);
    }

    @BindingAdapter({"bind:content", "bind:inboxLabel"})
    public static void setContent(TextView textView, String content, String inboxLabel) {
        setTextView(textView, content, inboxLabel);
    }

    @BindingAdapter({"bind:time", "bind:inboxLabel"})
    public static void setTime(TextView textView, String time, String inboxLabel) {
        // Check if in the same day to determine how to show the time
        setTextView(textView, time.split(SEPARATOR_MSG_LABEL)[time.split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(TimeUtils.getInstance().getDate()) ? 1 : 0], inboxLabel);
    }

    /**
     * Set textView UI for each message list item
     *
     * @param textView the textView to be styled and set
     * @param text the text content
     * @param label the label for styling the textView
     */
    private static void setTextView(TextView textView, String text, String label){
        Boolean isRead = label.split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(LABEL_MSG_READ);
        textView.setText(text);

        // Set read message as gray, unread message as white bold
        textView.setTextColor(IntelligentPowerSaving.getContext().getResources().getColor(isRead ? R.color.colorTint : R.color.white));
        textView.setTypeface(textView.getTypeface(), isRead ? Typeface.NORMAL : Typeface.BOLD);
    }

    /**
     * Start editing mode, which includes delete or mark as read
     *
     * @param position the position of the mail user clicked
     */
    private void startEditingMode(int position) {
        if (null == fm)
            fm = ((MainActivity) MainActivity.getContext()).getSupportFragmentManager();

        InboxFragment inboxFragment = (InboxFragment) fm.findFragmentByTag(INBOX_FRAGMENT);
        inboxFragment.initEditingInbox(position, messageList);
    }

    /**
     * Change the mail sender icon to checkbox views
     *
     * @param select the boolean indicates if the mail is selected
     * @param ivIcon the sender icon to be set to checkbox view
     */
    private void setImageViewOnEditing(Boolean select, ImageView ivIcon) {
        ivIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_label_check));
        ivIcon.setBackground(context.getResources().getDrawable(R.drawable.background_circle_colorprimary));

        if (select)
            ivIcon.setBackground(context.getResources().getDrawable(R.drawable.background_circle_colortint));
    }
}