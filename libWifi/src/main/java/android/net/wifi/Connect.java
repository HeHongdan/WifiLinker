package android.net.wifi;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.utils.IWifi;
import android.net.wifi.utils.NetworkUtils;
import android.os.Handler;

import com.blankj.utilcode.log.LogUtils;

import java.util.List;

public class Connect {
    /** WiFi连接(异步)任务。 */
    private ConnectTask mConnectAsyncTask = null;
    private IWifi listener;
    private BroadcastReceiver mWifiConnectBroadcastReceiver;

    
    /** 第一层锁：保证变量可见性 */
    private volatile static Connect instance ;
    private Connect() {
    }
    public static Connect getInstance() {
        //第一次判空：无需每次都加锁，提高性能
        if (instance == null) {
            //第二层锁：保证线程同步
            synchronized (Connect.class) {
                //第二次判空：避免多线程同时执行getInstance()（此）方法，产生多个instance（本类）对象
                if (instance == null) {
                    instance = new Connect();
                }
            }
        }
        return instance;
    }

    /**
     * 断开WiFi连接。
     */
    @SuppressLint("MissingPermission")
    public void disconnect(String ssid) {
        WifiConfiguration tempConfig = NetworkUtils.isExsits(ssid);
        if (tempConfig != null) {
            NetworkUtils.getWifiManager().removeNetwork(tempConfig.networkId);
            NetworkUtils.getWifiManager().saveConfiguration();
        }

        for (WifiConfiguration c : NetworkUtils.getWifiManager().getConfiguredNetworks()) {
            NetworkUtils.getWifiManager().disableNetwork(c.networkId);
        }
        NetworkUtils.getWifiManager().disconnect();
        NetworkUtils.closeWifi();
    }
    
    @SuppressLint("MissingPermission")
    public void connect(ScanResult scanResult, String password) {
        if (!NetworkUtils.getWifiEnabled()) {
            if (listener != null) {
                listener.err(IWifi.code_unenabled, new NetworkErrorException("WiFi不可用"));
            }
            return;
        }

        List<ScanResult> mScanResultList = NetworkUtils.getWifiScanResult().getAllResults();
        if (mScanResultList.isEmpty()) {
            LogUtils.d("没有扫描到WiFi");
            if (listener !=null) {
                listener.err(IWifi.code_noHava, new NetworkErrorException("附近没有可用WiFi"));
            }
            return;
        }

        String ssid = scanResult.SSID;
        if (ssid.equals(NetworkUtils.getSSID())) {
            LogUtils.d("需要连接和当前的相同= " + ssid);
            if (listener !=null) {
                listener.msg(IWifi.CONNECT_FINISH, "WiFi(" + ssid + ")已连接");
            }
            return;
        }

        if (mConnectAsyncTask != null) {
            mConnectAsyncTask.cancel(true);
            mConnectAsyncTask = null;
        }
        mConnectAsyncTask = new ConnectTask(scanResult, password, listener);
        mConnectAsyncTask.execute();
    }


    public Connect setListener(IWifi listener) {
        this.listener = listener;
        registerConnect();
        return instance;
    }

    @SuppressLint("MissingPermission")
    private Connect registerConnect() {
        if (mWifiConnectBroadcastReceiver == null) {
            //wifi 状态变化接收广播
            mWifiConnectBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {//WiFi状态发生变化
                        int wifState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                        if (wifState != WifiManager.WIFI_STATE_ENABLED) {
                            LogUtils.d("【广播状态】" , "Wifi模块启动失败");
                            if (listener != null) {
                                listener.err(IWifi.ERROR_DEVICE_NOT_HAVE_WIFI, new NetworkErrorException("WiFi打开失败"));
                            }
                        } else {
                            if (listener !=null) {
                                listener.msg(IWifi.ENABLED, "WiFi已打开");
                            }
                            LogUtils.d("【广播状态】" , "WiFi可用");
                        }
                    } else if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {// WiFi 连接状态变化时
                        int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
                        if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                            LogUtils.d("【广播状态】" , "密码错误");
                            if (listener != null) {
                                listener.err(IWifi.ERROR_PASSWORD, new NetworkErrorException("WiFi密码错误"));
                            }
                        }
                    } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//网络连接状态变化
                        NetworkInfo.DetailedState state = ((NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getDetailedState();
                        if (state == NetworkInfo.DetailedState.CONNECTED) {
                            LogUtils.d("【广播状态】" , "连接成功");
                            if (mConnectAsyncTask!=null) {
                                mConnectAsyncTask.isLinked = true;
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {//CONNECT_FINISH
                                    if (listener !=null) {
                                        listener.msg(IWifi.CONNECT_FINISH, "WiFi(" + NetworkUtils.getSSID() + ")已连接");
                                    }
                                }
                            }, 500);

                        } else if (state == NetworkInfo.DetailedState.FAILED) {
                            if (mConnectAsyncTask!=null) {
                                mConnectAsyncTask.isLinked = false;
                            }
                            if (listener != null) {
                                listener.err(IWifi.ERROR_CONNECT, new NetworkErrorException("WiFi(" + NetworkUtils.getSSID() + ")连接失败"));
                            }
                            LogUtils.d("【广播状态】", "WiFi连接失败");
                        } else if (state == NetworkInfo.DetailedState.DISCONNECTED) {
                            if (mConnectAsyncTask!=null) {
                                mConnectAsyncTask.isLinked = false;
                            }
                            LogUtils.d("【广播状态】", "WiFi已断开");
                            if (listener != null) {
                                listener.msg(IWifi.DISCONNECTED, "WiFi已断开");
                            }
                        }

                        else if (state == NetworkInfo.DetailedState.CONNECTING) {
                            LogUtils.d("【广播状态】" , "WiFi连接中= " + NetworkUtils.getSSID());
                            if (mConnectAsyncTask!=null) {
                                mConnectAsyncTask.isLinked = false;
                            }
                        }
                        else if (state == NetworkInfo.DetailedState.DISCONNECTING) {
                            if (mConnectAsyncTask!=null) {
                                mConnectAsyncTask.isLinked = false;
                            }
                            LogUtils.d("【广播状态】", "断开WiFi中");
                        } else if (state == NetworkInfo.DetailedState.SCANNING) {
                            LogUtils.d("【广播状态】", "搜索中");
                        } else if (state == NetworkInfo.DetailedState.AUTHENTICATING) {
                            LogUtils.d("【广播状态】", "认证中");
                        } else if (state == NetworkInfo.DetailedState.BLOCKED) {
                            LogUtils.d("【广播状态】", "阻塞");
                        }

                        else if (state == NetworkInfo.DetailedState.IDLE) {

                        } else if (state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {

                        } else if (state == NetworkInfo.DetailedState.SUSPENDED) {

                        }
                    }
                }
            };
        }

        return instance;
    }


}
