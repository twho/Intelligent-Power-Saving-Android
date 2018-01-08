package com.tsungweiho.intelligentpowersaving.objects;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;
import com.tsungweiho.intelligentpowersaving.databinding.ObjIconBuildingBinding;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtils;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;

/**
 * Object class to show building icon in dashboard view (HomeFragment)
 * <p>
 * This class is used to display interactive building icon used in HomeFragment
 *
 * @author Tsung Wei Ho
 * @version 1222.2017
 * @since 1.0.0
 */
public class BuildingIcon extends View implements BuildingConstants {

    // The building shown in the icon
    private Building building;

    // Main view
    private View view;

    public BuildingIcon(Context context) {
        super(context);
    }

    /**
     * BuildingIcon constructor
     *
     * @param context  the context uses this class
     * @param building the building information as an object to be displayed
     */
    public BuildingIcon(Context context, Building building) {
        super(context);

        this.building = building;

        // Data binding
        LayoutInflater li = LayoutInflater.from(context);
        ObjIconBuildingBinding binding = DataBindingUtil.inflate(li, R.layout.obj_icon_building, null, false);
        binding.setBuilding(building);
        view = binding.getRoot();

        initViews();
    }

    /**
     * Get application context for buildingIcon use
     *
     * @return application context
     */
    private Context getApplicationContext() {
        return IPowerSaving.getContext();
    }

    /**
     * Load building image to imageView
     *
     * @param imageView the imageView to show building image
     * @param url       the image url resource
     */
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

    /**
     * Link views to buildingIcon class, no need to cast views while compile with SDK 26
     */
    private void initViews() {
        ImageView ivFollowIndicator = view.findViewById(R.id.obj_building_icon_iv_follow);
        ivFollowIndicator.setVisibility(Boolean.parseBoolean(building.getIfFollow()) ? View.VISIBLE : View.GONE);

        ImageView ivIndicator = view.findViewById(R.id.obj_building_icon_iv_indicator);
        TextView tvConsump = view.findViewById(R.id.obj_building_icon_tv_consumption);
        TextView tvConsumpPercent = view.findViewById(R.id.obj_building_icon_tv_consumption_percentage);

        // Use different colors to represent increase or decrease in energy consumption
        Boolean energyIncrease = Integer.valueOf(building.getEfficiency().split(",")[0]) > 0;
        tvConsump.setText(getApplicationContext().getString(energyIncrease ? R.string.increase_weekly : R.string.decrease_weekly));
        setTextViewColor(tvConsump, tvConsumpPercent, getApplicationContext().getResources().getColor(energyIncrease ? R.color.light_red : R.color.green));
        ivIndicator.setImageDrawable(getApplicationContext().getResources().getDrawable(energyIncrease ? R.mipmap.ic_increase : R.mipmap.ic_decrease));

        AnimUtils.getInstance().fadeInToVisible(ivIndicator, AnimUtils.getInstance().FAST_ANIM_DURATION);
        tvConsumpPercent.setText(Math.abs(Integer.valueOf(building.getEfficiency().split(",")[0])) + getApplicationContext().getResources().getString(R.string.energy_eff_unit));
    }

    /**
     * Set textView color and text based on the energy consumption efficiency of the building
     *
     * @param tvConsump        the text that says increase or decrease in energy consumption of the building
     * @param tvConsumpPercent the numbers that represents building energy consumption efficiency
     * @param color            set the color of the textView
     */
    private void setTextViewColor(TextView tvConsump, TextView tvConsumpPercent, int color) {
        tvConsump.setTextColor(color);
        tvConsumpPercent.setTextColor(color);
    }

    /**
     * Get UI views of the class
     *
     * @return the buildingIcon view
     */
    public View getView() {
        return view;
    }
}
