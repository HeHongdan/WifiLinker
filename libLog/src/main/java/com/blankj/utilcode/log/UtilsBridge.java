package com.blankj.utilcode.log;

import android.app.Application;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

final class UtilsBridge {

    static void init(Application app) {
        UtilsActivityLifecycleImpl.INSTANCE.init(app);
    }

    static void unInit(Application app) {
        UtilsActivityLifecycleImpl.INSTANCE.unInit(app);
    }

    static void preLoad() {
        //preLoad(AdaptScreenUtils.getPreLoadRunnable());
    }

    static String formatJson(String json) {
        return JsonUtils.formatJson(json);
    }

    static Gson getGson4LogUtils() {
        return GsonUtils.getGson4LogUtils();
    }

    static String getFullStackTrace(Throwable throwable) {
        return ThrowableUtils.getFullStackTrace(throwable);
    }

    static boolean isSpace(final String s) {
        return StringUtils.isSpace(s);
    }


    static boolean isSDCardEnableByEnvironment() {
        return SDCardUtils.isSDCardEnableByEnvironment();
    }

    static boolean createOrExistsDir(final File file) {
        return FileUtils.createOrExistsDir(file);
    }

    static File getFileByPath(final String filePath) {
        return FileUtils.getFileByPath(filePath);
    }

    static boolean createOrExistsFile(final File file) {
        return FileUtils.createOrExistsFile(file);
    }

    static boolean writeFileFromString(final String filePath, final String content, final boolean append) {
        return FileIOUtils.writeFileFromString(filePath, content, append);
    }

    static String getCurrentProcessName() {
        return ProcessUtils.getCurrentProcessName();
    }



    static final class FileHead {

        private String                        mName;
        private LinkedHashMap<String, String> mFirst = new LinkedHashMap<>();
        private LinkedHashMap<String, String> mLast  = new LinkedHashMap<>();

        FileHead(String name) {
            mName = name;
        }

        void addFirst(String key, String value) {
            append2Host(mFirst, key, value);
        }

        void append(Map<String, String> extra) {
            append2Host(mLast, extra);
        }

        void append(String key, String value) {
            append2Host(mLast, key, value);
        }

        private void append2Host(Map<String, String> host, Map<String, String> extra) {
            if (extra == null || extra.isEmpty()) {
                return;
            }
            for (Map.Entry<String, String> entry : extra.entrySet()) {
                append2Host(host, entry.getKey(), entry.getValue());
            }
        }

        private void append2Host(Map<String, String> host, String key, String value) {
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                return;
            }
            int delta = 19 - key.length(); // 19 is length of "Device Manufacturer"
            if (delta > 0) {
                key = key + "                   ".substring(0, delta);
            }
            host.put(key, value);
        }

        public String getAppended() {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : mLast.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            String border = "************* " + mName + " Head ****************\n";
            sb.append(border);
            for (Map.Entry<String, String> entry : mFirst.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }

            sb.append("Rom Info           : ").append(RomUtils.getRomInfo()).append("\n");
            sb.append("Device Manufacturer: ").append(Build.MANUFACTURER).append("\n");
            sb.append("Device Model       : ").append(Build.MODEL).append("\n");
            sb.append("Android Version    : ").append(Build.VERSION.RELEASE).append("\n");
            sb.append("Android SDK        : ").append(Build.VERSION.SDK_INT).append("\n");
            sb.append("App VersionName    : ").append(AppUtils.getAppVersionName()).append("\n");
            sb.append("App VersionCode    : ").append(AppUtils.getAppVersionCode()).append("\n");

            sb.append(getAppended());
            return sb.append(border).append("\n").toString();
        }
    }

    static Application getApplicationByReflect() {
        return UtilsActivityLifecycleImpl.INSTANCE.getApplicationByReflect();
    }

}
