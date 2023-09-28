package com.hehongdan.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.Connect;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.utils.IWifi;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


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
            wifiList[i] = scanResults.get(i).SSID;
        }

        Spinner wifiSpinner = findViewById(R.id.wifiSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, wifiList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wifiSpinner.setAdapter(adapter);
    }


    private void connectToWiFi(String ssid, String password) {

        if (true) {
            Connect.getInstance()
                    .setListener(new IWifi() {
                        @Override
                        public void msg(int code, String msg) {

                        }

                        @Override
                        public void err(int code, Throwable t) {

                        }
                    })
                    .connect(selected, password);
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

