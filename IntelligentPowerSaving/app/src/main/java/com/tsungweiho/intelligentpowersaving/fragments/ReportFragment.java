package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsungweiho.intelligentpowersaving.IntelligentPowerSaving;
import com.tsungweiho.intelligentpowersaving.R;

/**
 * Created by tsung on 2018/1/1.
 */

public class ReportFragment extends Fragment {

    private View view;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = IntelligentPowerSaving.getContext();

        init();

        return view;
    }

    private void init() {

    }
}
