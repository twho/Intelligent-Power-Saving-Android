package com.tsungweiho.intelligentpowersaving.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.SplashActivity;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.EventDBHelper;
import com.tsungweiho.intelligentpowersaving.databinding.FragmentEventBinding;
import com.tsungweiho.intelligentpowersaving.objects.Event;
import com.tsungweiho.intelligentpowersaving.objects.ImageResponse;
import com.tsungweiho.intelligentpowersaving.objects.Upload;
import com.tsungweiho.intelligentpowersaving.tools.AlertDialogManager;
import com.tsungweiho.intelligentpowersaving.tools.UploadService;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtilities;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtilities;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/18.
 */

public class EventFragment extends Fragment implements FragmentTags, PubNubAPIConstants, OnMapReadyCallback {

    private String TAG = "EventFragment";

    // Event Fragment View
    private View view;

    // UIs
    private FrameLayout flTopBarAddEvent;
    private LinearLayout llTopBarAddEvent, llMarkerInfo, llOpen;
    private TextView tvTitle, tvBottom;
    private EditText edEvent;
    private ImageView ivAddIcon, ivMarker;
    private ImageButton ibAdd, ibCancel, ibCamera;
    private static ProgressBar pbMarker, pbTopBar;
    private Button btnFullMap;

    // Functions
    private Context context;
    private EventDBHelper eventDBHelper;
    private ArrayList<Event> eventList;
    private static ImageUtilities imageUtilities;
    private AnimUtilities animUtilities;
    private TimeUtilities timeUtilities;
    private AlertDialogManager alertDialogManager;
    private EventFragmentListener eventFragmentListener;
    private FragmentEventBinding binding;
    private Upload upload;
    private File tempImgFile;
    private Thread uiThread;
    private static Handler handler;
    private static Runnable runnable;

    // Google map
    private Bundle savedInstanceState;
    private GoogleMap googleMap;
    private MapView mapView;
    private LatLngBounds bounds;
    private LatLng clickedLatLng;
    private HashMap<Marker, Event> mapMarkers;
    private boolean ifMarkerViewUp = false;
    private boolean lockLocation = false;

    // Camera
    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_IMAGE = 0;
    private Bitmap bmpBuffer = null;

    // PubNub
    private PubNub pubnub = null;
    public static boolean ifFragmentActive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false);
        view = binding.getRoot();

        this.savedInstanceState = savedInstanceState;
        context = MainActivity.getContext();
        init();

        return view;
    }

    private void init() {
        imageUtilities = MainActivity.getImageUtilities();
        animUtilities = new AnimUtilities(context);
        timeUtilities = new TimeUtilities(context);
        eventFragmentListener = new EventFragmentListener();
        alertDialogManager = new AlertDialogManager(context);
        eventDBHelper = new EventDBHelper(context);
        pubnub = MainActivity.getPubNub();

        findViews();
        setAllListeners();
    }

    private void findViews() {
        flTopBarAddEvent = (FrameLayout) view.findViewById(R.id.fragment_event_top_bar_add_event1);
        llTopBarAddEvent = (LinearLayout) view.findViewById(R.id.fragment_event_top_bar_add_event2);
        llMarkerInfo = (LinearLayout) view.findViewById(R.id.fragment_event_ll_marker_info);
        llOpen = (LinearLayout) view.findViewById(R.id.fragment_event_ll_open);
        ibAdd = (ImageButton) view.findViewById(R.id.fragment_event_ib_add);
        ibCancel = (ImageButton) view.findViewById(R.id.fragment_event_ib_cancel);
        ibCamera = (ImageButton) view.findViewById(R.id.fragment_event_ib_camera);
        tvTitle = (TextView) view.findViewById(R.id.fragment_event_title);
        tvBottom = (TextView) view.findViewById(R.id.fragment_event_bottom);
        edEvent = (EditText) view.findViewById(R.id.fragment_event_ed_event);
        btnFullMap = (Button) view.findViewById(R.id.fragment_event_btn_full_map);
        ivAddIcon = (ImageView) view.findViewById(R.id.fragment_event_add_icon);
        ivMarker = (ImageView) view.findViewById(R.id.fragment_event_iv_marker_img);
        pbMarker = (ProgressBar) view.findViewById(R.id.fragment_event_pb_marker_img);
        pbTopBar = (ProgressBar) view.findViewById(R.id.fragment_event_pb);
    }

    private void setAllListeners() {
        ibAdd.setOnClickListener(eventFragmentListener);
        ibCancel.setOnClickListener(eventFragmentListener);
        ibCamera.setOnClickListener(eventFragmentListener);
        btnFullMap.setOnClickListener(eventFragmentListener);
    }

    private class EventFragmentListener implements View.OnClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_event_ib_add:
                    if (edEvent.getText().toString().length() == 0) {
                        alertDialogManager.showAlertDialog(context.getResources().getString(R.string.alert_dialog_manager_error), context.getResources().getString(R.string.fragment_event_err_no_ed));
                    } else {
                        pbTopBar.animate();
                        pbTopBar.setVisibility(View.VISIBLE);
                        tempImgFile = imageUtilities.getFileFromBitmap(bmpBuffer);
                        createUpload(tempImgFile);
                        new UploadService(context).Execute(upload, new UiCallback());
                    }
                    break;
                case R.id.fragment_event_ib_cancel:
                    dismissAddView();
                    break;
                case R.id.fragment_event_ib_camera:
                    closeKeyboard(context, edEvent.getWindowToken());
                    if (null == bmpBuffer)
                        alertDialogManager.showCameraDialog(EVENT_FRAGMENT);
                    else {
                        alertDialogManager.showImageDialog(EVENT_FRAGMENT, bmpBuffer);
                    }
                    break;
                case R.id.fragment_event_btn_full_map:
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(bounds.getCenter()).zoom(18.0f).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    break;
            }
        }

        @Override
        public void onMapLongClick(LatLng latLng) {
            animUtilities.setflAnimToVisible(flTopBarAddEvent);
            animUtilities.setllAnimToVisible(llTopBarAddEvent);
            tvTitle.setText(context.getResources().getString(R.string.fragment_event_title_add));
            tvBottom.setText(context.getString(R.string.fragment_event_bottom_add));
            clickedLatLng = latLng;
            lockLocation = true;
        }

        @Override
        public void onMapClick(LatLng latLng) {
            if (!lockLocation)
                clickedLatLng = latLng;

            if (ifMarkerViewUp) {
                animUtilities.setllSlideDown(llMarkerInfo);
                ifMarkerViewUp = false;
            }
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            ivMarker.setImageDrawable(null);
            Event event = mapMarkers.get(marker);
            binding.setEvent(event);
            binding.executePendingBindings();
            animUtilities.setllSlideUp(llMarkerInfo);
            ifMarkerViewUp = true;
            return false;
        }
    }

    @BindingAdapter({"bind:image"})
    public static void loadImage(final ImageView imageView, final String url) {
        imageUtilities = new ImageUtilities(MainActivity.getContext());
        imageUtilities.setImageViewFromUrl(url, imageView, pbMarker);

        if (null != handler)
            handler.removeCallbacks(runnable);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                imageUtilities.setImageViewFromUrl(url, imageView, pbMarker);
            }
        };

        handler.postDelayed(runnable, 5000);
    }

    private void dismissAddView() {
        flTopBarAddEvent.setVisibility(View.GONE);
        llTopBarAddEvent.setVisibility(View.GONE);
        tvTitle.setText(context.getString(R.string.fragment_event_title));
        tvBottom.setText(context.getString(R.string.fragment_event_bottom));
        edEvent.setText("");
        ivAddIcon.setVisibility(View.GONE);
        bmpBuffer = null;
        closeKeyboard(context, edEvent.getWindowToken());
        lockLocation = false;
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createUpload(File image) {
        upload = new Upload();
        upload.image = imageUtilities.getCompressedImgFile(image);
        upload.title = edEvent.getText().toString();
        upload.description = edEvent.getText().toString();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Play animation when loading map
        llOpen.setVisibility(View.VISIBLE);
        initMap(savedInstanceState);
        mapView.onResume();
        ifFragmentActive = true;
    }

    private void initMap(Bundle savedInstanceState) {
        mapView = (MapView) view.findViewById(R.id.fragment_event_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Limit the map to NTUST campus
        bounds = new LatLngBounds(new LatLng(25.011353, 121.540963), new LatLng(25.015593, 121.542648));
        final LatLngBounds ADELAIDE = bounds;
        googleMap.setLatLngBoundsForCameraTarget(ADELAIDE);
        googleMap.setMinZoomPreference(18.0f);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(bounds.getCenter()).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // Map initial settings
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Check permission before execute
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            googleMap.setMyLocationEnabled(true);

        // Map listeners
        googleMap.setOnMapLongClickListener(eventFragmentListener);
        googleMap.setOnMapClickListener(eventFragmentListener);

        this.googleMap = googleMap;
        getEventChannelHistory();
        llOpen.setVisibility(View.GONE);
        animUtilities.setMapAnimToVisible(mapView);
    }

    private void setAllMarkers() {
        eventList = eventDBHelper.getAllEventList();
        mapMarkers = new HashMap<>();

        googleMap.clear();

        for (int index = 0; index < eventList.size(); index++) {
            Event event = eventList.get(index);

            double latitude = Double.valueOf(event.getPosition().split(",")[0]);
            double longitude = Double.valueOf(event.getPosition().split(",")[1]);

            // create marker
            MarkerOptions markerOpt = new MarkerOptions().position(new LatLng(latitude, longitude)).title(event.getDetail());

            // Changing marker icon
            markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            Marker marker = this.googleMap.addMarker(markerOpt);
            mapMarkers.put(marker, event);
        }
        // Set all markers listener
        googleMap.setOnMarkerClickListener(eventFragmentListener);
    }

    private void getEventChannelHistory() {
        pubnub.history().channel(EVENT_CHANNEL).count(100)
                .async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(PNHistoryResult result, PNStatus status) {
                        try {
                            if (null != result) {
                                eventDBHelper.deleteAllDB();
                                for (int index = 0; index < result.getMessages().size(); index++) {
                                    JSONObject jObject = new JSONObject(String.valueOf(result.getMessages().get(index).getEntry()));
                                    insertDataToDB(jObject);
                                }
                                setAllMarkers();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void insertDataToDB(JSONObject jObject) {
        Event event = getEventByJSONObj(jObject);
        if (!eventDBHelper.checkIfExist(event.getUniqueId())) {
            eventDBHelper.insertDB(event);
        }
    }

    private Event getEventByJSONObj(JSONObject jsonObject) {
        Event event = null;
        try {
            String uniqueId = jsonObject.getString(EVENT_UNID);
            String detail = jsonObject.getString(EVENT_DETAIL);
            String position = jsonObject.getString(EVENT_POS);
            String image = jsonObject.getString(EVENT_IMG);
            String poster = jsonObject.getString(EVENT_POSTER);
            String time = jsonObject.getString(EVENT_TIME);
            String ifFixed = jsonObject.getString(EVENT_IF_FIXED);
            event = new Event(uniqueId, detail, position, image, poster, time, ifFixed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }

    @Override
    public void onPause() {
        super.onPause();

        ifFragmentActive = false;

        // Clean memory
        eventDBHelper.closeDB();
        mapView.onPause();
    }

    public void setAllMarkerOnUiThread() {
        uiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) MainActivity.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAllMarkers();
                    }
                });
            }
        });
        uiThread.start();
    }

    private class UiCallback implements Callback<ImageResponse> {

        @Override
        public void success(ImageResponse imageResponse, Response response) {
            tempImgFile.delete();
            pubnub.publish().message(new Event(timeUtilities.getTimeMillies(), edEvent.getText().toString(), clickedLatLng.latitude + "," +
                    clickedLatLng.longitude, imageResponse.data.link, getString(R.string.testing_name), timeUtilities.getDate() + "," + timeUtilities.getTimehhmm(), "0"))
                    .channel(EVENT_CHANNEL)
                    .async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            // handle publish result, status always present, result if successful
                            // status.isError to see if error happened
                        }
                    });
            pbTopBar.clearAnimation();
            pbTopBar.setVisibility(View.GONE);
            dismissAddView();
        }

        @Override
        public void failure(RetrofitError error) {
            //Assume we have no connection, since error is null
            if (error == null) {
                Log.d(TAG, "Upload error: " + error.getMessage());
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public static void closeKeyboard(Context context, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }
}
