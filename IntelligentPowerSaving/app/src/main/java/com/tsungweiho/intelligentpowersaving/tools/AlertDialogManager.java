package com.tsungweiho.intelligentpowersaving.tools;

/**
 * Created by MichaelHo on 2015/4/15.
 * Updated by MichaelHo on 2017/2/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTag;
import com.tsungweiho.intelligentpowersaving.fragments.EventFragment;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class AlertDialogManager implements BuildingConstants, FragmentTag {
    private Context context;

    public AlertDialogManager(Context context) {
        this.context = context;
    }

    public void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setButton(context.getString(R.string.alert_dialog_manager_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    public void showMessageDialog(String title, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK).create();

        alertDialog.setTitle(title);

        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setButton(context.getString(R.string.alert_dialog_manager_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_IMAGE = 0;

    public void showCameraDialog(final String fragmentCalledThis) {
        FragmentManager fm = ((MainActivity) MainActivity.getContext()).getSupportFragmentManager();
        final Fragment fragment = fm.findFragmentByTag(fragmentCalledThis);

        AlertDialog.Builder dialog = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        dialog.setTitle(R.string.alert_dialog_manager_camera_title)
                .setPositiveButton(R.string.alert_dialog_manager_camera_camera,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                fragment.startActivityForResult(takeIntent, REQUEST_CODE_CAMERA);
                            }
                        })
                .setNegativeButton(R.string.alert_dialog_manager_camera_photo,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent takeIntent = new Intent();
                                takeIntent.setType("image/*");
                                takeIntent.setAction(Intent.ACTION_GET_CONTENT);
                                fragment.startActivityForResult(Intent.createChooser(takeIntent, "data source: "), REQUEST_CODE_IMAGE);
                            }
                        }).show();
    }

    public void showImageDialog(final String fragmentCalledThis, Bitmap bitmap) {
        final Dialog dialog = new Dialog(context);
        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.obj_dialog_image, null);
        Button btnTakeNew = (Button) dialogView.findViewById(R.id.obj_dialog_image_btn_retake);
        btnTakeNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showCameraDialog(fragmentCalledThis);
            }
        });
        Button btnCancel = (Button) dialogView.findViewById(R.id.obj_dialog_image_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ImageViewTouch photoImgView = (ImageViewTouch) dialogView.findViewById(R.id.obj_dialog_image_iv);
        photoImgView.setImageBitmap(bitmap);
        photoImgView.setDisplayType(ImageViewTouchBase.DisplayType.NONE);

        dialog.setContentView(dialogView);
        dialog.show();
    }

    public void showProgressDialog(String title, String message,
                                   ProgressDialog dialog) {
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.show();
    }
}