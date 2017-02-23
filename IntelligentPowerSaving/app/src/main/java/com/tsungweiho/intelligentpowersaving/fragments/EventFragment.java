package com.tsungweiho.intelligentpowersaving.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.databinding.tool.writer.LayoutBinderWriter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/18.
 */

public class EventFragment extends Fragment implements FragmentTag, PubNubAPIConstants, OnMapReadyCallback {

    private String TAG = "EventFragment";

    // Event Fragment View
    private View view;

    // UIs
    private RelativeLayout rlMap;
    private FrameLayout flTopBarAddEvent;
    private LinearLayout llTopBarAddEvent;
    private TextView tvTitle, tvBottom;
    private EditText edEvent;
    private ImageView ivAddIcon;
    private ImageButton ibAdd, ibCancel, ibCamera;
    private Button btnFullMap;
    private MapView mapView;

    // Functions
    private Context context;
    private EventDBHelper eventDBHelper;
    private ArrayList<Event> eventList;
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

        init(savedInstanceState);

        return view;
    }

    private void init(Bundle savedInstanceState) {
        imageUtilities = new ImageUtilities(context);
        animUtilities = new AnimUtilities(context);
        eventFragmentListener = new EventFragmentListener();
        alertDialogManager = new AlertDialogManager(context);
        eventDBHelper = new EventDBHelper(context);

        // subscribe to PubNub channels
        pubnub = new PubNub(MainActivity.getPNConfiguration());
        pubnub.subscribe().channels(Arrays.asList(EVENT_CHANNEL, EVENT_CHANNEL_DELETED)).execute();
        pubnub.addListener(eventFragmentListener);

        initMap(savedInstanceState);

        // find views
        flTopBarAddEvent = (FrameLayout) view.findViewById(R.id.fragment_event_top_bar_add_event1);
        llTopBarAddEvent = (LinearLayout) view.findViewById(R.id.fragment_event_top_bar_add_event2);
        ibAdd = (ImageButton) view.findViewById(R.id.fragment_event_btn_add);
        ibCancel = (ImageButton) view.findViewById(R.id.fragment_event_btn_cancel);
        ibCamera = (ImageButton) view.findViewById(R.id.fragment_event_btn_camera);
        tvTitle = (TextView) view.findViewById(R.id.fragment_event_title);
        tvBottom = (TextView) view.findViewById(R.id.fragment_event_bottom);
        edEvent = (EditText) view.findViewById(R.id.fragment_event_ed_event);
        btnFullMap = (Button) view.findViewById(R.id.fragment_event_btn_full_map);
        ivAddIcon = (ImageView) view.findViewById(R.id.fragment_event_add_icon);

        setAllListener();
    }

    private void initMap(Bundle savedInstanceState) {
        mapView = (MapView) view.findViewById(R.id.fragment_event_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMapLongClickListener(eventFragmentListener);

        // latitude and longitude
        double latitude = 17.385044;
        double longitude = 78.486671;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Hello Maps");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(17.385044, 78.486671)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void setAllListener() {
        ibAdd.setOnClickListener(eventFragmentListener);
        ibCancel.setOnClickListener(eventFragmentListener);
        ibCamera.setOnClickListener(eventFragmentListener);
        btnFullMap.setOnClickListener(eventFragmentListener);
    }

    private class EventFragmentListener extends SubscribeCallback implements View.OnClickListener, GoogleMap.OnMapLongClickListener {

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
                                .message(new Event(Calendar.getInstance().getTimeInMillis() + "", "PubNub Test", "0.5,0.5", "PubNub Test", "PubNub Test", "PubNub Test", "PubNub Test"))
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
                    break;
            }
        }

        @Override
        public void onMapLongClick(LatLng latLng) {
            animUtilities.setflAnimToVisible(flTopBarAddEvent);
            animUtilities.setllAnimToVisible(llTopBarAddEvent);
            tvTitle.setText(context.getResources().getString(R.string.fragment_event_title_add));
            tvBottom.setText(context.getString(R.string.fragment_event_bottom_add));
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
            try {
                JSONObject jObject = new JSONObject(message.getMessage().toString());
                String uniqueId = jObject.getString(EVENT_UNID);
                String detail = jObject.getString(EVENT_DETAIL);
                String position = jObject.getString(EVENT_POS);
                String image = jObject.getString(EVENT_IMG);
                String posterImg = jObject.getString(EVENT_POSTER_IMG);
                String time = jObject.getString(EVENT_TIME);
                String ifFixed = jObject.getString(EVENT_IF_FIXED);
                Event event = new Event(uniqueId, detail, position, image, posterImg, time, ifFixed);

                if (!eventDBHelper.checkIfExist(event.getUniqueId())) {
                    eventDBHelper.insertDB(event);
                }
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
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
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        // TODO Clean memory
        eventDBHelper.closeDB();
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

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
