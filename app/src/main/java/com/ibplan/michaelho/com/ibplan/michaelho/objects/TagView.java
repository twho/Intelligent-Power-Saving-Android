package com.ibplan.michaelho.com.ibplan.michaelho.objects;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibplan.michaelho.com.ibplan.michaelho.tools.AlertDialogManager;
import com.ibplan.michaelho.com.ibplan.michaelho.util.TimeUtilities;
import com.ibplan.michaelho.ibplan.R;

import org.w3c.dom.Text;

/**
 * Created by MichaelHo on 2015/5/25.
 */
public class TagView extends View {

    private View view;
    private Context context;
    private String event;
    private String time;
    private String location;
    private Bitmap bmp;
    private String ifFixed;

    public TagView(final Context context, final String event, final String location, final Bitmap bmp, final String time, final String ifFixed) {
        super(context);
        LayoutInflater li = LayoutInflater.from(context);
        view = li.inflate(R.layout.fragment_hashtag_tagview, null);
        this.context = context;
        this.event = event;
        this.time = time;
        this.location = location;
        this.bmp = bmp;
        this.ifFixed = ifFixed;
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showTagDetail();
            }
        });
        TextView tv_title = (TextView) view.findViewById(R.id.fragment_hashtag_tagview_title);
        ImageView iv = (ImageView) view.findViewById(R.id.fragment_hashtag_tagview_iv);
        if("1".equalsIgnoreCase(ifFixed)){
            iv.setImageDrawable(context.getResources().getDrawable(R.mipmap.btn_tag_fixed));
        }else{
            iv.setImageDrawable(context.getResources().getDrawable(R.mipmap.btn_tag));
        }
//        TextView tv_time = (TextView) view.findViewById(R.id.fragment_hashtag_tagview_time);
        tv_title.setText(event);
    }

    private void showTagDetail(){
        final Dialog dialog = new Dialog(context);
        LayoutInflater li = LayoutInflater.from(context);
        View v = li.inflate(R.layout.fragment_hashtag_tagview_detail, null);
        TextView tvTitle = (TextView) v.findViewById(R.id.fragment_hashtag_tagview_detail_title);
        TextView tvLocation = (TextView) v.findViewById(R.id.fragment_hashtag_tagview_detail_location);
        TextView tvTime = (TextView) v.findViewById(R.id.fragment_hashtag_tagview_detail_time);
        TextView tvFixed = (TextView) v.findViewById(R.id.fragment_hashtag_tagview_detail_fixed);
        ImageButton ibClose = (ImageButton) v.findViewById(R.id.fragment_hashtag_tagview_detail_ib1);
        ibClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ImageButton ibFollow = (ImageButton) v.findViewById(R.id.fragment_hashtag_tagview_detail_ib1);
        ImageView ivEvent = (ImageView) v.findViewById(R.id.fragment_hashtag_tagview_detail_iv);
        tvTitle.setText(getEvent());
        tvLocation.setText("Location: "+getLocation());
        tvTime.setText("Reported time: "+TimeUtilities.StringToTime(getTime()));
        setStatus(tvFixed, getIfFixed());
        ivEvent.setImageBitmap(getImage());
        dialog.setContentView(v);
        dialog.show();
    }

    public void setStatus(TextView tv, String ifFixed){
        String status;
        if("1".equalsIgnoreCase(ifFixed)){
            status = "This event has been solved";
            tv.setTextColor(Color.GREEN);
        }else{
            status = "We're figuring it out...";
            tv.setTextColor(Color.RED);
        }
        tv.setText("Status: "+status);
    }

    public View getView(){
        return view;
    }

    public void setView(View view){
        this.view = view;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String name) {
        this.event = event;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Bitmap getImage() {
        return bmp;
    }

    public void setImage(Bitmap bmp) {
        this.bmp = bmp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIfFixed() {
        return ifFixed;
    }

    public void setIfFixed(String ifFixed) {
        this.ifFixed = ifFixed;
    }

}
