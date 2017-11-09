package com.tsungweiho.intelligentpowersaving;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.fragments.BuildingFragment;
import com.tsungweiho.intelligentpowersaving.fragments.EventFragment;
import com.tsungweiho.intelligentpowersaving.fragments.HomeFragment;
import com.tsungweiho.intelligentpowersaving.fragments.InboxFragment;
import com.tsungweiho.intelligentpowersaving.fragments.MessageFragment;
import com.tsungweiho.intelligentpowersaving.fragments.SettingsFragment;
import com.tsungweiho.intelligentpowersaving.objects.Building;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;
import com.tsungweiho.intelligentpowersaving.services.MainService;
import com.tsungweiho.intelligentpowersaving.tools.PermissionManager;
import com.tsungweiho.intelligentpowersaving.tools.SharedPreferencesManager;
import com.tsungweiho.intelligentpowersaving.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener, FragmentTags, DBConstants, PubNubAPIConstants {

    private String TAG = "MainActivity";

    public static Context context;

    // functions
    private NetworkUtils networkUtils;
    private String[] mainTabList = {HOME_FRAGMENT, EVENT_FRAGMENT, INBOX_FRAGMENT, SETTINGS_FRAGMENT};

    // UI Widgets
    private FrameLayout flError;
    private ProgressBar pbError;

    //screen info
    public static float screenWidth;
    public static float screenHeight;
    public static ActionBar actionBar;
    private FragmentManager fragmentManager;

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

        // Request permissions
        if (!PermissionManager.hasAllPermissions(MainActivity.this))
            ActivityCompat.requestPermissions(this, PermissionManager.permissions, PermissionManager.PERMISSION_ALL);

        // View init
        flError = (FrameLayout) findViewById(R.id.activity_main_fl_error);
        pbError = (ProgressBar) findViewById(R.id.activity_main_pb_error);
        Button btnConnect = (Button) findViewById(R.id.activity_main_btn_reconnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == networkUtils)
                    networkUtils = new NetworkUtils();
                networkUtils.checkNetworkConnection();
                pbError.setVisibility(View.VISIBLE);
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = getResources().getDisplayMetrics().density;
        screenWidth = metrics.widthPixels / density;
        screenHeight = metrics.heightPixels / density;

        // Pubnub init
        pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(PUBNUB_SUBSCRIBE_KEY);
        pnConfiguration.setPublishKey(PUBNUB_PUBLISH_KEY);
        pnConfiguration.setSecure(false);
        pubnub = new PubNub(pnConfiguration);
        setupServiceInThread();

        setActionbar();
    }

    // Alleviate main thread work loading
    private void setupServiceInThread() {
        new Thread(new Runnable() {
            public void run() {
                startService();
            }
        }).start();
    }

    private void startService() {
        Intent intent = new Intent(MainActivity.this, MainService.class);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // Request permissions
        if (!PermissionManager.hasAllPermissions(MainActivity.this))
            ActivityCompat.requestPermissions(this, PermissionManager.permissions, PermissionManager.PERMISSION_ALL);
    }

    public void setIfShowErrorMessage(boolean ifConnected) {
        pbError.setVisibility(View.GONE);
        if (ifConnected) {
            flError.setVisibility(View.GONE);
        } else {
            flError.setVisibility(View.VISIBLE);
        }
    }

    public static PubNub getPubNub() {
        return pubnub;
    }

    private void setActionbar() {
        actionBar = getSupportActionBar();
        if (null == actionBar)
            return;

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayUseLogoEnabled(true);

        if (screenHeight >= 800)
            actionBar.setDisplayShowTitleEnabled(true);

        for (int index = 0; index < mainTabList.length; index++) {
            addTab(mainTabList[index], index);
        }
    }

    private void addTab(String fragmentTag, int index) {
        android.support.v7.app.ActionBar.Tab tab = actionBar.newTab().setTabListener(this);

        // All icons are from Android Studio with padding 25%
        switch (fragmentTag) {
            case HOME_FRAGMENT:
                tab.setIcon(R.mipmap.ic_home_unclick);
                break;
            case EVENT_FRAGMENT:
                tab.setIcon(R.mipmap.ic_event_unclick);
                break;
            case INBOX_FRAGMENT:
                tab.setIcon(R.mipmap.ic_mail_unclick);
                break;
            case SETTINGS_FRAGMENT:
                tab.setIcon(R.mipmap.ic_settings_unclick);
                break;
        }

        if (fragmentTag.equalsIgnoreCase(HOME_FRAGMENT)) {
            actionBar.addTab(tab);
        } else {
            actionBar.addTab(tab, index, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check internet connection
        networkUtils = new NetworkUtils();
        networkUtils.checkNetworkConnection();

        // Read users preference
        String PREF_SEPARATOR = ",";
        MyAccountInfo myAccountInfo;

        myAccountInfo = SharedPreferencesManager.getInstance().getMyAccountInfo();
        if (myAccountInfo.getSubscription().split(PREF_SEPARATOR)[0].equalsIgnoreCase("1"))
            pubnub.subscribe().channels(Arrays.asList(EVENT_CHANNEL, EVENT_CHANNEL_DELETED)).execute();
        if (myAccountInfo.getSubscription().split(PREF_SEPARATOR)[1].equalsIgnoreCase("1"))
            pubnub.subscribe().channels(Arrays.asList(MESSAGE_CHANNEL, MESSAGE_CHANNEL_DELETED)).execute();
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

    public void setFragment(String fragmentTag) {
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
        }

        startReplaceFragment(fragment, fragmentTag);
    }

    public void setBuildingFragment(Building building) {
        Bundle bundle = new Bundle();
        bundle.putString(BUILDING_FRAGMENT_KEY, building.getName());
        BuildingFragment buildingFragment = new BuildingFragment();
        buildingFragment.setArguments(bundle);

        startReplaceFragment(buildingFragment, BUILDING_FRAGMENT);
    }

    public void setMessageFragment(Message message, int position) {
        Bundle bundle = new Bundle();
        ArrayList<String> messageInfo = new ArrayList<>(2);
        messageInfo.add(message.getUniqueId());
        messageInfo.add(position + "");
        bundle.putStringArrayList(MESSAGE_FRAGMENT_KEY, messageInfo);
        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(bundle);

        startReplaceFragment(messageFragment, MESSAGE_FRAGMENT);
    }

    private void startReplaceFragment(Fragment fragment, String fragmentTag) {
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

    @Override
    public void onBackPressed() {
        // lock back button
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // clean background listeners
        stopService();
        pubnub.destroy();
    }

    private void stopService() {
        Intent intent = new Intent(MainActivity.this, MainService.class);
        stopService(intent);
    }
}
