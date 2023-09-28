package android.net.wifi.scan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.log.LogUtils;
import com.blankj.utilcode.util.ActivityUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class WifiScanner implements LifecycleObserver {

    private static WifiScanner instance;
    private Activity activity;
    private WifiManager wifiManager;
    private BroadcastReceiver wifiScanReceiver;
    private ScannerListener listener;

    private WifiScanner() {
        activity = ActivityUtils.getTopActivity();
        if (activity instanceof ComponentActivity) {
            ((ComponentActivity)activity).getLifecycle().addObserver(this);
        }
        LogUtils.d("【扫描WiFi】宿主= " + activity);
        wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static synchronized WifiScanner getInstance() {
        if (instance == null) {
            instance = new WifiScanner();
        }
        return instance;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void startWiFiScan() {
        LogUtils.d("【扫描WiFi】开始...");

        wifiScanReceiver = new BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            @Override
            public void onReceive(Context context, Intent intent) {
                if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    List<ScanResult> scanResultList = wifiManager.getScanResults();
                    sortScanByLevel(scanResultList);
                    LogUtils.d("【扫描WiFi】扫描排序的结果= " + scanResultList.size());
                    if (listener != null) {
                        listener.onScan(scanResultList);
                    }
                }
            }
        };
        activity.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stopWiFiScan() {
        LogUtils.d("【扫描WiFi】停止");

        if (wifiScanReceiver != null) {
            activity.unregisterReceiver(wifiScanReceiver);
            wifiScanReceiver = null;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        LogUtils.d("【扫描WiFi】销毁");

        // Remove the WiFiScanner instance on activity destroy
        instance = null;
    }



    //==============================================================================================

    public WifiScanner setListener(ScannerListener listener) {
        this.listener = listener;

        return this;
    }

    public static List<ScanResult> sortScanByLevel(List<ScanResult> scanResults) {
        Collections.sort(scanResults, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult result1, ScanResult result2) {
                return Integer.compare(result2.level, result1.level);
            }
        });

        return scanResults;
    }
}
