package com.tsungweiho.intelligentpowersaving.objects;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;
import com.tsungweiho.intelligentpowersaving.databinding.ObjIconBuildingBinding;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtils;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;

/**
 * Created by Tsung Wei Ho on 2/18/2017.
 * Updated by Tsung Wei Ho on 12/22/2017
 */

public class BuildingIcon extends View implements BuildingConstants {

    private static Context context;
    private Building building;

    // UI
    private View view;
    private ImageView ivIndicator, ivFollowIndicator;
    private TextView tvConsumpPercent, tvConsump;

    // Functions
    private ObjIconBuildingBinding binding;

    public BuildingIcon(Context context, Building building) {
        super(context);

        this.context = context;
        this.building = building;

        // Data binding
        LayoutInflater li = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(li, R.layout.obj_icon_building, null, false);
        binding.setBuilding(building);
        view = binding.getRoot();

        initViews();
    }


    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(final ImageView imageView, final String url) {
        final ImageUtils imageUtils = ImageUtils.getInstance();
        ImageUtils.getInstance().setRoundedCornerImageViewFromUrl(url, imageView, imageUtils.IMG_ROUNDED_CORNER);

        // Auto refresh after 3.5 seconds.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageUtils.getInstance().setRoundedCornerImageViewFromUrl(url, imageView, imageUtils.IMG_ROUNDED_CORNER);
            }
        }, 3500);
    }

    // Compile with SDK 26, no need to cast views
    private void initViews() {
        ivFollowIndicator = view.findViewById(R.id.obj_building_icon_iv_follow);
        ivFollowIndicator.setVisibility(Boolean.parseBoolean(building.getIfFollow()) ? View.VISIBLE : View.GONE);

        ivIndicator = view.findViewById(R.id.obj_building_icon_iv_indicator);
        tvConsump = view.findViewById(R.id.obj_building_icon_tv_consumption);
        tvConsumpPercent = view.findViewById(R.id.obj_building_icon_tv_consumption_percentage);

        // Use different colors to represent increase or decrease in energy consumption
        Boolean energyIncrease = Integer.valueOf(building.getEfficiency().split(",")[0]) > 0;
        tvConsump.setText(context.getString(energyIncrease ? R.string.increase_weekly : R.string.decrease_weekly));
        setTextViewColor(tvConsump, tvConsumpPercent, context.getResources().getColor(energyIncrease ? R.color.light_red : R.color.green));
        ivIndicator.setImageDrawable(context.getResources().getDrawable(energyIncrease ? R.mipmap.ic_increase : R.mipmap.ic_decrease));

        AnimUtils.getInstance().fadeinToVisible(ivIndicator, AnimUtils.getInstance().FAST_ANIM_DURATION);
        tvConsumpPercent.setText(Math.abs(Integer.valueOf(building.getEfficiency().split(",")[0])) + context.getResources().getString(R.string.energy_eff_unit));
    }

    private void setTextViewColor(TextView tvConsump, TextView tvConsumpPercent, int color) {
        tvConsump.setTextColor(color);
        tvConsumpPercent.setTextColor(color);
    }

    public View getView() {
        return view;
    }
}
