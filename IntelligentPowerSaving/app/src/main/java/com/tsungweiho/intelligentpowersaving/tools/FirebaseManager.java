package com.tsungweiho.intelligentpowersaving.tools;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;

import java.io.File;

import io.reactivex.annotations.NonNull;

/**
 * Created by Tsung Wei Ho on 12/23/2017.
 */

public class FirebaseManager implements DBConstants {
    private StorageReference usrProfilePicRef;

    private static final FirebaseManager ourInstance = new FirebaseManager();

    public static FirebaseManager getInstance() {
        return ourInstance;
    }

    private FirebaseManager() {}

    /**
     * Upload profile image to Firebase storage
     * @param imgPath the path to the image to be uploaded
     * @param textView the textView for showing uploading status
     * @param progressBar the progressBar for showing uploading progress
     */
    public void uploadProfileImg(String imgPath, TextView textView, ProgressBar progressBar) {
        Uri imgFile = Uri.fromFile(new File(imgPath));

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        usrProfilePicRef = FirebaseStorage.getInstance().getReferenceFromUrl(FDB_STORAGE_PROFILEPIC);

        // Upload file and metadata to the path 'usrProfilePic/[displayName].jpg'
        UploadTask uploadTask = usrProfilePicRef.child(PreferencesManager.getInstance().getMyAccountInfo().getName() + "/"
                + imgFile.getLastPathSegment()).putFile(imgFile, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();

                // Store to local user preference
                MyAccountInfo myAccountInfo = PreferencesManager.getInstance().getMyAccountInfo();
                myAccountInfo.setImageUrl(downloadUrl.toString());
            }
        });
    }
}
