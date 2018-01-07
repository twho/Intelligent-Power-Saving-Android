package com.tsungweiho.intelligentpowersaving.tools;

import android.content.Context;

import com.tsungweiho.intelligentpowersaving.IPowerSaving;

/**
 * Class for managing image resource
 * <p>
 * This class is used to manage resource for any use within app
 *
 * @author Tsung Wei Ho
 * @version 0101.2018
 * @since 2.0.0
 */
// TODO unused class, needs to be developed
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
