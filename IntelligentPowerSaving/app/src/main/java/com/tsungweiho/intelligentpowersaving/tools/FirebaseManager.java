package com.tsungweiho.intelligentpowersaving.tools;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.utils.SharedPrefsUtils;

import java.io.File;

/**
 * Class for performing Firebase tasks
 * <p>
 * This class is used to perform different kinds of Firebase tasks.
 *
 * @author Tsung Wei Ho
 * @version 1223.2017
 * @since 2.0.0
 */
public class FirebaseManager implements DBConstants {
    private String TAG = "FirebaseManager";

    private static final FirebaseManager instance = new FirebaseManager();

    public static FirebaseManager getInstance() {
        return instance;
    }

    private FirebaseAuth firebaseAuth;

    private FirebaseManager() {
    }

    /**
     * Sign in Firebase using system account and password
     *
     * @param onCompleteListener listener for UI callback
     */
    public void signInSystemAccount(OnCompleteListener<AuthResult> onCompleteListener) {
        if (null == firebaseAuth)
            firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(SYSTEM_ACCOUNT, SYSTEM_PWD).addOnCompleteListener(onCompleteListener);
    }

    /**
     * Sign out Firebase system account
     */
    public void signOutSystemAccount(){
        if (null == firebaseAuth)
            firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signOut();
    }

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
        return usrProfilePicRef.child(SharedPrefsUtils.getInstance().getMyAccountInfo().getName() + "/"
                + SharedPrefsUtils.getInstance().getMyAccountInfo().getUid()).putFile(imgFile, metadata);
    }

    public void downloadProfileImg(){

    }
}
