package com.hehongdan.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ConnectTest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.utils.Constant;
import android.net.wifi.utils.StringUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.blankj.utilcode.log.LogUtils;

import java.util.List;

public class WifiActivity extends AppCompatActivity {
    private WifiManager wifiManager;
    private List<ScanResult> scanResults;
    private String[] wifiList;
    private String selectedWifi;
    ScanResult selected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        EditText etName = findViewById(R.id.etName);
        Spinner wifiSpinner = findViewById(R.id.wifiSpinner);
        Button connectButton = findViewById(R.id.connectButton);

        // Populate the WiFi Spinner with nearby WiFi networks
        scanWiFiNetworks();

        wifiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedWifi = wifiList[position];
                selected = scanResults.get(position);
                etName.setText(selectedWifi);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText passwordEditText = findViewById(R.id.passwordEditText);
                String password = passwordEditText.getText().toString();

                connectToWiFi(selectedWifi, password);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void scanWiFiNetworks() {
        wifiManager.startScan();
        scanResults = wifiManager.getScanResults();

        wifiList = new String[scanResults.size()];
        for (int i = 0; i < scanResults.size(); i++) {
            ScanResult result = scanResults.get(i);
            wifiList[i] = result.SSID;
            //LogUtils.d("【WiFi扫描】" + result.SSID + "，加密= " + result.capabilities);
        }

        Spinner wifiSpinner = findViewById(R.id.wifiSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, wifiList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wifiSpinner.setAdapter(adapter);
    }




    private void connectToWiFi(String ssid, String password) {

        if (true) {
            if (true) {
                ScanActivity.start(this, "");
                return;
            }



            List<WifiConfiguration> hhdList = ConnectTest.wifi同名多个配置findWiFiNetworks(this, "HHD");
            for (WifiConfiguration wifiConfiguration : hhdList) {

            }
            LogUtils.d("【同名】同名多个配置HHD= " + hhdList.size());


            if (!hhdList.isEmpty()) {
                if (false) {
                    boolean b = ConnectTest.wifi切换已连接过(this, selected.SSID);
                    LogUtils.d("【】连接已配置过，成功= " + b);
                } else {
                    int networkId = hhdList.get(2).networkId;//2、3
                    boolean b = ConnectTest.connectToExistingWiFi(this, networkId);
                    LogUtils.d("【同名】连接Id= " + networkId + "；是否成功= " + b);
                    //TODO 写个检查的循环，判断当前SSID是否需要的，相同就停止
                }
            } else {
                String capabilities = selected.capabilities;
                LogUtils.d("【】加密类型= " + capabilities);


                /**
                 * 【WiFi扫描】TP-LINK_5G_65D7，加密= [ESS]
                 * 【WiFi扫描】TP-LINK_65D7，加密= [ESS]
                 * 【WiFi扫描】HHD_5G1，加密= [ESS][WPS]
                 *
                 * 【WiFi扫描】DIRECT-A3-HP Smart Tank 510，加密= [WPA2-PSK-CCMP][RSN-PSK-CCMP][ESS][WPS]
                 * 【WiFi扫描】MAXHUB-YK8，加密= [WPA2-PSK-CCMP][RSN-PSK-CCMP][ESS]
                 * 【WiFi扫描】HHD，加密= [WPA2-PSK-CCMP][RSN-PSK-CCMP][ESS][WPS]
                 * 【WiFi扫描】R2_5G，加密= [WPA2-PSK-CCMP+TKIP][RSN-PSK-CCMP+TKIP][WPA-PSK-CCMP+TKIP][ESS]
                 * 【WiFi扫描】直播专线，加密= [WPA2-PSK-CCMP][RSN-PSK-CCMP][WPA-PSK-CCMP][ESS][WPS]
                 * 【WiFi扫描】Clifford-Group-Center，加密= [WPA2-PSK-CCMP][RSN-PSK-CCMP][WPA-PSK-CCMP][ESS]
                 * 【WiFi扫描】R2，加密= [WPA2-PSK-CCMP+TKIP][RSN-PSK-CCMP+TKIP][WPA-PSK-CCMP+TKIP][ESS]
                 * 【WiFi扫描】hhSTAFF，加密= [WPA-PSK-CCMP][ESS]
                 * 【WiFi扫描】hhSTAFF，加密= [WPA-PSK-CCMP][ESS]
                 * 【WiFi扫描】ChinaNet-aZ7Q，加密= [WPA2-PSK-CCMP+TKIP][RSN-PSK-CCMP+TKIP][WPA-PSK-CCMP+TKIP][ESS][WPS]
                 * 【WiFi扫描】Clifford-Group-Center，加密= [WPA2-PSK-CCMP][RSN-PSK-CCMP][WPA-PSK-CCMP][ESS]
                 * 【WiFi扫描】ChinaNet-aZ7Q-5G，加密= [WPA2-PSK-CCMP+TKIP][RSN-PSK-CCMP+TKIP][WPA-PSK-CCMP+TKIP][ESS][WPS]
                 * 【WiFi扫描】ChinaNet-KJwX-5G，加密= [WPA2-PSK-CCMP+TKIP][RSN-PSK-CCMP+TKIP][WPA-PSK-CCMP+TKIP][ESS][WPS]
                 * 【WiFi扫描】ChinaNet-KJwX，加密= [WPA2-PSK-CCMP+TKIP][RSN-PSK-CCMP+TKIP][WPA-PSK-CCMP+TKIP][ESS][WPS]
                 * 【WiFi扫描】直播专线_5G，加密= [WPA2-PSK-CCMP][RSN-PSK-CCMP][WPA-PSK-CCMP][ESS][WPS]
                 */
                if (StringUtils.containsByUpper(capabilities, Constant.CAPABILITIES_WPA)){
                    LogUtils.w("【WiFi加密】WPA= " + selected.SSID);
                    boolean b = ConnectTest.wifi连接WPA加密_connectToWPAWiFi(this, selected.SSID, password);
                    LogUtils.w("【】连接成功= " + b);
                } else if (StringUtils.containsByUpper(capabilities, Constant.CAPABILITIES_WEP)) {
                    LogUtils.w("【WiFi加密】WEP= " + selected.SSID);
                    boolean b = ConnectTest.wifi连接WEP加密_connectToWEPWiFi(this, selected.SSID, password);
                    LogUtils.w("【】连接成功= " + b);
                }
                else {
                    LogUtils.w("【WiFi加密】没有密码= " + selected.SSID);
                    boolean b = ConnectTest.wifi连接无密_connectToOpenWiFi(this, selected.SSID);
                    LogUtils.w("【】连接成功= " + b);
                }
            }




            //Connect.wifi连接无密_connectToOpenWiFi(this, selected.SSID);
        } else {
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = "\"" + ssid + "\"";
            wifiConfig.preSharedKey = "\"" + password + "\"";

            int netId = wifiManager.addNetwork(wifiConfig);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();

            Toast.makeText(getApplicationContext(), "Connecting to WiFi: " + ssid, Toast.LENGTH_SHORT).show();
        }
    }
}

