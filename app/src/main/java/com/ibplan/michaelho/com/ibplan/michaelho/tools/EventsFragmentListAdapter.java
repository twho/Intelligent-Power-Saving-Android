package com.ibplan.michaelho.com.ibplan.michaelho.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibplan.michaelho.com.ibplan.michaelho.constants.EventConstants;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.Event;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.Location;
import com.ibplan.michaelho.com.ibplan.michaelho.util.ImageUtilities;
import com.ibplan.michaelho.com.ibplan.michaelho.util.TimeUtilities;
import com.ibplan.michaelho.ibplan.R;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MichaelHo on 2015/5/25.
 */
public class EventsFragmentListAdapter extends BaseAdapter implements EventConstants{
    private Context context;
    private ArrayList<HashMap<String, Object>> list;
    private LayoutInflater layoutInflater;
    private sqlOpenHelper_events sqliteEventList;

    public EventsFragmentListAdapter(Context context, ArrayList<HashMap<String, Object>> list) {
        this.context = context;
        this.list = list;
        sqliteEventList = new sqlOpenHelper_events(context);
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
            convertView = layoutInflater.inflate(R.layout.fragment_hashtag_list_item,
                    null);
            viewHolder = new ViewHolder();
            viewHolder.ivPor = (ImageView) convertView
                    .findViewById(R.id.fragment_hahstag_list_item_iv1);
//            viewHolder.ivEvent = (ImageView) convertView
//
            viewHolder.ifFixed = (ImageView) convertView
                    .findViewById(R.id.fragment_hahstag_list_item_iv3);
            viewHolder.ibLike = (ImageView) convertView
                    .findViewById(R.id.fragment_hashtag_list_item_iv2);
            viewHolder.tvEvent = (TextView) convertView
                    .findViewById(R.id.fragment_hashtag_list_item_tv2);
            viewHolder.tvName = (TextView) convertView
                    .findViewById(R.id.fragment_hashtag_list_item_tv1);
            viewHolder.tvEvent = (TextView) convertView
                    .findViewById(R.id.fragment_hashtag_list_item_tv2);
            viewHolder.tvLocation = (TextView) convertView
                    .findViewById(R.id.fragment_hashtag_list_item_tv3);
            viewHolder.tvTime = (TextView) convertView
                    .findViewById(R.id.fragment_hashtag_list_item_tv4);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.btn_follow);
        viewHolder.ibLike.setImageBitmap(
                ImageUtilities.getRoundedCroppedBitmap(bmp, (int) (context.getResources().getDimension(R.dimen.img_width))));
        byte[] bitmapBytes = (byte[]) list.get(position).get(POSTERIMG);
        String bmpStr = new String(bitmapBytes, Charset.defaultCharset());
        Bitmap bmpPor = ImageUtilities.decodeBase64(bmpStr);
        viewHolder.ivPor.setImageBitmap(ImageUtilities.getRoundedCroppedBitmap(bmpPor, (int) (context.getResources().getDimension(R.dimen.img_width))));
        setStatus(viewHolder.ifFixed, (String)list.get(position).get(IFFIXED));
        viewHolder.tvName.setText((String)list.get(position).get(DEPARTMENT)+ " " +(String)list.get(position).get(STUDENT_NAME));
        viewHolder.tvEvent.setText("Events: "+(String)list.get(position).get(EVENTS));
        viewHolder.tvLocation.setText("Location: " + (String)list.get(position).get(LOCATION));
        viewHolder.tvTime.setText("Time: " + TimeUtilities.StringToTime((String) list.get(position).get(TIME)));
        return convertView;
    }

    private void setStatus(ImageView iv, String ifFixed){
        if("1".equalsIgnoreCase(ifFixed)){
            iv.setImageDrawable(context.getResources().getDrawable(R.drawable.img_fixed));
        }else {
            iv.setImageDrawable(context.getResources().getDrawable(R.drawable.img_not_fixed));
        }
    }

    private class ViewHolder {
        ImageView ivPor;
        ImageView ifFixed;
        ImageView ibLike;
        TextView tvName;
        TextView tvEvent;
        TextView tvLocation;
        TextView tvTime;
    }
}
