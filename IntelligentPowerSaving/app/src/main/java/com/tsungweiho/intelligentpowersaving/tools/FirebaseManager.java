package com.tsungweiho.intelligentpowersaving.tools;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.objects.Building;
import com.tsungweiho.intelligentpowersaving.utils.SharedPrefsUtils;

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
public class FirebaseManager implements DBConstants, BuildingConstants {
    private String TAG = "FirebaseManager";

    private static final FirebaseManager instance = new FirebaseManager();

    public static FirebaseManager getInstance() {
        return instance;
    }

    private FirebaseAuth firebaseAuth;

    private FirebaseManager() {
    }

    /**
     * Get application context for animation use
     *
     * @return application context
     */
    private Context getContext() {
        return IPowerSaving.getContext();
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

    public void loadBuildings(ValueEventListener valueEventListener){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(BUILDING_DB);
        databaseReference.addValueEventListener(valueEventListener);
    }

    public Building loadBuildingBySnapshot(DataSnapshot buildingSnapshot){
        String name = buildingSnapshot.child(FDB_NAME).getValue() + "";
        String efficiency = buildingSnapshot.child(FDB_EFFICIENCY).getValue() + "";
        String consumption = buildingSnapshot.child(FDB_CONSUMPTION).getValue() + "";
        String detail = buildingSnapshot.child(FDB_DETAIL).getValue() + "";
        String imgUrl = buildingSnapshot.child(FDB_IMGURL).getValue() + "";

        return new Building(name, detail, efficiency, consumption, imgUrl, BUILDING_NOT_FOLLOW);
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

        // Upload file and metadata to the path 'usrProfilePic/[uid].jpg'
        return usrProfilePicRef.child(SharedPrefsUtils.getInstance().getMyAccountInfo().getUid() + "/" + SharedPrefsUtils.getInstance().getMyAccountInfo().getUid()).putFile(imgFile, metadata);
    }

    /**
     * Download user profile image from Firebase
     *
     * @param imgUrl is the user name plus user uid in the format of [userName]/[uid]
     * @param successListener success event listener for UI callback
     * @param failureListener failure event listener for UI callback
     */
    public void downloadProfileImg(String imgUrl, OnSuccessListener<Uri> successListener, OnFailureListener failureListener){
        StorageReference load = FirebaseStorage.getInstance().getReference(FDB_STORAGE_PROFILEPIC).child(imgUrl);

        load.getDownloadUrl().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }
}
