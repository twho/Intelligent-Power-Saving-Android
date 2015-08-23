package com.ibplan.michaelho.com.ibplan.michaelho.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibplan.michaelho.com.ibplan.michaelho.constants.EventConstants;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.Event;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.Location;
import com.ibplan.michaelho.com.ibplan.michaelho.util.ImageUtilities;
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
            viewHolder.tvName = (TextView) convertView
                    .findViewById(R.id.fragment_hashtag_list_item_tv1);
            viewHolder.tvLocation = (TextView) convertView
                    .findViewById(R.id.fragment_hashtag_list_item_tv2);
            viewHolder.tvX = (TextView) convertView
                    .findViewById(R.id.fragment_hashtag_list_item_tv3);
            viewHolder.tvY = (TextView) convertView
                    .findViewById(R.id.fragment_hashtag_list_item_tv4);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        Event event = sqliteEventList.getSingleEvent(position+1);
//        Log.d("POSITION", (position + 1) + "");
//        viewHolder.tvName.setText(event.getEvent());
//        viewHolder.tvLocation.setText(event.getLocation());
//        viewHolder.tvX.setText("X: " + event.getPosX());
//        viewHolder.tvY.setText("Y: " + event.getPosY());
        viewHolder.tvName.setText((String)list.get(position).get(EVENTS));
        viewHolder.tvLocation.setText((String)list.get(position).get(LOCATION));
        viewHolder.tvX.setText("X: " + (String)list.get(position).get(X_POS));
        viewHolder.tvY.setText("Y: " + (String)list.get(position).get(Y_POS));
        return convertView;
    }

    private class ViewHolder {
        TextView tvName;
        TextView tvLocation;
        TextView tvX;
        TextView tvY;
    }
}
