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
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databinding.FragmentSettingsBinding;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;
import com.tsungweiho.intelligentpowersaving.tools.AlertDialogManager;
import com.tsungweiho.intelligentpowersaving.tools.FirebaseManager;
import com.tsungweiho.intelligentpowersaving.tools.PubNubHelper;
import com.tsungweiho.intelligentpowersaving.utils.SharedPrefsUtils;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

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

    // Functions
    private Context context;
    private SettingsFragmentListener settingsFragmentListener;
    private MyAccountInfo myAccountInfo;
    private FragmentSettingsBinding binding;
    private AlertDialogManager alertDialogMgr;
    private FirebaseManager firebaseMgr;
    private PubNubHelper pubNubHelper;
    private ImageUtils imageUtils;
    private Bitmap bmpBuffer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        view = binding.getRoot();

        // Get access to main context
        context = IPowerSaving.getContext();

        init();

        return view;
    }

    /**
     * Init all classes needed in this fragment, no need to cast views while compile with SDK 26
     */
    private void init() {
        settingsFragmentListener = new SettingsFragmentListener();

        // Singleton classes
        alertDialogMgr = AlertDialogManager.getInstance();
        firebaseMgr = FirebaseManager.getInstance();
        imageUtils = ImageUtils.getInstance();
        pubNubHelper = PubNubHelper.getInstance();

        // Find views
        edName = view.findViewById(R.id.fragment_settings_ed_name);
        edEmail = view.findViewById(R.id.fragment_settings_ed_email);
        tvProgress = view.findViewById(R.id.fragment_settings_tv_progress);
        progressBar = view.findViewById(R.id.fragment_settings_progressBar);
        ivProfile = view.findViewById(R.id.fragment_settings_iv);
        swEvent = view.findViewById(R.id.fragment_settings_sw_event);
        swPublic = view.findViewById(R.id.fragment_settings_sw_public);

        // Set listeners
        tvProgress.setOnClickListener(settingsFragmentListener);
        ivProfile.setOnClickListener(settingsFragmentListener);
        swEvent.setOnCheckedChangeListener(settingsFragmentListener);
        swPublic.setOnCheckedChangeListener(settingsFragmentListener);
    }

    /**
     * Get the result from the intent
     *
     * @param requestCode the code that represents requested resource
     * @param resultCode  the code that represents request status
     * @param data        the data get from requested resource
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == alertDialogMgr.REQUEST_CODE_IMAGE) {
                ContentResolver resolver = context.getContentResolver();
                Uri uri = data.getData();

                try {
                    bmpBuffer = MediaStore.Images.Media.getBitmap(resolver, uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == alertDialogMgr.REQUEST_CODE_CAMERA) {
                if (null != data.getExtras())

                bmpBuffer = (Bitmap) data.getExtras().get("data");
            }

            if (null != bmpBuffer) {
                Dialog imgCropDialog = alertDialogMgr.showCropImageDialog(ivProfile, bmpBuffer);
                imgCropDialog.setOnDismissListener(settingsFragmentListener);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * All listeners used in SettingsFragment
     */
    private class SettingsFragmentListener implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, DialogInterface.OnDismissListener,
            OnProgressListener<UploadTask.TaskSnapshot>, OnFailureListener, OnSuccessListener<UploadTask.TaskSnapshot> {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_settings_iv: // Profile imageView
                    alertDialogMgr.showCameraDialog(MainFragment.SETTINGS.toString());
                    break;
                case R.id.fragment_settings_tv_progress: // TextView shows upload progress
                    performUploadTask();
                    break;
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            switch (compoundButton.getId()) {
                case R.id.fragment_settings_sw_event: // Switch of event channel
                    setChannelSubscription(isChecked, 0);
                    break;
                case R.id.fragment_settings_sw_public: // Switch of message channel
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

            // Set text progress
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
            showUploadFail();
        }

        /**
         * Handle successful uploads on complete
         *
         * @param taskSnapshot the Firebase task item
         */
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            if (null == taskSnapshot.getMetadata()) {
                showUploadFail();
                return;
            }

            // Store to local user preference
            myAccountInfo.setImageUrl(taskSnapshot.getMetadata().getDownloadUrl() + "");
            binding.setMyAccountInfo(myAccountInfo);

            tvProgress.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Show upload fail messages in UI
     */
    private void showUploadFail() {
        tvProgress.setClickable(true);
        tvProgress.setText(context.getResources().getString(R.string.notification_fail));
        tvProgress.setTextColor(context.getResources().getColor(R.color.light_red));
    }

    /**
     * Get UI ready for upload task and add listeners to upload progress
     */
    private void performUploadTask() {
        tvProgress.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        // Create Firebase upload task
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
        ActiveChannels channels = index == 0 ? ActiveChannels.EVENT : ActiveChannels.MESSAGE;

        if (isChecked)
            pubNubHelper.subscribeToChannels(IPowerSaving.getPubNub(), channels);
        else
            pubNubHelper.unsubscribeToChannels(IPowerSaving.getPubNub(), channels);

        // Change share preference values
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
        imageView.setImageDrawable(IPowerSaving.getContext().getResources().getDrawable(R.mipmap.ic_preload_profile));

        // Attempt to load from Firebase if local memory does not have image resource
        String uid = SharedPrefsUtils.getInstance().getMyAccountInfo().getUid();
        if (url.equalsIgnoreCase("") && null != uid && !"".equals(uid)) {
            FirebaseManager.getInstance().downloadProfileImg(uid, new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    imageView.invalidate();
                    ImageUtils.getInstance().setRoundedCornerImageViewFromUrl(uri.toString(), imageView, ImageUtils.getInstance().IMG_CIRCULAR);
                }
            }, null);

            return;
        }

        imageView.invalidate();
        ImageUtils.getInstance().setRoundedCornerImageViewFromUrl(url, imageView, ImageUtils.getInstance().IMG_CIRCULAR);
    }

    @Override
    public void onResume() {
        super.onResume();

        tvProgress.setClickable(false);
        myAccountInfo = SharedPrefsUtils.getInstance().getMyAccountInfo();

        // Bind data to UIs
        binding.setMyAccountInfo(myAccountInfo);
        swEvent.setChecked(myAccountInfo.getSubscriptionBools()[0]);
        swPublic.setChecked(myAccountInfo.getSubscriptionBools()[1]);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Auto save user profile information
        myAccountInfo.setName(edName.getText().toString());
        myAccountInfo.setEmail(edEmail.getText().toString());
        SharedPrefsUtils.getInstance().saveMyAccountInfo(myAccountInfo);
    }
}
