package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.databinding.FragmentSettingsBinding;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;

/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/18.
 */

public class SettingsFragment extends Fragment implements DBConstants {
    private String TAG = "SettingsFragment";

    // Settings Fragment View
    private View view;

    // UI Views
    private Switch swEvent, swPublic;

    // Functions
    private Context context;
    private SharedPreferences sharedPreferences;
    private MyAccountInfo myAccountInfo;
    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        view = binding.getRoot();

        context = MainActivity.getContext();
        init();
        return view;
    }

    private void init() {
        swEvent = (Switch) view.findViewById(R.id.fragment_settings_sw_event);
        swEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        swPublic = (Switch) view.findViewById(R.id.fragment_settings_sw_public);
        swPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadUserImage(final ImageView imageView, final String url) {
        if (url.equalsIgnoreCase("")) {
            imageView.setImageDrawable(MainActivity.getContext().getResources().getDrawable(R.mipmap.ic_preload_profile));
        } else {

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String defaultAccount = gson.toJson(new MyAccountInfo(context.getString(R.string.email), context.getString(R.string.diaplay_name), "", "1,1"));
        String json = sharedPreferences.getString(PREF_USER_ACCOUNT, defaultAccount);
        myAccountInfo = gson.fromJson(json, MyAccountInfo.class);
        binding.setMyAccountInfo(myAccountInfo);
        swEvent.setChecked(Boolean.valueOf(myAccountInfo.getSubscription().split(PREF_SEPARTOR)[0]));
        swPublic.setChecked(Boolean.valueOf(myAccountInfo.getSubscription().split(PREF_SEPARTOR)[0]));
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String savedAccount = gson.toJson(myAccountInfo);
        prefsEditor.putString(PREF_USER_ACCOUNT, savedAccount);
        prefsEditor.apply();
    }
}
