package com.hehongdan.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.scan.ScannerListener;
import android.net.wifi.scan.WifiScanner;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.log.LogUtils;

import java.util.List;

public class ScanActivity extends AppCompatActivity {

    public static final String KEY_TITLE = "KEY_TITLE";


    /**
     * 开始(启动)Activity。
     *
     * @param context 上下文(之前页面)。
     */
    public static void start(Context context, String title) {
        Intent intent = new Intent(context, ScanActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        WifiScanner.getInstance()
                .setListener(new ScannerListener() {
                    @Override
                    public void onScan(List<ScanResult> scanResultList) {
                        if (scanResultList != null && !scanResultList.isEmpty()) {
                            for (ScanResult scanResult : scanResultList) {
                                LogUtils.w("【扫描WiFi】扫描排序的结果= " + scanResult.SSID + "（" + scanResult.level + "）");
                            }
                        }
                    }
                });


    }
}