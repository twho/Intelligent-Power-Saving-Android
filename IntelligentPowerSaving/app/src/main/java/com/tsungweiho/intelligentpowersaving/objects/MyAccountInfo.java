package com.tsungweiho.intelligentpowersaving.objects;

/**
 * Created by Tsung Wei Ho on 2017/2/22.
 */

public class MyAccountInfo {

    private String email;
    private String name;
    private String imageUrl;

    public MyAccountInfo(String email, String name, String imageUrl) {
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
