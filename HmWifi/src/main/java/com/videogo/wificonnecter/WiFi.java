package com.videogo.wificonnecter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.NetworkUtils;
import com.videogo.util.StringUtils;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes5.dex */
public class WiFi {
    public static final String IEEE8021X = "IEEE8021X";
    public static final int MAX_PRIORITY = 99999;
    public static final String OPEN = "Open";
    public static final String TAG = "Wifi Connecter";
    public static final String WEP = "WEP";
    public static final int WEP_PASSWORD_ASCII = 1;
    public static final int WEP_PASSWORD_AUTO = 0;
    public static final int WEP_PASSWORD_HEX = 2;
    public static final String WPA = "WPA";
    public static final String WPA2 = "WPA2";
    public static final String WPA_EAP = "WPA-EAP";
    public static final String[] EAP_METHOD = {"PEAP", "TLS", "TTLS"};
    public static final String[] SECURITY_MODES = {"WEP", "WPA", "WPA2", "WPA-EAP", "IEEE8021X"};

    public static boolean changePasswordAndConnect(WifiManager wifiManager, WifiConfiguration wifiConfiguration, String str) {
        setupSecurity(wifiConfiguration, getWifiConfigurationSecurity(wifiConfiguration), str);
        if (wifiManager.updateNetwork(wifiConfiguration) == -1) {
            int addNetwork = wifiManager.addNetwork(wifiConfiguration);
            if (addNetwork != -1) {
                return wifiManager.enableNetwork(addNetwork, true);
            }
            return false;
        }
        return connectToConfiguredNetwork(wifiManager, wifiConfiguration, true);
    }

    @SuppressLint("MissingPermission")
    public static boolean checkForExcessOpenNetworkAndSave(WifiManager wifiManager, int i) {
//        List<WifiConfiguration> configuredNetworks = PrivacyMethod.getConfiguredNetworks(wifiManager);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();

        boolean z = false;
        if (configuredNetworks == null) {
            return false;
        }
        sortByPriority(configuredNetworks);
        int i2 = 0;
        for (int size = configuredNetworks.size() - 1; size >= 0; size--) {
            WifiConfiguration wifiConfiguration = configuredNetworks.get(size);
            if (getWifiConfigurationSecurity(wifiConfiguration).equals("Open") && (i2 = i2 + 1) >= i) {
                wifiManager.removeNetwork(wifiConfiguration.networkId);
                z = true;
            }
        }
        if (z) {
            return wifiManager.saveConfiguration();
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    public static boolean configWifiInfo(WifiManager wifiManager, String str, String str2, ScanResult scanResult) {
        WifiConfiguration wifiConfiguration;
        int type = getType(scanResult);
        if (wifiManager != null) {
//            Iterator<WifiConfiguration> it = PrivacyMethod.getConfiguredNetworks(wifiManager).iterator();
            Iterator<WifiConfiguration> it = wifiManager.getConfiguredNetworks().iterator();

            while (it.hasNext()) {
                wifiConfiguration = it.next();
                if (wifiConfiguration != null) {
                    String str3 = wifiConfiguration.SSID;
                    if (str3.equals("\"" + str + "\"")) {
                        break;
                    }
                }
            }
        }
        wifiConfiguration = null;
        if (wifiConfiguration == null) {
            wifiConfiguration = new WifiConfiguration();
        }
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedGroupCiphers.clear();
        wifiConfiguration.allowedKeyManagement.clear();
        wifiConfiguration.allowedPairwiseCiphers.clear();
        wifiConfiguration.allowedProtocols.clear();
        wifiConfiguration.SSID = "\"" + str + "\"";
        if (type == 0) {
            wifiConfiguration.allowedKeyManagement.set(0);
        } else if (type == 1) {
            wifiConfiguration.hiddenSSID = true;
            String[] strArr = wifiConfiguration.wepKeys;
            strArr[0] = "\"" + str2 + "\"";
            wifiConfiguration.allowedAuthAlgorithms.set(1);
            wifiConfiguration.allowedGroupCiphers.set(3);
            wifiConfiguration.allowedGroupCiphers.set(2);
            wifiConfiguration.allowedGroupCiphers.set(0);
            wifiConfiguration.allowedGroupCiphers.set(1);
            wifiConfiguration.allowedKeyManagement.set(0);
            wifiConfiguration.wepTxKeyIndex = 0;
        } else if (type == 2) {
            wifiConfiguration.preSharedKey = "\"" + str2 + "\"";
            wifiConfiguration.hiddenSSID = true;
            wifiConfiguration.allowedAuthAlgorithms.set(0);
            wifiConfiguration.allowedGroupCiphers.set(2);
            wifiConfiguration.allowedKeyManagement.set(1);
            wifiConfiguration.allowedPairwiseCiphers.set(1);
            wifiConfiguration.allowedGroupCiphers.set(3);
            wifiConfiguration.allowedPairwiseCiphers.set(2);
            wifiConfiguration.status = 2;
        }
        int i = wifiConfiguration.networkId;
        if (i == -1) {
            i = wifiManager.addNetwork(wifiConfiguration);
        }
        return wifiManager.enableNetwork(i, true);
    }

    public static boolean connectToConfiguredNetwork(WifiManager wifiManager, WifiConfiguration wifiConfiguration, boolean z) {
        String wifiConfigurationSecurity = getWifiConfigurationSecurity(wifiConfiguration);
        int i = wifiConfiguration.priority;
        int maxPriority = getMaxPriority(wifiManager) + 1;
        if (maxPriority > 99999) {
            maxPriority = shiftPriorityAndSave(wifiManager);
            wifiConfiguration = getWifiConfiguration(wifiManager, wifiConfiguration, wifiConfigurationSecurity);
            if (wifiConfiguration == null) {
                return false;
            }
        }
        wifiConfiguration.priority = maxPriority;
        int updateNetwork = wifiManager.updateNetwork(wifiConfiguration);
        if (updateNetwork != -1) {
            if (!wifiManager.enableNetwork(updateNetwork, false)) {
                wifiConfiguration.priority = i;
                return false;
            } else if (!wifiManager.saveConfiguration()) {
                wifiConfiguration.priority = i;
                return false;
            } else {
                wifiConfiguration = getWifiConfiguration(wifiManager, wifiConfiguration, wifiConfigurationSecurity);
                if (wifiConfiguration == null) {
                    return false;
                }
            }
        }
        if (wifiManager.enableNetwork(wifiConfiguration.networkId, true)) {
            return z ? wifiManager.reassociate() : wifiManager.reconnect();
        }
        return false;
    }

    public static boolean connectToNewNetwork(WifiManager wifiManager, ScanResult scanResult, String str) {
        WifiConfiguration wifiConfiguration;
        String scanResultSecurity = getScanResultSecurity(scanResult);
        if (scanResultSecurity.equals("Open")) {
            checkForExcessOpenNetworkAndSave(wifiManager, 10);
        }
        WifiConfiguration wifiConfiguration2 = getWifiConfiguration(wifiManager, scanResult, scanResultSecurity);
        if (wifiConfiguration2 != null && !wifiManager.removeNetwork(wifiConfiguration2.networkId)) {
            return changePasswordAndConnect(wifiManager, wifiConfiguration2, str);
        }
        WifiConfiguration wifiConfiguration3 = new WifiConfiguration();
        wifiConfiguration3.SSID = StringUtils.convertToQuotedString(scanResult.SSID);
        wifiConfiguration3.BSSID = scanResult.BSSID;
        setupSecurity(wifiConfiguration3, scanResultSecurity, str);
        if (wifiManager.addNetwork(wifiConfiguration3) == -1 || !wifiManager.saveConfiguration() || (wifiConfiguration = getWifiConfiguration(wifiManager, wifiConfiguration3, scanResultSecurity)) == null) {
            return false;
        }
        return connectToConfiguredNetwork(wifiManager, wifiConfiguration, true);
    }

    @SuppressLint("MissingPermission")
    public static int getMaxPriority(WifiManager wifiManager) {
//        List<WifiConfiguration> configuredNetworks = PrivacyMethod.getConfiguredNetworks(wifiManager);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();

        int i = 0;
        if (configuredNetworks == null) {
            return 0;
        }
        for (WifiConfiguration wifiConfiguration : configuredNetworks) {
            int i2 = wifiConfiguration.priority;
            if (i2 > i) {
                i = i2;
            }
        }
        return i;
    }

    @SuppressLint("MissingPermission")
    public static String getSSIDSecurity(Context context, String str) {
        List<ScanResult> scanResults;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        if (wifiManager != null && (scanResults = PrivacyMethod.getScanResults(wifiManager)) != null) {
        if (wifiManager != null && (scanResults = wifiManager.getScanResults()) != null) {
            for (ScanResult scanResult : scanResults) {
                if (isSsidEquals(str, scanResult.SSID, true)) {
                    return getScanResultSecurity(scanResult);
                }
            }
        }
        return "";
    }

    public static String getScanResultSecurity(ScanResult scanResult) {
        String str = scanResult.capabilities;
        for (int length = SECURITY_MODES.length - 1; length >= 0; length--) {
            if (str.contains(SECURITY_MODES[length])) {
                return SECURITY_MODES[length];
            }
        }
        return "Open";
    }

    @SuppressLint("MissingPermission")
    public static String getSecurityType(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return "Open";
        }
//        WifiInfo connectionInfo = PrivacyMethod.getConnectionInfo(wifiManager);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
//        List<WifiConfiguration> configuredNetworks = PrivacyMethod.getConfiguredNetworks(wifiManager);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();

        if (connectionInfo != null && configuredNetworks != null) {
            for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                if (NetworkUtils.getSSID().replace("\"", "").equals(wifiConfiguration.SSID.replace("\"", "")) && connectionInfo.getNetworkId() == wifiConfiguration.networkId) {
                    return getWifiConfigurationSecurity(wifiConfiguration);
                }
            }
        }
        return "Open";
    }

    public static int getType(ScanResult scanResult) {
        if (scanResult.capabilities.contains("WPA")) {
            return 2;
        }
        return scanResult.capabilities.contains("WEP") ? 1 : 0;
    }

    @SuppressLint("MissingPermission")
    public static WifiConfiguration getWifiConfiguration(WifiManager wifiManager, ScanResult scanResult, String str) {
        String str2;
        String str3;
        String convertToQuotedString = StringUtils.convertToQuotedString(scanResult.SSID);
        if (convertToQuotedString.length() == 0 || (str2 = scanResult.BSSID) == null) {
            return null;
        }
        if (str == null) {
            str = getScanResultSecurity(scanResult);
        }
//        List<WifiConfiguration> configuredNetworks = PrivacyMethod.getConfiguredNetworks(wifiManager);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();

        if (configuredNetworks == null) {
            return null;
        }
        for (WifiConfiguration wifiConfiguration : configuredNetworks) {
            String str4 = wifiConfiguration.SSID;
            if (str4 != null && convertToQuotedString.equals(str4) && ((str3 = wifiConfiguration.BSSID) == null || str3.equals("any") || str2.equals(wifiConfiguration.BSSID))) {
                if (str.equals(getWifiConfigurationSecurity(wifiConfiguration))) {
                    return wifiConfiguration;
                }
            }
        }
        return null;
    }

    public static String getWifiConfigurationSecurity(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.allowedKeyManagement.get(0)) {
            return (wifiConfiguration.allowedGroupCiphers.get(3) || !(wifiConfiguration.allowedGroupCiphers.get(0) || wifiConfiguration.allowedGroupCiphers.get(1))) ? "Open" : "WEP";
        } else if (wifiConfiguration.allowedProtocols.get(1)) {
            return "WPA2";
        } else {
            if (wifiConfiguration.allowedKeyManagement.get(2)) {
                return "WPA-EAP";
            }
            if (wifiConfiguration.allowedKeyManagement.get(3)) {
                return "IEEE8021X";
            }
            if (wifiConfiguration.allowedProtocols.get(0)) {
                return "WPA";
            }
            Log.w("Wifi Connecter", "Unknown security type from WifiConfiguration, falling back on open.");
            return "Open";
        }
    }

    public static boolean isHex(String str) {
        for (int length = str.length() - 1; length >= 0; length--) {
            char charAt = str.charAt(length);
            if ((charAt < '0' || charAt > '9') && ((charAt < 'A' || charAt > 'F') && (charAt < 'a' || charAt > 'f'))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isHexWepKey(String str) {
        int length = str.length();
        if (length == 10 || length == 26 || length == 58) {
            return isHex(str);
        }
        return false;
    }

    public static boolean isSsidEquals(String str, String str2, boolean z) {
        boolean equals;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return false;
        }
        if (z) {
            equals = str.equalsIgnoreCase(str2);
        } else {
            equals = str.equals(str2);
        }
        if (equals) {
            return equals;
        }
        String convertToQuotedString = StringUtils.convertToQuotedString(str);
        return z ? convertToQuotedString.equalsIgnoreCase(str2) : convertToQuotedString.equals(str2);
    }

    public static void setupSecurity(WifiConfiguration wifiConfiguration, String str, String str2) {
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedGroupCiphers.clear();
        wifiConfiguration.allowedKeyManagement.clear();
        wifiConfiguration.allowedPairwiseCiphers.clear();
        wifiConfiguration.allowedProtocols.clear();
        if (TextUtils.isEmpty(str)) {
            Log.w("Wifi Connecter", "Empty security, assuming open");
            str = "Open";
        }
        if (str.equals("WEP")) {
            if (!TextUtils.isEmpty(str2)) {
                if (isHexWepKey(str2)) {
                    wifiConfiguration.wepKeys[0] = str2;
                } else {
                    wifiConfiguration.wepKeys[0] = StringUtils.convertToQuotedString(str2);
                }
            }
            wifiConfiguration.wepTxKeyIndex = 0;
            wifiConfiguration.allowedAuthAlgorithms.set(0);
            wifiConfiguration.allowedAuthAlgorithms.set(1);
            wifiConfiguration.allowedKeyManagement.set(0);
            wifiConfiguration.allowedGroupCiphers.set(0);
            wifiConfiguration.allowedGroupCiphers.set(1);
        } else if (!str.equals("WPA") && !str.equals("WPA2")) {
            if (str.equals("Open")) {
                wifiConfiguration.allowedKeyManagement.set(0);
            } else if (str.equals("WPA-EAP") || str.equals("IEEE8021X")) {
                wifiConfiguration.allowedGroupCiphers.set(2);
                wifiConfiguration.allowedGroupCiphers.set(3);
                if (str.equals("WPA-EAP")) {
                    wifiConfiguration.allowedKeyManagement.set(2);
                } else {
                    wifiConfiguration.allowedKeyManagement.set(3);
                }
                if (TextUtils.isEmpty(str2)) {
                    return;
                }
                wifiConfiguration.preSharedKey = StringUtils.convertToQuotedString(str2);
            }
        } else {
            wifiConfiguration.allowedGroupCiphers.set(2);
            wifiConfiguration.allowedGroupCiphers.set(3);
            wifiConfiguration.allowedKeyManagement.set(1);
            wifiConfiguration.allowedPairwiseCiphers.set(2);
            wifiConfiguration.allowedPairwiseCiphers.set(1);
            wifiConfiguration.allowedProtocols.set(str.equals("WPA2") ? 1 : 0);
            if (TextUtils.isEmpty(str2)) {
                return;
            }
            if (str2.length() == 64 && isHex(str2)) {
                wifiConfiguration.preSharedKey = str2;
            } else {
                wifiConfiguration.preSharedKey = StringUtils.convertToQuotedString(str2);
            }
        }
    }

    @SuppressLint("MissingPermission") public static int shiftPriorityAndSave(WifiManager wifiManager) {
//        List<WifiConfiguration> configuredNetworks = PrivacyMethod.getConfiguredNetworks(wifiManager);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();

        if (configuredNetworks == null) {
            return 0;
        }
        sortByPriority(configuredNetworks);
        int size = configuredNetworks.size();
        for (int i = 0; i < size; i++) {
            WifiConfiguration wifiConfiguration = configuredNetworks.get(i);
            wifiConfiguration.priority = i;
            wifiManager.updateNetwork(wifiConfiguration);
        }
        wifiManager.saveConfiguration();
        return size;
    }

    public static void sortByPriority(List<WifiConfiguration> list) {
        Collections.sort(list, new Comparator<WifiConfiguration>() { // from class: com.videogo.wificonnecter.WiFi.1
            @Override // java.util.Comparator
            public int compare(WifiConfiguration wifiConfiguration, WifiConfiguration wifiConfiguration2) {
                return wifiConfiguration.priority - wifiConfiguration2.priority;
            }
        });
    }

    @SuppressLint("MissingPermission")
    public static WifiConfiguration getWifiConfiguration(WifiManager wifiManager, WifiConfiguration wifiConfiguration, String str) {
        String str2;
        String str3 = wifiConfiguration.SSID;
        if (str3.length() == 0) {
            return null;
        }
        String str4 = wifiConfiguration.BSSID;
        if (str == null) {
            str = getWifiConfigurationSecurity(wifiConfiguration);
        }
//        List<WifiConfiguration> configuredNetworks = PrivacyMethod.getConfiguredNetworks(wifiManager);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks == null) {
            return null;
        }
        for (WifiConfiguration wifiConfiguration2 : configuredNetworks) {
            String str5 = wifiConfiguration2.SSID;
            if (str5 != null && str3.equals(str5) && ((str2 = wifiConfiguration2.BSSID) == null || str4 == null || str2.equals("any") || str4.equals(wifiConfiguration2.BSSID))) {
                if (str.equals(getWifiConfigurationSecurity(wifiConfiguration2))) {
                    return wifiConfiguration2;
                }
            }
        }
        return null;
    }
}