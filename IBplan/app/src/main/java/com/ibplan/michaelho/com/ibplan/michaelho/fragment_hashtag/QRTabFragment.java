package com.ibplan.michaelho.com.ibplan.michaelho.fragment_hashtag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibplan.michaelho.com.ibplan.michaelho.tools.AlertDialogManager;
import com.ibplan.michaelho.com.ibplan.michaelho.util.TimeUtilities;
import com.ibplan.michaelho.ibplan.R;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by MichaelHo on 2015/5/18.
 */
public class QRTabFragment extends Fragment {
    // download package
    private static final String PACKAGE = "com.google.zxing.client.android";

    private static final String TAG = "QRScannerFragment";

    // UIs
    private TextView tvMessage;
    private ImageView btScan;
    private View rootView;

    // datePicker params
    private Calendar cal;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    // String used to generate qrcode
    private String chooseDate1, chooseDate2;
    private String chooseTime2;

    // Contact details
    private String reg_id;

    public static Fragment newInstance(Context context) {
        Fragment f = new QRTabFragment();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater
                .inflate(R.layout.fragment_qrcode, container, false);
        init();
        return rootView;
    }

    private void init() {
        tvMessage = (TextView) rootView.findViewById(R.id.tvMessage);
        tvMessage.setVisibility(View.GONE);
        btScan = (ImageView) rootView.findViewById(R.id.qr_tab_fragment_iv1);
        btScan.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(
                        "com.google.zxing.client.android.SCAN");
                intent.setPackage("com.google.zxing.client.android");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                try {
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException ex) {
                    showDownloadDialog();
                }
            }

        });
    }

    // "FleetChat" + "duration:" + "expiration:"+ chooseDate2 + "-" +
    // chooseTime2 + "" + "regID:"
    // + reg_id + "UserName:" + USER_NAME + "PortraitID:"
    // + PORTRAIT_ID;
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            String message = "";
            if (resultCode == Activity.RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                if (format.equalsIgnoreCase("QR_CODE")
                        && contents.split("duration:")[0]
                        .equalsIgnoreCase("FleetChat")) {

                    HashMap<String, Object> item = new HashMap<String, Object>();
                    String name = contents.split("UserName:")[1]
                            .split("PortraitID:")[0];
                    String gcmidFromOther = contents.split("regID:")[1]
                            .split("UserName:")[0];
                    String deadline = contents.split("expiration:")[1]
                            .split("regID:")[0];
                    String portrait = contents.split("PortraitID:")[1];
                    String addDate = timeCreater(TimeUtilities
                            .getTimeyyyyMMddHHmmss());
                    String dt;
                    Log.i("dtInfo", deadline.trim());
                    if (!deadline.equalsIgnoreCase("No Limitation-")) {
                        dt = deadline.trim().substring(0, 4)
                                + deadline.trim().substring(4, 6)
                                + deadline.trim().substring(6, 8)
                                + deadline.trim().substring(8, 10)
                                + deadline.trim().substring(11, 13);
                    } else {
                        dt = "No Limitation-";
                    }

                } else {
                    message = null;
                    new AlertDialogManager().showMessageDialog(getActivity(),
                            "Warning",
                            "This format is wrong, please try to scan QRcode!");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                message = "Scan was Cancelled!";
            }
            tvMessage.setText(message);
        }
    }

    private void showDownloadDialog() {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(
                getActivity());
        downloadDialog.setTitle("No Barcode Scanner Found");
        downloadDialog
                .setMessage("Please download and install Barcode Scanner!");
        downloadDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri = Uri.parse("market://search?q=pname:"
                                + PACKAGE);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Log.e(ex.toString(),
                                    "Play Store is not installed; cannot install Barcode Scanner");
                        }
                    }
                });
        downloadDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        downloadDialog.show();
    }

    private String timeCreater(String time) {
        String timeAfterchange;
        timeAfterchange = time.substring(0, 4) + "/" + time.substring(4, 6)
                + "/" + time.substring(6, 8);
        return timeAfterchange;
    }

}

