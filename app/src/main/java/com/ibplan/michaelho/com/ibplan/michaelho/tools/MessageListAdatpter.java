package com.ibplan.michaelho.com.ibplan.michaelho.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibplan.michaelho.com.ibplan.michaelho.objects.SingleMessage;
import com.ibplan.michaelho.com.ibplan.michaelho.util.ImageUtilities;
import com.ibplan.michaelho.ibplan.R;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MichaelHo on 2015/6/1.
 */
public class MessageListAdatpter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, Object>> list;
    private LayoutInflater layoutInflater;
    private sqlOpenHelper_messageBox sqliteMessageList;

    public MessageListAdatpter(Context context, ArrayList<HashMap<String, Object>> list) {
        this.context = context;
        this.list = list;
        sqliteMessageList = new sqlOpenHelper_messageBox(context);
    }

    @Override
    public int getCount() {
        return list.size();
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
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_message_list_item,
                    null);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) convertView
                    .findViewById(R.id.fragment_message_list_item_tv1);
            viewHolder.tvDetail = (TextView) convertView
                    .findViewById(R.id.fragment_message_list_item_tv3);
            viewHolder.ivSender = (ImageView) convertView
                    .findViewById(R.id.fragment_message_list_item_iv1);
            viewHolder.ivIfRead = (ImageView) convertView
                    .findViewById(R.id.fragment_message_list_item_iv2);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SingleMessage singleMessage = sqliteMessageList.getFullDetail(list.size() - position);
        viewHolder.tvTitle.setText(singleMessage.getTitle());
        setIfRead(viewHolder.ivIfRead, singleMessage.getIfRead());
        setSenderImg(viewHolder.ivSender, singleMessage.getSender());
        if(singleMessage.getDetail().length()>20){
            viewHolder.tvDetail.setText(singleMessage.getDetail().substring(0, 20));
        }else{
            viewHolder.tvDetail.setText(singleMessage.getDetail());
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView ivSender;
        ImageView ivIfRead;
        TextView tvTitle;
        TextView tvDetail;
    }

    private void setSenderImg(ImageView iv, String sender){
        if("SYSTEM".equalsIgnoreCase(sender)){
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.img_message_system);
            iv.setImageBitmap(
                    ImageUtilities.getRoundedCroppedBitmap(bmp, (int) (context.getResources().getDimension(R.dimen.img_width))));
        }else if("LIKE".equalsIgnoreCase(sender)){
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.img_message_like);
            iv.setImageBitmap(
                    ImageUtilities.getRoundedCroppedBitmap(bmp, (int) (context.getResources().getDimension(R.dimen.img_width))));
        }

    }

    private void setIfRead(ImageView iv, String ifRead){
        if(ifRead.equals("0")){
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_message_read);
            iv.setImageBitmap(
                    ImageUtilities.getRoundedCroppedBitmap(bmp, (int) (context.getResources().getDimension(R.dimen.img_width))));
        }else{
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_message_mail);
            iv.setImageBitmap(
                    ImageUtilities.getRoundedCroppedBitmap(bmp, (int) (context.getResources().getDimension(R.dimen.img_width))));
        }
    }
}
