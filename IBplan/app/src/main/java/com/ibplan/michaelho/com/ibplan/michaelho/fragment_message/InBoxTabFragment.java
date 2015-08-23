package com.ibplan.michaelho.com.ibplan.michaelho.fragment_message;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibplan.michaelho.ibplan.R;

/**
 * Created by MichaelHo on 2015/5/26.
 */
public class InBoxTabFragment extends Fragment {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message_inbox, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
