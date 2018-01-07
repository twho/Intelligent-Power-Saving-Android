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
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.EventDBHelper;
import com.tsungweiho.intelligentpowersaving.databinding.FragmentEventBinding;
import com.tsungweiho.intelligentpowersaving.objects.Event;
import com.tsungweiho.intelligentpowersaving.objects.ImageResponse;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;
import com.tsungweiho.intelligentpowersaving.tools.AlertDialogManager;
import com.tsungweiho.intelligentpowersaving.tools.FirebaseManager;
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
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Fragment for user to report FIXIT event
 * <p>
 * This fragment is the user interface that user can use to report events.
 *
 * @author Tsung Wei Ho
 * @version 0218.2017
 * @since 1.0.0
 */
public class EventFragment extends Fragment implements FragmentTags, BuildingConstants, PubNubAPIConstants, OnMapReadyCallback {

    private final String TAG = "EventFragment";

    // Event Fragment View
    private View view;

    // UIs
    private FrameLayout flTopBarAddEvent;
    private LinearLayout llTopBarAddEvent, llMarkerInfo, llOpen;
    private TextView tvTitle, tvBottom;
    private EditText edEvent;
    private ImageView ivAddIcon, ivMarker;
    private ImageButton ibAdd, ibCancel, ibCamera;
    private Button btnFullMap;
    private static ProgressBar pbMarker, pbTopBar;

    // Functions
    private Context context;
    private EventDBHelper eventDBHelper;
    private static ImageUtils imageUtils;
    private AnimUtils animUtils;
    private TimeUtils timeUtils;
    private AlertDialogManager alertDialogMgr;
    private EventFragmentListener eventFragmentListener;
    private FragmentEventBinding binding;
    private ImgurHelper imgurHelper;
    private File tempImgFile;

    // Google map
    private GoogleMap googleMap;
    private MapView mapView;
    private LatLngBounds bounds;
    private LatLng clickedLatLng;
    private HashMap<Marker, Event> mapMarkers;
    private boolean ifMarkerViewUp = false;
    private boolean lockLocation = false;
    private Float initMapZoom = 17.0f;

    // Camera
    private Bitmap bmpBuffer = null;

    // PubNub
    private PubNubHelper pubNubHelper;
    public static boolean isActive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false);
        view = binding.getRoot();

        context = IPowerSaving.getContext();

        init(savedInstanceState);

        return view;
    }

    /**
     * Initialize objects needed in this fragment
     *
     * @param savedInstanceState the bundled information needed for creating map view
     */
    private void init(Bundle savedInstanceState) {
        eventFragmentListener = new EventFragmentListener();
        eventDBHelper = new EventDBHelper(context);
        imgurHelper = new ImgurHelper(context);

        // Singleton classes
        timeUtils = TimeUtils.getInstance();
        animUtils = AnimUtils.getInstance();
        alertDialogMgr = AlertDialogManager.getInstance();
        imageUtils = ImageUtils.getInstance();
        pubNubHelper = PubNubHelper.getInstance();

        findViews();
        setListeners();

        // Play animation when loading map
        llOpen.setVisibility(View.VISIBLE);
        initMap(savedInstanceState);
    }

    /**
     * Link views in EventFragment, no need to cast views while compile with SDK 26
     */
    private void findViews() {
        flTopBarAddEvent = view.findViewById(R.id.fragment_event_top_bar_add_event1);
        llTopBarAddEvent = view.findViewById(R.id.fragment_event_top_bar_add_event2);
        llMarkerInfo = view.findViewById(R.id.fragment_event_ll_marker_info);
        llOpen = view.findViewById(R.id.fragment_event_ll_open);
        ibAdd = view.findViewById(R.id.fragment_event_ib_add);
        ibCancel = view.findViewById(R.id.fragment_event_ib_cancel);
        ibCamera = view.findViewById(R.id.fragment_event_ib_camera);
        tvTitle = view.findViewById(R.id.fragment_event_title);
        tvBottom = view.findViewById(R.id.fragment_event_bottom);
        edEvent = view.findViewById(R.id.fragment_event_ed_event);
        btnFullMap = view.findViewById(R.id.fragment_event_btn_full_map);
        ivAddIcon = view.findViewById(R.id.fragment_event_add_icon);
        ivMarker = view.findViewById(R.id.fragment_event_iv_marker_img);
        pbMarker = view.findViewById(R.id.fragment_event_pb_marker_img);
        pbTopBar = view.findViewById(R.id.fragment_event_pb);
    }

    /**
     * Set all listeners in EventFragment
     */
    private void setListeners() {
        ibAdd.setOnClickListener(eventFragmentListener);
        ibCancel.setOnClickListener(eventFragmentListener);
        ibCamera.setOnClickListener(eventFragmentListener);
        btnFullMap.setOnClickListener(eventFragmentListener);
    }

    /**
     * All listeners used in EventFragment
     */
    private class EventFragmentListener implements View.OnClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_event_ib_add:
                    if (edEvent.getText().toString().length() == 0) {
                        alertDialogMgr.showAlertDialog(context.getResources().getString(R.string.alert_dialog_manager_error), context.getResources().getString(R.string.fragment_event_err_no_ed));
                    } else {
                        pbTopBar.animate();
                        pbTopBar.setVisibility(View.VISIBLE);

                        if (null == bmpBuffer) {
                            publishEvent(NO_IMG);
                            return;
                        }

                        tempImgFile = imageUtils.getFileFromBitmap(bmpBuffer);
                        imgurHelper.Execute(imgurHelper.createUpload(tempImgFile, edEvent.getText().toString()), new UiCallback());
                    }
                    break;
                case R.id.fragment_event_ib_cancel:
                    dismissAddView();
                    setMarkersOnUiThread();
                    break;
                case R.id.fragment_event_ib_camera:
                    MainActivity.closeKeyboard(context, edEvent.getWindowToken());

                    if (null == bmpBuffer)
                        alertDialogMgr.showCameraDialog(MainFragment.EVENT.toString());
                    else
                        alertDialogMgr.showImageDialog(MainFragment.EVENT.toString(), bmpBuffer);
                    break;
                case R.id.fragment_event_btn_full_map:
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(bounds.getCenter()).zoom(initMapZoom).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    break;
            }
        }

        @Override
        public void onMapLongClick(LatLng latLng) {
            animUtils.fadeInToVisible(flTopBarAddEvent, animUtils.FAST_ANIM_DURATION);
            animUtils.fadeInToVisible(llTopBarAddEvent, animUtils.FAST_ANIM_DURATION);

            tvTitle.setText(context.getResources().getString(R.string.fragment_event_title_add));
            tvBottom.setText(context.getString(R.string.fragment_event_bottom_add));

            clickedLatLng = latLng;
            lockLocation = true;

            // Add a temporary marker
            MarkerOptions markerOpt = new MarkerOptions().position(clickedLatLng);
            markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }

        @Override
        public void onMapClick(LatLng latLng) {
            if (!lockLocation)
                clickedLatLng = latLng;

            if (ifMarkerViewUp) {
                animUtils.slideDown(llMarkerInfo, animUtils.FAST_ANIM_DURATION);
                ifMarkerViewUp = false;
            }
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            ivMarker.setImageDrawable(null);

            // Set data binding
            Event event = mapMarkers.get(marker);
            binding.setEvent(event);
            binding.executePendingBindings();

            animUtils.slideUpToVisible(llMarkerInfo, animUtils.FAST_ANIM_DURATION);
            ifMarkerViewUp = true;

            return false;
        }
    }

    /**
     * Load marker image to marker imageView
     *
     * @param imageView the imageView of clicked map marker
     * @param url       the url resource for loading image
     */
    @BindingAdapter({"bind:image"})
    public static void loadImage(final ImageView imageView, final String url) {
        imageView.invalidate();

        if (NO_IMG.equals(url)) {
            pbMarker.setVisibility(View.GONE);
            imageView.setImageDrawable(IPowerSaving.getContext().getResources().getDrawable(R.mipmap.ic_preload_noimage));
        } else {
            ImageUtils.getInstance().setImageViewFromUrl(url, imageView, pbMarker);
        }
    }

    /**
     * Load event poster image to imageView
     *
     * @param imageView the imageView of event poster
     * @param imgUrl    the url resource for loading image
     */
    @BindingAdapter({"bind:posterImg"})
    public static void loadPosterImg(final ImageView imageView, String imgUrl) {
        imageView.invalidate();
        FirebaseManager.getInstance().downloadProfileImg(imgUrl + "/" + imgUrl, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageUtils.getInstance().setRoundedCornerImageViewFromUrl(uri.toString(), imageView, ImageUtils.getInstance().IMG_CIRCULAR);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setImageDrawable(IPowerSaving.getContext().getResources().getDrawable(R.mipmap.ic_preload_profile));
            }
        });
    }

    /**
     * Set add FIXIT event view invisible and alternate text shown
     */
    private void dismissAddView() {
        flTopBarAddEvent.setVisibility(View.GONE);
        llTopBarAddEvent.setVisibility(View.GONE);
        ivAddIcon.setVisibility(View.GONE);

        tvTitle.setText(context.getString(R.string.fragment_event_title));
        tvBottom.setText(context.getString(R.string.fragment_event_bottom));
        edEvent.setText("");

        bmpBuffer = null;
        lockLocation = false;

        MainActivity.closeKeyboard(context, edEvent.getWindowToken());
    }

    /**
     * Get image data after user selects from resources
     *
     * @param requestCode indicates how resources map to specified resources
     * @param resultCode  indicates which resources did user choose image from
     * @param data        the image data use chose
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
                bmpBuffer = (Bitmap) data.getExtras().get("data");
            }

            if (null != bmpBuffer)
                animUtils.fadeInToVisible(ivAddIcon, animUtils.FAST_ANIM_DURATION);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Initialize Google map view
     *
     * @param savedInstanceState the bundled information needed for creating map view
     */
    private void initMap(Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.fragment_event_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    /**
     * Configure Google map once it finishes loading
     *
     * @param googleMap the Google map to be configured
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Limit the map to University of Michigan campus
        bounds = ntustBounds;
        googleMap.setLatLngBoundsForCameraTarget(bounds);

        googleMap.setMinZoomPreference(initMapZoom);
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

        switchToMainView();
    }

    /**
     * Switch to main event view after 2 seconds
     */
    private void switchToMainView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                llOpen.setVisibility(View.GONE);
                animUtils.fadeInToVisible(mapView, animUtils.FAST_ANIM_DURATION);
            }
        }, 2000);
    }

    /**
     * Get all event data objects in PubNub event channel
     */
    private void getEventChannelHistory() {
        pubNubHelper.getChannelHistory(IPowerSaving.getPubNub(), ActiveChannels.EVENT, new PubNubHelper.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean isSuccessful) {
                if (isSuccessful)
                    setMarkersOnUiThread();
            }
        });
    }

    /**
     * Add all markers on UI thread
     */
    public void setMarkersOnUiThread() {
        final ArrayList<Event> eventList = eventDBHelper.getAllEventList();

        ((MainActivity) MainActivity.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setAllMarkers(eventList);
            }
        });
    }

    /**
     * Add all markers on the map
     */
    private void setAllMarkers(ArrayList<Event> eventList) {
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

    @Override
    public void onResume() {
        super.onResume();

        mapView.onResume();
        isActive = true;
    }

    @Override
    public void onPause() {
        super.onPause();

        isActive = false;

        // Clean memory
        eventDBHelper.closeDB();
        mapView.onPause();

        if (null != bmpBuffer && !bmpBuffer.isRecycled())
            bmpBuffer.recycle();
    }

    /**
     * UI callback after Imgur API call gets response
     */
    private class UiCallback implements Callback<ImageResponse> {

        @Override
        public void success(ImageResponse imageResponse, Response response) {
            tempImgFile = null;
            publishEvent(imageResponse.data.link);
        }

        @Override
        public void failure(RetrofitError error) {
            // Assume we have no connection, since error is null
            if (error != null)
                Log.d(TAG, "ImgurUpload error: " + error.getMessage());
        }
    }

    /**
     * Publish event to PubNub channel
     *
     * @param link the link to image uploaded
     */
    private void publishEvent(String link) {
        MyAccountInfo myAccountInfo = SharedPrefsUtils.getInstance().getMyAccountInfo();
        pubNubHelper.publishMessage(IPowerSaving.getPubNub(), ActiveChannels.EVENT, new Event(timeUtils.getTimeMillies(), edEvent.getText().toString(), clickedLatLng.latitude + "," +
                clickedLatLng.longitude, link, myAccountInfo.getName(), myAccountInfo.getUid(), timeUtils.getDate() + "," + timeUtils.getTimehhmm(), "0"), new PubNubHelper.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean isSuccessful) {
                if (!isSuccessful)
                    alertDialogMgr.showAlertDialog(getContext().getString(R.string.alert_dialog_manager_error), getContext().getString(R.string.alert_dialog_manager_fail_publish));

                pbTopBar.clearAnimation();
                pbTopBar.setVisibility(View.GONE);
                dismissAddView();
            }
        });
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
}
