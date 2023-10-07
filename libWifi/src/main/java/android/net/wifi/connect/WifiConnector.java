package android.net.wifi.connect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.utils.Constant;
import android.net.wifi.utils.StringUtils;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.blankj.utilcode.log.LogUtils;
import com.blankj.utilcode.util.ActivityUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class WifiConnector implements LifecycleObserver {

    private Activity context;
    private WifiManager wifiManager;
    private BroadcastReceiver wifiConnectReceiver;
    private IntentFilter wifiConnectFilter;
    private ConnectListener<WifiConfiguration> listener;
    private static WifiConfiguration wifiConfiguration;

    /** 第一层锁：保证变量可见性。 */
    private volatile static WifiConnector instance;

    private WifiConnector() {
        context = ActivityUtils.getTopActivity();//最好做下判空
        if (context instanceof ComponentActivity) {
            ((ComponentActivity) context).getLifecycle().addObserver(this);
        }
        LogUtils.d("【扫描WiFi】宿主= " + context);
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiConnector getInstance() {
        //第一次判空：无需每次都加锁，提高性能
        if (instance == null) {
            //第二层锁：保证线程同步
            synchronized (WifiConnector.class) {
                //第二次判空：避免多线程同时执行getInstance()（此）方法，产生多个instance（本类）对象
                if (instance == null) {
                    instance = new WifiConnector();
                }
            }
        }
        return instance;
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void startReceive() {
        LogUtils.d("【连接WiFi】开始...");

        //wifi 状态变化接收广播
        wifiConnectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null) {
                    if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                        int wifState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                        if (wifState != WifiManager.WIFI_STATE_ENABLED) {
                            LogUtils.d("【连接WiFi】Wifi模块启动失败...");

                            if (listener != null) {
                                listener.onFailure("WiFi不可用");
                            }
                        }
                    } else if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                        int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
                        if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                            LogUtils.d("【连接WiFi】密码错误...");

                            if (listener != null) {
                                listener.onFailure("WiFi密码错误");
                            }
                        }
                    } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                        NetworkInfo.DetailedState state = ((NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getDetailedState();
                        if (listener != null) {
                            listener.onState(state);
                        }
                        setDetailedState(state);
                    }
                }
            }
        };
        if (wifiConnectFilter == null) {
            wifiConnectFilter = new IntentFilter();
            wifiConnectFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
            wifiConnectFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            wifiConnectFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
            wifiConnectFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        }
        //注册接收器
        context.registerReceiver(wifiConnectReceiver, wifiConnectFilter);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stopReceive() {
        LogUtils.d("【连接WiFi】停止");

        if (wifiConnectReceiver != null) {
            context.unregisterReceiver(wifiConnectReceiver);
            wifiConnectReceiver = null;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        LogUtils.d("【连接WiFi】销毁");

        //instance = null;
    }


    //----------------------------------------------------------------------------------------
    @SuppressLint({"NewApi", "MissingPermission"})
    public boolean connectWifiConfiguration(String targetSSID, String password) {
        LogUtils.d("【连接WiFi】已经存在= " + targetSSID + "；密码= " + password);

        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();

        if (configuredNetworks != null) {
            for (WifiConfiguration config : configuredNetworks) {
                String ssid = addQuotes(targetSSID);  // Add quotes around SSID
                if (ssid.equals(config.SSID)) {
                    // Disconnect from current network
                    wifiManager.disconnect();

                    // Enable the network
                    int networkId = wifiManager.updateNetwork(config);
                    if (networkId == -1) {
                        networkId = wifiManager.addNetwork(config);
                    }

                    // Reconnect to the specified network
                    if (wifiManager.enableNetwork(networkId, true)) {
                        // Wait for connection to complete
                        boolean connected = waitForConnection(targetSSID, 10*1000);  // 10 seconds timeout
                        if (connected) {
                            LogUtils.w("【连接WiFi】已经存在，连接成功= " + targetSSID + "；密码= " + password);

                            return true;  // Successfully connected, exit loop
                        }
                    }
                }
            }
        }

        LogUtils.w("【连接WiFi】已经存在，连接失败= " + targetSSID + "；密码= " + password);

        return false;  // Configuration not found or failed to connect
    }

    private boolean waitForConnection(String targetSSID, int timeoutMillis) {
        LogUtils.d("【连接WiFi】已经存在= " + targetSSID + "；等待= " + timeoutMillis);

        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null && wifiInfo.getSSID().equals(addQuotes(targetSSID))) {
                return true;  // Successfully connected to the targetSSID
            }

            // Wait for a moment
            //SystemClock.sleep(1000);
            SystemClock.sleep(500);
        }

        return false;  // Timeout or failed to connect
    }

    private String addQuotes(String input) {
        String pj = "\"" + input + "\"";
        LogUtils.d("【连接WiFi】已经存在= " + input + "；拼接= " + pj);

        return pj;
    }

    //----------------------------------------------------------------------------------------
    @SuppressLint("NewApi")
    public void connect(String ssid) {
        connect(ssid, "", "");
    }

    @SuppressLint("NewApi")
    public void connect(String ssid, String password, String capabilities) {
        ScanResult result = new ScanResult();
        result.SSID = ssid;
        result.capabilities = capabilities;
        connect(result, password);
    }

    public void connect(ScanResult result) {
        connect(result, "");
    }

    public void connect(ScanResult result, String password) {
        String ssid = result.BSSID;
        String capabilities = result.capabilities;

        List<WifiConfiguration> havaConfigurations = wifi同名多个配置findWiFiNetworks(context, ssid);
        if (havaConfigurations.isEmpty())  {
            if (StringUtils.containsByUpper(capabilities, Constant.CAPABILITIES_WPA)) {
                LogUtils.w("【WiFi加密】WPA= " + ssid);
                connectToWpa(context, ssid, password);
            } else if (StringUtils.containsByUpper(capabilities, Constant.CAPABILITIES_WEP)) {
                LogUtils.w("【WiFi加密】WEP= " + ssid);
                connectToWep(context, ssid, password);
            }
            else {
                LogUtils.w("【WiFi加密】没有密码= " + ssid);
                connectToOpen(context, ssid);
            }

        } else {
            for (int i = 0; i < havaConfigurations.size(); i++) {
                WifiConfiguration config = havaConfigurations.get(i);

                LogUtils.w("【遍历】对比 SSID= " + config.SSID+ " = " + result.SSID
                        + "\n【遍历】对比 BSSID= " + config.BSSID + " = " + result.BSSID);
                boolean sSsid = config.SSID != null && config.SSID.equals("\"" + result.SSID + "\"");
                boolean sBssid = config.BSSID != null && config.BSSID.equals("\"" + result.BSSID + "\"");
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String cSsid = wifiInfo.getSSID();
                LogUtils.w("【连接WiFi】对比 需要连接= " + result.SSID
                        + "\n配置= " + config.SSID
                        + "\n当前= " + cSsid
                );
                LogUtils.w("【连接WiFi】条件= " + (sSsid && !cSsid.contains(result.SSID))
                        + "\n配置与需要= " + sSsid
                        + "\n当前与需要= " + !cSsid.contains(result.SSID)
                );

                if (sSsid && !cSsid.contains(result.SSID)) {
                    int netId = config.networkId;
                    boolean disconnect = wifiManager.disconnect();
                    boolean enabled = wifiManager.enableNetwork(netId, true);
                    boolean reconnect = wifiManager.reconnect();
                    Log.d("WiFi Connection", "遍历->Connecting to WiFi: " + enabled + " = " +reconnect);
                    //return reconnect;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    //==============================================================================================
    @SuppressLint({"WifiManagerLeak", "WifiManagerPotentialLeak"})
    public static boolean connectToOpen(Context context, String ssid) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        Log.d("WiFi Connection", "Connecting to open WiFi: " + ssid);

        boolean enabled = wifiManager.enableNetwork(netId, true);
        Log.d("WiFi Connection", "WiFi是否连接成功：" + enabled);

        wifiConfiguration = wifiConfig;
        return enabled;
    }

    @SuppressLint({"WifiManagerLeak", "WifiManagerPotentialLeak"})
    public static boolean connectToWpa(Context context, String ssid, String password) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.preSharedKey = "\"" + password + "\"";
        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        Log.d("WiFi Connection", "Connecting to WPA WiFi: " + ssid);

        boolean enabled = wifiManager.enableNetwork(netId, true);
        Log.d("WiFi Connection", "WiFi是否连接成功：" + enabled);

        wifiConfiguration = wifiConfig;
        return enabled;
    }

    @SuppressLint({"WifiManagerLeak", "WifiManagerPotentialLeak"})
    public static boolean connectToWep(Context context, String ssid, String wepKey) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.wepKeys[0] = "\"" + wepKey + "\"";
        wifiConfig.wepTxKeyIndex = 0;
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        Log.d("WiFi Connection", "Connecting to WEP WiFi: " + ssid);

        boolean enabled = wifiManager.enableNetwork(netId, true);
        Log.d("WiFi Connection", "WiFi是否连接成功：" + enabled);

        wifiConfiguration = wifiConfig;
        return enabled;
    }
    //==============================================================================================
    /** 初始化（弱引用）Handler */
    private WeakHandler weakHandler = new WeakHandler(context);

    /**
     * （第一种）弱引用Handler
     */
    private static class WeakHandler extends Handler {
        WeakReference<Activity> weakReference;

        public WeakHandler(Activity activity) {
            weakReference = new WeakReference<Activity>(activity);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            final Activity activity = weakReference.get();
            switch (msg.what) {
                case 1:
                    String obj = (String) msg.obj;
                    String name = activity.getClass().getName();
                    name.equals(obj);
                    break;

                default:break;
            }
        }
    }

    /**
     * 创建&发送消息
     */
    private void sendMessage(){
        Message message = Message.obtain();
        message.what = 1;
        message.arg1 = 1;
        message.arg2 = 2;
        message.obj = "对象";
        weakHandler.sendMessage(message);
    }

    @SuppressLint("MissingPermission")
    public static List<WifiConfiguration> wifi同名多个配置findWiFiNetworks(Context context, String ssid) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        List<WifiConfiguration> matchingNetworks = new ArrayList<>();

        if (configuredNetworks != null) {
            for (WifiConfiguration config : configuredNetworks) {
                if (config.SSID != null && config.SSID.equals("\"" + ssid + "\"")) {
                    matchingNetworks.add(config);
                }
            }
        }

        if (!matchingNetworks.isEmpty()) {
            Log.d("WiFi", "Found " + matchingNetworks.size() + " configurations for WiFi network: " + ssid);
            for (WifiConfiguration config : matchingNetworks) {
                Log.d("WiFi", "SSID: " + config.SSID + ", Network ID: " + config.networkId);
            }
        } else {
            Log.d("WiFi", "No configurations found for WiFi network: " + ssid);
        }

        return matchingNetworks;
    }

    /**
     * wifi是否已配置过。
     *
     * @param context 上下文。
     * @param ssid 需要检查的WiFi。
     * @return 已存在配置的Id。
     */
    @SuppressLint({"WifiManagerLeak", "MissingPermission"})
    public static int wifi是否已配置过_getExistingNetworkId(Context context, String ssid) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration config : configuredNetworks) {
                if (config.SSID != null && config.SSID.equals("\"" + ssid + "\"")) {
                    return config.networkId;
                }
            }
        }

        return -1;
    }

    public static boolean wifi忘记已配置过forgetWiFiNetwork(Context context, String ssid) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        @SuppressLint("MissingPermission") List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration config : configuredNetworks) {
                if (config.SSID != null && config.SSID.equals("\"" + ssid + "\"")) {
                    wifiManager.removeNetwork(config.networkId);
                    boolean b = wifiManager.saveConfiguration();
                    Log.d("WiFi", "Forgot WiFi network: " + b);
                    return b;
                }
            }
        }

        return false;
    }


    public WifiConnector setListener(ConnectListener<WifiConfiguration> listener) {
        this.listener = listener;
        return this;
    }

//        mWifiSearchBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {// 扫描结果改表
//                    mScanResultList = WifiAutoConnectManager.getScanResults();
//                    if (onWifiScanListener != null) {
//                        onWifiScanListener.onScan(mScanResultList);
//                    }
//                }
//            }
//        };
//        mWifiSearchIntentFilter = new IntentFilter();
//        mWifiSearchIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        mWifiSearchIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        mWifiSearchIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
//
//        //wifi 状态变化接收广播
//        wifiConnectReceiver = new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
//                    int wifState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
//                    if (wifState != WifiManager.WIFI_STATE_ENABLED) {
//                        errorLogUtils.d("【连接WiFi】Wifi模块启动失败");
//                        if (onWifiConnectStatusChangeListener != null) {
//                            onWifiConnectStatusChangeListener.onStatusChange(false, ERROR_DEVICE_NOT_HAVE_WIFI);
//                        }
//                    }
//                } else if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
//                    int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
//                    if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
//                        errorLogUtils.d("【连接WiFi】密码错误");
//                        if (onWifiConnectStatusChangeListener != null) {
//                            onWifiConnectStatusChangeListener.onStatusChange(false, ERROR_PASSWORD);
//                        }
//                    }
//                } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
//                    NetworkInfo.DetailedState state = ((NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getDetailedState();
//                    setWifiState(state);
//                }
//            }
//        };
//        mWifiConnectIntentFilter = new IntentFilter();
//        mWifiConnectIntentFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
//        mWifiConnectIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        mWifiConnectIntentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
//        mWifiConnectIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//
//        //注册接收器
//        context.registerReceiver(mWifiSearchBroadcastReceiver, mWifiSearchIntentFilter);
//        context.registerReceiver(wifiConnectReceiver, mWifiConnectIntentFilter);


    private void setDetailedState(NetworkInfo.DetailedState state) {
        if (state == NetworkInfo.DetailedState.CONNECTED) {
            LogUtils.d("【连接WiFi】连接成功");
            if (listener != null) {
                listener.onDetailedState(state, "WiFi连接成功");
                listener.onSuccess(wifiConfiguration);
            }
        } else if (state == NetworkInfo.DetailedState.FAILED) {
            LogUtils.d("【连接WiFi】连接失败");
            if (listener != null) {
                listener.onDetailedState(state, "WiFi连接失败");
                listener.onFailure("WiFi连接失败");
            }
        } else if (state == NetworkInfo.DetailedState.DISCONNECTED) {
            LogUtils.d("【连接WiFi】已断开连接");
            if (listener != null) {
                listener.onDetailedState(state, "WiFi已断开连接");
            }
        }
        else if (state == NetworkInfo.DetailedState.CONNECTING) {
            LogUtils.d("【连接WiFi】连接中...");
            if (listener != null) {
                listener.onDetailedState(state, "WiFi连接中...");
            }
        } else if (state == NetworkInfo.DetailedState.DISCONNECTING) {
            LogUtils.d("【连接WiFi】断开连接中");
            if (listener != null) {
                listener.onDetailedState(state, "WiFi断开中...");
            }
        }

        else if (state == NetworkInfo.DetailedState.AUTHENTICATING) {
            LogUtils.d("【连接WiFi】认证中");
            if (listener != null) {
                listener.onDetailedState(state, "WiFi认证中...");
            }
        } else if (state == NetworkInfo.DetailedState.BLOCKED) {
            LogUtils.d("【连接WiFi】阻塞");
            if (listener != null) {
                listener.onDetailedState(state, "WiFi阻塞中...");
            }
        } else if (state == NetworkInfo.DetailedState.SCANNING) {
            LogUtils.d("【连接WiFi】搜索中");
            if (listener != null) {
                listener.onDetailedState(state, "WiFi搜索中...");
            }
        }

        else if (state == NetworkInfo.DetailedState.SUSPENDED) {

        } else if (state == NetworkInfo.DetailedState.IDLE) {

        } else if (state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {

        }
    }



}
