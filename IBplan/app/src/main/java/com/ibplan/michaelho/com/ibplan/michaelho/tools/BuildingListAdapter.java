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

import com.ibplan.michaelho.com.ibplan.michaelho.constants.BuildingConstants;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.Location;
import com.ibplan.michaelho.com.ibplan.michaelho.util.ImageUtilities;
import com.ibplan.michaelho.ibplan.R;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by MichaelHo on 2015/4/7.
 */
public class BuildingListAdapter extends BaseAdapter implements BuildingConstants {

    private Context context;
    private ArrayList<HashMap<String, Object>> list;
    private LayoutInflater layoutInflater;
    private sqlOpenHelper_building_details sqliteBuildingList;

    public BuildingListAdapter(Context context, ArrayList<HashMap<String, Object>> list) {
        this.context = context;
        this.list = list;
        sqliteBuildingList = new sqlOpenHelper_building_details(context);
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
            convertView = layoutInflater.inflate(R.layout.activity_main_list_item,
                    null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView
                    .findViewById(R.id.activity_main_list_item_tv1);
            viewHolder.tvToday = (TextView) convertView
                    .findViewById(R.id.activity_main_list_item_tv2);
            viewHolder.tvTodayFee = (TextView) convertView
                    .findViewById(R.id.activity_main_list_item_tv3);
            viewHolder.tvCompare = (TextView) convertView
                    .findViewById(R.id.activity_main_list_item_tv4);
            viewHolder.imageView = (ImageView) convertView
                    .findViewById(R.id.activity_main_list_item_iv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Location location = sqliteBuildingList.getFullDetail(position + 1);
        viewHolder.tvName.setText(location.getName());
        byte[] bitmapBytes = sqliteBuildingList.getFullDetail(position+1).getImage();
        String bmpStr = new String(bitmapBytes, Charset.defaultCharset());
        Bitmap bmp =  ImageUtilities.getRoundedCroppedBitmap(ImageUtilities.decodeBase64(bmpStr),
                (int) (context.getResources().getDimension(R.dimen.img_width)));

        viewHolder.imageView.setImageBitmap(bmp);
        viewHolder.tvToday.setText("Today's consumption(kw): " + (int) ((Math.random() * 99 + 101)) * 15 + "");
        viewHolder.tvTodayFee.setText("Estimated price: NT. " + (int) ((Math.random() * 99 + 101)) * 5 + "");
        viewHolder.tvCompare.setText("Last 7 days: " + (int) ((Math.random() * 100)) + "" + " %");
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView tvName;
        TextView tvToday;
        TextView tvTodayFee;
        TextView tvCompare;
    }

}
