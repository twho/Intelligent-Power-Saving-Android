package com.tsungweiho.intelligentpowersaving.tools;

import android.content.Context;

import com.tsungweiho.intelligentpowersaving.constants.ImgurAPIConstants;
import com.tsungweiho.intelligentpowersaving.objects.ImageResponse;
import com.tsungweiho.intelligentpowersaving.objects.ImgurUpload;
import com.tsungweiho.intelligentpowersaving.utils.NetworkUtils;
import com.tsungweiho.intelligentpowersaving.utils.NotificationHelper;

import java.lang.ref.WeakReference;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by AKiniyalocts on 1/12/15.
 * Modified by Tsung Wei Ho on 11/10/17
 */
public class UploadService implements ImgurAPIConstants {

    private WeakReference<Context> context;

    public UploadService(Context context) {
        this.context = new WeakReference<>(context);
    }

    public void Execute(ImgurUpload imgurUpload, Callback<ImageResponse> callback) {
        final Callback<ImageResponse> cb = callback;

        if (!NetworkUtils.isConnected(context.get())) {
            //Callback will be called, so we prevent a unnecessary notification
            cb.failure(null);
            return;
        }

        final NotificationHelper notificationHelper = NotificationHelper.getInstance();
        notificationHelper.createUploadingNotification();

        RestAdapter restAdapter = buildRestAdapter();

        restAdapter.create(ImgurAPIConstants.class).postImage(
                IMGUR_POST_CLIENT_ID,
                imgurUpload.getTitle(),
                imgurUpload.getDescription(),
                imgurUpload.getAlbumId(),
                null,
                new TypedFile("image/*", imgurUpload.image),
                new Callback<ImageResponse>() {
                    @Override
                    public void success(ImageResponse imageResponse, Response response) {
                        if (cb != null) cb.success(imageResponse, response);
                        if (response == null) {
                            // Notify image was NOT uploaded successfully
                            notificationHelper.createFailedUploadNotification();
                            return;
                        }

                        // Notify image was uploaded successfully
                        if (imageResponse.success) {
                            notificationHelper.createUploadedNotification(imageResponse);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (cb != null) cb.failure(error);
                        notificationHelper.createFailedUploadNotification();
                    }
                });
    }

    private RestAdapter buildRestAdapter() {
        RestAdapter imgurAdapter = new RestAdapter.Builder()
                .setEndpoint(ImgurAPIConstants.server)
                .build();

        // Set rest adapter logging if we're already logging
        if (LOGGING)
            imgurAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        return imgurAdapter;
    }

    @Override
    public void postImage(@Header("Authorization") String auth, @Query("title") String title, @Query("description") String description, @Query("album") String albumId, @Query("account_url") String username, @Body TypedFile file, Callback<ImageResponse> cb) {
        // unused
    }
}
