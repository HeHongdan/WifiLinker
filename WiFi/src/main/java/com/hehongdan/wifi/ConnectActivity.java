package com.hehongdan.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.connect.ConnectListener;
import android.net.wifi.connect.WifiConnector;
import android.net.wifi.scan.ScannerListener;
import android.net.wifi.scan.WifiScanner;
import android.net.wifi.utils.NetworkUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.log.LogUtils;

import java.util.List;

public class ConnectActivity extends AppCompatActivity {

    public static final String KEY_TITLE = "KEY_TITLE";


    /**
     * 开始(启动)Activity。
     *
     * @param context 上下文(之前页面)。
     */
    public static void start(Context context, String title) {
        Intent intent = new Intent(context, ConnectActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Button btConnect = findViewById(R.id.btConnect);
        EditText etName = findViewById(R.id.etName);
        EditText etPassword = findViewById(R.id.etPassword);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                WifiConnector.getInstance()
                        .setListener(new ConnectListener.Simple<WifiConfiguration>() {
                            @Override
                            public void onSuccess(WifiConfiguration response) {
                                LogUtils.v("【监听器】连接成功= " + response.SSID);
                                LogUtils.e("【监听器】连接成功= " + NetworkUtils.getSSID());
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                LogUtils.e("【监听器】连接(不)成功= " + errorMessage);
                            }

                            @Override
                            public void onDetailedState(Object o, String msg) {
                                //LogUtils.e("【监听器】连接(不)成功= " + NetworkUtils.getSSID());
                            }
                        })
                        .connect(name, password);
            }
        });

    }
}