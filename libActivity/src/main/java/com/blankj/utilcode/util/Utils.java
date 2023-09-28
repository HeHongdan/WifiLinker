package com.blankj.utilcode.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

final class Utils {

    @SuppressLint("StaticFieldLeak")
    private static Application sApp;

    /**
     * Init utils.
     * <p>Init it in the class of UtilsFileProvider.</p>
     *
     * @param app application
     */
    public static void init(final Application app) {
        if (app == null) {
            Log.e("Utils", "app is null.");
            return;
        }
        if (sApp == null) {
            sApp = app;
            UtilsBridge.init(sApp);
            //UtilsBridge.preLoad();
            return;
        }
        if (sApp.equals(app)) return;
        UtilsBridge.unInit(sApp);
        sApp = app;
        UtilsBridge.init(sApp);
    }

    /**
     * Return the Application object.
     * <p>Main process get app by UtilsFileProvider,
     * and other process get app by reflect.</p>
     *
     * @return the Application object
     */
    public static Application getApp() {
        if (sApp != null) return sApp;
        init(UtilsBridge.getApplicationByReflect());
        if (sApp == null) throw new NullPointerException("reflect failed.");
        Log.i("Utils", UtilsBridge.getCurrentProcessName() + " reflect app success.");
        return sApp;
    }

    public interface OnAppStatusChangedListener {
        void onForeground(Activity activity);

        void onBackground(Activity activity);
    }

    public static class ActivityLifecycleCallbacks {

        public void onActivityCreated(@NonNull Activity activity) {/**/}

        public void onActivityStarted(@NonNull Activity activity) {/**/}

        public void onActivityResumed(@NonNull Activity activity) {/**/}

        public void onActivityPaused(@NonNull Activity activity) {/**/}

        public void onActivityStopped(@NonNull Activity activity) {/**/}

        public void onActivityDestroyed(@NonNull Activity activity) {/**/}

        public void onLifecycleChanged(@NonNull Activity activity, Lifecycle.Event event) {/**/}
    }

}
