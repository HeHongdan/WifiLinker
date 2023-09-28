package android.net.wifi.connect;

public class Connector {


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
