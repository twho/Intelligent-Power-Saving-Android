package com.tsungweiho.intelligentpowersaving.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.databases.EventDBHelper;
import com.tsungweiho.intelligentpowersaving.utils.AnimUtilities;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by Tsung Wei Ho on 2015/4/15.
 * Updated by Tsung Wei Ho on 2017/2/18.
 */

public class EventFragment extends Fragment {

    // Event Fragment View
    private View view;

    // UIs
    private ImageViewTouch ivMap;
    private RelativeLayout rlMap;
    private FrameLayout flTopBarAddEvent;
    private LinearLayout llTopBarAddEvent;
    private TextView tvTitle, tvBottom;
    private EditText edEvent;
    private ImageButton ibAdd, ibCancel, ibCamera;
    private Button btnFullMap;

    // Functions
    private Context context;
    private EventDBHelper eventDBHelper;
    private RelativeLayout.LayoutParams params;
    private String xPos, yPos;
    private AnimUtilities animUtilities;
    private EventFragmentListener eventFragmentListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_event, container, false);
        context = MainActivity.getContext();
        init();
        return view;
    }

    private void init() {
        animUtilities = new AnimUtilities(context);
        eventFragmentListener = new EventFragmentListener();

        // find views
        flTopBarAddEvent = (FrameLayout) view.findViewById(R.id.fragment_event_top_bar_add_event1);
        llTopBarAddEvent = (LinearLayout) view.findViewById(R.id.fragment_event_top_bar_add_event2);
        ibAdd = (ImageButton) view.findViewById(R.id.fragment_event_btn_add);
        ibCancel = (ImageButton) view.findViewById(R.id.fragment_event_btn_cancel);
        ibCamera = (ImageButton) view.findViewById(R.id.fragment_event_btn_camera);
        tvTitle = (TextView) view.findViewById(R.id.fragment_event_title);
        tvBottom = (TextView) view.findViewById(R.id.fragment_event_bottom);
        edEvent = (EditText) view.findViewById(R.id.fragment_event_ed_event);
        rlMap = (RelativeLayout) view.findViewById(R.id.fragment_event_relative_layout);
        ivMap = (ImageViewTouch) view.findViewById(R.id.fragment_event_iv_map);
        btnFullMap = (Button) view.findViewById(R.id.fragment_event_btn_full_map);

        // init map view
        ivMap.setDisplayType(ImageViewTouchBase.DisplayType.FIT_IF_BIGGER);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.img_campus_map);
        params = new RelativeLayout.LayoutParams(ivMap.getWidth() / 10, ivMap.getWidth() / 10);
        ivMap.setImageBitmap(icon);

        setAllListener();
    }

    private void setAllListener() {
        ivMap.setOnTouchListener(eventFragmentListener);
        ivMap.setOnLongClickListener(eventFragmentListener);
        ibAdd.setOnClickListener(eventFragmentListener);
        ibCancel.setOnClickListener(eventFragmentListener);
        ibCamera.setOnClickListener(eventFragmentListener);
        btnFullMap.setOnClickListener(eventFragmentListener);
    }

    private float[] getRelativeCoordinate(MotionEvent event) {
        float[] values = new float[9];
        ivMap.getDisplayMatrix().getValues(values);

        float[] coord = new float[2];
        coord[0] = (event.getX() - values[2]) / values[0];
        coord[1] = (event.getY() - values[5]) / values[4];
        return coord;
    }

    private class EventFragmentListener implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_event_btn_add:
                    dismissAddView();
                    break;
                case R.id.fragment_event_btn_cancel:
                    dismissAddView();
                    break;
                case R.id.fragment_event_btn_camera:
                    break;
                case R.id.fragment_event_btn_full_map:
                    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.img_campus_map);
                    ivMap.setImageBitmap(icon);
                    break;
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            xPos = String.valueOf(getRelativeCoordinate(event)[0] / ivMap.getWidth());
            yPos = String.valueOf(getRelativeCoordinate(event)[1] / ivMap.getHeight());
            return false;
        }

        @Override
        public boolean onLongClick(View view) {
            animUtilities.setflAnimToVisible(flTopBarAddEvent);
            animUtilities.setllAnimToVisible(llTopBarAddEvent);
            tvTitle.setText(context.getResources().getString(R.string.fragment_event_title_add));
            tvBottom.setText(context.getString(R.string.fragment_event_bottom_add));
            return false;
        }
    }

    private void dismissAddView() {
        flTopBarAddEvent.setVisibility(View.GONE);
        llTopBarAddEvent.setVisibility(View.GONE);
        tvTitle.setText(context.getString(R.string.fragment_event_title));
        tvBottom.setText(context.getString(R.string.fragment_event_bottom));
    }

    @Override
    public void onPause() {
        super.onPause();

        // clean text
        edEvent.setText("");
    }
}
