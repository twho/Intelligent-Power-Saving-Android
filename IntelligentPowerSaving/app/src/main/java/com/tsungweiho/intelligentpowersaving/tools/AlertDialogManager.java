package com.tsungweiho.intelligentpowersaving.tools;

/**
 * Created by MichaelHo on 2015/4/15.
 * Updated by MichaelHo on 2017/2/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogManager {
    private Context context;


    public AlertDialogManager(Context context) {
        this.context = context;
    }

    public void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    public void showMessageDialog(String title, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);

        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void showExitDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .create();

        alertDialog.setTitle("Exit");
        alertDialog.setMessage("You are leaving CourseCloud");

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Comfirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).finish();
                    }
                });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        // Showing Alert Message
        alertDialog.show();
    }

    public void showProgressDialog(String title, String message,
                                   ProgressDialog dialog) {
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.show();
    }
}