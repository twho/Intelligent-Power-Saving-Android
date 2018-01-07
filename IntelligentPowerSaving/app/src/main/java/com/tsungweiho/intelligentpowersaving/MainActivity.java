package com.tsungweiho.intelligentpowersaving;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.fragments.BuildingFragment;
import com.tsungweiho.intelligentpowersaving.fragments.EventFragment;
import com.tsungweiho.intelligentpowersaving.fragments.HomeFragment;
import com.tsungweiho.intelligentpowersaving.fragments.InboxFragment;
import com.tsungweiho.intelligentpowersaving.fragments.MessageFragment;
import com.tsungweiho.intelligentpowersaving.fragments.ReportFragment;
import com.tsungweiho.intelligentpowersaving.fragments.SettingsFragment;
import com.tsungweiho.intelligentpowersaving.objects.Building;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;
import com.tsungweiho.intelligentpowersaving.services.MainService;
import com.tsungweiho.intelligentpowersaving.tools.FirebaseManager;
import com.tsungweiho.intelligentpowersaving.tools.PermissionManager;
import com.tsungweiho.intelligentpowersaving.tools.PubNubHelper;
import com.tsungweiho.intelligentpowersaving.utils.SharedPrefsUtils;
import com.tsungweiho.intelligentpowersaving.utils.NetworkUtils;

import java.util.ArrayList;

/**
 * Activity as main user interface
 * <p>
 * This activity is the user interface that provide most of the app functions.
 *
 * @author Tsung Wei Ho
 * @version 1222.2017
 * @since 1.0.0
 */
public class MainActivity extends AppCompatActivity implements ActionBar.TabListener, FragmentTags, DBConstants, PubNubAPIConstants {

    private String TAG = "MainActivity";

    // functions
    private static Context context;
    private NetworkUtils networkUtils;

    // UI Widgets
    private FrameLayout flError;
    private ProgressBar pbError;
    private TextView tvError;

    //screen info
    public static float screenWidth;
    public static float screenHeight;
    public static ActionBar actionBar;
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        init();
    }

    /**
     * Init functions and UIs in the app
     */
    private void init() {
        fragmentManager = getSupportFragmentManager();

        // Request permissions
        if (!PermissionManager.hasAllPermissions(MainActivity.this))
            ActivityCompat.requestPermissions(this, PermissionManager.permissions, PermissionManager.PERMISSION_ALL);

        // Init views, compile with SDK 26, no need to cast views
        flError = findViewById(R.id.activity_main_fl_error);
        pbError = findViewById(R.id.activity_main_pb_error);

        Button btnConnect = findViewById(R.id.activity_main_btn_reconnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == networkUtils)
                    networkUtils = new NetworkUtils();
                networkUtils.checkNetworkConnection();
                pbError.setVisibility(View.VISIBLE);
            }
        });

        tvError = findViewById(R.id.activity_main_tv_error);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = getResources().getDisplayMetrics().density;
        screenWidth = metrics.widthPixels / density;
        screenHeight = metrics.heightPixels / density;

        setActionbar();
    }

    /**
     * Get the context of MainActivity
     *
     * @return the context of MainActivity
     */
    public static Context getContext() {
        return context;
    }

    public static FragmentManager getFragmentMgr() {
        return fragmentManager;
    }

    /**
     * Start service in separate thread to reduce workload of main thread
     */
    private void setupServiceInThread() {
        new Thread(new Runnable() {
            public void run() {
                startService(new Intent(MainActivity.this, MainService.class));
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // Request permissions
        if (!PermissionManager.hasAllPermissions(MainActivity.this))
            ActivityCompat.requestPermissions(this, PermissionManager.permissions, PermissionManager.PERMISSION_ALL);
    }

    public void setConnectionMessage(boolean isConnected) {
        pbError.setVisibility(View.GONE);

        if (!isConnected) {
            flError.setVisibility(View.GONE);
            tvError.setText(this.getResources().getString(R.string.internet_error));
            return;
        }

        // Sign in Firebase using system account and pwd
        FirebaseManager.getInstance().signInSystemAccount(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                tvError.setText(MainActivity.this.getResources().getString(R.string.auth_error));
                flError.setVisibility(task.isSuccessful() ? View.GONE : View.VISIBLE);
            }
        });
    }

    /**
     * Set up action bar for different screen sizes
     */
    private void setActionbar() {
        actionBar = getSupportActionBar();
        if (null == actionBar)
            return;

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayUseLogoEnabled(true);

        if (screenHeight >= 800)
            actionBar.setDisplayShowTitleEnabled(true);

        for (int index = 0; index < mainFragments.size(); index++) {
            addTab(mainFragments.get(index), index);
        }
    }

    /**
     * Add action bar tab to main UI
     *
     * @param fragmentTag the fragment tag of action bar tab
     * @param index       the order of action bar tab to be shown
     */
    private void addTab(MainFragment fragmentTag, int index) {
        android.support.v7.app.ActionBar.Tab tab = actionBar.newTab().setTabListener(this);

        // All icons are from Android Studio with padding 25%
        tab.setIcon(inactiveIcons[mainFragments.indexOf(fragmentTag)]);

        if (fragmentTag == MainFragment.HOME) {
            actionBar.addTab(tab);
        } else {
            actionBar.addTab(tab, index, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check internet connection
        if (null == networkUtils)
            networkUtils = new NetworkUtils();
        networkUtils.checkNetworkConnection();

        // Check if user preference exists
        SharedPrefsUtils.getInstance().initMyAccountInfo();

        // Read users preference
        MyAccountInfo myAccountInfo = SharedPrefsUtils.getInstance().getMyAccountInfo();
        if (myAccountInfo.getSubscriptionBools()[0])
            PubNubHelper.getInstance().subscribeToChannels(IPowerSaving.getPubNub(), ActiveChannels.EVENT);
        if (myAccountInfo.getSubscriptionBools()[1])
            PubNubHelper.getInstance().subscribeToChannels(IPowerSaving.getPubNub(), ActiveChannels.MESSAGE);

        // Start background service
        setupServiceInThread();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        tab.setIcon(activeIcons[tab.getPosition()]);
        setFragment(mainFragments.get(tab.getPosition()));
    }

    /**
     * Set fragment by its tag
     *
     * @param fragmentTag the fragment tag of the fragment to be set
     */
    public void setFragment(MainFragment fragmentTag) {
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag.toString());

        // Instantiate fragment if it cannot be found in stack
        if (null == fragment) {
            switch (fragmentTag) {
                case HOME:
                    fragment = new HomeFragment();
                    break;
                case EVENT:
                    fragment = new EventFragment();
                    break;
                case INBOX:
                    fragment = new InboxFragment();
                    break;
                case SETTINGS:
                    fragment = new SettingsFragment();
                    break;
            }
        }

        startReplaceFragment(fragment, fragmentTag.toString(), false);
    }

    /**
     * Set up BuildingFragment
     *
     * @param building the building object to be shown in BuildingFragment
     */
    public void setBuildingFragment(Building building) {
        Bundle bundle = new Bundle();
        bundle.putString(BUILDING_FRAGMENT_KEY, building.getName());

        BuildingFragment buildingFragment = new BuildingFragment();
        buildingFragment.setArguments(bundle);

        startReplaceFragment(buildingFragment, ChildFragment.BUILDING.toString(), true);
    }

    /**
     * Set up MessageFragment
     *
     * @param message  the message object to be shown in MessageFragment
     * @param position the position of the message list item clicked by user
     */
    public void setMessageFragment(Message message, int position) {
        Bundle bundle = new Bundle();
        ArrayList<String> messageInfo = new ArrayList<>(2);
        messageInfo.add(message.getUniqueId());
        messageInfo.add(position + "");
        bundle.putStringArrayList(MESSAGE_FRAGMENT_KEY, messageInfo);

        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(bundle);

        startReplaceFragment(messageFragment, ChildFragment.MESSAGE.toString(), true);
    }

    /**
     * Set up ReportFragment
     */
    public void setReportFragment() {
        // TODO pass draft content as argument to the fragment
        ReportFragment reportFragment = new ReportFragment();

        startReplaceFragment(reportFragment, ChildFragment.REPORT.toString(), true);
    }

    /**
     * Start replacing current fragment
     *
     * @param fragment    the fragment to be set active
     * @param fragmentTag the fragment tag of active fragment
     * @param animate     show animation in fragment transition
     */
    private void startReplaceFragment(Fragment fragment, String fragmentTag, boolean animate) {
        try {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();

            if (animate)
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);

            transaction.replace(R.id.activity_main_frameLayout, fragment, fragmentTag).addToBackStack(fragmentTag).commit();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        tab.setIcon(inactiveIcons[tab.getPosition()]);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Clean background listeners
        stopService(new Intent(MainActivity.this, MainService.class));
        IPowerSaving.getPubNub().destroy();
        FirebaseManager.getInstance().signOutSystemAccount();
    }

    /**
     * Close keyboard
     *
     * @param context     the context that calls this method
     * @param windowToken the windowToken of the UI widget that is currently focused
     */
    public static void closeKeyboard(Context context, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (null != mgr)
            mgr.hideSoftInputFromWindow(windowToken, 0);
    }
}
