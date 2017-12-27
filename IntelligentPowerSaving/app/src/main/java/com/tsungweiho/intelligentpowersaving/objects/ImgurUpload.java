package com.tsungweiho.intelligentpowersaving.objects;

import java.io.File;

/**
 * Object class to store Imgur API upload task
 *
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

    public ImgurUpload(File image, String title, String description, String albumId){
        this.image = image;
        this.title = title;
        this.description = description;
        this.albumId = albumId;
    }

    public File getImage(){
        return this.image;
    }

    public String getTitle(){
        return this.title;
    }

    public String getDescription(){
        return this.description;
    }

    public String getAlbumId(){
        return this.albumId;
    }
}
