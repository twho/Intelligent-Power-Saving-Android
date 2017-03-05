package com.tsungweiho.intelligentpowersaving.objects;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;
import com.tsungweiho.intelligentpowersaving.databinding.ObjIconBuildingBinding;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtilities;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtilities;

/**
 * Created by Tsung Wei Ho on 2/18/2017.
 */

public class BuildingIcon extends View implements BuildingConstants {

    private static Context context;
    private Building building;

    // UI
    private View view;
    private ImageView ivIndicator, ivFollowIndicator;
    private TextView tvConsumpPercent, tvConsump;

    // Functions
    private static ImageUtilities imageUtilities;
    private AnimUtilities animUtilities;
    private ObjIconBuildingBinding binding;

    public BuildingIcon(Context context, Building building) {
        super(context);
        this.context = context;
        this.building = building;
        LayoutInflater li = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(li, R.layout.obj_icon_building, null, false);
        binding.setBuilding(building);
        view = binding.getRoot();
        initViews();
    }


    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(final ImageView imageView, final String url) {
        imageUtilities = new ImageUtilities(context);
        imageUtilities.setRoundCornerImageViewFromUrl(url, imageView);

        // Auto refresh after 5 seconds.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageUtilities.setRoundCornerImageViewFromUrl(url, imageView);
            }
        }, 3500);
    }

    private void initViews() {
        setupFollowIndicator();
        ivIndicator = (ImageView) view.findViewById(R.id.obj_building_icon_iv_indicator);
        tvConsump = (TextView) view.findViewById(R.id.obj_building_icon_tv_consumption);
        tvConsumpPercent = (TextView) view.findViewById(R.id.obj_building_icon_tv_consumption_percentage);

        if (ENERGY_HIGH.equalsIgnoreCase(building.getConsumption().split(",")[0])) {
            tvConsump.setText(context.getString(R.string.increase_weekly));
            setTextViewColor(tvConsump, tvConsumpPercent, context.getResources().getColor(R.color.light_red));
            ivIndicator.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_increase));
        } else {
            tvConsump.setText(context.getString(R.string.decrease_weekly));
            setTextViewColor(tvConsump, tvConsumpPercent, context.getResources().getColor(R.color.green));
            ivIndicator.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_decrease));
        }
        animUtilities = new AnimUtilities(context);
        animUtilities.setIconAnimToVisible(ivIndicator);
        tvConsumpPercent.setText(building.getConsumption().split(",")[1] + " %");
    }

    private void setupFollowIndicator(){
        ivFollowIndicator = (ImageView) view.findViewById(R.id.obj_building_icon_iv_follow);
        if (Boolean.parseBoolean(building.getIfFollow())){
            ivFollowIndicator.setVisibility(View.VISIBLE);
        } else {
            ivFollowIndicator.setVisibility(View.GONE);
        }
    }

    private void setTextViewColor(TextView tvConsump, TextView tvConsumpPercent, int color) {
        tvConsump.setTextColor(color);
        tvConsumpPercent.setTextColor(color);
    }

    public View getView() {
        return view;
    }
}
