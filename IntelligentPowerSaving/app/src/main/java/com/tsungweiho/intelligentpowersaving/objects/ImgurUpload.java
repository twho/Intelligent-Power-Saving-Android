package com.tsungweiho.intelligentpowersaving.objects;

import java.io.File;

/**
 * Object class to store Imgur API upload task
 * <p>
 * This class is used to store the information needed for Imgur upload task
 *
 * @author Tsung Wei Ho
 * @version 1227.2017
 * @since 1.0.0
 */
public class ImgurUpload {
    public File image;
    public String title;
    public String description;
    public String albumId;

    /**
     * ImgurUpload constructor
     *
     * @param image       the image file resource to be uploaded to Imgur
     * @param title       the title of of the image to be shown on Imgur
     * @param description the description of of the image to be shown on Imgur
     * @param albumId     the albumId to store the image on Imgur
     */
    public ImgurUpload(File image, String title, String description, String albumId) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.albumId = albumId;
    }

    public File getImage() {
        return this.image;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAlbumId() {
        return this.albumId;
    }
}
