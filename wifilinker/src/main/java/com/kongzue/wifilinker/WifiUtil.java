package com.kongzue.wifilinker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.kongzue.wifilinker.interfaces.OnWifiConnectStatusChangeListener;
import com.kongzue.wifilinker.interfaces.OnWifiScanListener;
import com.kongzue.wifilinker.util.WifiAutoConnectManager;
import com.kongzue.wifilinker.util.WifiInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.WIFI_SERVICE;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2019/7/26 15:38
 */
public class WifiUtil {

    public static boolean DEBUGMODE = true;

    private Activity context;

    public static final int ERROR_DEVICE_NOT_HAVE_WIFI = -1;    //设备无Wifi模块
    public static final int ERROR_CONNECT = -2;                 //连接失败
    public static final int ERROR_CONNECT_SYS_EXISTS_SAME_CONFIG = -3;                 //连接失败：系统已存在相同Wifi配置（需手动删除已存储连接）
    public static final int ERROR_PASSWORD = -11;               //密码错误

    public static final int CONNECT_START = 1;                  //开始连接
    public static final int CONNECT_FINISH = 2;                 //已连接
    public static final int DISCONNECTED = 3;                   //已断开连接

    private boolean isLinked = false;

    private OnWifiScanListener onWifiScanListener;
    private OnWifiConnectStatusChangeListener onWifiConnectStatusChangeListener;

    private BroadcastReceiver mWifiSearchBroadcastReceiver;
    private IntentFilter mWifiSearchIntentFilter;
    private BroadcastReceiver mWifiConnectBroadcastReceiver;
    private IntentFilter mWifiConnectIntentFilter;
    private WifiAutoConnectManager mWifiAutoConnectManager;

    public WifiUtil(Activity context) {
        this.context = context;

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        mWifiAutoConnectManager = WifiAutoConnectManager.newInstance(wifiManager);

        init();
    }

    private List<ScanResult> mScanResultList = new ArrayList<>();

    private void init() {
        mWifiSearchBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {// 扫描结果改表
                    mScanResultList = WifiAutoConnectManager.getScanResults();
                    if (onWifiScanListener != null) {
                        onWifiScanListener.onScan(mScanResultList);
                    }
                }
            }
        };
        mWifiSearchIntentFilter = new IntentFilter();
        mWifiSearchIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mWifiSearchIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mWifiSearchIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

        //wifi 状态变化接收广播
        mWifiConnectBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    int wifState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                    if (wifState != WifiManager.WIFI_STATE_ENABLED) {
                        errorLog("Wifi模块启动失败");
                        if (onWifiConnectStatusChangeListener != null) {
                            onWifiConnectStatusChangeListener.onStatusChange(false, ERROR_DEVICE_NOT_HAVE_WIFI);
                        }
                    }
                } else if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                    int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
                    if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                        errorLog("密码错误");
                        if (onWifiConnectStatusChangeListener != null) {
                            onWifiConnectStatusChangeListener.onStatusChange(false, ERROR_PASSWORD);
                        }
                    }
                } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    NetworkInfo.DetailedState state = ((NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getDetailedState();
                    setWifiState(state);
                }
            }
        };
        mWifiConnectIntentFilter = new IntentFilter();
        mWifiConnectIntentFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
        mWifiConnectIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mWifiConnectIntentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        mWifiConnectIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

        //注册接收器
        context.registerReceiver(mWifiSearchBroadcastReceiver, mWifiSearchIntentFilter);
        context.registerReceiver(mWifiConnectBroadcastReceiver, mWifiConnectIntentFilter);
    }

    private boolean isConnected = false;//标记WiFi是否在已连状态

    private void setWifiState(NetworkInfo.DetailedState state) {
        if (state == NetworkInfo.DetailedState.AUTHENTICATING) {
            log("认证中");
        } else if (state == NetworkInfo.DetailedState.BLOCKED) {
            log("阻塞");
        } else if (state == NetworkInfo.DetailedState.CONNECTED) {
            log("连接成功");

            if (!isConnected) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //延迟一秒
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (onWifiConnectStatusChangeListener != null) {
                                    onWifiConnectStatusChangeListener.onStatusChange(true, CONNECT_FINISH);

                                    onWifiConnectStatusChangeListener.onConnect(new WifiInfo(
                                            getNowLinkedWifiSSID(),
                                            WifiAutoConnectManager.getIpAddress(),
                                            WifiAutoConnectManager.getMacAddress(),
                                            WifiAutoConnectManager.getGateway()
                                    ));
                                }
                            }
                        });
                    }
                },1000);
                isConnected = true;
            }
            isLinked = true;
        } else if (state == NetworkInfo.DetailedState.CONNECTING) {
            isLinked = false;
            log("连接中: " + WifiAutoConnectManager.getSSID());
        } else if (state == NetworkInfo.DetailedState.DISCONNECTED) {
            isLinked = false;
            log("已断开连接");
            if (onWifiConnectStatusChangeListener != null) {
                onWifiConnectStatusChangeListener.onStatusChange(true, DISCONNECTED);
            }
        } else if (state == NetworkInfo.DetailedState.DISCONNECTING) {
            isLinked = false;
            log("断开连接中");
        } else if (state == NetworkInfo.DetailedState.FAILED) {
            isLinked = false;
            if (onWifiConnectStatusChangeListener != null) {
                onWifiConnectStatusChangeListener.onStatusChange(false, ERROR_CONNECT);
            }
            log("连接失败");
        } else if (state == NetworkInfo.DetailedState.IDLE) {

        } else if (state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {

        } else if (state == NetworkInfo.DetailedState.SCANNING) {
            log("搜索中");
        } else if (state == NetworkInfo.DetailedState.SUSPENDED) {

        }
    }

    public String getNowLinkedWifiSSID(){
        String linkedWifiSSID;
        //通过以下方法获取连接的Wifi的真实SSID：
        //因部分手机系统限制，直接获取SSID可能是“unknow ssid”，此方法原理是通过获取已连接的Wifi的网络ID，然后去已保存的Wifi信息库中查找相同的网络ID的wifi信息的SSID
        WifiManager my_wifiManager = ((WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE));
        assert my_wifiManager != null;
        android.net.wifi.WifiInfo wifiInfo = my_wifiManager.getConnectionInfo();
        linkedWifiSSID = wifiInfo.getSSID();
        int networkId = wifiInfo.getNetworkId();
        @SuppressLint("MissingPermission")
        List<WifiConfiguration> configuredNetworks = my_wifiManager.getConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : configuredNetworks) {
            if (wifiConfiguration.networkId == networkId) {
                linkedWifiSSID = wifiConfiguration.SSID;
                break;
            }
        }
        return linkedWifiSSID;
    }

    public void close() {
        try {
            context.unregisterReceiver(mWifiSearchBroadcastReceiver);
            context.unregisterReceiver(mWifiConnectBroadcastReceiver);
        } catch (Exception e) {
            if (DEBUGMODE) {
                e.printStackTrace();
            }
        }
    }

    private WifiAutoConnectManager.WifiCipherType type = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS;

    private String ssid;
    private String password;

    public void link(String ssid, String password, OnWifiConnectStatusChangeListener listener) {//扫描后连接
        isConnected = false;
        if (mScanResultList.isEmpty()) {
            errorLog("此连接方式需要先进行查找");
            return;
        }
        log("准备连接：" + ssid + " 密码：" + password);
        this.ssid = ssid;
        this.password = password;
        onWifiConnectStatusChangeListener = listener;
        type = WifiAutoConnectManager.getCipherType(ssid);

        if (ssid.equals(WifiAutoConnectManager.getSSID())) {
            log("已连接");
            return;
        }
        if (mConnectAsyncTask != null) {
            mConnectAsyncTask.cancel(true);
            mConnectAsyncTask = null;
        }
        mConnectAsyncTask = new ConnectAsyncTask(ssid, password, type);
        mConnectAsyncTask.execute();
    }


    private ConnectAsyncTask mConnectAsyncTask = null;

    public void link(String ssid, String password, WifiAutoConnectManager.WifiCipherType wifiCipherType, OnWifiConnectStatusChangeListener listener) {//不用扫描直接连接
        isConnected = false;
        this.ssid = ssid;
        this.password = password;
        log("准备连接：" + ssid + " 密码：" + password);
        type = wifiCipherType;
        onWifiConnectStatusChangeListener = listener;

        if (ssid.equals(WifiAutoConnectManager.getSSID())) {
            log("已连接");
            return;
        }
        if (mConnectAsyncTask != null) {
            mConnectAsyncTask.cancel(true);
            mConnectAsyncTask = null;
        }
        mConnectAsyncTask = new ConnectAsyncTask(ssid, password, type);
        mConnectAsyncTask.execute();
    }

    private WorkAsyncTask mWorkAsyncTask = null;

    public void scan(OnWifiScanListener listener) {
        onWifiScanListener = listener;
        if (mWorkAsyncTask != null) {
            mWorkAsyncTask.cancel(true);
            mWorkAsyncTask = null;
        }
        mWorkAsyncTask = new WorkAsyncTask();
        mWorkAsyncTask.execute();
    }

    public void stopScan() {
        if (mWorkAsyncTask != null) {
            mWorkAsyncTask.cancel(true);
            mWorkAsyncTask = null;
        }
    }

    /**
     * 获取wifi列表
     */
    private class WorkAsyncTask extends AsyncTask<Void, Void, List<ScanResult>> {
        private List<ScanResult> mScanResult = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            log("搜索中...");
            if (onWifiScanListener != null) {
                onWifiScanListener.onScanStart();
            }
        }

        @Override
        protected List<ScanResult> doInBackground(Void... params) {
            if (WifiAutoConnectManager.startStan()) {
                mScanResult = WifiAutoConnectManager.getScanResults();
            }
            List<ScanResult> filterScanResultList = new ArrayList<>();
            if (mScanResult != null) {
                for (ScanResult wifi : mScanResult) {
                    filterScanResultList.add(wifi);
                    log("查找到：" + wifi);
                }
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return filterScanResultList;
        }

        @Override
        protected void onPostExecute(final List<ScanResult> result) {
            super.onPostExecute(result);
            mScanResultList = result;
            if (onWifiScanListener != null) {
                onWifiScanListener.onScanStop(mScanResultList);
            }

        }
    }

    /**
     * 连接指定的wifi
     */
    class ConnectAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private String ssid;
        private String password;
        private WifiAutoConnectManager.WifiCipherType type;
        WifiConfiguration tempConfig;

        public ConnectAsyncTask(String ssid, String password, WifiAutoConnectManager.WifiCipherType type) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (onWifiConnectStatusChangeListener != null) {
                onWifiConnectStatusChangeListener.onStatusChange(false, CONNECT_START);
            }
            log("开始连接");
        }

        @SuppressLint("MissingPermission")
        @Override
        protected Boolean doInBackground(Void... voids) {
            // 打开wifi
            mWifiAutoConnectManager.openWifi();
            // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi，状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
            while (mWifiAutoConnectManager.wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    errorLog("错误");
                    if (DEBUGMODE) ie.printStackTrace();
                }
            }

            tempConfig = mWifiAutoConnectManager.isExsits(ssid);
            //禁掉所有wifi
            List<WifiConfiguration> configuredNetworks = mWifiAutoConnectManager.wifiManager.getConfiguredNetworks();
            for (WifiConfiguration c : configuredNetworks) {
                mWifiAutoConnectManager.wifiManager.disableNetwork(c.networkId);
            }
            if (tempConfig != null) {
                log(ssid + "是已存在配置，尝试连接");
                boolean result = mWifiAutoConnectManager.wifiManager.enableNetwork(tempConfig.networkId, true);
                if (!isLinked && type != WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS) {
                    try {
                        Thread.sleep(5000);//超过5s提示失败
                        if (!isLinked) {
                            log(ssid + "连接失败");
                            mWifiAutoConnectManager.wifiManager.disableNetwork(tempConfig.networkId);
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (onWifiConnectStatusChangeListener != null) {
                                        onWifiConnectStatusChangeListener.onStatusChange(false, ERROR_CONNECT_SYS_EXISTS_SAME_CONFIG);
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return result;
            } else {
                log(ssid + "是新的配置，开始连接");
                if (type != WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS) {
                    WifiConfiguration wifiConfig = mWifiAutoConnectManager.createWifiInfo(ssid, password, type);
                    if (wifiConfig == null) {
                        errorLog("错误：Wifi配置为null");
                        return false;
                    }

                    log("开始连接：" + wifiConfig.SSID);
                    int netID = mWifiAutoConnectManager.wifiManager.addNetwork(wifiConfig);
                    log("添加(成功)网络ID：" + netID);
                    boolean enabled = mWifiAutoConnectManager.wifiManager.enableNetwork(netID, true);
                    log("设置网络配置：" + enabled);

                    return enabled;
                } else {//没有秘密的WiFi
                    if (true) {
                        WifiConfiguration wifiConfig = new WifiConfiguration();
                        wifiConfig.SSID = "\"" + ssid + "\"";
                        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                        WifiManager wifiManager = mWifiAutoConnectManager.wifiManager;
                        int netId = wifiManager.addNetwork(wifiConfig);
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(netId, true);
                        wifiManager.reconnect();

                        boolean enabled = mWifiAutoConnectManager.wifiManager.enableNetwork(netId, true);
                        log("设置网络配置【无密码】：" + enabled);

                        return enabled;
                    } else {
                        WifiConfiguration wifiConfig = mWifiAutoConnectManager.createWifiInfo(ssid, password, type);
                        if (wifiConfig == null) {
                            errorLog("错误：Wifi配置为null");
                            return false;
                        }
                        log("开始连接：" + wifiConfig.SSID);
                        int netID = mWifiAutoConnectManager.wifiManager.addNetwork(wifiConfig);
                        log("添加(成功)网络ID：" + netID);
                        boolean enabled = mWifiAutoConnectManager.wifiManager.enableNetwork(netID, true);
                        log("设置网络配置：" + enabled);

                        return enabled;
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mConnectAsyncTask = null;
        }
    }

    @SuppressLint("MissingPermission")
    public void disconnect() {
        WifiConfiguration tempConfig = mWifiAutoConnectManager.isExsits(ssid);
        if (tempConfig != null) {
            mWifiAutoConnectManager.wifiManager.removeNetwork(tempConfig.networkId);
            mWifiAutoConnectManager.wifiManager.saveConfiguration();
        }
        
        for (WifiConfiguration c : mWifiAutoConnectManager.wifiManager.getConfiguredNetworks()) {
            mWifiAutoConnectManager.wifiManager.disableNetwork(c.networkId);
        }
        mWifiAutoConnectManager.wifiManager.disconnect();
        mWifiAutoConnectManager.closeWifi();
    }
    
    private void errorLog(Object msg) {
        Log.e(">>>", "WifiUtil: " + msg);
    }
    
    private void log(Object msg) {
        Log.i(">>>", "WifiUtil: " + msg);
    }
}
