package com.tsungweiho.intelligentpowersaving.adapters;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.databinding.ObjSpinnerListItemBinding;
import com.tsungweiho.intelligentpowersaving.objects.Building;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;

import java.util.ArrayList;


/**
 * Class for setting spinner in ReportFragment
 * <p>
 * This class is the user interface of spinner in ReportFragment
 *
 * @author Tsung Wei Ho
 * @version 0102.2018
 * @since 2.0.0
 */
public class SpinnerItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Building> buildingList;

    /**
     * DrawerListAdapter constructor
     *
     * @param context      the context that uses this class
     * @param buildingList the buildingList that contains Building objects to be shown
     */
    public SpinnerItemAdapter(Context context, ArrayList<Building> buildingList) {
        this.context = context;
        this.buildingList = buildingList;
    }

    @Override
    public int getCount() {
        return buildingList.size();
    }

    @Override
    public Object getItem(int position) {
        return buildingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SpinnerItemAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            ObjSpinnerListItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.obj_spinner_list_item, parent, false);

            viewHolder = new SpinnerItemAdapter.ViewHolder(itemBinding);
            convertView = viewHolder.view;

            viewHolder.textView = convertView.findViewById(R.id.obj_spinner_list_item_tv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SpinnerItemAdapter.ViewHolder) convertView.getTag();
        }

        Building building = buildingList.get(position);
        viewHolder.binding.setBuilding(building);

        viewHolder.textView.setText(context.getResources().getString(R.string.loading));
        return convertView;
    }

    /**
     * All UI widgets in each spinner list item
     */
    private class ViewHolder {
        ImageView imageView;
        TextView textView;

        View view;
        ObjSpinnerListItemBinding binding;

        ViewHolder(ObjSpinnerListItemBinding binding) {
            this.view = binding.getRoot();
            this.binding = binding;
        }
    }

    /**
     * Load building image to spinner item
     *
     * @param imageView the imageView in each spinner item
     * @param imgUrl    the url resource of the image
     */
    @BindingAdapter({"bind:spImage"})
    public static void setBuildingImg(final ImageView imageView, final String imgUrl) {
        final ImageUtils imageUtils = ImageUtils.getInstance();
        imageUtils.setRoundedCornerImageViewFromUrl(imgUrl, imageView, imageUtils.IMG_CIRCULAR);

        // Auto refresh after 3 seconds.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageUtils.setRoundedCornerImageViewFromUrl(imgUrl, imageView, imageUtils.IMG_CIRCULAR);
            }
        }, 3000);
    }
}
