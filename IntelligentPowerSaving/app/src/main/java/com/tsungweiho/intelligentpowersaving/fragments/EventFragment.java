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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTag;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.EventDBHelper;
import com.tsungweiho.intelligentpowersaving.objects.Event;
import com.tsungweiho.intelligentpowersaving.objects.ImageResponse;
import com.tsungweiho.intelligentpowersaving.objects.Upload;
import com.tsungweiho.intelligentpowersaving.tools.AlertDialogManager;
import com.tsungweiho.intelligentpowersaving.tools.UploadService;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtilities;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/18.
 */

public class EventFragment extends Fragment implements FragmentTag, PubNubAPIConstants {

    private String TAG = "EventFragment";

    // Event Fragment View
    private View view;

    // UIs
    private ImageViewTouch ivMap;
    private RelativeLayout rlMap;
    private FrameLayout flTopBarAddEvent;
    private LinearLayout llTopBarAddEvent;
    private TextView tvTitle, tvBottom;
    private EditText edEvent;
    private ImageView ivAddIcon;
    private ImageButton ibAdd, ibCancel, ibCamera;
    private Button btnFullMap;

    // Functions
    private Context context;
    private EventDBHelper eventDBHelper;
    private RelativeLayout.LayoutParams params;
    private String xPos, yPos;
    private ImageUtilities imageUtilities;
    private AnimUtilities animUtilities;
    private AlertDialogManager alertDialogManager;
    private EventFragmentListener eventFragmentListener;
    private Upload upload;

    // Camera
    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_IMAGE = 0;
    private Bitmap bmpBuffer = null;

    // PubNub
    PubNub pubnub = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_event, container, false);
        context = MainActivity.getContext();
        init();
        return view;
    }

    private void init() {
        imageUtilities = new ImageUtilities(context);
        animUtilities = new AnimUtilities(context);
        eventFragmentListener = new EventFragmentListener();
        alertDialogManager = new AlertDialogManager(context);

        // subscribe to PubNub channels
        pubnub = new PubNub(MainActivity.getPNConfiguration());
        pubnub.subscribe().channels(Arrays.asList(EVENT_CHANNEL, EVENT_CHANNEL_DELETED)).execute();
        pubnub.addListener(eventFragmentListener);

        // find views
        flTopBarAddEvent = (FrameLayout) view.findViewById(R.id.fragment_event_top_bar_add_event1);
        llTopBarAddEvent = (LinearLayout) view.findViewById(R.id.fragment_event_top_bar_add_event2);
        ibAdd = (ImageButton) view.findViewById(R.id.fragment_event_btn_add);
        ibCancel = (ImageButton) view.findViewById(R.id.fragment_event_btn_cancel);
        ibCamera = (ImageButton) view.findViewById(R.id.fragment_event_btn_camera);
        tvTitle = (TextView) view.findViewById(R.id.fragment_event_title);
        tvBottom = (TextView) view.findViewById(R.id.fragment_event_bottom);
        edEvent = (EditText) view.findViewById(R.id.fragment_event_ed_event);
        rlMap = (RelativeLayout) view.findViewById(R.id.fragment_event_relative_layout);
        ivMap = (ImageViewTouch) view.findViewById(R.id.fragment_event_iv_map);
        btnFullMap = (Button) view.findViewById(R.id.fragment_event_btn_full_map);
        ivAddIcon = (ImageView) view.findViewById(R.id.fragment_event_add_icon);

        // init map view
        ivMap.setDisplayType(ImageViewTouchBase.DisplayType.FIT_IF_BIGGER);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.img_campus_map);
        params = new RelativeLayout.LayoutParams(ivMap.getWidth() / 10, ivMap.getWidth() / 10);
        ivMap.setImageBitmap(icon);

        setAllListener();
    }

    private void setAllListener() {
        ivMap.setOnTouchListener(eventFragmentListener);
        ivMap.setOnLongClickListener(eventFragmentListener);
        ibAdd.setOnClickListener(eventFragmentListener);
        ibCancel.setOnClickListener(eventFragmentListener);
        ibCamera.setOnClickListener(eventFragmentListener);
        btnFullMap.setOnClickListener(eventFragmentListener);
    }

    private float[] getRelativeCoordinate(MotionEvent event) {
        float[] values = new float[9];
        ivMap.getDisplayMatrix().getValues(values);

        float[] coord = new float[2];
        coord[0] = (event.getX() - values[2]) / values[0];
        coord[1] = (event.getY() - values[5]) / values[4];
        return coord;
    }

    private class EventFragmentListener extends SubscribeCallback implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_event_btn_add:
                    if (edEvent.getText().toString().length() == 0) {
                        alertDialogManager.showAlertDialog(context.getResources().getString(R.string.alert_dialog_manager_error), context.getResources().getString(R.string.fragment_event_err_no_ed));
                    } else {
                        createUpload(imageUtilities.getFileFromBitmap(bmpBuffer));
                        new UploadService(context).Execute(upload, new UiCallback());
                        pubnub.publish()
                                .message(new Event(Calendar.getInstance().getTimeInMillis() + "", "PubNub Test", "PubNub Test", "PubNub Test", "PubNub Test", "PubNub Test", "PubNub Test"))
                                .channel(EVENT_CHANNEL)
                                .async(new PNCallback<PNPublishResult>() {
                                    @Override
                                    public void onResponse(PNPublishResult result, PNStatus status) {
                                        // handle publish result, status always present, result if successful
                                        // status.isError to see if error happened
                                    }
                                });
                        dismissAddView();
                    }
                    break;
                case R.id.fragment_event_btn_cancel:
                    dismissAddView();
                    break;
                case R.id.fragment_event_btn_camera:
                    if (null == bmpBuffer)
                        alertDialogManager.showCameraDialog(EVENT_FRAGMENT);
                    else {
                        alertDialogManager.showImageDialog(EVENT_FRAGMENT, bmpBuffer);
                    }
                    break;
                case R.id.fragment_event_btn_full_map:
                    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.img_campus_map);
                    ivMap.setImageBitmap(icon);
                    break;
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            xPos = String.valueOf(getRelativeCoordinate(event)[0] / ivMap.getWidth());
            yPos = String.valueOf(getRelativeCoordinate(event)[1] / ivMap.getHeight());
            return false;
        }

        @Override
        public boolean onLongClick(View view) {
            animUtilities.setflAnimToVisible(flTopBarAddEvent);
            animUtilities.setllAnimToVisible(llTopBarAddEvent);
            tvTitle.setText(context.getResources().getString(R.string.fragment_event_title_add));
            tvBottom.setText(context.getString(R.string.fragment_event_bottom_add));
            return false;
        }

        @Override
        public void status(PubNub pubnub, PNStatus status) {
            if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                // TODO connectivity
            }
        }

        @Override
        public void message(PubNub pubnub, PNMessageResult message) {
            Log.d("PubNub Temporary Test", message.toString());
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {

        }
    }

    private void dismissAddView() {
        flTopBarAddEvent.setVisibility(View.GONE);
        llTopBarAddEvent.setVisibility(View.GONE);
        tvTitle.setText(context.getString(R.string.fragment_event_title));
        tvBottom.setText(context.getString(R.string.fragment_event_bottom));
        edEvent.setText("");
        ivAddIcon.setVisibility(View.GONE);
        bmpBuffer = null;
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

            if (null != bmpBuffer)
                animUtilities.setIconAnimToVisible(ivAddIcon);
        } else if (resultCode == Activity.RESULT_CANCELED) {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createUpload(File image) {
        upload = new Upload();

        upload.image = image;
        upload.title = edEvent.getText().toString();
        upload.description = edEvent.getText().toString();
    }

    @Override
    public void onPause() {
        super.onPause();

        // TODO Clean memory
    }

    private class UiCallback implements Callback<ImageResponse> {

        @Override
        public void success(ImageResponse imageResponse, Response response) {
            Log.d(TAG, imageResponse.toString());
            Log.d(TAG, response.toString());
        }

        @Override
        public void failure(RetrofitError error) {
            //Assume we have no connection, since error is null
            if (error == null) {

            }
        }
    }
}
