package com.blankj.utilcode.util;

import android.app.Activity;
import android.os.Build;

public class ActivityUtils {

    /**
     * Return the top activity in activity's stack.
     *
     * @return the top activity in activity's stack
     */
    public static Activity getTopActivity() {
        return UtilsBridge.getTopActivity();
    }

    /**
     * Return whether the activity is alive.
     *
     * @param activity The activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isActivityAlive(final Activity activity) {
        return activity != null && !activity.isFinishing()
                && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !activity.isDestroyed());
    }

}
