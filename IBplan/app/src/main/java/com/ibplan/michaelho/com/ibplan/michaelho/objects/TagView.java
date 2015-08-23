package com.ibplan.michaelho.com.ibplan.michaelho.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ibplan.michaelho.com.ibplan.michaelho.tools.AlertDialogManager;
import com.ibplan.michaelho.ibplan.R;

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

    public TagView(final Context context, final String event, final String location, Bitmap bmp, String time) {
        super(context);
        LayoutInflater li = LayoutInflater.from(context);
        view = li.inflate(R.layout.fragment_hashtag_tagview, null);
        this.context = context;
        this.event = event;
        this.time = time;
        this.location = location;
        this.bmp = bmp;
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialogManager().showMessageDialog(context, event, location);
            }
        });
        TextView tv_title = (TextView) view.findViewById(R.id.fragment_hashtag_tagview_title);
//        TextView tv_time = (TextView) view.findViewById(R.id.fragment_hashtag_tagview_time);
        tv_title.setText(event);
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

}
