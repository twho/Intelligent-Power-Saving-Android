package com.ibplan.michaelho.com.ibplan.michaelho.fragment_hashtag;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ibplan.michaelho.com.ibplan.michaelho.constants.EventConstants;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.Event;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.AlertDialogManager;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.EventsFragmentListAdapter;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.sqlOpenHelper_events;
import com.ibplan.michaelho.com.ibplan.michaelho.util.ImageUtilities;
import com.ibplan.michaelho.com.ibplan.michaelho.util.PHPUtilities;
import com.ibplan.michaelho.com.ibplan.michaelho.util.TimeUtilities;
import com.ibplan.michaelho.ibplan.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MichaelHo on 2015/5/25.
 */
public class TimelineTabFragment extends Fragment implements EventConstants{
    private static Context context;
    private static sqlOpenHelper_events sqlEvents;
    View view;
    private AlertDialogManager adm;
    private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    private EventsFragmentListAdapter eventsFragmentListAdapter;
    private ListView eventListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_event_timeline, container, false);
        init();
        return view;
    }

    private void init(){
        adm = new AlertDialogManager();
        sqlEvents = new sqlOpenHelper_events(getActivity());
        context = getActivity();
        setListView();
    }

    private void setListView() {
        eventListView = (ListView) view
                .findViewById(R.id.fragment_timeline_listView);
        list = sqlEvents.getAllEvents();
        if (list != null) {
            eventsFragmentListAdapter = new EventsFragmentListAdapter(getActivity(), list);
            eventListView.setAdapter(eventsFragmentListAdapter);
        }
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showEventDetail(position);
            }
        });
    }
    private void showEventDetail(int position){
        final Dialog dialog = new Dialog(context);
        LayoutInflater li = LayoutInflater.from(context);
        View v = li.inflate(R.layout.fragment_hashtag_tagview_detail, null);
        TextView tvTitle = (TextView) v.findViewById(R.id.fragment_hashtag_tagview_detail_title);
        TextView tvLocation = (TextView) v.findViewById(R.id.fragment_hashtag_tagview_detail_location);
        TextView tvTime = (TextView) v.findViewById(R.id.fragment_hashtag_tagview_detail_time);
        TextView tvFixed = (TextView) v.findViewById(R.id.fragment_hashtag_tagview_detail_fixed);
        ImageButton ibClose = (ImageButton) v.findViewById(R.id.fragment_hashtag_tagview_detail_ib1);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ImageButton ibFollow = (ImageButton) v.findViewById(R.id.fragment_hashtag_tagview_detail_ib1);
        ImageView ivEvent = (ImageView) v.findViewById(R.id.fragment_hashtag_tagview_detail_iv);
        tvTitle.setText((String)list.get(position).get(EVENTS));
        tvLocation.setText("Location: "+(String)list.get(position).get(LOCATION));
        tvTime.setText("Reported time: "+ TimeUtilities.StringToTime((String)list.get(position).get(TIME)));
        setStatus(tvFixed, (String)list.get(position).get(IFFIXED));
        byte[] bitmapBytes = (byte[]) list.get(position).get(IMG);
        String bmpStr = new String(bitmapBytes, Charset.defaultCharset());
        Bitmap bmp =  ImageUtilities.decodeBase64(bmpStr);
        ivEvent.setImageBitmap(bmp);
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

    @Override
    public void onResume() {
        super.onResume();
        if (sqlEvents == null) {
            sqlEvents = new sqlOpenHelper_events(getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sqlEvents != null) {
            sqlEvents.close();
            sqlEvents = null;
        }
    }
}
