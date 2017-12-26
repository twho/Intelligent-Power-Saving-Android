package com.tsungweiho.intelligentpowersaving.tools;

import android.net.Uri;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;

import java.io.File;

/**
 * Class for performing Firebase tasks
 *
 * This class is used to perform different kinds of Firebase tasks.
 *
 * @author Tsung Wei Ho
 * @version 1223.2017
 * @since 2.0.0
 */
public class FirebaseManager implements DBConstants {
    private String TAG = "FirebaseManager";

    private static final FirebaseManager ourInstance = new FirebaseManager();

    public static FirebaseManager getInstance() {
        return ourInstance;
    }

    private FirebaseManager() {}

    /**
     * Upload profile image to Firebase storage
     *
     * @param imgPath the path to the image to be uploaded
     */
    public UploadTask uploadProfileImg(String imgPath) {
        Uri imgFile = Uri.fromFile(new File(imgPath));

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        StorageReference usrProfilePicRef = FirebaseStorage.getInstance().getReference(FDB_STORAGE_PROFILEPIC);

        // Upload file and metadata to the path 'usrProfilePic/[displayName].jpg'
        UploadTask uploadTask = usrProfilePicRef.child(PreferencesManager.getInstance().getMyAccountInfo().getName() + "/"
                + imgFile.getLastPathSegment()).putFile(imgFile, metadata);


        return uploadTask;
    }
}
