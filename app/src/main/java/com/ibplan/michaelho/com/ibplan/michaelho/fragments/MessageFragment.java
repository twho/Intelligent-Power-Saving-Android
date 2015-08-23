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
import android.widget.TextView;

import com.ibplan.michaelho.com.ibplan.michaelho.fragment_hashtag.QRTabFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.fragment_message.InBoxTabFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.fragment_message.SearchBoxFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.DrawerListAdapter;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.sqlOpenHelper_messageBox;
import com.ibplan.michaelho.ibplan.R;

/**
 * Created by MichaelHo on 2015/4/7.
 */
public class MessageFragment extends Fragment {

    private View view;
    private sqlOpenHelper_messageBox sqlMessage;
    final String[] listData ={"Search Message", "Inbox","Following Events", "Mail Us", "Q&A"};
    final int[] drawables = {R.mipmap.btn_image_search, R.mipmap.btn_message, R.mipmap.btn_like, R.mipmap.btn_mail_us ,R.mipmap.img_ask};
    private DrawerListAdapter eventsAdapter;
    private ImageButton btn_func, btn_delAll;
    private TextView title;
    private LinearLayout ll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }


    private void init(){
        sqlMessage = new sqlOpenHelper_messageBox(getActivity());
        eventsAdapter = new DrawerListAdapter(getActivity(), listData, drawables);
        title = (TextView) view.findViewById(R.id.fragment_events_textView);
        final DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.fragment_message_drawer_layout);
        final ListView navList = (ListView) view.findViewById(R.id.fragment_message_drawer);
        btn_func = (ImageButton) view.findViewById(R.id.fragment_message_imageButton1);
        btn_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        btn_delAll = (ImageButton) view.findViewById(R.id.fragment_message_imageButton2);
        btn_delAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ll = (LinearLayout) view.findViewById(R.id.fragment_message_linear_layout);
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
                                fragment = new SearchBoxFragment();
                                title.setText("Search Message");
                                break;
                            case 1:
                                fragment = new InBoxTabFragment();
                                title.setText("Inbox Message");
                                break;
                        }
                        super.onDrawerClosed(drawerView);
                        try {
                            android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                                    .beginTransaction();
                            transaction.replace(R.id.fragment_message_container,fragment);
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
        tx.replace(R.id.fragment_message_container,new InBoxTabFragment());
        tx.commit();
    }
}
