package android.net.wifi.utils;

public interface IWifi {

    /** 开始连接 */
    public static final int CONNECT_START = 1;






    /** 已连接。 */
    int CONNECT_FINISH = 200;
    /** WiFi模块可用。 */
    int ENABLED = 201;
    /** WiFi已断开。 */
   int DISCONNECTED = 202;


    int code_strat = 300;

    int code_unenabled = 400;
    int code_noHava = 401;
    int code_Thread = 402;
    /** 连接失败：系统已存在相同Wifi配置（需手动删除已存储连接） */
    int ERROR_CONNECT_SYS_EXISTS_SAME_CONFIG = 403;
    /** 连接失败 */
    int ERROR_CONNECT = 407;
    /** 创建WiFi配置为空。 */
    int Not = 404;
    /** WiFi模块(不)可用。 */
    int ERROR_DEVICE_NOT_HAVE_WIFI = 405;
    /** 密码错误 */
    int ERROR_PASSWORD = 406;


    int code_end = 500;


    void msg(int code, String msg);
    void err(int code, Throwable t);
}
