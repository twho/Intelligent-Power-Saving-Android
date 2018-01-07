package com.tsungweiho.intelligentpowersaving.adapters;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
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
import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.ListAdapterConstants;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databinding.ObjMessageListItemBinding;
import com.tsungweiho.intelligentpowersaving.fragments.InboxFragment;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.tools.FirebaseManager;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Class in background to subscribe to PubNub channels
 * <p>
 * This class is the user interface of mail overview in InboxFragment
 *
 * @author Tsung Wei Ho
 * @version 1228.2017
 * @since 1.0.0
 */
public class MessageListAdapter extends BaseAdapter implements ListAdapterConstants, PubNubAPIConstants, DBConstants, FragmentTags {
    private Context context;

    private ArrayList<Message> msgList;
    private ArrayList<Boolean> msgSelectedList;
    private InboxMode mode;

    // functions
    private FragmentManager fm;
    private InboxFragment inboxFragment;

    /**
     * Constructor of MessageListAdapter
     *
     * @param context the context that uses this class
     * @param msgList the messageList that contains Message objects to be displayed
     * @param mode    the mode of MessageListAdapter, which includes viewing and editing mode
     */
    public MessageListAdapter(Context context, ArrayList<Message> msgList, InboxMode mode) {
        this.context = context;
        this.msgList = msgList;
        this.mode = mode;

        // Give initial value to msgSelectedList
        resetSelectedList();
    }

    /**
     * Set messageList in this adapter
     *
     * @param msgList the messageList to set from
     */
    public void setMsgList(ArrayList<Message> msgList) {
        this.msgList = msgList;
        this.notifyDataSetChanged();
    }

    /**
     * Reset all item to be unselected in selectedList
     */
    private void resetSelectedList() {
        this.msgSelectedList = new ArrayList<>(Arrays.asList(new Boolean[msgList.size()]));
        Collections.fill(msgSelectedList, Boolean.FALSE);
    }

    /**
     * Set msgSelectedList in this adapter
     *
     * @param msgSelectedList the selectedList to set from
     */
    private void setSelectedList(ArrayList<Boolean> msgSelectedList) {
        this.msgSelectedList = msgSelectedList;
        this.notifyDataSetChanged();
    }

    /**
     * Get selectedList used in this adapter
     *
     * @return the selectedList used in this adapter
     */
    public ArrayList<Boolean> getMsgSelectedList() {
        return this.msgSelectedList;
    }

    /**
     * Get current InboxMode
     *
     * @return current InboxMode used in this adapter
     */
    public InboxMode getMode() {
        return mode;
    }

    /**
     * Set current InboxMode
     *
     * @param mode the mode to set from
     */
    public void setMode(InboxMode mode) {
        this.mode = mode;
    }

    @Override
    public int getCount() {
        return msgList.size();
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

        final Message message = msgList.get(newOrderPosition);
        viewHolder.binding.setMessage(message);

        if (mode == InboxMode.VIEW)
            setImageView(viewHolder.imageView, message.getSenderImg());

        // Set star icon
        boolean isStarred = message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[1].equalsIgnoreCase(LABEL_MSG_STAR);
        viewHolder.ibStar.setImageDrawable(context.getResources().getDrawable(isStarred ? R.mipmap.ic_follow : R.mipmap.ic_unfollow));

        viewHolder.ibStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isStarred = message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[1].equalsIgnoreCase(LABEL_MSG_STAR);
                viewHolder.ibStar.setImageDrawable(context.getResources().getDrawable(isStarred ? R.mipmap.ic_unfollow : R.mipmap.ic_follow));
                isStarred = !isStarred;

                setupInboxFragment();
                inboxFragment.markMailStar(newOrderPosition, isStarred);
            }
        });

        // Viewing Mode
        if (mode == InboxMode.VIEW) {
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

        } else if (mode == InboxMode.EDIT) {
            viewHolder.imageView.setClickable(false);
            viewHolder.relativeLayout.setOnLongClickListener(null);

            // On click in editing mode: select mail
            viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setIndexSelected(newOrderPosition, !msgSelectedList.get(newOrderPosition));
                }
            });

            setImageViewOnEditing(msgSelectedList.get(newOrderPosition), viewHolder.imageView);
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

    // TODO move to ImgResourceManager

    /**
     * Set the icon of the mail sender or resource
     *
     * @param imageView the icon of the mail sender or resource
     * @param senderImg the image of the sender
     */
    private void setImageView(ImageView imageView, String senderImg) {
        switch (senderImg) {
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
                loadImgFromFirebase(senderImg, imageView);
                imageView.setBackground(context.getResources().getDrawable(R.drawable.background_circle_lightred));
                break;
        }
    }

    private void loadImgFromFirebase(String imgUrl, final ImageView imageView) {
        FirebaseManager.getInstance().downloadProfileImg(imgUrl + "/" + imgUrl, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageUtils.getInstance().setRoundedCornerImageViewFromUrl(uri.toString(), imageView, ImageUtils.getInstance().IMG_CIRCULAR);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setImageDrawable(IPowerSaving.getContext().getResources().getDrawable(R.mipmap.ic_preload_profile));
            }
        });
    }

    private void setAdminSenderIcon(ImageView imageView, int icon, int background) {
        imageView.setImageDrawable(context.getResources().getDrawable(icon));
        imageView.setBackground(context.getResources().getDrawable(background));
    }

    // Duplicate code ends here

    /**
     * Set message sender to textView
     *
     * @param textView   the textView to display message sender
     * @param sender     the sender of the message
     * @param inboxLabel the inbox label of the message
     */
    @BindingAdapter({"bind:sender", "bind:inboxLabel"})
    public static void setSender(TextView textView, String sender, String inboxLabel) {
        setTextView(textView, sender, inboxLabel);
    }

    /**
     * Set message title to textView
     *
     * @param textView   the textView to display message title
     * @param title      the title of the message
     * @param inboxLabel the inbox label of the message
     */
    @BindingAdapter({"bind:title", "bind:inboxLabel"})
    public static void setTitle(TextView textView, String title, String inboxLabel) {
        setTextView(textView, title, inboxLabel);
    }

    /**
     * Set message content to textView
     *
     * @param textView   the textView to display message content
     * @param content    the content of the message
     * @param inboxLabel the inbox label of the message
     */
    @BindingAdapter({"bind:content", "bind:inboxLabel"})
    public static void setContent(TextView textView, String content, String inboxLabel) {
        setTextView(textView, content, inboxLabel);
    }

    /**
     * Set message received time to textView
     *
     * @param textView   the textView to display time
     * @param time       the received time of the message
     * @param inboxLabel the inbox label of the message
     */
    @BindingAdapter({"bind:time", "bind:inboxLabel"})
    public static void setTime(TextView textView, String time, String inboxLabel) {
        // Check if in the same day to determine how to show the time
        setTextView(textView, time.split(SEPARATOR_MSG_LABEL)[time.split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(TimeUtils.getInstance().getDate()) ? 1 : 0], inboxLabel);
    }

    /**
     * Set textView UI for each message list item
     *
     * @param textView the textView to be styled and set
     * @param text     the text content
     * @param label    the label for styling the textView
     */
    private static void setTextView(TextView textView, String text, String label) {
        Boolean isRead = label.split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(LABEL_MSG_READ);
        textView.setText(text);

        // Set read message as gray, unread message as white bold
        textView.setTextColor(IPowerSaving.getContext().getResources().getColor(isRead ? R.color.colorTint : R.color.white));
        textView.setTypeface(textView.getTypeface(), isRead ? Typeface.NORMAL : Typeface.BOLD);
    }

    /**
     * Start editing mode, which includes delete or mark as read
     *
     * @param position the position of the mail user clicked
     */
    private void startEditingMode(int position) {
        setupInboxFragment();
        inboxFragment.initEditingInbox(msgList);

        resetSelectedList();

        setMsgList(msgList);
        setSelectedList(msgSelectedList);
        setMode(InboxMode.EDIT);

        setIndexSelected(position, Boolean.TRUE);
    }

    /**
     * Set the mail selected/unselected
     *
     * @param selectedPosition the position of selected mail item
     * @param isSelected       the boolean indicate if the mail is selected
     */
    private void setIndexSelected(int selectedPosition, Boolean isSelected) {
        msgSelectedList.set(selectedPosition, isSelected);
        notifyDataSetChanged();

        setupInboxFragment();
        inboxFragment.setIsSelectedRead(msgList.get(selectedPosition).getInboxLabel().split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(LABEL_MSG_READ));
    }

    /**
     * Get InboxFragment to execute functions
     */
    private void setupInboxFragment() {
        if (null != inboxFragment)
            return;

        if (null == fm)
            fm = ((MainActivity) MainActivity.getContext()).getSupportFragmentManager();

        inboxFragment = (InboxFragment) fm.findFragmentByTag(MainFragment.INBOX.toString());
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