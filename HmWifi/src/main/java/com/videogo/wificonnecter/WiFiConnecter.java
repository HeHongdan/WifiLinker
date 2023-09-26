package com.videogo.wificonnecter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
//import com.videogo.privacy.PrivacyMethod;
//import com.videogo.permission.PermissionHelper;
//import com.videogo.util.LogUtil;
//import com.videogo.util.NetworkUtil;
//import com.videogo.util.StringUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.videogo.util.StringUtils;
import com.videogo.util.ThreadManager;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes5.dex */
public class WiFiConnecter {

    public static final int CONNECT_ERROR = 3;
    public static final int COUNTOUT_ERROR = 4;
    public static final int MAX_TRY_COUNT = 3;
    public static final int PARAM_ERROR = 1;
    public static final int PASSWORD_ERROR = 2;
    public static final int SCAN_ERROR = 4;
    public static final int SECURITY_EAP = 3;
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_WEP = 1;
    public static final String TAG = "WiFiConnecter";
    public static final int WIFI_RESCAN_INTERVAL_MS = 5000;
    public static boolean bySsidIgnoreCase = false;
    public static String connectSsid = null;
    public static boolean isActiveScan = false;
    public static boolean isStrongConnectMode = false;
    public boolean isRegistered;
    public Activity mActivity;
    public ActionListener mListener;
    public String mPassword;
    public final BroadcastReceiver mReceiver;
    public final Scanner mScanner;
    public String mSsid;
    public WifiManager mWifiManager;
    public int linkWifiResult = -1;
    public NetworkInfo.DetailedState detailedState = NetworkInfo.DetailedState.IDLE;
    public final IntentFilter mFilter = new IntentFilter();

    /* loaded from: classes5.dex */
    public interface ActionListener {
        void onFailure(int i);

        void onFinished(boolean z);

        void onStarted(String str);

        void onSuccess(WifiInfo wifiInfo);
    }

    @SuppressLint({"HandlerLeak"})
    /* loaded from: classes5.dex */
    public class Scanner extends Handler {
        public int mRetry;

        public Scanner() {
            this.mRetry = 0;
        }

        public void forceScan() {
            removeMessages(0);
            sendEmptyMessage(0);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = this.mRetry;
            boolean z = false;
            if (i < 3) {
                this.mRetry = i + 1;
                boolean unused = WiFiConnecter.isActiveScan = true;
//                boolean isWifiEnabled = PrivacyMethod.isWifiEnabled(WiFiConnecter.this.mWifiManager);
                @SuppressLint("MissingPermission") boolean isWifiEnabled = NetworkUtils.getWifiEnabled();
                if (!isWifiEnabled) {
                    try {
                        isWifiEnabled = WiFiConnecter.this.mWifiManager.setWifiEnabled(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                String str = WiFiConnecter.TAG;
                Log.d(str, "setWifiEnabled:" + isWifiEnabled);
                if (isWifiEnabled) {
                    try {
                        z = WiFiConnecter.this.mWifiManager.startScan();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    String str2 = WiFiConnecter.TAG;
                    Log.d(str2, "startScan:" + z);
                }
                WiFiConnecter.this.handleScanResults(true);
                return;
            }
            this.mRetry = 0;
            boolean unused2 = WiFiConnecter.isActiveScan = false;
            if (WiFiConnecter.this.mListener != null) {
                WiFiConnecter.this.mListener.onFailure(WiFiConnecter.this.isPasswordError() ? 2 : 4);
                WiFiConnecter.this.mListener.onFinished(false);
            }
            WiFiConnecter.this.onPause();
        }

        public void pause() {
            this.mRetry = 0;
            boolean unused = WiFiConnecter.isActiveScan = false;
            removeMessages(0);
        }

        public void resume() {
            if (hasMessages(0)) {
                return;
            }
            sendEmptyMessage(0);
        }
    }

    public WiFiConnecter(Activity activity) {
        this.mActivity = activity;
//        this.mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService("wifi");
        this.mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.mFilter.addAction("android.net.wifi.SCAN_RESULTS");
        this.mFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        this.mFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.mFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mReceiver = new BroadcastReceiver() { // from class: com.videogo.wificonnecter.WiFiConnecter.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                WiFiConnecter.this.handleEvent(context, intent);
            }
        };
        bySsidIgnoreCase = true;
        this.mScanner = new Scanner();
    }

    public static void connectWifiConfig(Activity activity) {
        if (activity == null || connectSsid == null) {
            return;
        }
        connectWifiConfigSsid(activity);
    }

    public static void connectWifiConfigSsid(final Context context) {
        ThreadManager.getSinglePool(TAG).execute(new Runnable() { // from class: com.videogo.wificonnecter.WiFiConnecter.4
            /* JADX WARN: Code restructure failed: missing block: B:22:0x004b, code lost:
                r2 = com.videogo.wificonnecter.WiFi.getWifiConfiguration(r1, r3, com.videogo.wificonnecter.WiFi.getScanResultSecurity(r3));
             */
            /* JADX WARN: Code restructure failed: missing block: B:23:0x0053, code lost:
                if (r2 == null) goto L24;
             */
            /* JADX WARN: Code restructure failed: missing block: B:25:0x005a, code lost:
                if (com.videogo.wificonnecter.WiFi.connectToConfiguredNetwork(r1, r2, true) == false) goto L24;
             */
            /* JADX WARN: Code restructure failed: missing block: B:27:0x005d, code lost:
                android.util.Log.d(com.videogo.wificonnecter.WiFiConnecter.TAG, "connectWifiConfig fail");
             */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public void run() {
                /*
                    r6 = this;
                    r0 = 1000(0x3e8, double:4.94E-321)
                    java.lang.Thread.sleep(r0)     // Catch: java.lang.InterruptedException -> L6
                    goto La
                L6:
                    r0 = move-exception
                    r0.printStackTrace()
                La:
                    r0 = 0
                    android.content.Context r1 = r1     // Catch: java.lang.Exception -> L67
                    java.lang.String r2 = "wifi"
                    java.lang.Object r1 = r1.getSystemService(r2)     // Catch: java.lang.Exception -> L67
                    android.net.wifi.WifiManager r1 = (android.net.wifi.WifiManager) r1     // Catch: java.lang.Exception -> L67
                    boolean r2 = com.videogo.privacy.PrivacyMethod.isWifiEnabled(r1)     // Catch: java.lang.Exception -> L67
                    if (r2 == 0) goto L6b
                    android.content.Context r2 = r1     // Catch: java.lang.Exception -> L67
                    boolean r2 = com.videogo.util.NetworkUtil.isNetworkAvailable(r2)     // Catch: java.lang.Exception -> L67
                    if (r2 != 0) goto L6b
                    java.util.List r2 = com.videogo.privacy.PrivacyMethod.getScanResults(r1)     // Catch: java.lang.Exception -> L28
                    goto L2d
                L28:
                    r2 = move-exception
                    r2.printStackTrace()     // Catch: java.lang.Exception -> L67
                    r2 = r0
                L2d:
                    if (r2 == 0) goto L6b
                    java.util.Iterator r2 = r2.iterator()     // Catch: java.lang.Exception -> L67
                L33:
                    boolean r3 = r2.hasNext()     // Catch: java.lang.Exception -> L67
                    if (r3 == 0) goto L6b
                    java.lang.Object r3 = r2.next()     // Catch: java.lang.Exception -> L67
                    android.net.wifi.ScanResult r3 = (android.net.wifi.ScanResult) r3     // Catch: java.lang.Exception -> L67
                    java.lang.String r4 = com.videogo.wificonnecter.WiFiConnecter.access$1400()     // Catch: java.lang.Exception -> L67
                    java.lang.String r5 = r3.SSID     // Catch: java.lang.Exception -> L67
                    boolean r4 = com.videogo.wificonnecter.WiFiConnecter.access$700(r4, r5)     // Catch: java.lang.Exception -> L67
                    if (r4 == 0) goto L33
                    java.lang.String r2 = com.videogo.wificonnecter.WiFi.getScanResultSecurity(r3)     // Catch: java.lang.Exception -> L67
                    android.net.wifi.WifiConfiguration r2 = com.videogo.wificonnecter.WiFi.getWifiConfiguration(r1, r3, r2)     // Catch: java.lang.Exception -> L67
                    if (r2 == 0) goto L5d
                    r3 = 1
                    boolean r1 = com.videogo.wificonnecter.WiFi.connectToConfiguredNetwork(r1, r2, r3)     // Catch: java.lang.Exception -> L67
                    if (r1 == 0) goto L5d
                    goto L6b
                L5d:
                    java.lang.String r1 = com.videogo.wificonnecter.WiFiConnecter.access$200()     // Catch: java.lang.Exception -> L67
                    java.lang.String r2 = "connectWifiConfig fail"
                    android.util.Log.d(r1, r2)     // Catch: java.lang.Exception -> L67
                    goto L6b
                L67:
                    r1 = move-exception
                    r1.printStackTrace()
                L6b:
                    com.videogo.wificonnecter.WiFiConnecter.access$1402(r0)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.videogo.wificonnecter.WiFiConnecter.AnonymousClass4.run():void");
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint("MissingPermission")
    public void connectWifiScan(String ssid, String pwd, ActionListener actionListener) {
        WifiInfo connectionInfo;
        this.mListener = actionListener;
        this.mSsid = ssid;
        this.mPassword = pwd;
        if (TextUtils.isEmpty(this.mSsid)) {
            if (actionListener != null) {
                actionListener.onFailure(1);
                actionListener.onFinished(false);
                return;
            }
            return;
        }
        if (actionListener != null) {
            actionListener.onStarted(ssid);
        }
//        if (PrivacyMethod.isWifiEnabled(this.mWifiManager)
        if (NetworkUtils.getWifiEnabled()
//                && (connectionInfo = PrivacyMethod.getConnectionInfo(this.mWifiManager)) != null
                && (connectionInfo = mWifiManager.getConnectionInfo()) != null
//                && NetworkUtil.isWifi(this.mActivity)
                && NetworkUtils.getWifiEnabled()
//                && PrivacyMethod.getSSID(connectionInfo) != null) {
                && NetworkUtils.getSSID() != null) {
//            if (isSsidEquals(this.mSsid, PrivacyMethod.getSSID(connectionInfo))) {
            if (isSsidEquals(this.mSsid, NetworkUtils.getSSID())) {
                connectSsid = null;
                if (actionListener != null) {
                    actionListener.onSuccess(connectionInfo);
                    actionListener.onFinished(true);
                    return;
                }
                return;
            }
            connectSsid = StringUtils.convertToNoQuotedString(NetworkUtils.getSSID());
        }
        try {
            if (!this.isRegistered) {
                this.mActivity.registerReceiver(this.mReceiver, this.mFilter);
                this.isRegistered = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mScanner.forceScan();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint("MissingPermission")
    public void handleEvent(Context context, Intent intent) {
        WifiInfo connectionInfo;
        String action = intent.getAction();
        if ("android.net.wifi.SCAN_RESULTS".equals(action) && isActiveScan) {
            handleScanResults(false);
        } else if ("android.net.wifi.STATE_CHANGE".equals(action)) {
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            if (networkInfo != null) {
                String str = TAG;
                Log.d(str, "NETWORK_STATE_CHANGED_ACTION state:" + networkInfo.getState());
                this.detailedState = networkInfo.getDetailedState();
                String str2 = TAG;
                Log.d(str2, "NETWORK_STATE_CHANGED_ACTION DetailedState:" + this.detailedState);
            }
        } else if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
//            if (!NetworkUtil.isWifi(this.mActivity) || (connectionInfo = PrivacyMethod.getConnectionInfo(this.mWifiManager)) == null || PrivacyMethod.getSSID(connectionInfo) == null || !isSsidEquals(this.mSsid, PrivacyMethod.getSSID(connectionInfo))) {
            if (!NetworkUtils.getWifiEnabled() || (connectionInfo = mWifiManager.getConnectionInfo()) == null || NetworkUtils.getSSID() == null || !isSsidEquals(this.mSsid, NetworkUtils.getSSID())) {
                return;
            }
            isActiveScan = false;
            ActionListener actionListener = this.mListener;
            if (actionListener != null) {
                actionListener.onSuccess(connectionInfo);
                this.mListener.onFinished(true);
            }
            onPause();
        } else if (action.equals("android.net.wifi.supplicant.STATE_CHANGE")) {
            SupplicantState supplicantState = SupplicantState.INVALID;
            try {
                String str3 = TAG;
                Log.d(str3, "SUPPLICANT_STATE_CHANGED_ACTION state:" + ((SupplicantState) intent.getParcelableExtra("newState")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.linkWifiResult = intent.getIntExtra("supplicantError", -1);
            String str4 = TAG;
            Log.d(str4, "SUPPLICANT_STATE_CHANGED_ACTION linkWifiResult:" + this.linkWifiResult);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScanResults(final boolean z) {
        ThreadManager.getSinglePool(TAG).execute(new Runnable() { // from class: com.videogo.wificonnecter.WiFiConnecter.3
            @SuppressLint("MissingPermission")
            @Override // java.lang.Runnable
            public void run() {
                boolean z2;
                if (WiFiConnecter.isActiveScan) {
                    List<ScanResult> list = null;
                    try {
//                        list = PrivacyMethod.getScanResults(WiFiConnecter.this.mWifiManager);
                        NetworkUtils.WifiScanResults wifiScanResult = NetworkUtils.getWifiScanResult();
                        list = wifiScanResult.getAllResults();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (list != null) {
                        Iterator<ScanResult> it = list.iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            ScanResult next = it.next();
                            if (!WiFiConnecter.isActiveScan) {
                                return;
                            }
                            if (WiFiConnecter.isSsidEquals(WiFiConnecter.this.mSsid, next.SSID)) {
                                try {
                                    z2 = WiFiConnecter.isStrongConnectMode ? WiFi.configWifiInfo(WiFiConnecter.this.mWifiManager, WiFiConnecter.this.mSsid, WiFiConnecter.this.mPassword, next) : WiFi.connectToNewNetwork(WiFiConnecter.this.mWifiManager, next, WiFiConnecter.this.mPassword);
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                    z2 = false;
                                }
                                if (!z2) {
                                    WiFiConnecter.this.mScanner.post(new Runnable() { // from class: com.videogo.wificonnecter.WiFiConnecter.3.1
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            if (WiFiConnecter.this.mListener != null) {
                                                WiFiConnecter.this.mListener.onFailure(WiFiConnecter.this.isPasswordError() ? 2 : 3);
                                                WiFiConnecter.this.mListener.onFinished(false);
                                            }
                                            WiFiConnecter.this.onPause();
                                        }
                                    });
                                }
                            }
                        }
                    }
                    if (z) {
                        WiFiConnecter.this.mScanner.sendEmptyMessageDelayed(0, 5000L);
                    }
                }
            }
        });
    }

    public static boolean isIsStrongConnectMode() {
        return isStrongConnectMode;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPasswordError() {
        return this.linkWifiResult == 1 || this.detailedState == NetworkInfo.DetailedState.AUTHENTICATING;
    }

    public static boolean isSsidEquals(String str, String str2) {
        return WiFi.isSsidEquals(str, str2, bySsidIgnoreCase);
    }

    public static void setIsStrongConnectMode(boolean z) {
        isStrongConnectMode = z;
    }

    /**
     * TODO 连接。
     *
     * @param str
     * @param str2
     * @param actionListener
     */
    public void connect(final String str, final String str2, final ActionListener actionListener) {
        final String NEARBY_WIFI_DEVICES = "android.permission.NEARBY_WIFI_DEVICES";
        final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";

        if (Build.VERSION.SDK_INT >= 23) {
            if (Build.VERSION.SDK_INT >= 33) {
                if (!PermissionUtils.isGranted(NEARBY_WIFI_DEVICES)) {
                    PermissionUtils.permission(NEARBY_WIFI_DEVICES)
                            .callback(new PermissionUtils.SimpleCallback() {

                                @Override
                                public void onGranted() {
                                    PermissionUtils.permission(ACCESS_FINE_LOCATION)
                                            .callback(new PermissionUtils.SimpleCallback() {

                                                @Override
                                                public void onGranted() {
                                                    connectWifiScan(str, str2, actionListener);
                                                }

                                                @Override
                                                public void onDenied() {//拒绝
                                                    LogUtils.e("【权限】拒绝= ACCESS_FINE_LOCATION");
                                                }
                                            })
                                            .request();
                                }

                                @Override
                                public void onDenied() {//拒绝
                                    LogUtils.e("【权限】拒绝= NEARBY_WIFI_DEVICES");
                                }
                            })
                            .request();
                }
            } else {
                PermissionUtils.permission(ACCESS_FINE_LOCATION)
                        .callback(new PermissionUtils.SimpleCallback() {

                            @Override
                            public void onGranted() {
                                connectWifiScan(str, str2, actionListener);
                            }

                            @Override
                            public void onDenied() {//拒绝
                                LogUtils.e("【权限】拒绝= ACCESS_FINE_LOCATION");
                            }
                        })
                        .request();
            }

        } else {
            connectWifiScan(str, str2, actionListener);
        }

//        PermissionHelper.requestNearbyWiFiDevices(this.mActivity, new PermissionHelper.PermissionListener() { // from class: com.videogo.wificonnecter.WiFiConnecter.2
//                @Override // com.videogo.permission.PermissionHelper.PermissionListener
//                public void permissionCancel(int i) {
//                    String str3 = WiFiConnecter.TAG;
//                    LogUtils.d(str3, "permissionCancel " + i);
//                    WiFiConnecter.this.connectWifiScan(str, str2, actionListener);
//                }
//
//                @Override // com.joker.api.wrapper.ListenerWrapper.PermissionRequestListener
//                public void permissionDenied(int i) {
//                    String str3 = WiFiConnecter.TAG;
//                    LogUtils.d(str3, "permissionDenied " + i);
//                    WiFiConnecter.this.connectWifiScan(str, str2, actionListener);
//                }
//
//                @Override // com.joker.api.wrapper.ListenerWrapper.PermissionRequestListener
//                public void permissionGranted(int i) {
//                    String str3 = WiFiConnecter.TAG;
//                    LogUtils.d(str3, "permissionGranted " + i);
//                    WiFiConnecter.this.connectWifiScan(str, str2, actionListener);
//                }
//
//                @Override // com.joker.api.wrapper.ListenerWrapper.PermissionRequestListener
//                public void permissionRationale(int i) {
//                    String str3 = WiFiConnecter.TAG;
//                    LogUtils.d(str3, "permissionRationale " + i);
//                }
//
//                @Override // com.videogo.permission.PermissionHelper.PermissionListener
//                public void permissionSetting(int i) {
//                    String str3 = WiFiConnecter.TAG;
//                    LogUtils.d(str3, "permissionSetting " + i);
//                }
//            });
//        } else {
//            connectWifiScan(str, str2, actionListener);
//        }
    }

    /**
     * TODO 连接。
     *
     * @param ssid
     * @param pwd
     * @param actionListener
     */
    public void connectStrongMode(String ssid, String pwd, ActionListener actionListener) {
        isStrongConnectMode = true;
        connectWifiScan(ssid, pwd, actionListener);
    }

    public void destroy() {
        this.mListener = null;
        onPause();
    }

    public void onPause() {
        try {
            if (this.isRegistered) {
                this.mActivity.unregisterReceiver(this.mReceiver);
                this.isRegistered = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mScanner.pause();
        ThreadManager.getSinglePool(TAG).stop();
    }

    public void onResume() {
        try {
            if (!this.isRegistered) {
                this.mActivity.registerReceiver(this.mReceiver, this.mFilter);
                this.isRegistered = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mScanner.resume();
    }

}