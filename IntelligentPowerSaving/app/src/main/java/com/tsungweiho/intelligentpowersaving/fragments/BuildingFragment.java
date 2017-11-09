package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.databases.BuildingDBHelper;
import com.tsungweiho.intelligentpowersaving.databinding.FragmentBuildingBinding;
import com.tsungweiho.intelligentpowersaving.objects.Building;
import com.tsungweiho.intelligentpowersaving.utils.AChartUtils;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtils;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Tsung Wei Ho on 2017/2/18.
 * Updated by Tsung Wei Ho on 2017/11/9
 */

public class BuildingFragment extends Fragment implements FragmentTags, BuildingConstants {
    private final String TAG = "BuildingFragment";

    // Settings Fragment View
    private View view;

    // UI views
    private LinearLayout llChart;
    private TextView tvIbFollow;
    private ImageView ivIbFollow, ivFollowIndicator;

    // Functions
    private Context context;
    private String buildingName;
    private AnimUtils animUtils;
    private AChartUtils aChartUtils;
    private BuildingDBHelper buildingDBHelper;
    private Building building;
    private FragmentBuildingBinding binding;
    private static ArrayList<String> consumptionList;
    private static int currentHour;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_building, container, false);
        view = binding.getRoot();

        context = MainActivity.getContext();
        this.buildingName = this.getArguments().getString(BUILDING_FRAGMENT_KEY);
        init();
        return view;
    }

    private void init() {
        buildingDBHelper = new BuildingDBHelper(context);

        // singleton classes
        aChartUtils = AChartUtils.getInstance();
        animUtils = AnimUtils.getInstance();

        building = buildingDBHelper.getBuildingByName(buildingName);
        binding.setBuilding(building);

        ivFollowIndicator = (ImageView) view.findViewById(R.id.fragment_building_iv_follow);
        ivIbFollow = (ImageView) view.findViewById(R.id.fragment_building_iv_ib_follow);
        tvIbFollow = (TextView) view.findViewById(R.id.fragment_building_tv_ib_follow);

        LinearLayout ibFollow;
        ibFollow = (LinearLayout) view.findViewById(R.id.fragment_building_ib_follow);
        ibFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Boolean.parseBoolean(building.getIfFollow())) {
                    building.setIfFollow(BUILDING_NOT_FOLLOW);
                } else {
                    building.setIfFollow(BUILDING_FOLLOW);
                }
                setFollowButton(Boolean.parseBoolean(building.getIfFollow()));
            }
        });
        setFollowButton(Boolean.parseBoolean(building.getIfFollow()));

        llChart = (LinearLayout) view.findViewById(R.id.fragment_building_ll_chart);

        ImageButton ibBack;
        ibBack = (ImageButton) view.findViewById(R.id.fragment_building_ib_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) MainActivity.getContext()).setFragment(HOME_FRAGMENT);
            }
        });

        currentHour = Integer.parseInt(TimeUtils.getInstance().getTimeHH());
        consumptionList = new ArrayList<>(Arrays.asList(new String[TIME_HOURS.length]));
        currentHour += currentHour == 0 ? 24 : 0;

        Collections.fill(consumptionList, "0");
    }

    private void setFollowButton(Boolean ifFollow) {
        if (ifFollow) {
            animUtils.setIconAnimToVisible(ivFollowIndicator);
            ivIbFollow.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_label_unhighlight));
            tvIbFollow.setText(getString(R.string.unfollow_this));
        } else {
            ivFollowIndicator.setVisibility(View.GONE);
            ivIbFollow.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_label_highlight));
            tvIbFollow.setText(getString(R.string.follow_this));
        }
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(final ImageView imageView, final String url) {
        ImageUtils.getInstance().setRoundCornerImageViewFromUrl(url, imageView);

        // Auto refresh after 5 seconds.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageUtils.getInstance().setRoundCornerImageViewFromUrl(url, imageView);
            }
        }, 5000);
    }

    @BindingAdapter({"bind:detail"})
    public static void loadDetail(TextView textView, String detail) {
        textView.setText(MainActivity.getContext().getString(R.string.use_dept) + " " + detail);
    }

    @BindingAdapter({"bind:consumption"})
    public static void loadTitle(TextView textView, String consumption) {
        String energyInfo;
        if (ENERGY_HIGH.equalsIgnoreCase(consumption.split(",")[0])) {
            energyInfo = consumption.split(SEPARATOR_CONSUMPTION)[1] + "% " + MainActivity.getContext().getString(R.string.increase_weekly);
        } else {
            energyInfo = consumption.split(SEPARATOR_CONSUMPTION)[1] + "% " + MainActivity.getContext().getString(R.string.decrease_weekly);
        }

        // Get the data from yesterday
        int lastHour = currentHour - 2;
        lastHour += lastHour < 0 ? 24 : 0;

        textView.setText(MainActivity.getContext().getString(R.string.consump_this_hour) + " " + consumptionList.get(currentHour - 1) +
                BUILDING_UNIT + MainActivity.getContext().getString(R.string.consump_last_hour) + " " + consumptionList.get(lastHour) +
                BUILDING_UNIT + energyInfo);
    }

    @Override
    public void onResume() {
        super.onResume();

        setupChartView();
    }

    private void setupChartView() {
        // The first two data are building's energy efficiency, not hourly power consumption
        for (int x = 2; x < currentHour + 2; x++) {
            consumptionList.set(x - 2, building.getConsumption().split(SEPARATOR_CONSUMPTION)[x]);
        }

        View barView;
        try {
            barView = aChartUtils.getBarChart(context, context.getResources().getString(R.string.chart_title),
                    context.getResources().getString(R.string.chart_x_title), context.getResources().getString(R.string.chart_y_title), consumptionList);
            llChart.removeAllViews();
            llChart.addView(barView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (MainActivity.screenHeight / 1.4)));
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        buildingDBHelper.updateDB(building);
    }
}