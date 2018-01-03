package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.databases.BuildingDBHelper;
import com.tsungweiho.intelligentpowersaving.databinding.FragmentBuildingBinding;
import com.tsungweiho.intelligentpowersaving.objects.Building;
import com.tsungweiho.intelligentpowersaving.utils.ChartUtils;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtils;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Fragment for showing building energy consumption information
 *
 * This fragment is the user interface that display real-time energy consumption with charts
 *
 * @author Tsung Wei Ho
 * @version 1222.2017
 * @since 1.0.0
 */
public class BuildingFragment extends Fragment implements FragmentTags, BuildingConstants {
    private final String TAG = "BuildingFragment";

    // Building Fragment View
    private View view;

    // UI views
    private LineChart lineChart;
    private ImageView ivFollowIndicator;
    private Button btnFollow; // Button icon: crop, no trim with padding 15%

    // Functions
    private Context context;
    private String buildingName;
    private AnimUtils animUtils;
    private ChartUtils chartUtils;
    private BuildingDBHelper buildingDBHelper;
    private Building building;
    private FragmentBuildingBinding binding;
    private static ArrayList<String> consumptionList;
    private static int currentHour;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_building, container, false);
        view = binding.getRoot();

        context = IPowerSaving.getContext();

        this.buildingName = this.getArguments().getString(BUILDING_FRAGMENT_KEY);

        init();

        return view;
    }

    /**
     * Init all classes needed in this fragment
     */
    private void init() {
        buildingDBHelper = new BuildingDBHelper(context);

        // Singleton classes
        chartUtils = ChartUtils.getInstance();
        animUtils = AnimUtils.getInstance();

        building = buildingDBHelper.getBuildingByName(buildingName);
        binding.setBuilding(building);

        // Compile with SDK 26, no need to cast views
        ivFollowIndicator = view.findViewById(R.id.fragment_building_iv_follow);

        btnFollow = view.findViewById(R.id.fragment_building_ib_follow);
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                building.setIfFollow(Boolean.parseBoolean(building.getIfFollow()) ? BUILDING_NOT_FOLLOW : BUILDING_FOLLOW);
                setFollowButton(Boolean.parseBoolean(building.getIfFollow()));
            }
        });
        setFollowButton(Boolean.parseBoolean(building.getIfFollow()));

        // Draw chart
        lineChart = view.findViewById(R.id.fragment_building_chart);

        ImageButton ibBack = view.findViewById(R.id.fragment_building_ib_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setFragment(MainFragment.HOME);
            }
        });

        currentHour = Integer.parseInt(TimeUtils.getInstance().getTimeHH());
        consumptionList = new ArrayList<>(Arrays.asList(new String[TIME_HOURS.length]));
        currentHour += currentHour == 0 ? 24 : 0;
        Collections.fill(consumptionList, "0");
    }

    /**
     * Setup follow button
     *
     * @param isFollow the boolean indicates if the building is followed by user
     */
    private void setFollowButton(Boolean isFollow) {
        if (isFollow) {
            animUtils.fadeinToVisible(ivFollowIndicator, animUtils.FAST_ANIM_DURATION);
        } else {
            ivFollowIndicator.setVisibility(View.GONE);
        }

        btnFollow.setCompoundDrawablesWithIntrinsicBounds(isFollow ? R.mipmap.ic_label_unhighlight : R.mipmap.ic_label_highlight, 0, 0, 0);
        btnFollow.setText(getString(isFollow ? R.string.unfollow_this : R.string.follow_this));
    }

    /**
     * Load building image into imageView
     *
     * @param imageView the imageView to set image resource to
     * @param url       the image resource url
     */
    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(final ImageView imageView, final String url) {
        final ImageUtils imageUtils = ImageUtils.getInstance();
        imageUtils.setRoundedCornerImageViewFromUrl(url, imageView, imageUtils.IMG_ROUNDED_CORNER);

        // Auto refresh after 5 seconds.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageUtils.setRoundedCornerImageViewFromUrl(url, imageView, imageUtils.IMG_ROUNDED_CORNER);
            }
        }, 5000);
    }

    /**
     * Set building basic information
     *
     * @param textView the textView to display building basic information
     * @param detail   the information details of the building
     */
    @BindingAdapter({"bind:detail"})
    public static void loadDetail(TextView textView, String detail) {
        textView.setText(IPowerSaving.getContext().getString(R.string.use_dept) + detail);
    }

    /**
     * Set building energy consumption efficiency
     *
     * @param textView   the textView to display energy consumption efficiency
     * @param efficiency the information of energy consumption efficiency
     */
    @BindingAdapter({"bind:efficiency"})
    public static void loadTitle(TextView textView, String efficiency) {
        String energyInfo = Math.abs(Integer.valueOf(efficiency.split(SEPARATOR_CONSUMPTION)[0])) + IPowerSaving.getContext().getResources().getString(R.string.energy_eff_unit) +
                (Integer.valueOf(efficiency.split(",")[0]) > 0 ?
                        IPowerSaving.getContext().getString(R.string.increase_weekly) : IPowerSaving.getContext().getString(R.string.decrease_weekly));

        // Get the data from yesterday
        int lastHour = currentHour - 2;
        lastHour += lastHour < 0 ? 24 : 0;

        StringBuilder strBuilder = new StringBuilder(IPowerSaving.getContext().getString(R.string.consump_this_hour));
        strBuilder.append(consumptionList.get(currentHour - 1));
        strBuilder.append(BUILDING_UNIT);
        strBuilder.append(IPowerSaving.getContext().getString(R.string.consump_last_hour));
        strBuilder.append(consumptionList.get(lastHour));
        strBuilder.append(BUILDING_UNIT);
        strBuilder.append(energyInfo);

        textView.setText(strBuilder);
    }

    @Override
    public void onResume() {
        super.onResume();

        setChartData();
    }

    /**
     * Setup chart with energy consumption data of the building
     */
    private void setChartData() {
        // Dummy data: The first two data are building's energy efficiency, not hourly power consumption
        for (int x = 0; x < currentHour; x++) {
            consumptionList.set(x, building.getConsumption().split(SEPARATOR_CONSUMPTION)[x]);
        }

        chartUtils.setupLineChart(lineChart, consumptionList); // Setup chart and its data
    }

    @Override
    public void onPause() {
        super.onPause();

        buildingDBHelper.updateDB(building);
    }
}