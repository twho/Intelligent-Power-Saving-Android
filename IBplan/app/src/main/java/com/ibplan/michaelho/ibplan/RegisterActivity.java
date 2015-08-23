package com.ibplan.michaelho.ibplan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ibplan.michaelho.com.ibplan.michaelho.constants.GCMConfig;
import com.ibplan.michaelho.com.ibplan.michaelho.fragment_register.AdminFragment;
import com.ibplan.michaelho.com.ibplan.michaelho.fragment_register.ClientFragment;
/**
 * Created by MichaelHo on 2015/5/11.
 */
public class RegisterActivity extends ActionBarActivity implements android.support.v7.app.ActionBar.TabListener, GCMConfig {

    public final static String TAG = "MainActivity";
    public static boolean ifRegister = false;

    public GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTab();
        Log.d("GCM", "MyGCM");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setTab() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);

        android.support.v7.app.ActionBar.Tab tab1 = actionBar.newTab()
                .setText("Student").setTabListener(this);

        actionBar.addTab(tab1);
        actionBar.selectTab(tab1);

        android.support.v7.app.ActionBar.Tab tab2 = actionBar
                .newTab().setText("Administrator").setTabListener(this);

        actionBar.addTab(tab2);
        actionBar.selectTab(tab2);
    }

    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        Fragment fragment = null;
        switch (tab.getPosition()) {
            case 0:
                fragment = new ClientFragment();
                tab.setText("Student");
                break;
            case 1:
                fragment = new AdminFragment();
                tab.setText("Administrator");
                break;
        }

        try {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();

            // Replace whatever is in the fragment_container view with
            // this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.main_activity_frameLayout, fragment);

            // Commit the transaction
            transaction.commit();

        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }
    }

    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        switch (tab.getPosition()) {
            case 0:
                tab.setText("Student");
                break;
            case 1:
                tab.setText("Administrator");
                break;
        }
    }

    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }


}