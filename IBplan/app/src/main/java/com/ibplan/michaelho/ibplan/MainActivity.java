package com.ibplan.michaelho.ibplan;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ibplan.michaelho.com.ibplan.michaelho.constants.BuildingList;
import com.ibplan.michaelho.com.ibplan.michaelho.fragment_hashtag.HashTagTabFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.fragments.EventsFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.fragments.HomeFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.fragments.MessageFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.fragments.SettingsFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.AlertDialogManager;
import com.ibplan.michaelho.com.ibplan.michaelho.util.PHPUtilities;


public class MainActivity extends ActionBarActivity implements android.support.v7.app.ActionBar.TabListener, BuildingList {


    public static final String PREF = "REG_PREF";
    public static final String PREF_NAME = "REG_PREF_NAME";
    public static final String PREF_EMAIL = "REG_PREF_EMAIL";
    public static final String PREF_REGID = "REG_PREF_REGID";
    //personal info
    public static String myName;
    public static String MacAddr;
    //screen info
    public static int screenWidth;
    public static int screenHeight;
    public static ActionBar actionBar;
    private AlertDialogManager adm;

    public static String getUserName(Context context) {
        return context.getSharedPreferences(MainActivity.PREF,
                Context.MODE_PRIVATE).getString(MainActivity.PREF_NAME, "");
    }

    public static String getUserEmail(Context context) {
        return context.getSharedPreferences(MainActivity.PREF,
                Context.MODE_PRIVATE).getString(MainActivity.PREF_EMAIL, "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        adm = new AlertDialogManager();
        myName = getUserName(this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        setTab();
        initMac();
    }

    private void initMac() {
        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        MacAddr = myBluetoothAdapter.getAddress();
        if ("".equalsIgnoreCase(MacAddr)) {
            WifiManager wifiMan = (WifiManager) this
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInf = wifiMan.getConnectionInfo();
            MacAddr = wifiInf.getMacAddress();
        }
        if ("".equalsIgnoreCase(MacAddr)) {
            adm.showMessageDialog(MainActivity.this, "Error",
                    "No mac address detected.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new TaskCheckMacExist().execute(MacAddr);
    }

    private void setTab() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setLogo(R.mipmap.ibplan_logo);
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);
        android.support.v7.app.ActionBar.Tab tab1 = actionBar.newTab()
                .setIcon(R.mipmap.btn_home_unclick).setTabListener(this);
        actionBar.addTab(tab1);
        actionBar.selectTab(tab1);

        android.support.v7.app.ActionBar.Tab tab2 = actionBar
                .newTab().setIcon(R.mipmap.btn_events_unclick).setTabListener(this);
        actionBar.addTab(tab2);
        actionBar.selectTab(tab2);

        android.support.v7.app.ActionBar.Tab tab3 = actionBar
                .newTab().setIcon(R.mipmap.btn_message_unclick).setTabListener(this);
        actionBar.addTab(tab3);
        actionBar.selectTab(tab3);

        android.support.v7.app.ActionBar.Tab tab4 = actionBar
                .newTab().setIcon(R.mipmap.btn_settings_unclick).setTabListener(this);
        actionBar.addTab(tab4);
        actionBar.selectTab(tab4);
    }

    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        Fragment fragment = null;
        switch (tab.getPosition()) {
            case 0:
                fragment = new HomeFragment();
                tab.setIcon(R.mipmap.btn_home);
                tab.setText(getResources().getString(R.string.navi_home));
                break;
            case 1:
                fragment = new EventsFragment();
                tab.setIcon(R.mipmap.btn_events);
                tab.setText(getResources().getString(R.string.navi_events));
                break;
            case 2:
                fragment = new MessageFragment();
                tab.setIcon(R.mipmap.btn_message);
                tab.setText(getResources().getString(R.string.navi_message));
                break;
            case 3:
                fragment = new SettingsFragment();
                tab.setIcon(R.mipmap.btn_settings);
                tab.setText(getResources().getString(R.string.navi_settings));
                break;
        }

        try {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.main_activity_frameLayout, fragment);
            transaction.commit();

        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }
    }

    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        switch (tab.getPosition()) {
            case 0:
                tab.setIcon(R.mipmap.btn_home_unclick);
                tab.setText(getResources().getString(R.string.navi_home));
                break;
            case 1:
                tab.setIcon(R.mipmap.btn_events_unclick);
                tab.setText(getResources().getString(R.string.navi_events));
                break;
            case 2:
                tab.setIcon(R.mipmap.btn_message_unclick);
                tab.setText(getResources().getString(R.string.navi_message));
                break;
            case 3:
                tab.setIcon(R.mipmap.btn_settings_unclick);
                tab.setText(getResources().getString(R.string.navi_settings));
                break;
        }
    }

    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    private class TaskCheckMacExist extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = new PHPUtilities().checkMacExist(params);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.equalsIgnoreCase("macOk")) {
                Intent intent = new Intent(MainActivity.this,
                        RegisterActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        }

    }

    ;
}
