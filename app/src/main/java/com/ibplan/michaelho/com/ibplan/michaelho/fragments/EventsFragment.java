package com.ibplan.michaelho.com.ibplan.michaelho.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ibplan.michaelho.com.ibplan.michaelho.fragment_hashtag.HashTagTabFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.fragment_hashtag.QRTabFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.fragment_hashtag.TimelineTabFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.DrawerListAdapter;
import com.ibplan.michaelho.ibplan.R;

/**
 * Created by MichaelHo on 2015/5/18.
 */
public class EventsFragment extends Fragment{

    private View view;
    final String[] listData ={"Hashtag", "QRcode Scanner", "Timeline", "Q&A"};
    final int[] drawables = {R.mipmap.img_hashtag, R.mipmap.img_qrcode, R.mipmap.img_time ,R.mipmap.img_ask};
    private DrawerListAdapter eventsAdapter;
    private ImageButton btn_func, btn_search;
    private LinearLayout ll;
    private EditText ed1;
    private ImageButton imgBtn2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_events, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }


    private void init(){
        eventsAdapter = new DrawerListAdapter(getActivity(), listData, drawables);
        final DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        final ListView navList = (ListView) view.findViewById(R.id.drawer);
        btn_func = (ImageButton) view.findViewById(R.id.fragment_events_imageButton1);
        btn_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        btn_search = (ImageButton) view.findViewById(R.id.fragment_events_imageButton2);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        ll = (LinearLayout) view.findViewById(R.id.fragment_events_linear_layout);
        ed1 = (EditText) view.findViewById(R.id.fragment_events_editText);
        imgBtn2 = (ImageButton) view.findViewById(R.id.fragment_events_imageButton2);
        navList.setAdapter(eventsAdapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos,long id){
                drawer.setDrawerListener( new DrawerLayout.SimpleDrawerListener(){
                    Fragment fragment = null;

                    @Override
                    public void onDrawerClosed(View drawerView){
                        switch(pos){
                            case 0:
                                fragment = new HashTagTabFragment();
                                ll.setVisibility(View.VISIBLE);
                                ed1.setEnabled(true);
                                imgBtn2.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                fragment = new QRTabFragment();
                                ll.setVisibility(View.VISIBLE);
                                ed1.setEnabled(false);
                                imgBtn2.setVisibility(View.GONE);
                                break;
                            case 2:
                                fragment = new TimelineTabFragment();
                                ll.setVisibility(View.VISIBLE);
                                ed1.setEnabled(false);
                                imgBtn2.setVisibility(View.GONE);
                                break;
                        }
                        super.onDrawerClosed(drawerView);
                        try {
                            android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                                    .beginTransaction();
                            transaction.replace(R.id.fragment_events_container,fragment);
                            transaction.commit();

                        } catch (Exception e) {
                            Log.e("TAG", e.toString());
                        }
                    }
                });
                drawer.closeDrawer(navList);
            }
        });
        android.support.v4.app.FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragment_events_container,new HashTagTabFragment());
        tx.commit();
    }
}
