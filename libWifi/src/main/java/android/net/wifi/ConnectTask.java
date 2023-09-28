package android.net.wifi;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.net.wifi.utils.Constant;
import android.net.wifi.utils.IWifi;
import android.net.wifi.utils.NetworkUtils;
import android.os.AsyncTask;

import com.blankj.utilcode.log.LogUtils;

public class ConnectTask extends AsyncTask<Void, Void, Boolean> {
    static volatile boolean isLinked = false;

    private ScanResult scanResult;
    private String password;
    WifiConfiguration tempConfig;
    private IWifi listener;

    public ConnectTask(ScanResult scanResult, String password, IWifi listener) {
        this.scanResult = scanResult;
        this.password = password;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (listener != null) {
            listener.msg(IWifi.code_strat, "开始连接WiFi");
        }
        LogUtils.d("【连接任务】","开始连接WiFi");
    }
    @SuppressLint("MissingPermission")
    @Override
    protected Boolean doInBackground(Void... voids) {

        if (!NetworkUtils.getWifiEnabled()) {
            NetworkUtils.openWifi();
        }

        // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi，状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (NetworkUtils.getWifiManager().getWifiState() == WifiManager.WIFI_STATE_ENABLING){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LogUtils.d("【连接任务】","连接WiFi线程中断");
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }

                if (listener != null) {
                    listener.err(IWifi.code_Thread, new InterruptedException("连接WiFi线程中断"));
                }
            }
        }

        String ssid = scanResult.SSID;
        String capabilities = scanResult.capabilities.toUpperCase();
        tempConfig = NetworkUtils.isExsits(ssid);

        //禁掉所有wifi
        for (WifiConfiguration c : NetworkUtils.getWifiManager().getConfiguredNetworks()) {
            NetworkUtils.getWifiManager().disableNetwork(c.networkId);
        }

        if (tempConfig != null) {
            LogUtils.d("【连接任务】", "已存在配置= " + ssid);

            //enable
            boolean result = NetworkUtils.getWifiManager().enableNetwork(tempConfig.networkId, true);
            LogUtils.d("【连接任务】", "WiFi加密方式= " + capabilities);
            LogUtils.w("【连接任务】", "检查= " + (!isLinked) + (!capabilities.contains(Constant.WifiCipherType_NOPASS)));

            //if (!isLinked && !StringUtils.equalsIgnoreCase(Constant.WifiCipherType_NOPASS, type)) {
            if (!isLinked && !capabilities.contains(Constant.WifiCipherType_NOPASS)) {
                try {
                    Thread.sleep(5000);//5s后，检查广播，并提示结果
                    if (!isLinked) {
                        LogUtils.d("【连接任务】", "WiFi连接失败= " + ssid);
                        NetworkUtils.getWifiManager().disableNetwork(tempConfig.networkId);
                        if (listener != null) {
                            listener.err(IWifi.ERROR_CONNECT_SYS_EXISTS_SAME_CONFIG, new NetworkErrorException("连接WiFi(" + ssid + ")失败"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.d("【连接任务】", "WiFi连接失败= " + e);

                    if (listener != null) {
                        listener.err(IWifi.ERROR_CONNECT_SYS_EXISTS_SAME_CONFIG, new NetworkErrorException("连接WiFi(" + ssid + ")失败"));
                    }
                }
            } else {
                //TODO
            }

            return result;
        } else {
            LogUtils.d("【连接任务】", "连接(不)存在配置的WiFi= " + ssid);


            if (!capabilities.contains(Constant.WifiCipherType_NOPASS)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WifiConfiguration wifiConfig = NetworkUtils.createWifiInfo(ssid, password, capabilities);//创建一个WiFi配置
                        if (wifiConfig == null) {
                            LogUtils.d("【连接任务】","错误：Wifi配置为null= " + wifiConfig);
                            if (listener != null) {
                                listener.err(IWifi.Not, new NetworkErrorException("创建WiFi配置为空"));
                            }
                            return;
                        }

                        LogUtils.d("【连接任务】", "开始连接= " + wifiConfig.SSID);
                        int netId = NetworkUtils.getWifiManager().addNetwork(wifiConfig);
                        LogUtils.d("【连接任务】", "添加WiFi成功ID= " + netId);
                        boolean enabled = NetworkUtils.getWifiManager().enableNetwork(netId, true);
                        LogUtils.d("【连接任务】", "创建WiFi配置，并成功连接，是否可用= ：" + enabled);

                    }
                }).start();//TODO 去掉线程//return enabled;
            } else {
                WifiConfiguration wifiConfig = NetworkUtils.createWifiInfo(ssid, password, capabilities);
                if (wifiConfig == null) {
                    LogUtils.d("【连接任务】","错误：Wifi配置为null= " + wifiConfig);
                    if (listener != null) {
                        listener.err(IWifi.Not, new NetworkErrorException("创建WiFi配置为空"));
                    }
                    return false;
                }
                LogUtils.d("【连接任务】", "开始连接= " + wifiConfig.SSID);
                int netId = NetworkUtils.getWifiManager().addNetwork(wifiConfig);
                LogUtils.d("【连接任务】", "添加WiFi成功ID= " + netId);
                boolean enabled = NetworkUtils.getWifiManager().enableNetwork(netId, true);
                LogUtils.d("【连接任务】", "创建WiFi配置，并成功连接，是否可用= ：" + enabled);

                return enabled;
            }
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        //TODO 回调 mConnectAsyncTask = null;

        if (listener != null) {
            listener.msg(IWifi.code_end, "连接WiFi任务执行完(不一定成功)！");
        }
    }

}
