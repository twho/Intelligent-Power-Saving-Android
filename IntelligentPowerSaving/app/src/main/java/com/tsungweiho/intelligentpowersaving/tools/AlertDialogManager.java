package com.tsungweiho.intelligentpowersaving.tools;

/**
 * Created by MichaelHo on 4/15/2015.
 * Updated by MichaelHo on 12/24/2017.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.isseiaoki.simplecropview.CropImageView;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;
import com.tsungweiho.intelligentpowersaving.constants.FragmentTags;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

// Singleton class
public class AlertDialogManager implements BuildingConstants, FragmentTags {

    private static final AlertDialogManager ourInstance = new AlertDialogManager();

    public static AlertDialogManager getInstance() {
        return ourInstance;
    }

    private Context context;

    private AlertDialogManager() {
        this.context = MainActivity.getContext();
    }

    /**
     * Show general alert dialog with customizable title and message content
     * @param title the title of the alert dialog
     * @param message the message content in the alert dialog
     */
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

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_IMAGE = 0;

    /**
     * Show photo selection dialog
     * @param fragmentTag the tag of the fragment calls this method
     */
    public void showCameraDialog(final String fragmentTag) {
        FragmentManager fm = ((MainActivity) MainActivity.getContext()).getSupportFragmentManager();
        final Fragment fragment = fm.findFragmentByTag(fragmentTag);

        AlertDialog.Builder dialog = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        dialog.setTitle(R.string.alert_dialog_manager_camera_title)
                .setPositiveButton(R.string.alert_dialog_manager_camera_camera,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    fragment.startActivityForResult(takeIntent, REQUEST_CODE_CAMERA);
                                } else {
                                    PermissionManager permissionManager = new PermissionManager(context);
                                    permissionManager.requestCameraPermission();
                                }
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

    /**
     * Show image dialog for user to report FIXIT event
     * @param fragmentTag the tag of the fragment calls this method
     * @param bitmap the bitmap of the image user wants to upload
     */
    public void showImageDialog(final String fragmentTag, Bitmap bitmap) {
        final Dialog dialog = new Dialog(context);
        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.obj_dialog_image, null);

        Button btnTakeNew = dialogView.findViewById(R.id.obj_dialog_image_btn_retake);
        btnTakeNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showCameraDialog(fragmentTag);
            }
        });

        Button btnCancel = dialogView.findViewById(R.id.obj_dialog_image_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final ImageView ivTouch = dialogView.findViewById(R.id.obj_dialog_image_iv_cover);
        ImageViewTouch photoImgView = dialogView.findViewById(R.id.obj_dialog_image_iv);
        photoImgView.setImageBitmap(bitmap);
        photoImgView.setDisplayType(ImageViewTouchBase.DisplayType.NONE);
        photoImgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ivTouch.setVisibility(View.GONE);
                return false;
            }
        });

        dialog.setContentView(dialogView);
        dialog.show();
    }

    private boolean isCropped = false;

    /**
     * Show image editing dialog
     * @param imageView the imageView to be set in UI
     * @param bitmap the bitmap to be set to imageView
     * @return the dialog created
     */
    public Dialog showCropImageDialog(final ImageView imageView, final Bitmap bitmap) {
        // Setup dialog view
        final Dialog dialog = new Dialog(context);
        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.obj_dialog_crop_image, null);

        // Setup cropped image view
        final CropImageView cropImageView = dialogView.findViewById(R.id.obj_dialog_crop_image_civ);
        cropImageView.setImageBitmap(bitmap);
        cropImageView.setEnabled(true);

        final Button btnCrop = dialogView.findViewById(R.id.obj_dialog_crop_image_btn_crop);
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.setCropEnabled(isCropped);
                cropImageView.setImageBitmap(isCropped ? bitmap : cropImageView.getCircularBitmap(cropImageView.getCroppedBitmap()));
                btnCrop.setText(context.getString(isCropped ? R.string.alert_dialog_manager_crop_img_crop : R.string.alert_dialog_manager_crop_img_revert));
                isCropped = !isCropped;
            }
        });

        Button btnUse = dialogView.findViewById(R.id.obj_dialog_crop_image_btn_use);
        btnUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageBitmap(cropImageView.getCircularBitmap(cropImageView.getCroppedBitmap()));
                imageView.buildDrawingCache();
                dialog.dismiss();
            }
        });

        dialog.setContentView(dialogView);
        dialog.show();

        return dialog;
    }
}