package com.tsungweiho.intelligentpowersaving.tools;

import android.content.Context;

import com.tsungweiho.intelligentpowersaving.IPowerSaving;

/**
 * Created by tsung on 2018/1/1.
 */

public class ImgResourceMananger {

    private static final ImgResourceMananger instance = new ImgResourceMananger();

    public static ImgResourceMananger getInstance() {
        return instance;
    }

    private ImgResourceMananger() {
    }

    /**
     * Get application context for animation use
     *
     * @return application context
     */
    private Context getContext() {
        return IPowerSaving.getContext();
    }
}
