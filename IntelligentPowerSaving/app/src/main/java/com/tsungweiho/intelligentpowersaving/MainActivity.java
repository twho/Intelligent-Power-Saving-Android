package com.tsungweiho.intelligentpowersaving;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.tsungweiho.intelligentpowersaving.fragments.EventFragment;
import com.tsungweiho.intelligentpowersaving.fragments.HomeFragment;
import com.tsungweiho.intelligentpowersaving.fragments.MessageFragment;
import com.tsungweiho.intelligentpowersaving.fragments.SettingsFragment;
import com.tsungweiho.intelligentpowersaving.tools.AlertDialogManager;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

    // functions
    public static Context context;
    private String TAG = "MainActivity";

    //screen info
    public static int screenWidth;
    public static int screenHeight;
    public static ActionBar actionBar;
    private AlertDialogManager alertDialogManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        init();
    }

    private void init() {
        alertDialogManager = new AlertDialogManager(context);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        setTab();
    }

    private void setTab() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);
        android.support.v7.app.ActionBar.Tab tabHome = actionBar.newTab()
                .setIcon(R.mipmap.ic_home_unclick).setTabListener(this);
        actionBar.addTab(tabHome);


        // All icons are padding 25%
        android.support.v7.app.ActionBar.Tab tabEvent = actionBar
                .newTab().setIcon(R.mipmap.ic_event_unclick).setTabListener(this);
        actionBar.addTab(tabEvent);
        actionBar.selectTab(tabEvent);

        android.support.v7.app.ActionBar.Tab tabMessage = actionBar
                .newTab().setIcon(R.mipmap.ic_message_unclick).setTabListener(this);
        actionBar.addTab(tabMessage);
        actionBar.selectTab(tabMessage);

        android.support.v7.app.ActionBar.Tab tabSettings = actionBar
                .newTab().setIcon(R.mipmap.ic_settings_unclick).setTabListener(this);
        actionBar.addTab(tabSettings);
        actionBar.selectTab(tabSettings);

        actionBar.selectTab(tabHome);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public static Context getContext() {
        return context;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Fragment fragment = null;
        switch (tab.getPosition()) {
            case 0:
                fragment = new HomeFragment();
                tab.setIcon(R.mipmap.ic_home);
                tab.setText(getResources().getString(R.string.navi_home));
                break;
            case 1:
                fragment = new EventFragment();
                tab.setIcon(R.mipmap.ic_event);
                tab.setText(getResources().getString(R.string.navi_event));
                break;
            case 2:
                fragment = new MessageFragment();
                tab.setIcon(R.mipmap.ic_message);
                tab.setText(getResources().getString(R.string.navi_message));
                break;
            case 3:
                fragment = new SettingsFragment();
                tab.setIcon(R.mipmap.ic_settings);
                tab.setText(getResources().getString(R.string.navi_settings));
                break;
        }

        try {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.activity_main_frameLayout, fragment);
            transaction.commit();

        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        switch (tab.getPosition()) {
            case 0:
                tab.setIcon(R.mipmap.ic_home_unclick);
                tab.setText(getResources().getString(R.string.navi_home));
                break;
            case 1:
                tab.setIcon(R.mipmap.ic_event_unclick);
                tab.setText(getResources().getString(R.string.navi_event));
                break;
            case 2:
                tab.setIcon(R.mipmap.ic_message_unclick);
                tab.setText(getResources().getString(R.string.navi_message));
                break;
            case 3:
                tab.setIcon(R.mipmap.ic_settings_unclick);
                tab.setText(getResources().getString(R.string.navi_settings));
                break;
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
