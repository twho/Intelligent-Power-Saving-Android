package com.ibplan.michaelho.com.ibplan.michaelho.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibplan.michaelho.com.ibplan.michaelho.objects.Location;
import com.ibplan.michaelho.com.ibplan.michaelho.util.ImageUtilities;
import com.ibplan.michaelho.ibplan.R;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/6/22.
 */
public class LabListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, Object>> list;
    private LayoutInflater layoutInflater;
    private sqlOpenHelper_labDetails sqliteLabList;

    public LabListAdapter(Context context, ArrayList<HashMap<String, Object>> list) {
        this.context = context;
        this.list = list;
        sqliteLabList = new sqlOpenHelper_labDetails(context);
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
            convertView = layoutInflater.inflate(R.layout.fragment_home_labs_list_item,
                    null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView
                    .findViewById(R.id.fragment_home_labs_list_item_tv1);
            viewHolder.tvDetail = (TextView) convertView
                    .findViewById(R.id.fragment_home_labs_list_item_tv2);
            viewHolder.imageView = (ImageView) convertView
                    .findViewById(R.id.fragment_home_labs_list_item_iv1);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Location location = sqliteLabList.getFullDetail(position + 1);
        viewHolder.tvName.setText("Lab: "+location.getName());
        viewHolder.tvDetail.setText(location.getDetail());
        byte[] bitmapBytes = sqliteLabList.getFullDetail(position+1).getImage();
        String bmpStr = new String(bitmapBytes, Charset.defaultCharset());
        Bitmap bmp =  ImageUtilities.getRoundedCroppedBitmap(ImageUtilities.decodeBase64(bmpStr),
                (int) (context.getResources().getDimension(R.dimen.img_width)));
        viewHolder.imageView.setImageBitmap(bmp);
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView tvName;
        TextView tvDetail;
    }
}
