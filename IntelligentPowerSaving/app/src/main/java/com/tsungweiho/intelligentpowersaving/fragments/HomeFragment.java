package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.databases.BuildingDBHelper;
import com.tsungweiho.intelligentpowersaving.objects.Building;
import com.tsungweiho.intelligentpowersaving.objects.BuildingIcon;
import com.tsungweiho.intelligentpowersaving.tools.FirebaseManager;
import com.tsungweiho.intelligentpowersaving.utils.SharedPrefsUtils;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Fragment for user to view overview of building energy consumption
 *
 * This fragment is the user interface that user can view building energy consumption information.
 *
 * @author Tsung Wei Ho
 * @version 0218.2017
 * @since 1.0.0
 */
public class HomeFragment extends Fragment implements DBConstants, BuildingConstants {

    private final String TAG = "HomeFragment";

    // Home Fragment View
    private View view;

    // UI Views;
    private GridLayout gridLayout;
    private LinearLayout llProgress;
    private ImageButton ibFollowing;
    private TextView tvNoBuilding;

    // Functions
    private Context context;
    private AnimUtils animUtils;
    private BuildingDBHelper buildingDBHelper;
    private ArrayList<Building> buildingList;
    private boolean ifShowFollow = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = IPowerSaving.getContext();

        init();

        return view;
    }

    private void init() {
        animUtils = AnimUtils.getInstance();
        buildingDBHelper = new BuildingDBHelper(context);

        // find views
        // Compile with SDK 26, no need to cast views
        gridLayout = view.findViewById(R.id.fragment_home_grid_layout);
        llProgress = view.findViewById(R.id.fragment_home_progress_layout);
        tvNoBuilding = view.findViewById(R.id.fragmnet_home_tv_no_building);
        animUtils.fadeinToVisible(llProgress, animUtils.FAST_ANIM_DURATION);

        ibFollowing = view.findViewById(R.id.fragment_home_ib_following);
        ibFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ifShowFollow = !ifShowFollow;
                setupFollowingBuildings();
            }
        });

        ImageButton ibRefresh = view.findViewById(R.id.fragment_home_ib_refresh);
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataFromFDB();
            }
        });

        // Configure for small screen with width under 1080dp
        if (MainActivity.screenWidth < 600)
            gridLayout.setColumnCount(2);
    }

    private void setupFollowingBuildings() {
        ArrayList<Building> buildingList = ifShowFollow ? buildingDBHelper.getFollowedBuildingSet() : buildingDBHelper.getAllBuildingSet();
        ibFollowing.setImageDrawable(context.getResources().getDrawable(ifShowFollow ? R.mipmap.ic_label_highlight : R.mipmap.ic_label_unhighlight));

        refreshBuildingIcons(buildingList);

        tvNoBuilding.setVisibility(buildingList.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        loadDataFromFDB();

        // Read current status
        ifShowFollow = SharedPrefsUtils.getInstance().getIfShowFollowedBuilding();
        setupFollowingBuildings();
    }

    /**
     * Parse JSON downloaded from Firebase database
     */
    public void loadDataFromFDB() {
        if (null == buildingDBHelper)
            buildingDBHelper = new BuildingDBHelper(context);

        buildingList = new ArrayList<>(); // Instantiate building arrayList

        FirebaseManager.getInstance().loadBuildings(new ValueEventListener() {
            Building building = null;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot buildingSnapshot : dataSnapshot.getChildren()) {
                    building = FirebaseManager.getInstance().loadBuildingBySnapshot(buildingSnapshot);

                    if (!buildingDBHelper.isExist(building.getName())) {
                        buildingDBHelper.insertDB(building);
                    } else {
                        building.setIfFollow(buildingDBHelper.getBuildingByName(building.getName()).getIfFollow());
                        buildingDBHelper.updateDB(building);
                    }
                }

                buildingList = buildingDBHelper.getAllBuildingSet();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

        // If Firebase is inaccessible, read local file
        if (buildingList.size() == 0)
            addLocalDataToDatabase();

        llProgress.setVisibility(View.GONE);
        setupFollowingBuildings();
        animUtils.fadeinToVisible(gridLayout, animUtils.MID_ANIM_DURATION);
    }

    private void refreshBuildingIcons(ArrayList<Building> buildingList) {
        Building building;
        gridLayout.removeAllViews();

        for (int i = 0; i < buildingList.size(); i++) {
            building = buildingList.get(i);
            BuildingIcon buildingIcon = new BuildingIcon(context, building);

            final Building finalBuilding = building;
            buildingIcon.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).setBuildingFragment(finalBuilding);
                }
            });
            gridLayout.addView(buildingIcon.getView());
        }
    }

    /**
     * Load data from local storage if network is bad
     */
    private void addLocalDataToDatabase() {
        JSONObject obj;
        try {
            obj = new JSONObject(loadJSONFromAsset());
            JSONArray jArr = obj.getJSONArray(JSON_ARRAY_NAME);

            for (int i = 0; i < jArr.length(); i++) {
                JSONObject currentObj = jArr.getJSONObject(i);
                String name = currentObj.getString(FDB_NAME);

                // if already have in local, don't override the data
                if (!buildingDBHelper.isExist(name)) {
                    String detail = currentObj.getString(FDB_DETAIL);
                    String efficiency = currentObj.getString(FDB_EFFICIENCY);
                    String consumption = currentObj.getString(FDB_CONSUMPTION);
                    String imgUrl = currentObj.getString(FDB_IMGURL);
                    buildingDBHelper.insertDB(new Building(name, detail, efficiency, consumption, imgUrl, BUILDING_NOT_FOLLOW));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAsset() {
        String json = null;
        InputStream is;
        try {
            is = context.getAssets().open(LOCAL_BUILDING_JSON);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return json;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save current status
        SharedPrefsUtils.getInstance().saveIfShowFollowedBuilding(ifShowFollow);
    }
}
