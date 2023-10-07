package android.net.wifi.utils;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.content.Context.WIFI_SERVICE;

import android.annotation.SuppressLint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import androidx.annotation.RequiresPermission;

import com.blankj.utilcode.log.LogUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class NetworkUtils {

    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, ACCESS_COARSE_LOCATION})
    @SuppressLint("MissingPermission")
    public static WifiScanResults getWifiScanResult() {
        WifiScanResults result = new WifiScanResults();
        if (!NetworkUtils.getWifiEnabled()) return result;
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) ContextProvider.CONTEXT.getSystemService(WIFI_SERVICE);
        //noinspection ConstantConditions
        List<ScanResult> results = wm.getScanResults();
        if (results != null) {
            result.setAllResults(results);
        }
        return result;
    }


    public static final class WifiScanResults {

        private List<ScanResult> allResults    = new ArrayList<>();
        private List<ScanResult> filterResults = new ArrayList<>();

        public WifiScanResults() {
        }

        public List<ScanResult> getAllResults() {
            return allResults;
        }

        public List<ScanResult> getFilterResults() {
            return filterResults;
        }

        public void setAllResults(List<ScanResult> allResults) {
            this.allResults = allResults;
            filterResults = filterScanResult(allResults);
        }

        private static List<ScanResult> filterScanResult(final List<ScanResult> results) {
            if (results == null || results.isEmpty()) {
                return new ArrayList<>();
            }
            LinkedHashMap<String, ScanResult> map = new LinkedHashMap<>(results.size());
            for (ScanResult result : results) {
                if (TextUtils.isEmpty(result.SSID)) {
                    continue;
                }
                ScanResult resultInMap = map.get(result.SSID);
                if (resultInMap != null && resultInMap.level >= result.level) {
                    continue;
                }
                map.put(result.SSID, result);
            }
            return new ArrayList<>(map.values());
        }

    }








    @RequiresPermission(ACCESS_WIFI_STATE)
    public static WifiManager getWifiManager() {
        @SuppressLint("WifiManagerLeak")
        WifiManager manager = (WifiManager) ContextProvider.CONTEXT.getSystemService(WIFI_SERVICE);
        return manager;
    }

    /**
     * Return whether wifi is enabled.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />}</p>
     *
     * @return {@code true}: enabled<br>{@code false}: disabled
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static boolean getWifiEnabled() {
        if (getWifiManager() == null) return false;
        return getWifiManager().isWifiEnabled();
    }

    /**
     * Return the ssid.
     *
     * @return the ssid.
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getSSID() {
        if (getWifiManager() == null) return "";
        WifiInfo wi = getWifiManager().getConnectionInfo();
        if (wi == null) return "";
        String ssid = wi.getSSID();
        if (TextUtils.isEmpty(ssid)) {
            return "";
        }
        if (ssid.length() > 2 && ssid.charAt(0) == '"' && ssid.charAt(ssid.length() - 1) == '"') {
            return ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    /**
     * Enable or disable wifi.
     * <p>Must hold {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />}</p>
     *
     * @param enabled True to enabled, false otherwise.
     */
//    @RequiresPermission(CHANGE_WIFI_STATE)
    @SuppressLint("MissingPermission")
    public static void setWifiEnabled(final boolean enabled) {
        if (getWifiManager() == null) return;
        if (enabled == getWifiManager().isWifiEnabled()) return;
        getWifiManager().setWifiEnabled(enabled);
    }

    @SuppressLint("MissingPermission")
    public static boolean openWifi() {
        if (getWifiManager() == null) return false;
        boolean bRet = getWifiManager().setWifiEnabled(true);

        return bRet;
    }

    @SuppressLint("MissingPermission")
    public static boolean closeWifi() {
        if (getWifiManager() == null) return false;
        boolean bRet = getWifiManager().setWifiEnabled(false);

        return bRet;
    }



















    /**
     * 获取ssid的加密方式
     */
    @SuppressLint("MissingPermission")
    public static String getCipherType(String ssid) {
        List<ScanResult> list = getWifiScanResult().getAllResults();
        for (ScanResult scResult : list) {
            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                String capabilities = scResult.capabilities.toUpperCase();
                if (!TextUtils.isEmpty(capabilities)) {

                    if (capabilities.contains(Constant.CAPABILITIES_WPA)) {
                        LogUtils.d("【加密方式】", "WPA");
                        return Constant.CAPABILITIES_WPA;
                    } else if (capabilities.contains(Constant.CAPABILITIES_WEP)) {
                        LogUtils.d("【加密方式】", "WEP");
                        return Constant.CAPABILITIES_WEP;
                    } else {
                        LogUtils.d("【加密方式】", "NOPASS");
                        return Constant.CAPABILITIES_NOPASS;
                    }
                }
            }
        }
        return Constant.CAPABILITIES_INVALID;
    }

    /**
     * 查看以前是否也配置过这个网络
     *
     * @param ssid
     * @return
     */
    @SuppressLint("MissingPermission")
    public static WifiConfiguration isExsits(String ssid) {
        List<WifiConfiguration> existingConfigs = NetworkUtils.getWifiManager().getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * 创建WiFi配置。
     *
     * @param ssid 名称。
     * @param password 秘密。
     * @param type 加密类型。
     * @return WiFi配置。
     */
    public static WifiConfiguration createWifiInfo(final String ssid, final String password, final String type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        // config.SSID = SSID;


        if (type.equals(Constant.CAPABILITIES_NOPASS)) {// nopass
            // config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            // config.wepTxKeyIndex = 0;
        } else if (type.equals(Constant.CAPABILITIES_WEP)) {// wep
            if (!TextUtils.isEmpty(password)) {
                if (isHexWepKey(password)) {
                    config.wepKeys[0] = password;
                } else {
                    config.wepKeys[0] = "\"" + password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type.equals(Constant.CAPABILITIES_WPA)) {// wpa
            config.preSharedKey = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    /**
     * 获取连接的Wifi的真实SSID：
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getNowLinkedWifiSSID(){
        String linkedWifiSSID;
        //通过以下方法获取连接的Wifi的真实SSID：
        //因部分手机系统限制，直接获取SSID可能是“unknow ssid”，此方法原理是通过获取已连接的Wifi的网络ID，然后去已保存的Wifi信息库中查找相同的网络ID的wifi信息的SSID
        WifiManager my_wifiManager = ((WifiManager) ContextProvider.CONTEXT.getSystemService(WIFI_SERVICE));
        assert my_wifiManager != null;
        android.net.wifi.WifiInfo wifiInfo = my_wifiManager.getConnectionInfo();
        linkedWifiSSID = wifiInfo.getSSID();
        int networkId = wifiInfo.getNetworkId();
        List<WifiConfiguration> configuredNetworks = my_wifiManager.getConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : configuredNetworks) {
            if (wifiConfiguration.networkId == networkId) {
                linkedWifiSSID = wifiConfiguration.SSID;
                break;
            }
        }
        return linkedWifiSSID;
    }


    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }

        return true;
    }
}
