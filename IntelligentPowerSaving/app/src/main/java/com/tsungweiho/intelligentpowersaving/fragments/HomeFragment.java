package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.databases.BuildingDBHelper;
import com.tsungweiho.intelligentpowersaving.objects.Building;
import com.tsungweiho.intelligentpowersaving.objects.BuildingIcon;
import com.tsungweiho.intelligentpowersaving.tools.SharedPreferencesManager;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/18.
 */

public class HomeFragment extends Fragment implements DBConstants, BuildingConstants {

    private String TAG = "HomeFragment";

    // Home Fragment View
    private View view;

    // UI Views;
    private GridLayout gridLayout;
    private LinearLayout llProgress;
    private ImageButton ibRefresh, ibFollowing;
    private TextView tvNoBuilding;

    // Functions
    private Context context;
    private AnimUtilities animUtilities;
    private BuildingDBHelper buildingDBHelper;
    private SharedPreferencesManager sharedPreferencesManager;
    private boolean ifShowFollow = false;

    // Firebase
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = MainActivity.getContext();
        init();
        return view;
    }

    private void init() {
        animUtilities = new AnimUtilities(context);
        buildingDBHelper = new BuildingDBHelper(context);

        // find views
        gridLayout = (GridLayout) view.findViewById(R.id.fragment_home_grid_layout);
        llProgress = (LinearLayout) view.findViewById(R.id.fragment_home_progress_layout);
        tvNoBuilding = (TextView) view.findViewById(R.id.fragmnet_home_tv_no_building);
        animUtilities.setllAnimToVisible(llProgress);

        ibFollowing = (ImageButton) view.findViewById(R.id.fragment_home_ib_following);
        ibFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ifShowFollow) {
                    ifShowFollow = false;
                } else {
                    ifShowFollow = true;
                }
                setupFollowingBuildings();
            }
        });

        ibRefresh = (ImageButton) view.findViewById(R.id.fragment_home_ib_refresh);
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataFromFirebase();
            }
        });

        // Configure for small screen with width under 1080dp
        if (MainActivity.screenWidth < 600)
            gridLayout.setColumnCount(2);
    }

    private void setupFollowingBuildings() {
        ArrayList<Building> buildingList;
        if (ifShowFollow) {
            buildingList = buildingDBHelper.getFollowedBuildingList();
            ibFollowing.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_label_highlight));
        } else {
            buildingList = buildingDBHelper.getAllBuildingList();
            ibFollowing.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_label_unhighlight));
        }
        refreshBuildingIcons(buildingList);

        if (buildingList.size() == 0) {
            tvNoBuilding.setVisibility(View.VISIBLE);
        } else {
            tvNoBuilding.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        loadDataFromFirebase();

        // Read current status
        sharedPreferencesManager = new SharedPreferencesManager(context);
        ifShowFollow = sharedPreferencesManager.getIfShowFollowedBuilding();
        setupFollowingBuildings();
    }

    public void loadDataFromFirebase() {
        if (null == buildingDBHelper)
            buildingDBHelper = new BuildingDBHelper(context);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(BUILDING_DB);
        databaseReference.addValueEventListener(new ValueEventListener() {
            Building building = null;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot buildingSnapshot : dataSnapshot.getChildren()) {
                    String name = buildingSnapshot.child(FDB_NAME).getValue().toString();
                    String consumption = buildingSnapshot.child(FDB_CONSUMPTION).getValue().toString();

                    if (!buildingDBHelper.checkIfExist(name)) {
                        String detail = buildingSnapshot.child(FDB_DETAIL).getValue().toString();
                        String imgUrl = buildingSnapshot.child(FDB_IMGURL).getValue().toString();
                        building = new Building(name, detail, consumption, imgUrl, BUILDING_NOT_FOLLOW);
                        buildingDBHelper.insertDB(building);
                    } else {
                        building = buildingDBHelper.getBuildingByName(name);
                        building.setConsumption(consumption);
                        buildingDBHelper.updateDB(building);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

        ArrayList<Building> buildingList = buildingDBHelper.getAllBuildingList();

        // If Firebase is inaccessible, read local file
        if (buildingList.size() == 0)
            addLocalDataToDatabase();

        llProgress.setVisibility(View.GONE);
        setupFollowingBuildings();
        animUtilities.setglAnimToVisible(gridLayout);
    }

    private void refreshBuildingIcons(ArrayList<Building> buildingList) {
        Building building;
        gridLayout.removeAllViews();
        for (int index = 0; index < buildingList.size(); index++) {
            building = buildingList.get(index);
            BuildingIcon buildingIcon = new BuildingIcon(context, building);
            final Building finalBuilding = building;
            buildingIcon.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) MainActivity.getContext()).setBuildingFragment(finalBuilding);
                }
            });
            gridLayout.addView(buildingIcon.getView());
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

    private void addLocalDataToDatabase() {
        JSONObject obj;
        try {
            obj = new JSONObject(loadJSONFromAsset());
            JSONArray jArry = obj.getJSONArray(JSON_ARRAY_NAME);

            Building building;
            for (int i = 0; i < jArry.length(); i++) {
                JSONObject currentObj = jArry.getJSONObject(i);
                String name = currentObj.getString(FDB_NAME);

                // if already have in local, don't override the data
                if (!buildingDBHelper.checkIfExist(name)) {
                    String detail = currentObj.getString(FDB_DETAIL);
                    String consumption = currentObj.getString(FDB_CONSUMPTION);
                    String imgUrl = currentObj.getString(FDB_IMGURL);
                    building = new Building(name, detail, consumption, imgUrl, BUILDING_NOT_FOLLOW);
                    buildingDBHelper.insertDB(building);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save current status
        sharedPreferencesManager.saveIfShowFollowedBuilding(ifShowFollow);
    }
}
