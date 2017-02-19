package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DrawerListConstants;
import com.tsungweiho.intelligentpowersaving.tools.DrawerListAdapter;

/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/18.
 */

public class MessageFragment extends Fragment implements DrawerListConstants {

    // Message Fragment View
    private View view;

    // UI Views
    private DrawerListAdapter drawerListAdapter;
    private FrameLayout flTopBar;
    private ImageButton ibOptions;

    // Functions
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        context = MainActivity.getContext();
        init();
        return view;
    }

    private void init() {
        drawerListAdapter = new DrawerListAdapter(context, MESSAGE_DRAWER, MESSAGE_DRAWER_IMG);
        final DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.fragment_message_drawer_layout);
        final ListView navList = (ListView) view.findViewById(R.id.fragment_message_drawer);
        ibOptions = (ImageButton) view.findViewById(R.id.fragment_message_ib_options);
        ibOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        flTopBar = (FrameLayout) view.findViewById(R.id.fragment_message_frame_layout);
        navList.setAdapter(drawerListAdapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
                drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        switch (pos) {
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                        }
                        super.onDrawerClosed(drawerView);
                    }
                });
                drawer.closeDrawer(navList);
            }
        });
    }
}
