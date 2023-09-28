package android.net.wifi.scan;

import android.net.wifi.ScanResult;

import java.util.List;

public interface ScannerListener {
    void onScan(List<ScanResult> scanResultList);
}
