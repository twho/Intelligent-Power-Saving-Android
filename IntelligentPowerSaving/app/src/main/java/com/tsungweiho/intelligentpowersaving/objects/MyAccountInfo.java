package com.tsungweiho.intelligentpowersaving.objects;

import android.util.Log;

/**
 * Created by Tsung Wei Ho on 12/22/2017.
 */

public class MyAccountInfo {

    private String email;
    private String name;
    private String imageUrl;
    private String subscription;
    private String isRequestPermission;

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

    public Boolean[] getSubscriptionBools() {
        return new Boolean[]{this.subscription.split(",")[0].equals("1"), this.subscription.split(",")[1].equals("1")};
    }

    public void setSubscriptionBools(int index, Boolean subscription) {
        String strSub = subscription ? "1" : "0";

        if (index == 0) {
            this.subscription = strSub + "," + this.subscription.split(",")[1];
        } else if (index == 1) {
            this.subscription = this.subscription.split(",")[0] + "," + strSub;
        }
    }
}
