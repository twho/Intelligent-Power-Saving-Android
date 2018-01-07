package com.tsungweiho.intelligentpowersaving.constants;

import com.tsungweiho.intelligentpowersaving.objects.ImageResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Interface for Imgur API usage
 * <p>
 * This interface is used to store all constants used to make Imgur API calls, was created by AKiniyalocts on 2/23/2015.
 *
 * @author Tsung Wei Ho
 * @version 0220.2017
 * @since 1.0.0
 */
public interface ImgurAPIConstants {
    String server = "https://api.imgur.com";

    // Imgur
    String IMGUR_CLIENT_ID = "07560f65c2774dc";
    String IMGUR_POST_CLIENT_ID = "Client-ID 07560f65c2774dc";
    String IMGUR_CLIENT_SECRET = "1893bd07d25e1083d272fe4a5069f2e56a03d07c";
    boolean LOGGING = false;

    /**
     * ImgurUpload image to imgur website
     *
     * @param auth        the type of authorization for upload
     * @param title       title of image
     * @param description description of image
     * @param albumId     ID for album (if the user is adding this image to an album)
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
