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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
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
import java.util.Arrays;

/**
 * Fragment for user to set basic user information
 * <p>
 * This fragment is the user interface that user can set account information.
 *
 * @author Tsung Wei Ho
 * @version 1224.2017
 * @since 1.0.0
 */
public class SettingsFragment extends Fragment implements FragmentTags, DBConstants, PubNubAPIConstants {
    private final String TAG = "SettingsFragment";

    // Settings Fragment View
    private View view;

    // UI Views
    private Switch swEvent, swPublic;
    private ImageView ivProfile;
    private EditText edName, edEmail;
    private TextView tvProgress;
    private ProgressBar progressBar;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        view = binding.getRoot();

        // Get access to main context
        context = MainActivity.getContext();

        init();

        return view;
    }

    /**
     * Init all classes needed in this fragment
     */
    private void init() {
        settingsFragmentListener = new SettingsFragmentListener();

        // Singleton classes
        alertDialogMgr = AlertDialogManager.getInstance();
        firebaseMgr = FirebaseManager.getInstance();
        imageUtils = ImageUtils.getInstance();

        // Compile with SDK 26, no need to cast views
        edName = view.findViewById(R.id.fragment_settings_ed_name);
        edEmail = view.findViewById(R.id.fragment_settings_ed_email);
        tvProgress = view.findViewById(R.id.fragment_settings_tv_progress);
        tvProgress.setOnClickListener(settingsFragmentListener);
        progressBar = view.findViewById(R.id.fragment_settings_progressBar);

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
                imgCropDialog.setOnDismissListener(settingsFragmentListener);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SettingsFragmentListener implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, DialogInterface.OnDismissListener,
            OnProgressListener<UploadTask.TaskSnapshot>, OnFailureListener, OnSuccessListener<UploadTask.TaskSnapshot> {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_settings_iv:
                    alertDialogMgr.showCameraDialog(SETTINGS_FRAGMENT);
                    break;
                case R.id.fragment_settings_tv_progress:
                    performUploadTask();
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
        public void onDismiss(DialogInterface dialogInterface) {
            performUploadTask();
        }

        /**
         * Listen for state changes and completion of the upload.
         *
         * @param taskSnapshot the Firebase task item
         */
        @Override
        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            progressBar.setProgress((int) progress);

            // String concatenation
            StringBuilder strBuilder = new StringBuilder(String.valueOf(progress));
            strBuilder.append("\n");
            strBuilder.append(context.getResources().getString(R.string.upload_profilepic));

            tvProgress.setText(strBuilder);
            tvProgress.setTextColor(context.getResources().getColor(R.color.green));
        }

        /**
         * Handle unsuccessful uploads
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            tvProgress.setClickable(true);
            tvProgress.setText(context.getResources().getString(R.string.notification_fail));
            tvProgress.setTextColor(context.getResources().getColor(R.color.light_red));
        }

        /**
         * Handle successful uploads on complete
         *
         * @param taskSnapshot the Firebase task item
         */
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();

            // Store to local user preference
            myAccountInfo.setImageUrl(downloadUrl.toString());
            binding.setMyAccountInfo(myAccountInfo);

            tvProgress.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Get UI ready for upload task and add listeners to upload progress
     */
    private void performUploadTask() {
        tvProgress.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        UploadTask uploadTask = firebaseMgr.uploadProfileImg(imageUtils.getFileFromBitmap(ivProfile.getDrawingCache()).getPath());
        uploadTask.addOnProgressListener(settingsFragmentListener).addOnFailureListener(settingsFragmentListener).addOnSuccessListener(settingsFragmentListener);
    }

    /**
     * Set channel subscription information, which will be stored in user preference locally
     *
     * @param isChecked the boolean indicates if user check the checkbox
     * @param index     the index of the subscribed channel
     */
    private void setChannelSubscription(Boolean isChecked, int index) {
        Pair<String, String> channels = index == 0 ? new Pair<>(EVENT_CHANNEL, EVENT_CHANNEL_DELETED) : new Pair<>(MESSAGE_CHANNEL, MESSAGE_CHANNEL_DELETED);

        if (isChecked) {
            MainActivity.getPubNub().subscribe().channels(Arrays.asList(channels.first, channels.second)).execute();
        } else {
            MainActivity.getPubNub().unsubscribe().channels(Arrays.asList(channels.first, channels.second)).execute();
        }

        myAccountInfo.setSubscriptionBools(index, isChecked);
    }

    /**
     * Load user image to the profile imageView
     *
     * @param imageView the imageView to show user image
     * @param url       the resource url of the user image
     */
    @BindingAdapter({"bind:userImage"})
    public static void loadUserImage(final ImageView imageView, final String url) {
        if (url.equalsIgnoreCase("")) {
            imageView.setImageDrawable(MainActivity.getContext().getResources().getDrawable(R.mipmap.ic_preload_profile));
            return;
        }

        final ImageUtils imageUtils = ImageUtils.getInstance();
        imageUtils.setRoundedCornerImageViewFromUrl(url, imageView, imageUtils.IMG_TYPE_PROFILE);

        // Auto refresh after 3 seconds.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageUtils.setRoundedCornerImageViewFromUrl(url, imageView, imageUtils.IMG_TYPE_PROFILE);
            }
        }, 3000);
    }

    @Override
    public void onResume() {
        super.onResume();

        tvProgress.setClickable(false);
        myAccountInfo = PreferencesManager.getInstance().getMyAccountInfo();

        // Bind data to UIs
        binding.setMyAccountInfo(myAccountInfo);
        swEvent.setChecked(myAccountInfo.getSubscriptionBools()[0]);
        swPublic.setChecked(myAccountInfo.getSubscriptionBools()[1]);
    }

    @Override
    public void onPause() {
        super.onPause();

        myAccountInfo.setName(edName.getText().toString());
        myAccountInfo.setEmail(edEmail.getText().toString());
        PreferencesManager.getInstance().saveMyAccountInfo(myAccountInfo);
    }
}
