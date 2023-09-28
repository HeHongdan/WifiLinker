package com.blankj.utilcode.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

final class UtilsActivityLifecycleImpl implements Application.ActivityLifecycleCallbacks {
    static final UtilsActivityLifecycleImpl INSTANCE = new UtilsActivityLifecycleImpl();

    private final LinkedList<Activity> mActivityList = new LinkedList<>();
    private final List<Utils.OnAppStatusChangedListener> mStatusListeners               = new CopyOnWriteArrayList<>();
    private final Map<Activity, List<Utils.ActivityLifecycleCallbacks>> mActivityLifecycleCallbacksMap = new ConcurrentHashMap<>();
    private static final Activity STUB = new Activity();

    private boolean mIsBackground    = false;
    private int     mForegroundCount = 0;
    private int     mConfigCount     = 0;


    void init(Application app) {
        app.registerActivityLifecycleCallbacks(this);
    }

    void unInit(Application app) {
        mActivityList.clear();
        app.unregisterActivityLifecycleCallbacks(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // lifecycle start
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (mActivityList.size() == 0) {
            postStatus(activity, true);
        }

        setTopActivity(activity);
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_CREATE);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (!mIsBackground) {
            setTopActivity(activity);
        }
        if (mConfigCount < 0) {
            ++mConfigCount;
        } else {
            ++mForegroundCount;
        }
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_START);
    }


    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        setTopActivity(activity);
        if (mIsBackground) {
            mIsBackground = false;
            postStatus(activity, true);
        }
        //processHideSoftInputOnActivityDestroy(activity, false);
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_RESUME);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_PAUSE);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if (activity.isChangingConfigurations()) {
            --mConfigCount;
        } else {
            --mForegroundCount;
            if (mForegroundCount <= 0) {
                mIsBackground = true;
                postStatus(activity, false);
            }
        }
        //processHideSoftInputOnActivityDestroy(activity, true);
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_STOP);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        mActivityList.remove(activity);
        //UtilsBridge.fixSoftInputLeaks(activity);
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_DESTROY);
    }
    ///////////////////////////////////////////////////////////////////////////
    // lifecycle end
    ///////////////////////////////////////////////////////////////////////////


    private void setTopActivity(final Activity activity) {
        if (mActivityList.contains(activity)) {
            if (!mActivityList.getFirst().equals(activity)) {
                mActivityList.remove(activity);
                mActivityList.addFirst(activity);
            }
        } else {
            mActivityList.addFirst(activity);
        }
    }

    private void postStatus(final Activity activity, final boolean isForeground) {
        if (mStatusListeners.isEmpty()) return;
        for (Utils.OnAppStatusChangedListener statusListener : mStatusListeners) {
            if (isForeground) {
                statusListener.onForeground(activity);
            } else {
                statusListener.onBackground(activity);
            }
        }
    }

    private void consumeActivityLifecycleCallbacks(Activity activity, Lifecycle.Event event) {
        consumeLifecycle(activity, event, mActivityLifecycleCallbacksMap.get(activity));
        consumeLifecycle(activity, event, mActivityLifecycleCallbacksMap.get(STUB));
    }

    private void consumeLifecycle(Activity activity, Lifecycle.Event event, List<Utils.ActivityLifecycleCallbacks> listeners) {
        if (listeners == null) return;
        for (Utils.ActivityLifecycleCallbacks listener : listeners) {
            listener.onLifecycleChanged(activity, event);
            if (event.equals(Lifecycle.Event.ON_CREATE)) {
                listener.onActivityCreated(activity);
            } else if (event.equals(Lifecycle.Event.ON_START)) {
                listener.onActivityStarted(activity);
            } else if (event.equals(Lifecycle.Event.ON_RESUME)) {
                listener.onActivityResumed(activity);
            } else if (event.equals(Lifecycle.Event.ON_PAUSE)) {
                listener.onActivityPaused(activity);
            } else if (event.equals(Lifecycle.Event.ON_STOP)) {
                listener.onActivityStopped(activity);
            } else if (event.equals(Lifecycle.Event.ON_DESTROY)) {
                listener.onActivityDestroyed(activity);
            }
        }
        if (event.equals(Lifecycle.Event.ON_DESTROY)) {
            mActivityLifecycleCallbacksMap.remove(activity);
        }
    }


    Activity getTopActivity() {
        List<Activity> activityList = getActivityList();
        for (Activity activity : activityList) {
            if (!UtilsBridge.isActivityAlive(activity)) {
                continue;
            }
            return activity;
        }
        return null;
    }

    List<Activity> getActivityList() {
        if (!mActivityList.isEmpty()) {
            return new LinkedList<>(mActivityList);
        }
        List<Activity> reflectActivities = getActivitiesByReflect();
        mActivityList.addAll(reflectActivities);
        return new LinkedList<>(mActivityList);
    }


    /**
     * @return the activities which topActivity is first position
     */
    private List<Activity> getActivitiesByReflect() {
        LinkedList<Activity> list = new LinkedList<>();
        Activity topActivity = null;
        try {
            Object activityThread = getActivityThread();
            if (activityThread == null) return list;
            Field mActivitiesField = activityThread.getClass().getDeclaredField("mActivities");
            mActivitiesField.setAccessible(true);
            Object mActivities = mActivitiesField.get(activityThread);
            if (!(mActivities instanceof Map)) {
                return list;
            }
            Map<Object, Object> binder_activityClientRecord_map = (Map<Object, Object>) mActivities;
            for (Object activityRecord : binder_activityClientRecord_map.values()) {
                Class activityClientRecordClass = activityRecord.getClass();
                Field activityField = activityClientRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                if (topActivity == null) {
                    Field pausedField = activityClientRecordClass.getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (!pausedField.getBoolean(activityRecord)) {
                        topActivity = activity;
                    } else {
                        list.addFirst(activity);
                    }
                } else {
                    list.addFirst(activity);
                }
            }
        } catch (Exception e) {
            Log.e("UtilsActivityLifecycle", "getActivitiesByReflect: " + e.getMessage());
        }
        if (topActivity != null) {
            list.addFirst(topActivity);
        }
        return list;
    }

    private Object getActivityThread() {
        Object activityThread = getActivityThreadInActivityThreadStaticField();
        if (activityThread != null) return activityThread;
        return getActivityThreadInActivityThreadStaticMethod();
    }

    private Object getActivityThreadInActivityThreadStaticField() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            return sCurrentActivityThreadField.get(null);
        } catch (Exception e) {
            Log.e("UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticField: " + e.getMessage());
            return null;
        }
    }

    private Object getActivityThreadInActivityThreadStaticMethod() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            return activityThreadClass.getMethod("currentActivityThread").invoke(null);
        } catch (Exception e) {
            Log.e("UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticMethod: " + e.getMessage());
            return null;
        }
    }


    Application getApplicationByReflect() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object thread = getActivityThread();
            if (thread == null) return null;
            Object app = activityThreadClass.getMethod("getApplication").invoke(thread);
            if (app == null) return null;
            return (Application) app;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
