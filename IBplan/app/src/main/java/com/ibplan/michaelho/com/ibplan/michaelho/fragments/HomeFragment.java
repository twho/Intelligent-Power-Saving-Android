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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ibplan.michaelho.com.ibplan.michaelho.fragment_hashtag.QRTabFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.fragment_home.BuildingDashboardFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.fragment_message.InBoxTabFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.EventsFragmentAdapter;
import com.ibplan.michaelho.ibplan.R;

/**
 * Created by MichaelHo on 2015/4/7.
 */
public class HomeFragment extends Fragment{

    private View view;
    final String[] listData ={"Buildings", "Lab Partner","Weekly Best", "Daily Best", "Q&A"};
    final int[] drawables = {R.mipmap.btn_building, R.mipmap.btn_lab, R.mipmap.btn_best, R.mipmap.btn_weekly_one ,R.mipmap.img_ask};
    private EventsFragmentAdapter eventsAdapter;
    private ImageButton btn_func;
    private LinearLayout ll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }


    private void init(){
        eventsAdapter = new EventsFragmentAdapter(getActivity(), listData, drawables);
        final DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.fragment_home_drawer_layout);
        final ListView navList = (ListView) view.findViewById(R.id.fragment_home_drawer);
        btn_func = (ImageButton) view.findViewById(R.id.fragment_home_imageButton1);
        btn_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        ll = (LinearLayout) view.findViewById(R.id.fragment_home_linear_layout);
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
                                fragment = new BuildingDashboardFragment();
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            case 4:
                                break;
                        }
                        super.onDrawerClosed(drawerView);
                        try {
                            android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                                    .beginTransaction();
                            transaction.replace(R.id.fragment_home_container,fragment);
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
        tx.replace(R.id.fragment_home_container,new BuildingDashboardFragment());
        tx.commit();
    }
}
