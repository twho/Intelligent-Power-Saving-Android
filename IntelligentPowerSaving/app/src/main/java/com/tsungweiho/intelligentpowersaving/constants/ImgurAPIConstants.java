package com.tsungweiho.intelligentpowersaving.constants;

import com.tsungweiho.intelligentpowersaving.objects.ImageResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by AKiniyalocts on 2/23/2015.
 * Modified by Tsung Wei Ho on 2/20/2017
 * This is our imgur API. It generates a rest API via Retrofit from Square inc.
 */
public interface ImgurAPIConstants {
    String server = "https://api.imgur.com";

    // Imgur
    String IMGUR_CLIENT_ID = "07560f65c2774dc";
    String IMGUR_POST_CLIENT_ID = "Client-ID 07560f65c2774dc";
    String IMGUR_CLIENT_SECRET = "1893bd07d25e1083d272fe4a5069f2e56a03d07c";
    boolean LOGGING = false;

    /****************************************
     * Upload
     * ImageUt upload API
     */

    /**
     * @param auth        #Type of authorization for upload
     * @param title       #Title of image
     * @param description #Description of image
     * @param albumId     #ID for album (if the user is adding this image to an album)
     * @param username    username for upload
     * @param file        image
     * @param cb          Callback used for success/failures
     */
    @POST("/3/image")
    void postImage(
            @Header("Authorization") String auth,
            @Query("title") String title,
            @Query("description") String description,
            @Query("album") String albumId,
            @Query("account_url") String username,
            @Body TypedFile file,
            Callback<ImageResponse> cb
    );
}
