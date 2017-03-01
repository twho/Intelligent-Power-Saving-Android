package com.tsungweiho.intelligentpowersaving.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pubnub.api.PubNub;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databinding.FragmentSettingsBinding;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;
import com.tsungweiho.intelligentpowersaving.tools.AlertDialogManager;
import com.tsungweiho.intelligentpowersaving.tools.SharedPreferencesManager;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/18.
 */

public class SettingsFragment extends Fragment implements FragmentTags, DBConstants, PubNubAPIConstants {
    private String TAG = "SettingsFragment";

    // Settings Fragment View
    private View view;

    // UI Views
    private Switch swEvent, swPublic;
    private ImageView ivProfile;
    private EditText edName, edEmail;

    // Functions
    private Context context;
    private SharedPreferencesManager sharedPreferencesManager;
    private MyAccountInfo myAccountInfo;
    private FragmentSettingsBinding binding;
    private AlertDialogManager alertDialogManager;
    private static ImageUtilities imageUtilities;
    private Bitmap bmpBuffer;
    private String PREF_SEPARTOR = ",";

    // Camera
    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_IMAGE = 0;

    // PubNub
    private PubNub pubnub;

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
        pubnub = MainActivity.getPubNub();
        alertDialogManager = new AlertDialogManager(context);
        sharedPreferencesManager = new SharedPreferencesManager(context);
        imageUtilities = MainActivity.getImageUtilities();

        edName = (EditText) view.findViewById(R.id.fragment_settings_ed_name);
        edEmail = (EditText) view.findViewById(R.id.fragment_settings_ed_email);

        ivProfile = (ImageView) view.findViewById(R.id.fragment_settings_iv);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogManager.showCameraDialog(SETTINGS_FRAGMENT);
            }
        });
        swEvent = (Switch) view.findViewById(R.id.fragment_settings_sw_event);
        swEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ifChecked) {
                if (ifChecked) {
                    pubnub.subscribe().channels(Arrays.asList(EVENT_CHANNEL, EVENT_CHANNEL_DELETED)).execute();
                    myAccountInfo.setSubscription("1," + myAccountInfo.getSubscription().split(PREF_SEPARTOR)[1]);
                } else {
                    pubnub.unsubscribe().channels(Arrays.asList(EVENT_CHANNEL, EVENT_CHANNEL_DELETED)).execute();
                    myAccountInfo.setSubscription("0," + myAccountInfo.getSubscription().split(PREF_SEPARTOR)[1]);
                }
            }
        });

        swPublic = (Switch) view.findViewById(R.id.fragment_settings_sw_public);
        swPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ifChecked) {
                if (ifChecked) {
                    pubnub.subscribe().channels(Arrays.asList(MESSAGE_CHANNEL, MESSAGE_CHANNEL_DELETED)).execute();
                    myAccountInfo.setSubscription(myAccountInfo.getSubscription().split(PREF_SEPARTOR)[0] + ",1");
                } else {
                    pubnub.unsubscribe().channels(Arrays.asList(MESSAGE_CHANNEL, MESSAGE_CHANNEL_DELETED)).execute();
                    myAccountInfo.setSubscription(myAccountInfo.getSubscription().split(PREF_SEPARTOR)[0] + ",0");
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMAGE) {
                ContentResolver resolver = context.getContentResolver();
                Uri uri = data.getData();

                try {
                    bmpBuffer = MediaStore.Images.Media.getBitmap(resolver, uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_CAMERA) {
                bmpBuffer = (Bitmap) data.getExtras().get("data");
            }

            if (null != bmpBuffer) {
                alertDialogManager.showCropImageDialog(ivProfile, bmpBuffer);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @BindingAdapter({"bind:userImage"})
    public static void loadUserImage(final ImageView imageView, final String url) {
        if (url.equalsIgnoreCase("")) {
            imageView.setImageDrawable(MainActivity.getContext().getResources().getDrawable(R.mipmap.ic_preload_profile));
        } else {
            imageView.setImageBitmap(imageUtilities.getRoundedCroppedBitmap(imageUtilities.decodeBase64ToBitmap(url)));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        myAccountInfo = sharedPreferencesManager.getMyAccountInfo();
        binding.setMyAccountInfo(myAccountInfo);
        swEvent.setChecked(myAccountInfo.getSubscription().split(PREF_SEPARTOR)[0].equalsIgnoreCase("1"));
        swPublic.setChecked(myAccountInfo.getSubscription().split(PREF_SEPARTOR)[1].equalsIgnoreCase("1"));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != ivProfile.getDrawingCache()) {
            myAccountInfo.setImageUrl(imageUtilities.encodeBase64ToString(ivProfile.getDrawingCache()));
        }

        myAccountInfo.setName(edName.getText().toString());
        myAccountInfo.setEmail(edEmail.getText().toString());
        sharedPreferencesManager.saveMyAccountInfo(myAccountInfo);
    }
}
