package com.blankj.utilcode.util;

import android.app.Activity;
import android.app.Application;

final class UtilsBridge {

    static void init(Application app) {
        UtilsActivityLifecycleImpl.INSTANCE.init(app);
    }

    static void unInit(Application app) {
        UtilsActivityLifecycleImpl.INSTANCE.unInit(app);
    }

//    static void preLoad() {
//        preLoad(AdaptScreenUtils.getPreLoadRunnable());
//    }

    static Activity getTopActivity() {
        return UtilsActivityLifecycleImpl.INSTANCE.getTopActivity();
    }

    static boolean isActivityAlive(final Activity activity) {
        return ActivityUtils.isActivityAlive(activity);
    }


    static Application getApplicationByReflect() {
        return UtilsActivityLifecycleImpl.INSTANCE.getApplicationByReflect();
    }

    static String getCurrentProcessName() {
        return ProcessUtils.getCurrentProcessName();
    }


}
