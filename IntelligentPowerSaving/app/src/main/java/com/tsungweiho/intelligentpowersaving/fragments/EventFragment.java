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
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.tsungweiho.intelligentpowersaving.IntelligentPowerSaving;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.EventDBHelper;
import com.tsungweiho.intelligentpowersaving.databinding.FragmentEventBinding;
import com.tsungweiho.intelligentpowersaving.objects.Event;
import com.tsungweiho.intelligentpowersaving.objects.ImageResponse;
import com.tsungweiho.intelligentpowersaving.objects.ImgurUpload;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;
import com.tsungweiho.intelligentpowersaving.tools.AlertDialogManager;
import com.tsungweiho.intelligentpowersaving.tools.FirebaseManager;
import com.tsungweiho.intelligentpowersaving.tools.JsonParser;
import com.tsungweiho.intelligentpowersaving.tools.UploadService;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtils;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;
import com.tsungweiho.intelligentpowersaving.utils.SharedPrefsUtils;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtils;

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
    private ImgurUpload imgurUpload;
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
    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_IMAGE = 0;
    private Bitmap bmpBuffer = null;

    // PubNub
    private PubNub pubnub = null;
    public static boolean isActive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false);
        view = binding.getRoot();

        context = IntelligentPowerSaving.getContext();

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
        pubnub = MainActivity.getPubNub();

        // Singleton classes
        timeUtils = TimeUtils.getInstance();
        animUtils = AnimUtils.getInstance();
        alertDialogMgr = AlertDialogManager.getInstance();
        imageUtils = ImageUtils.getInstance();

        findViews();
        setAllListeners();

        // Play animation when loading map
        llOpen.setVisibility(View.VISIBLE);
        initMap(savedInstanceState);
    }

    // Compile with SDK 26, no need to cast views
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

    private void setAllListeners() {
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

                        tempImgFile = imageUtils.getFileFromBitmap(bmpBuffer);
                        createUpload(tempImgFile);
                        new UploadService(context).Execute(imgurUpload, new UiCallback());
                    }
                    break;
                case R.id.fragment_event_ib_cancel:
                    dismissAddView();
                    setAllMarkers();
                    break;
                case R.id.fragment_event_ib_camera:
                    closeKeyboard(context, edEvent.getWindowToken());

                    if (null == bmpBuffer)
                        alertDialogMgr.showCameraDialog(EVENT_FRAGMENT);
                    else
                        alertDialogMgr.showImageDialog(EVENT_FRAGMENT, bmpBuffer);
                    break;
                case R.id.fragment_event_btn_full_map:
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(bounds.getCenter()).zoom(initMapZoom).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    break;
            }
        }

        @Override
        public void onMapLongClick(LatLng latLng) {
            animUtils.fadeinToVisible(flTopBarAddEvent, animUtils.FAST_ANIM_DURATION);
            animUtils.fadeinToVisible(llTopBarAddEvent, animUtils.FAST_ANIM_DURATION);

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
     * @param imageView the imageView of clicked marker
     * @param url       the url resource to load image from
     */
    @BindingAdapter({"bind:image"})
    public static void loadImage(final ImageView imageView, final String url) {
        imageView.invalidate();
        ImageUtils.getInstance().setImageViewFromUrl(url, imageView, pbMarker);
    }

    @BindingAdapter({"bind:poster", "bind:posterImg"})
    public static void loadPosterImg(final ImageView imageView, final String poster, String imgUrl) {
        imageView.invalidate();
        FirebaseManager.getInstance().downloadProfileImg(poster + "/" + imgUrl, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageUtils.getInstance().setRoundedCornerImageViewFromUrl(uri.toString(), imageView, ImageUtils.getInstance().IMG_TYPE_PROFILE);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setImageDrawable(IntelligentPowerSaving.getContext().getResources().getDrawable(R.mipmap.ic_preload_profile));
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

        closeKeyboard(context, edEvent.getWindowToken());
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
                animUtils.fadeinToVisible(ivAddIcon, animUtils.FAST_ANIM_DURATION);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Create Imgur upload task
     *
     * @param image the image to be uploaded to Imgur
     */
    private void createUpload(File image) {
        try {
            imgurUpload = new ImgurUpload(imageUtils.getCompressedImgFile(image), edEvent.getText().toString(), edEvent.getText().toString(), "");
        } catch (IOException e) {
            e.printStackTrace();
            alertDialogMgr.showAlertDialog(context.getResources().getString(R.string.alert_dialog_manager_error), context.getResources().getString(R.string.notification_fail));
        }
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
                animUtils.fadeinToVisible(mapView, animUtils.FAST_ANIM_DURATION);
            }
        }, 2000);
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
                                    Event event = JsonParser.getInstance().getEventByJSONObj(new JSONObject(String.valueOf(result.getMessages().get(index).getEntry())));

                                    if (!eventDBHelper.isExist(event.getUniqueId()))
                                        eventDBHelper.insertDB(event);
                                }
                                setMarkersOnUiThread();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * Add all markers on UI thread
     */
    public void setMarkersOnUiThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAllMarkers();
                    }
                });
            }
        }).start();
    }

    /**
     * Add all markers on the map
     */
    private void setAllMarkers() {
        ArrayList<Event> eventList = eventDBHelper.getAllEventList();
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
    }

    private class UiCallback implements Callback<ImageResponse> {

        @Override
        public void success(ImageResponse imageResponse, Response response) {
            tempImgFile.delete();

            MyAccountInfo myAccountInfo = SharedPrefsUtils.getInstance().getMyAccountInfo();

            pubnub.publish().message(new Event(timeUtils.getTimeMillies(), edEvent.getText().toString(), clickedLatLng.latitude + "," +
                    clickedLatLng.longitude, imageResponse.data.link, myAccountInfo.getName(), myAccountInfo.getUid(), timeUtils.getDate() + "," + timeUtils.getTimehhmm(), "0"))
                    .channel(EVENT_CHANNEL)
                    .async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            // TODO handle publish result, status always present, result if successful
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
            if (error != null) {
                Log.d(TAG, "ImgurUpload error: " + error.getMessage());
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
