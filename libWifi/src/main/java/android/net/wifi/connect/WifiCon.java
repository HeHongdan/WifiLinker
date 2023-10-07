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

public class WifiCon implements LifecycleObserver {

    private Activity context;
    private BroadcastReceiver wifiConnectReceiver;
    private IntentFilter wifiConnectFilter;
    private ConnectListener<WifiConfiguration> listener;
    private static WifiConfiguration wifiConfiguration;

    /** 第一层锁：保证变量可见性。 */
    private volatile static WifiCon instance;

    private WifiCon() {
        context = ActivityUtils.getTopActivity();//最好做下判空
        if (context instanceof ComponentActivity) {
            ((ComponentActivity) context).getLifecycle().addObserver(this);
        }
        LogUtils.d("【扫描WiFi】宿主= " + context);
    }

    public static WifiCon getInstance() {
        //第一次判空：无需每次都加锁，提高性能
        if (instance == null) {
            //第二层锁：保证线程同步
            synchronized (WifiCon.class) {
                //第二次判空：避免多线程同时执行getInstance()（此）方法，产生多个instance（本类）对象
                if (instance == null) {
                    instance = new WifiCon();
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


    /**
     * 连接(没密码)WiFi。
     *
     * @param ssid WiFi名称。
     */
    @SuppressLint("NewApi")
    public boolean connect(String ssid) {
        return connect(ssid, "", "");
    }

    /**
     * 连接(有密码)WiFi。
     *
     * @param ssid WiFi名称。
     * @param password WiFi密码。
     */
    @SuppressLint("NewApi")
    public boolean connect(String ssid, String password) {
        ScanResult result = new ScanResult();
        result.SSID = ssid;
        result.capabilities = Constant.CAPABILITIES_WPA;
        return connect(result, password);
    }

    /**
     * 连接(有密码，知道加密类型)WiFi。
     *
     * @param ssid WiFi名称。
     * @param password WiFi密码。
     * @param capabilities 加密类型。
     */
    @SuppressLint("NewApi")
    public boolean connect(String ssid, String password, String capabilities) {
        ScanResult result = new ScanResult();
        result.SSID = ssid;
        result.capabilities = capabilities;
        return connect(result, password);
    }

    /**
     * 连接(扫描的)WiFi。
     *
     * @param result 扫描的WiFi。
     */
    public boolean connect(ScanResult result) {
        return connect(result, "");
    }

    /**
     * 连接(扫描的，有密码的)WiFi。
     *
     * @param result 扫描的WiFi。
     * @param password WiFi密码。
     */
    public boolean connect(ScanResult result, String password) {
        boolean isConnect = false;
        String ssid = result.SSID;
        String capabilities = result.capabilities;

        List<WifiConfiguration> sameConfigurationList = findAllSameConfiguration(context, ssid);
        if (sameConfigurationList.isEmpty())  {
            if (StringUtils.containsByUpper(capabilities, Constant.CAPABILITIES_WPA)) {
                LogUtils.w("【WiFi加密】WPA= " + ssid);
                isConnect = connectToWpa(context, ssid, password);
            } else if (StringUtils.containsByUpper(capabilities, Constant.CAPABILITIES_WEP)) {
                LogUtils.w("【WiFi加密】WEP= " + ssid);
                isConnect = connectToWep(context, ssid, password);
            }
            else {
                LogUtils.w("【WiFi加密】没有密码= " + ssid);
                isConnect = connectToOpen(context, ssid);
            }

        } else {
            isConnect = connectExist(context, result, sameConfigurationList);
            LogUtils.i("【连接WiFi】相同，连接成功= " + isConnect);

            if (!isConnect) {
                wifi忘记已配置过forgetWiFiNetwork(context, ssid);
                SystemClock.sleep(500);//TODO 拿CPU频率来，动态调整
                isConnect = connectToWpa(context, ssid, password);//TODO 不成功就重新配置新的然后再次连接
                LogUtils.i("【连接WiFi】相同，连接成功= " + isConnect);
            }
        }

        return isConnect;
    }


    //==============================================================================================

    /**
     * 连接(没密码)WiFi。
     *
     * @param context 上下文。
     * @param ssid WiFi名称。
     * @return 是否连接成功。
     */
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

    /**
     * 连接(有密码，WEP加密类型)WiFi。
     *
     * @param context 上下文。
     * @param ssid WiFi名称。
     * @param wpaKey WiFi密码。
     * @return 是否连接成功。
     */
    @SuppressLint({"WifiManagerLeak", "WifiManagerPotentialLeak"})
    public static boolean connectToWpa(Context context, String ssid, String wpaKey) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.preSharedKey = "\"" + wpaKey + "\"";
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

    /**
     * 连接(有密码，WEP加密类型)WiFi。
     *
     * @param context 上下文。
     * @param ssid WiFi名称。
     * @param wepKey WiFi密码。
     * @return 是否连接成功。
     */
    @SuppressLint({"WifiManagerLeak", "WifiManagerPotentialLeak"})
    public static boolean connectToWep(Context context, String ssid, String wepKey) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.wepKeys[0] = "\"" + wepKey + "\"";
        wifiConfig.wepTxKeyIndex = 0;
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

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


    @SuppressLint({"MissingPermission","WifiManagerLeak"})
    public boolean connectToWifi(Context context, String ssid, String password) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworkList = wifiManager.getConfiguredNetworks();

        if (configuredNetworkList != null) {
            for (WifiConfiguration config : configuredNetworkList) {
                String ssidQuotes = addQuotes(ssid);  // Add quotes around SSID
                if (ssidQuotes.equals(config.SSID)) {
                    // Disable the old network
                    wifiManager.disableNetwork(config.networkId);
                    wifiManager.disconnect();

                    // Create a new WifiConfiguration for the new password
                    WifiConfiguration newConfig = new WifiConfiguration();
                    newConfig.SSID = addQuotes(ssid);
                    newConfig.preSharedKey = addQuotes(password);

                    // Add the new network
                    int networkId = wifiManager.addNetwork(newConfig);

                    // Reconnect to the new network
                    if (networkId != -1 && wifiManager.enableNetwork(networkId, true)) {
                        // Wait for connection to complete
                        boolean connected = waitForConnection(ssid, 10000);  // 10 seconds timeout
                        if (connected) {
                            return true;  // Successfully connected, exit loop
                        }
                    }
                }
            }
        }

        return false;  // Configuration not found or failed to connect
    }
    @SuppressLint("WifiManagerLeak")
    private boolean waitForConnection(String targetSSID, int timeoutMillis) {
        long startTime = System.currentTimeMillis();

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null && wifiInfo.getSSID().equals(addQuotes(targetSSID))) {
                return true;  // Successfully connected to the targetSSID
            }

            // Wait for a moment
            SystemClock.sleep(1000);
        }

        return false;  // Timeout or failed to connect
    }

    //----------------------------------------------------------------------------------------
    /**
     * (遍历)连接(已经存在)WiFi。
     *
     * @param result 待连接。
     * @param sameConfigurationList 相同名称的WiFi配置列表。
     * @return 是否连接成功。
     */
    @SuppressLint({"WifiManagerLeak", "WifiManagerPotentialLeak"})
    private boolean connectExist(Context context, ScanResult result, List<WifiConfiguration> sameConfigurationList) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        for (int i = 0; i < sameConfigurationList.size(); i++) {
            WifiConfiguration config = sameConfigurationList.get(i);

            LogUtils.w("【遍历】对比 SSID= " + config.SSID+ " = " + result.SSID
                    + "\n【遍历】对比 BSSID= " + config.BSSID + " = " + result.BSSID);
            //boolean sameSsid = config.SSID.equals("\"" + result.SSID + "\"");
            //boolean sBssid = config.BSSID != null && config.BSSID.equals("\"" + result.BSSID + "\"");
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String currentSsid = wifiInfo.getSSID();
            LogUtils.w("【连接WiFi】对比 需要连接= " + result.SSID
                    + "\n配置= " + config.SSID
                    + "\n当前= " + currentSsid
            );

            if (!currentSsid.equals(addQuotes(result.SSID))) {
                int netId = config.networkId;
                boolean disconnect = wifiManager.disconnect();
                boolean enabled = wifiManager.enableNetwork(netId, true);
                boolean reconnect = wifiManager.reconnect();
                LogUtils.e("【连接WiFi】(不)相同，要连= " + result.SSID
                        + "\n断开= " + disconnect
                        + "\n可用= " + enabled
                        + "\n重连= " + reconnect
                );
            }

            SystemClock.sleep(500);//TODO 拿CPU频率来，动态调整
            if (currentSsid.equals(addQuotes(result.SSID))) {
                LogUtils.i("【连接WiFi】相同，要连= " + result.SSID
                        + "\n当前= " + currentSsid
                );
                return true;
            }
        }

        return false;
    }

    /**
     * 查找所有(相同名称)的WiFi配置。
     *
     * @param context 上下文。
     * @param ssid WiFi名称。
     * @return WiFi配置列表。
     */
    @SuppressLint({"MissingPermission", "WifiManagerPotentialLeak"})
    @NonNull
    public static List<WifiConfiguration> findAllSameConfiguration(Context context, String ssid) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        List<WifiConfiguration> configuredNetworkList = wifiManager.getConfiguredNetworks();
        List<WifiConfiguration> sameConfiguredNetworkList = new ArrayList<>();

        if (configuredNetworkList != null) {
            for (WifiConfiguration config : configuredNetworkList) {
                if (config.SSID != null && config.SSID.equals("\"" + ssid + "\"")) {
                    sameConfiguredNetworkList.add(config);
                }
            }
        }

        if (!sameConfiguredNetworkList.isEmpty()) {
            Log.d("WiFi", "Found " + sameConfiguredNetworkList.size() + " configurations for WiFi network: " + ssid);
            for (WifiConfiguration config : sameConfiguredNetworkList) {
                Log.d("WiFi", "SSID: " + config.SSID + ", Network ID: " + config.networkId);
            }
        } else {
            Log.d("WiFi", "No configurations found for WiFi network: " + ssid);
        }

        return sameConfiguredNetworkList;
    }

    //----------------------------------------------------------------------------------------


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

    /**
     * (WiFi名称)加引号。
     *
     * @param ssid WiFi名称。
     * @return 加引号的WiFi名称。
     */
    public static String addQuotes(String ssid) {
        String quotesSsid = "\"" + ssid + "\"";
        LogUtils.d("【连接WiFi】已经存在= " + ssid + "；拼接= " + quotesSsid);

        return quotesSsid;
    }

    public WifiCon setListener(ConnectListener<WifiConfiguration> listener) {
        this.listener = listener;
        return this;
    }

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

    @SuppressLint("MissingPermission")
    public static boolean wifi忘记已配置过forgetWiFiNetwork(Context context, String ssid) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworkList = wifiManager.getConfiguredNetworks();
        if (configuredNetworkList != null) {
            for (WifiConfiguration config : configuredNetworkList) {
                if (config.SSID != null && config.SSID.equals("\"" + ssid + "\"")) {
                    wifiManager.removeNetwork(config.networkId);
                    boolean save = wifiManager.saveConfiguration();
                    Log.d("WiFi", "Forgot WiFi network: " + save);
                    SystemClock.sleep(100);//TODO 拿CPU频率来，动态调整
                    return save;
                }
            }
        }

        return false;
    }

}
