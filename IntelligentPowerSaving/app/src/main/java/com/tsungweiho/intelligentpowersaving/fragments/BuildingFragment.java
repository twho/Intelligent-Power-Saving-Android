package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;

/**
 * Created by tsung on 2017/2/18.
 */

public class BuildingFragment extends Fragment {
    // Settings Fragment View
    private View view;

    // Functions
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_building, container, false);
        context = MainActivity.getContext();
        init();
        return view;
    }

    private void init() {

    }
}
