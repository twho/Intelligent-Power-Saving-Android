package com.ibplan.michaelho.com.ibplan.michaelho.fragment_hashtag;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ibplan.michaelho.com.ibplan.michaelho.objects.Event;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.AlertDialogManager;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.EventsFragmentListAdapter;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.sqlOpenHelper_events;
import com.ibplan.michaelho.com.ibplan.michaelho.util.PHPUtilities;
import com.ibplan.michaelho.ibplan.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MichaelHo on 2015/5/25.
 */
public class TimelineTabFragment extends Fragment {
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
        new TaskGetEvent().execute();
        sqlEvents = new sqlOpenHelper_events(getActivity());
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

    private class TaskGetEvent extends AsyncTask<String, Void, String> {
        ProgressDialog pd;
        Context context = getActivity();

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return new PHPUtilities().getEvent(params);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                sqlEvents.deleteAll();
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    String name = jsonData.getString("name");
                    String department = jsonData.getString("department");
                    String location = jsonData.getString("location");
                    String x_pos = jsonData.getString("x_pos");
                    String y_pos = jsonData.getString("y_pos");
                    String events = jsonData.getString("event");
//                    String image = jsonData.getString("image");
                    String time = jsonData.getString("time");
                    Event event = new Event(name, department, location, x_pos, y_pos, events, null, time);
                    sqlEvents = new sqlOpenHelper_events(getActivity());
                    sqlEvents.insertDB(event);
                }
            } catch (Exception e) {
                Log.e("TimelineTabFragment", e.toString());
                adm.showAlertDialog(context, "Error", getResources().getString(R.string.error_message));
            }
            pd.dismiss();
        }
    };


}
