package android.net.wifi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import com.blankj.utilcode.log.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class ConnectTest {


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
    //==============================================================================================
    public static boolean wifi同名多个配置遍历连接connectToCurrentLocationWiFi(Context context, ScanResult result) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        @SuppressLint("MissingPermission")
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {

            for (int i = 0; i < configuredNetworks.size(); i++) {
                WifiConfiguration config = configuredNetworks.get(i);

                LogUtils.w("【遍历】对比 SSID= " + config.SSID+ " = " + result.SSID
                        + "\n【遍历】对比 BSSID= " + config.BSSID + " = " + result.BSSID);
                boolean sSsid = config.SSID != null && config.SSID.equals("\"" + result.SSID + "\"");
                boolean sBssid = config.BSSID != null && config.BSSID.equals("\"" + result.BSSID + "\"");

                if (sSsid && sBssid) {
                    int netId = config.networkId;
                    boolean disconnect = wifiManager.disconnect();
                    boolean enabled = wifiManager.enableNetwork(netId, true);
                    boolean reconnect = wifiManager.reconnect();
                    Log.d("WiFi Connection", "遍历->Connecting to WiFi: " + enabled + " = " +reconnect);
                    return reconnect;
                }
            }

        }

        Log.d("WiFi Connection", "遍历->No matching WiFi configuration found for the current location.");

        return false;
    }
    //==============================================================================================
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
    //==============================================================================================
    public static boolean wifi是否已配置过_isExistingWiFiConfiguration(Context context, String ssid) {
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        @SuppressLint("MissingPermission")
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration config : configuredNetworks) {
                if (config.SSID != null && config.SSID.equals("\"" + ssid + "\"")) {
                    return true;
                }
            }
        }
        return false;
    }
    //==============================================================================================
    public static int wifi是否已配置过_getExistingNetworkId(Context context, String ssid) {
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        @SuppressLint("MissingPermission")
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


    public static boolean wifi切换已连接过(Context context, String ssid) {
        int netId = wifi是否已配置过_getExistingNetworkId(context, ssid); // Replace with your SSID
        if (netId != -1) {
            return connectToExistingWiFi(context, netId);
        } else {
            Log.e("WiFi Connection", "WiFi configuration not found for the specified SSID.");
        }

        return false;
    }


    public static boolean connectToExistingWiFi(Context context, int netId) {
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean disconnect = wifiManager.disconnect();
        boolean enabled = wifiManager.enableNetwork(netId, true);
        boolean reconnect = wifiManager.reconnect();

        Log.d("WiFi Connection", "Connecting to existing WiFi with networkId: " + netId);
        Log.d("WiFi Connection", "WiFi是否连接成功：" + reconnect);

        return reconnect;
    }

    //==============================================================================================
    public static boolean wifi连接无密_connectToOpenWiFi(Context context, String ssid) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        Log.d("WiFi Connection", "Connecting to open WiFi: " + ssid);

        boolean enabled = wifiManager.enableNetwork(netId, true);
        Log.d("WiFi Connection", "WiFi是否连接成功：" + enabled);
        return enabled;
    }

    public static boolean wifi连接WPA加密_connectToWPAWiFi(Context context, String ssid, String password) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.preSharedKey = "\"" + password + "\"";
        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        Log.d("WiFi Connection", "Connecting to WPA WiFi: " + ssid);

        boolean enabled = wifiManager.enableNetwork(netId, true);
        Log.d("WiFi Connection", "WiFi是否连接成功：" + enabled);
        return enabled;
    }

    public static boolean wifi连接WEP加密_connectToWEPWiFi(Context context, String ssid, String wepKey) {
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
        return enabled;
    }
    //==============================================================================================
    private BroadcastReceiver mWifiSearchBroadcastReceiver;
    private IntentFilter mWifiSearchIntentFilter;
    private BroadcastReceiver mWifiConnectBroadcastReceiver;
    private IntentFilter mWifiConnectIntentFilter;

    private void init() {
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
//        mWifiConnectBroadcastReceiver = new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
//                    int wifState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
//                    if (wifState != WifiManager.WIFI_STATE_ENABLED) {
//                        errorLog("Wifi模块启动失败");
//                        if (onWifiConnectStatusChangeListener != null) {
//                            onWifiConnectStatusChangeListener.onStatusChange(false, ERROR_DEVICE_NOT_HAVE_WIFI);
//                        }
//                    }
//                } else if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
//                    int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
//                    if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
//                        errorLog("密码错误");
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
//        context.registerReceiver(mWifiConnectBroadcastReceiver, mWifiConnectIntentFilter);
    }




}
