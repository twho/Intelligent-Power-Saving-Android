package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;

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
import com.tsungweiho.intelligentpowersaving.utils.AnimUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/18.
 */

public class HomeFragment extends Fragment implements DBConstants {

    private String TAG = "HomeFragment";

    // Home Fragment View
    private View view;

    // UI Views;
    private GridLayout gridLayout;
    private LinearLayout llProgress;

    // Functions
    private Context context;
    private AnimUtilities animUtilities;
    private BuildingDBHelper buildingDBHelper;
    private ArrayList<Building> buildingList;

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
        gridLayout = (GridLayout) view.findViewById(R.id.fragment_home_grid_layout);
        llProgress = (LinearLayout) view.findViewById(R.id.fragment_home_progress_layout);
        animUtilities.setllAnimToVisible(llProgress);
    }

    @Override
    public void onResume() {
        super.onResume();

        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        if (null == buildingDBHelper)
            buildingDBHelper = new BuildingDBHelper(context);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(BUILDING_DB);
        databaseReference.addValueEventListener(new ValueEventListener() {
            Building building = null;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot buildingSnapshot : dataSnapshot.getChildren()) {
                    String name = buildingSnapshot.child(FDB_NAME).getValue().toString();
                    String detail = buildingSnapshot.child(FDB_DETAIL).getValue().toString();
                    String consumption = buildingSnapshot.child(FDB_CONSUMPTION).getValue().toString();
                    String imgUrl = buildingSnapshot.child(FDB_IMGURL).getValue().toString();
                    building = new Building(name, detail, consumption, imgUrl);
                    if (!buildingDBHelper.checkIfExist(name))
                        buildingDBHelper.insertDB(building);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

        buildingList = buildingDBHelper.getAllBuildingList();

        // Read local file
        if (buildingList.size() == 0)
            addBuildingToDatabase();

        Building building;
        llProgress.setVisibility(View.GONE);
        gridLayout.removeAllViews();
        for (int index = 0; index < buildingList.size(); index++) {
            building = buildingList.get(index);
            BuildingIcon buildingIcon = new BuildingIcon(context, building);
            gridLayout.addView(buildingIcon.getView());
        }
        animUtilities.setglAnimToVisible(gridLayout);
    }

    private String loadJSONFromAsset() {
        String json = null;
        InputStream is = null;
        try {
            is = context.getAssets().open(LOCAL_BUILDING_JSON);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void addBuildingToDatabase() {
        JSONObject obj = null;
        try {
            obj = new JSONObject(loadJSONFromAsset());
            JSONArray jArry = obj.getJSONArray(JSON_ARRAY_NAME);

            Building building = null;
            for (int i = 0; i < jArry.length(); i++) {
                JSONObject currentObj = jArry.getJSONObject(i);

                String name = currentObj.getString(FDB_NAME);
                String detail = currentObj.getString(FDB_DETAIL);
                String consumption = currentObj.getString(FDB_CONSUMPTION);
                String imgUrl = currentObj.getString(FDB_IMGURL);
                building = new Building(name, detail, consumption, imgUrl);
                if (!buildingDBHelper.checkIfExist(name))
                    buildingDBHelper.insertDB(building);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
