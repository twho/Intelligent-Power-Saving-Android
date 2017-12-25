package com.tsungweiho.intelligentpowersaving.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.pubnub.api.PubNub;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databinding.FragmentSettingsBinding;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;
import com.tsungweiho.intelligentpowersaving.tools.AlertDialogManager;
import com.tsungweiho.intelligentpowersaving.tools.FirebaseManager;
import com.tsungweiho.intelligentpowersaving.tools.PreferencesManager;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Tsung Wei Ho on 4/15/2015.
 * Updated by Tsung Wei Ho on 12/24/2017.
 */

public class SettingsFragment extends Fragment implements FragmentTags, DBConstants, PubNubAPIConstants {
    private final String TAG = "SettingsFragment";

    // Settings Fragment View
    private View view;

    // UI Views
    private Switch swEvent, swPublic;
    private ImageView ivProfile;
    private EditText edName, edEmail;
    private Dialog imgCropDialog;

    // Functions
    private Context context;
    private SettingsFragmentListener settingsFragmentListener;
    private MyAccountInfo myAccountInfo;
    private FragmentSettingsBinding binding;
    private AlertDialogManager alertDialogMgr;
    private FirebaseManager firebaseMgr;
    private ImageUtils imageUtils;
    private Bitmap bmpBuffer;

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
        settingsFragmentListener = new SettingsFragmentListener();


        // Singleton classes
        alertDialogMgr = AlertDialogManager.getInstance();
        firebaseMgr = FirebaseManager.getInstance();
        imageUtils = ImageUtils.getInstance();

        // Compile with SDK 26, no need to cast views
        edName = view.findViewById(R.id.fragment_settings_ed_name);
        edEmail = view.findViewById(R.id.fragment_settings_ed_email);

        ivProfile = view.findViewById(R.id.fragment_settings_iv);
        ivProfile.setOnClickListener(settingsFragmentListener);

        swEvent = view.findViewById(R.id.fragment_settings_sw_event);
        swEvent.setOnCheckedChangeListener(settingsFragmentListener);

        swPublic = view.findViewById(R.id.fragment_settings_sw_public);
        swPublic.setOnCheckedChangeListener(settingsFragmentListener);
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
                imgCropDialog = alertDialogMgr.showCropImageDialog(ivProfile, bmpBuffer);
                imgCropDialog.setOnCancelListener(settingsFragmentListener);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SettingsFragmentListener implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, DialogInterface.OnCancelListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_settings_iv:
                    alertDialogMgr.showCameraDialog(SETTINGS_FRAGMENT);
                    break;
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            switch (compoundButton.getId()) {
                case R.id.fragment_settings_sw_event:
                    setChannelSubscription(isChecked, 0);
                    break;
                case R.id.fragment_settings_sw_public:
                    setChannelSubscription(isChecked, 1);
                    break;
            }
        }

        // Only handle image crop dialog
        @Override
        public void onCancel(DialogInterface dialogInterface) {
//            firebaseMgr.uploadProfileImg(imageUtils.getFileFromBitmap(ivProfile.getDrawingCache()).getPath(), );
        }
    }

    private void setChannelSubscription(Boolean isChecked, int index) {
        Pair<String, String> channels = index == 0 ? new Pair<>(EVENT_CHANNEL, EVENT_CHANNEL_DELETED) : new Pair<>(MESSAGE_CHANNEL, MESSAGE_CHANNEL_DELETED);

        if (isChecked) {
            pubnub.subscribe().channels(Arrays.asList(channels.first, channels.second)).execute();
        } else {
            pubnub.unsubscribe().channels(Arrays.asList(channels.first, channels.second)).execute();
        }

        myAccountInfo.setSubscriptionBools(index, isChecked);
    }

    @BindingAdapter({"bind:userImage"})
    public static void loadUserImage(final ImageView imageView, final String url) {
        if (url.equalsIgnoreCase("")) {
            imageView.setImageDrawable(MainActivity.getContext().getResources().getDrawable(R.mipmap.ic_preload_profile));
        } else {
            imageView.setImageBitmap(ImageUtils.getInstance().getRoundedCroppedBitmap(ImageUtils.getInstance().decodeBase64ToBitmap(url)));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        myAccountInfo = PreferencesManager.getInstance().getMyAccountInfo();

        // Bind data to UIs
        binding.setMyAccountInfo(myAccountInfo);
        swEvent.setChecked(myAccountInfo.getSubscriptionBools()[0]);
        swPublic.setChecked(myAccountInfo.getSubscriptionBools()[1]);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (null != ivProfile.getDrawingCache()) {
            myAccountInfo.setImageUrl(ImageUtils.getInstance().encodeBase64ToString(ivProfile.getDrawingCache()));
        }

        myAccountInfo.setName(edName.getText().toString());
        myAccountInfo.setEmail(edEmail.getText().toString());
        PreferencesManager.getInstance().saveMyAccountInfo(myAccountInfo);
    }
}
