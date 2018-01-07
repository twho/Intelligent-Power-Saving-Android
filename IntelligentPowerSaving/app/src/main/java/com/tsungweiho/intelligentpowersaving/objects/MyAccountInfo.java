package com.tsungweiho.intelligentpowersaving.objects;

/**
 * Object class to store user account information
 * <p>
 * This class is used to store user account information
 *
 * @author Tsung Wei Ho
 * @version 1222.2017
 * @since 1.0.0
 */
public class MyAccountInfo {

    private String uid;
    private String email;
    private String name;
    private String imageUrl;
    private String subscription;

    /**
     * MyAccountInfo constructor
     *
     * @param uid          the uid of the user from UUID
     * @param email        the email of the user
     * @param name         the display name of the user
     * @param imageUrl     the url resource of user profile image
     * @param subscription the status of subscription of the channels in the format of (1,1), where 1 represents subscribe and 0 represents not subscribe
     */
    public MyAccountInfo(String uid, String email, String name, String imageUrl, String subscription) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
        this.subscription = subscription;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    /**
     * Get subscription status as a boolean array
     *
     * @return an array that stores values of subscription of channels
     */
    public Boolean[] getSubscriptionBools() {
        return new Boolean[]{this.subscription.split(",")[0].equals("1"), this.subscription.split(",")[1].equals("1")};
    }

    /**
     * Set subscription of the channels
     *
     * @param index        the index of the channels
     * @param subscription the subscription status as boolean
     */
    public void setSubscriptionBools(int index, Boolean subscription) {
        String strSub = subscription ? "1" : "0";

        if (index == 0) {
            this.subscription = strSub + "," + this.subscription.split(",")[1];
        } else if (index == 1) {
            this.subscription = this.subscription.split(",")[0] + "," + strSub;
        }
    }
}
