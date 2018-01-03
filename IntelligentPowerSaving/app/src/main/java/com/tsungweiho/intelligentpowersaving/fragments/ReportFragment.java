package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.adapters.SpinnerItemAdapter;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;
import com.tsungweiho.intelligentpowersaving.databases.BuildingDBHelper;

/**
 * Fragment for user to send messages to building admin
 *
 * This fragment is the user interface that user can report or send messages to building administrator
 *
 * @author Tsung Wei Ho
 * @version 0102.2018
 * @since 2.0.0
 */
public class ReportFragment extends Fragment {

    private View view;

    private Context context;

    // UI widgets
    private ImageButton ibBack, ibDelete, ibSend;
    private EditText edTitle, edContent;
    private Spinner spBuilding;

    // Functions
    private BuildingDBHelper buildingDBHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_report, container, false);
        context = IPowerSaving.getContext();

        init();

        return view;
    }

    /**
     * Init all classes needed in ReportFragment
     */
    private void init() {
        ReportFragmentListener reportFragmentListener = new ReportFragmentListener();
        buildingDBHelper = new BuildingDBHelper(context);

        ibBack = view.findViewById(R.id.fragment_report_ib_back);
        ibDelete = view.findViewById(R.id.fragment_report_ib_delete);
        ibSend = view.findViewById(R.id.fragment_report_ib_send);

        ibBack.setOnClickListener(reportFragmentListener);
        ibDelete.setOnClickListener(reportFragmentListener);
        ibSend.setOnClickListener(reportFragmentListener);

        edTitle = view.findViewById(R.id.fragment_report_ed_title);
        edContent = view.findViewById(R.id.fragment_report_ed_content);

        spBuilding = view.findViewById(R.id.fragment_report_spinner);
        SpinnerItemAdapter spItemAdapter = new SpinnerItemAdapter(context, buildingDBHelper.getAllBuildingSet());
        spBuilding.setAdapter(spItemAdapter);

        spBuilding.setOnItemSelectedListener(reportFragmentListener);
    }

    /**
     * All listeners used in ReportFragment
     */
    private class ReportFragmentListener implements View.OnClickListener, AdapterView.OnItemSelectedListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_report_ib_back:
                    ((MainActivity) getActivity()).setFragment(FragmentTags.MainFragment.INBOX);
                    break;
                case R.id.fragment_report_ib_delete:
                    break;
                case R.id.fragment_report_ib_send:
                    break;
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
