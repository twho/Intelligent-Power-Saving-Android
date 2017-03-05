package com.tsungweiho.intelligentpowersaving.objects;

/**
 * Created by Tsung Wei Ho on 2017/2/22.
 */

public class MyAccountInfo {

    private String email;
    private String name;
    private String imageUrl;
    private String subscription;
    private String ifRequestPermission;

    // Subscription format = 1,1 means subscribe to event and public channels
    public MyAccountInfo(String email, String name, String imageUrl, String subscription) {
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
        this.subscription = subscription;
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

    public String getSubscription() {
        return this.subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }
}
