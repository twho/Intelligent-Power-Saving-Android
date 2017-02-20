package com.tsungweiho.intelligentpowersaving.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtilities;

/**
 * Created by tsung on 2017/2/18.
 */

public class BuildingIcon extends View {

    private Context context;
    private Building building;

    // UI
    private View view;
    private ImageView ivBuilding, ivIndicator;
    private TextView tvBuilding, tvConsumpPercent, tvConsump;

    // Functions
    private ImageUtilities imageUtilities;

    public BuildingIcon(Context context, Building building) {
        super(context);
        this.context = context;
        this.building = building;
        LayoutInflater li = LayoutInflater.from(context);
        view = li.inflate(R.layout.obj_icon_building, null);
        initViews();
    }

    private void initViews() {
        imageUtilities = new ImageUtilities(context);
        ivIndicator = (ImageView) view.findViewById(R.id.obj_building_icon_iv_indicator);
        ivBuilding = (ImageView) view.findViewById(R.id.obj_building_icon_iv);
        imageUtilities.setRoundCornerImageViewFromUrl(building.getImageUrl(), ivBuilding);

        tvBuilding = (TextView) view.findViewById(R.id.obj_building_icon_tv);
        tvConsump = (TextView) view.findViewById(R.id.obj_building_icon_tv_consumption);
        tvConsumpPercent = (TextView) view.findViewById(R.id.obj_building_icon_tv_consumption_percentage);
        tvBuilding.setText(building.getName());
        tvConsump.setText(building.getConsumption());
        if ("high".equalsIgnoreCase(building.getConsumption().split(",")[0])) {
            tvConsump.setText(context.getString(R.string.increase_weekly));
            setTextViewColor(tvConsump, tvConsumpPercent, context.getResources().getColor(R.color.light_red));
            ivIndicator.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_increase));
        } else {
            tvConsump.setText(context.getString(R.string.decrease_weekly));
            setTextViewColor(tvConsump, tvConsumpPercent, context.getResources().getColor(R.color.green));
            ivIndicator.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_decrease));
        }
        tvConsumpPercent.setText(building.getConsumption().split(",")[1] + " %");

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setTextViewColor(TextView tvConsump, TextView tvConsumpPercent, int color) {
        tvConsump.setTextColor(color);
        tvConsumpPercent.setTextColor(color);
    }

    public View getView() {
        return view;
    }
}
