package com.tsungweiho.intelligentpowersaving;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTag;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.fragments.BuildingFragment;
import com.tsungweiho.intelligentpowersaving.fragments.EventFragment;
import com.tsungweiho.intelligentpowersaving.fragments.HomeFragment;
import com.tsungweiho.intelligentpowersaving.fragments.InboxFragment;
import com.tsungweiho.intelligentpowersaving.fragments.SettingsFragment;
import com.tsungweiho.intelligentpowersaving.tools.PermissionManager;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener, FragmentTag, DBConstants, PubNubAPIConstants {

    // functions
    public static Context context;
    private String TAG = "MainActivity";
    private PermissionManager permissionManager;

    //screen info
    public static float screenWidth;
    public static float screenHeight;
    public static ActionBar actionBar;
    private FragmentManager fragmentManager;

    // Databases
    private FirebaseAuth auth;

    // PubNub Configuration
    public static PNConfiguration pnConfiguration;
    public static PubNub pubnub = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        init();
    }

    private void init() {
        fragmentManager = getSupportFragmentManager();
        permissionManager = new PermissionManager(context);
        permissionManager.requestLocationPermission();
        permissionManager.requestStoragePermission();
        permissionManager.requestNetworkPermission();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = getResources().getDisplayMetrics().density;
        screenWidth = metrics.widthPixels / density;
        screenHeight = metrics.heightPixels / density;

        // Firebase sign in
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(SYSTEM_ACCOUNT, SYSTEM_PWD);

        // Pubnub init
        pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(PUBNUB_SUBSCRIBE);
        pnConfiguration.setPublishKey(PUBNUB_PUBLISH);
        pnConfiguration.setSecure(false);
        pubnub = new PubNub(pnConfiguration);
        pubnub.subscribe().channels(Arrays.asList(EVENT_CHANNEL, EVENT_CHANNEL_DELETED, MESSAGE_CHANNEL, MESSAGE_CHANNEL_DELETED)).execute();

        setTab();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionManager.PERMISSION_ACCESS_COARSE_LOCATION:
                break;
            case PermissionManager.PERMISSION_ACCESS_FINE_LOCATION:
                break;
            case PermissionManager.PERMISSION_READ_EXTERNAL_STORAGE:
                break;
            case PermissionManager.PERMISSION_WRITE_EXTERNAL_STORAGE:
                break;
            case PermissionManager.PERMISSION_ACCESS_NETWORK_STATE:
                break;
            case PermissionManager.PERMISSION_ACCESS_WIFI_STATE:
                break;
        }
    }

    public static PubNub getPubNub() {
        return pubnub;
    }

    private void setTab() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayUseLogoEnabled(true);
        if (screenHeight >= 800)
            actionBar.setDisplayShowTitleEnabled(true);

        android.support.v7.app.ActionBar.Tab tabHome = actionBar.newTab()
                .setIcon(R.mipmap.ic_home_unclick).setTabListener(this);
        actionBar.addTab(tabHome);

        // All icons are padding 25%
        android.support.v7.app.ActionBar.Tab tabEvent = actionBar
                .newTab().setIcon(R.mipmap.ic_event_unclick).setTabListener(this);
        actionBar.addTab(tabEvent);
        actionBar.selectTab(tabEvent);

        android.support.v7.app.ActionBar.Tab tabMessage = actionBar
                .newTab().setIcon(R.mipmap.ic_mail_unclick).setTabListener(this);
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
        switch (tab.getPosition()) {
            case 0:
                setFragment(HOME_FRAGMENT);
                tab.setIcon(R.mipmap.ic_home);
                break;
            case 1:
                setFragment(EVENT_FRAGMENT);
                tab.setIcon(R.mipmap.ic_event);
                break;
            case 2:
                setFragment(INBOX_FRAGMENT);
                tab.setIcon(R.mipmap.ic_mail);
                break;
            case 3:
                setFragment(SETTINGS_FRAGMENT);
                tab.setIcon(R.mipmap.ic_settings);
                break;
        }

    }

    private void setFragment(String fragmentTag) {
        Fragment fragment = null;

        switch (fragmentTag) {
            case HOME_FRAGMENT:
                fragment = fragmentManager.findFragmentByTag(HOME_FRAGMENT);
                if (null == fragment)
                    fragment = new HomeFragment();
                break;
            case EVENT_FRAGMENT:
                fragment = fragmentManager.findFragmentByTag(EVENT_FRAGMENT);
                if (null == fragment)
                    fragment = new EventFragment();
                break;
            case INBOX_FRAGMENT:
                fragment = fragmentManager.findFragmentByTag(INBOX_FRAGMENT);
                if (null == fragment)
                    fragment = new InboxFragment();
                break;
            case SETTINGS_FRAGMENT:
                fragment = fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT);
                if (null == fragment)
                    fragment = new SettingsFragment();
                break;
            case BUILDING_FRAGMENT:
                fragment = fragmentManager.findFragmentByTag(BUILDING_FRAGMENT);
                if (null == fragment)
                    fragment = new BuildingFragment();
                break;
        }

        try {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.activity_main_frameLayout, fragment, fragmentTag).addToBackStack(fragmentTag).commit();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        switch (tab.getPosition()) {
            case 0:
                tab.setIcon(R.mipmap.ic_home_unclick);
                break;
            case 1:
                tab.setIcon(R.mipmap.ic_event_unclick);
                break;
            case 2:
                tab.setIcon(R.mipmap.ic_mail_unclick);
                break;
            case 3:
                tab.setIcon(R.mipmap.ic_settings_unclick);
                break;
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
