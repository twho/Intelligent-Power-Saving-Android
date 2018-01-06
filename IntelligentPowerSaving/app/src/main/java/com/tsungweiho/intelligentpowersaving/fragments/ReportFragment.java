package com.tsungweiho.intelligentpowersaving.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.adapters.SpinnerItemAdapter;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.BuildingDBHelper;
import com.tsungweiho.intelligentpowersaving.objects.Building;
import com.tsungweiho.intelligentpowersaving.objects.Event;
import com.tsungweiho.intelligentpowersaving.objects.ImageResponse;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;
import com.tsungweiho.intelligentpowersaving.tools.AlertDialogManager;
import com.tsungweiho.intelligentpowersaving.tools.PubNubHelper;
import com.tsungweiho.intelligentpowersaving.tools.ImgurHelper;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtils;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;
import com.tsungweiho.intelligentpowersaving.utils.SharedPrefsUtils;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Fragment for user to send messages to building admin
 * <p>
 * This fragment is the user interface that user can report or send messages to building administrator
 *
 * @author Tsung Wei Ho
 * @version 0102.2018
 * @since 2.0.0
 */
public class ReportFragment extends Fragment implements FragmentTags, PubNubAPIConstants {

    private String TAG = "ReportFragment";

    private View view;

    private Context context;

    // UI widgets
    private EditText edTitle, edContent;
    private TextView tvFab;
    private Spinner spBuilding;
    private ProgressBar progressBar;

    // Functions
    private AlertDialogManager alertDialogMgr;
    private AnimUtils animUtils;
    private ImageUtils imageUtils;
    private PubNubHelper pubNubHelper;
    private ImgurHelper imgurHelper;
    private ArrayList<Building> buildingList;
    private Bitmap bmpBuffer;
    private File tempImgFile;
    private String selectedBuilding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_report, container, false);
        context = IPowerSaving.getContext();

        init();

        return view;
    }

    /**
     * Init all classes needed in ReportFragment
     */
    private void init() {
        ReportFragmentListener reportFragmentListener = new ReportFragmentListener();
        BuildingDBHelper buildingDBHelper = new BuildingDBHelper(context);
        imgurHelper = new ImgurHelper(context);

        // Singleton classes
        alertDialogMgr = AlertDialogManager.getInstance();
        animUtils = AnimUtils.getInstance();
        pubNubHelper = PubNubHelper.getInstance();
        imageUtils = ImageUtils.getInstance();

        ImageButton ibBack = view.findViewById(R.id.fragment_report_ib_back);
        ImageButton ibDelete = view.findViewById(R.id.fragment_report_ib_delete);
        ImageButton ibSend = view.findViewById(R.id.fragment_report_ib_send);
        FloatingActionButton imgFab = view.findViewById(R.id.fragment_report_fab_add);

        ibBack.setOnClickListener(reportFragmentListener);
        ibDelete.setOnClickListener(reportFragmentListener);
        ibSend.setOnClickListener(reportFragmentListener);
        imgFab.setOnClickListener(reportFragmentListener);

        tvFab = view.findViewById(R.id.fragment_report_fab_tv_count);
        progressBar = view.findViewById(R.id.fragment_report_pb);
        edTitle = view.findViewById(R.id.fragment_report_ed_title);
        edContent = view.findViewById(R.id.fragment_report_ed_content);

        spBuilding = view.findViewById(R.id.fragment_report_spinner);
        buildingList = buildingDBHelper.getAllBuildingSet();
        SpinnerItemAdapter spItemAdapter = new SpinnerItemAdapter(context, buildingList);
        spBuilding.setAdapter(spItemAdapter);

        selectedBuilding = buildingList.get(0).getName();

        spBuilding.setOnItemSelectedListener(reportFragmentListener);
    }

    /**
     * All listeners used in ReportFragment
     */
    private class ReportFragmentListener implements View.OnClickListener, AdapterView.OnItemSelectedListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_report_ib_back:
                    ((MainActivity) getActivity()).setFragment(MainFragment.INBOX);
                    break;
                case R.id.fragment_report_ib_delete:
                    break;
                case R.id.fragment_report_ib_send:
                    if ("".equals(edTitle.getText().toString()) || "".equals(edContent.getText().toString()))
                        return;

                    animUtils.fadeinToVisible(progressBar, animUtils.FAST_ANIM_DURATION);

                    if (null == bmpBuffer) {
                        publishMessage(NO_IMG);
                        return;
                    }

                    tempImgFile = imageUtils.getFileFromBitmap(bmpBuffer);
                    imgurHelper.Execute(imgurHelper.createUpload(tempImgFile, edTitle.getText().toString()), new ReportFragment.UiCallback());
                    break;
                case R.id.fragment_report_fab_add:
                    MainActivity.closeKeyboard(context, edContent.getWindowToken());

                    if (null == bmpBuffer)
                        alertDialogMgr.showCameraDialog(MainFragment.EVENT.toString());
                    else
                        alertDialogMgr.showImageDialog(MainFragment.EVENT.toString(), bmpBuffer);
                    break;
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            selectedBuilding = buildingList.get(position).getName();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

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
                bmpBuffer = (Bitmap) data.getExtras().get("data");
            }

            if (null != bmpBuffer)
                animUtils.fadeinToVisible(tvFab, animUtils.FAST_ANIM_DURATION);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class UiCallback implements Callback<ImageResponse> {

        @Override
        public void success(ImageResponse imageResponse, Response response) {
            tempImgFile = null;
            publishMessage(imageResponse.data.link);
        }

        @Override
        public void failure(RetrofitError error) {
            // Assume we have no connection, since error is null
            if (error != null)
                Log.d(TAG, "ImgurUpload error: " + error.getMessage());
        }
    }

    private void publishMessage(String imgLink) {
        MyAccountInfo myAccountInfo = SharedPrefsUtils.getInstance().getMyAccountInfo();
        TimeUtils tu = TimeUtils.getInstance();
        pubNubHelper.publishMessage(IPowerSaving.getPubNub(), PubNubAPIConstants.ActiveChannels.MESSAGE,
                new Message(tu.getTimeMillies(), selectedBuilding + ": " + edTitle.getText().toString(), edContent.getText().toString(), myAccountInfo.getName(), myAccountInfo.getUid(), tu.getTimeMillies(), context.getString(R.string.label_default) + imgLink).toEncodedString(),
                new PubNubHelper.OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(boolean isSuccessful) {
                        if (!isSuccessful) {
                            alertDialogMgr.showAlertDialog(getContext().getString(R.string.alert_dialog_manager_error), getContext().getString(R.string.alert_dialog_manager_fail_publish));
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(context, getString(R.string.message_sent), Toast.LENGTH_SHORT).show();

                            // Return to inbox fragment
                            ((MainActivity) MainActivity.getContext()).setFragment(MainFragment.INBOX);
                        }
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();

        if (null != bmpBuffer && !bmpBuffer.isRecycled())
            bmpBuffer.recycle();
    }
}