package com.tsungweiho.intelligentpowersaving.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.R;

/**
 * Class to perform all animation tasks in app
 *
 * This singleton class consist of all animation tasks used in the app
 *
 * @author Tsung Wei Ho
 * @version 0203.2017
 * @since 1.0.0
 */
public class AnimUtils {
    // Animation duration
    public int ANIM_DURATION = 1000;
    public int MID_ANIM_DURATION = 500;
    public int FAST_ANIM_DURATION = 250;

    private static final AnimUtils ourInstance = new AnimUtils();

    public static AnimUtils getInstance() {
        return ourInstance;
    }

    private AnimUtils() {}

    /**
     * Get application context for animation use
     *
     * @return application context
     */
    private Context getContext() {
        return IPowerSaving.getContext();
    }

    public void fadeinToVisible(View view, int duration) {
        view.setVisibility(View.VISIBLE);
        Animation am = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        am.setDuration(duration);
        view.setAnimation(am);
        am.startNow();
    }

    public void slideUpToVisible(View view, int duration) {
        view.setVisibility(View.VISIBLE);
        Animation am = AnimationUtils.loadAnimation(getContext(), R.anim.design_bottom_sheet_slide_in);
        am.setDuration(duration);
        view.setAnimation(am);
        am.startNow();
    }

    public void slideDown(final View view, int duration) {
        Animation am = AnimationUtils.loadAnimation(getContext(), R.anim.design_bottom_sheet_slide_out);
        am.setDuration(duration);
        am.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.setAnimation(am);
        am.startNow();
    }

    public void rotateToIcon(ImageButton imageButton, int icon) {
        imageButton.setImageResource(icon);
        Animation am = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        am.setDuration(FAST_ANIM_DURATION);
        imageButton.setAnimation(am);
        am.startNow();
    }
}
