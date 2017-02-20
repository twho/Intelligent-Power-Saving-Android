package com.tsungweiho.intelligentpowersaving.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tsungweiho.intelligentpowersaving.R;

/**
 * Created by Tsung Wei Ho on 2/3/17.
 */

public class AnimUtilities {
    private static Context context;

    // Animation duration
    private int ANIM_DURATION = 1000;
    private int FAST_ANIM_DURATION = 500;

    public AnimUtilities(Context context) {
        this.context = context;
    }

    public void setllAnimToVisible(final LinearLayout linearLayout) {
        linearLayout.setVisibility(View.VISIBLE);
        Animation am = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        am.setDuration(ANIM_DURATION);
        linearLayout.setAnimation(am);
        am.startNow();
    }

    public void setglAnimToVisible(final GridLayout gridLayout) {
        gridLayout.setVisibility(View.VISIBLE);
        Animation am = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        am.setDuration(ANIM_DURATION);
        gridLayout.setAnimation(am);
        am.startNow();
    }

    public void setflAnimToVisible(final FrameLayout frameLayout) {
        frameLayout.setVisibility(View.VISIBLE);
        Animation am = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        am.setDuration(ANIM_DURATION);
        frameLayout.setAnimation(am);
        am.startNow();
    }

    public void setIconAnimToVisible(ImageView imageView) {
        imageView.setVisibility(View.VISIBLE);
        Animation am = AnimationUtils.loadAnimation(context, R.anim.design_fab_in);
        am.setDuration(ANIM_DURATION);
        imageView.setAnimation(am);
        am.startNow();
    }
}
