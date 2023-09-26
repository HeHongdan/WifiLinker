package com.videogo.privacy;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
//import androidx.annotation.Keep;
//import androidx.annotation.RequiresApi;
//import androidx.core.content.ContextCompat;
//import androidx.preference.PreferenceInflater;
//import com.bumptech.glide.manager.DefaultConnectivityMonitorFactory;
//import com.huawei.hms.framework.common.ExceptionCode;
//import com.lanshifu.asm_annotation.AsmMethodReplace;
//import com.thelittlefireman.appkillermanager.devices.Meizu;
//import com.videogo.constant.Config;
//import com.videogo.constant.Constant;
//import com.videogo.util.GlobalVariable;
//import com.videogo.util.LocalInfo;
//import com.videogo.util.PrivacyUtils;
//import com.videogo.util.PrivacyUtilsNew;
//import com.videogo.widget.WebViewEx;
//import com.videogo.ysmp.core.dev.LogFragment;
//import com.xiaomi.mipush.sdk.Constants;
//import com.ys7.sdk.devops.log.YLog;
//import java.io.File;
//import java.lang.reflect.Method;
//import java.net.NetworkInterface;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import kotlin.Metadata;
//import kotlin.collections.CollectionsKt__CollectionsKt;
//import kotlin.jvm.JvmStatic;
//import kotlin.jvm.internal.Intrinsics;
//import kotlin.text.StringsKt__StringsKt;
//import okhttp3.Call;
//import okhttp3.EventListener;
//import okhttp3.internal.connection.RealConnection;
//import okhttp3.internal.http2.Http2Codec;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//@Keep
//@Metadata(bv = {1, 0, 3}, d1 = {"\u0000ª\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\r\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\bÇ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0004H\u0002J\n\u0010\u0010\u001a\u0004\u0018\u00010\u0004H\u0007J\u000e\u0010\u0011\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u0004J\u0016\u0010\u0011\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\fJ\u000e\u0010\u0013\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u0004J\u0010\u0010\u0014\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u0004H\u0002J\u0018\u0010\u0015\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\bH\u0002J\u0018\u0010\u0017\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\bH\u0002JH\u0010\u0018\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\b2\u0006\u0010\u001c\u001a\u00020\b2\u0006\u0010\u001d\u001a\u00020\b2\u0006\u0010\u001e\u001a\u00020\b2\u0006\u0010\u001f\u001a\u00020\f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0007J\n\u0010$\u001a\u0004\u0018\u00010\u0004H\u0007J\u0012\u0010%\u001a\u0004\u0018\u00010\u00042\u0006\u0010&\u001a\u00020'H\u0007J\u0018\u0010(\u001a\n\u0012\u0004\u0012\u00020*\u0018\u00010)2\u0006\u0010&\u001a\u00020+H\u0007J\u0014\u0010,\u001a\u0004\u0018\u00010\u00042\b\u0010&\u001a\u0004\u0018\u00010-H\u0007J\u001d\u0010.\u001a\u0004\u0018\u0001H/\"\u0004\b\u0000\u0010/2\u0006\u00100\u001a\u00020\u0004H\u0002¢\u0006\u0002\u00101J\u0012\u00102\u001a\u0004\u0018\u00010-2\u0006\u00100\u001a\u00020\u0004H\u0007J\u0018\u00103\u001a\n\u0012\u0004\u0012\u000204\u0018\u00010)2\u0006\u00100\u001a\u00020\u0004H\u0002J\u0018\u00105\u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010)2\u0006\u00100\u001a\u00020\u0004H\u0002J\u0010\u00106\u001a\u00020\b2\u0006\u00100\u001a\u00020\u0004H\u0007J\u0018\u00107\u001a\n\u0012\u0004\u0012\u000208\u0018\u00010)2\u0006\u0010&\u001a\u000209H\u0007J\u0012\u0010:\u001a\u0004\u0018\u00010-2\u0006\u0010&\u001a\u000209H\u0007J\u0012\u0010;\u001a\u0004\u0018\u00010\n2\u0006\u0010<\u001a\u00020\u0004H\u0002J\u0012\u0010=\u001a\u0004\u0018\u00010\u00042\u0006\u0010&\u001a\u00020+H\u0007J\u001a\u0010=\u001a\u0004\u0018\u00010\u00042\u0006\u0010&\u001a\u00020+2\u0006\u0010>\u001a\u00020\bH\u0007J\u0012\u0010?\u001a\u0004\u0018\u00010@2\u0006\u0010&\u001a\u000209H\u0007J\n\u0010A\u001a\u0004\u0018\u00010BH\u0007J\u0012\u0010C\u001a\u0004\u0018\u00010D2\u0006\u0010&\u001a\u00020EH\u0007J\u0012\u0010F\u001a\u0004\u0018\u00010\u00042\u0006\u0010&\u001a\u00020+H\u0007J\u001a\u0010F\u001a\u0004\u0018\u00010\u00042\u0006\u0010&\u001a\u00020+2\u0006\u0010>\u001a\u00020\bH\u0007J \u0010G\u001a\n\u0012\u0004\u0012\u000204\u0018\u00010)2\u0006\u0010&\u001a\u00020H2\u0006\u0010I\u001a\u00020\bH\u0007J3\u0010G\u001a\u0004\u0018\u00010\u00012\b\u0010J\u001a\u0004\u0018\u00010\u00012\u0006\u0010K\u001a\u00020L2\u0010\u0010M\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0001\u0018\u00010NH\u0007¢\u0006\u0002\u0010OJ(\u0010P\u001a\n\u0012\u0004\u0012\u000204\u0018\u00010)2\u0006\u0010&\u001a\u00020H2\u0006\u0010I\u001a\u00020\b2\u0006\u0010Q\u001a\u00020\bH\u0007J \u0010R\u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010)2\u0006\u0010&\u001a\u00020H2\u0006\u0010I\u001a\u00020\bH\u0007J3\u0010R\u001a\u0004\u0018\u00010\u00012\b\u0010J\u001a\u0004\u0018\u00010\u00012\u0006\u0010K\u001a\u00020L2\u0010\u0010M\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0001\u0018\u00010NH\u0007¢\u0006\u0002\u0010OJ(\u0010S\u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010)2\u0006\u0010&\u001a\u00020H2\u0006\u0010I\u001a\u00020\b2\u0006\u0010Q\u001a\u00020\bH\u0007J\u001a\u0010T\u001a\u0004\u0018\u00010\u00042\u0006\u0010&\u001a\u00020H2\u0006\u0010<\u001a\u00020\u0004H\u0007J3\u0010T\u001a\u0004\u0018\u00010\u00012\b\u0010J\u001a\u0004\u0018\u00010\u00012\u0006\u0010K\u001a\u00020L2\u0010\u0010M\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0001\u0018\u00010NH\u0007¢\u0006\u0002\u0010OJ\u0012\u0010U\u001a\u00020\b2\b\u0010&\u001a\u0004\u0018\u00010-H\u0007J\u001a\u0010V\u001a\u0004\u0018\u00010W2\u0006\u0010&\u001a\u00020X2\u0006\u0010Y\u001a\u00020\u0004H\u0007J\u001a\u0010Z\u001a\u0004\u0018\u00010[2\u0006\u0010&\u001a\u00020H2\u0006\u0010<\u001a\u00020\u0004H\u0007J\u0012\u0010\\\u001a\u0004\u0018\u00010\u00042\u0006\u0010&\u001a\u00020+H\u0007J\u001e\u0010]\u001a\n\u0012\u0004\u0012\u0002H/\u0018\u00010)\"\u0004\b\u0000\u0010/2\u0006\u00100\u001a\u00020\u0004H\u0002J\u001d\u0010^\u001a\u0004\u0018\u0001H/\"\u0004\b\u0000\u0010/2\u0006\u00100\u001a\u00020\u0004H\u0002¢\u0006\u0002\u00101J\u0014\u0010_\u001a\u0004\u0018\u00010\u00042\b\u0010&\u001a\u0004\u0018\u00010-H\u0007J\u0012\u0010`\u001a\u0004\u0018\u00010\u00042\u0006\u0010&\u001a\u00020+H\u0007J\u001a\u0010`\u001a\u0004\u0018\u00010\u00042\u0006\u0010&\u001a\u00020+2\u0006\u0010>\u001a\u00020\bH\u0007J\u0012\u0010a\u001a\u0004\u0018\u00010\u00042\u0006\u0010&\u001a\u00020+H\u0007J\"\u0010b\u001a\u0004\u0018\u00010\n2\u0006\u0010&\u001a\u00020H2\u0006\u0010<\u001a\u00020\u00042\u0006\u0010I\u001a\u00020\bH\u0007J*\u0010b\u001a\u0004\u0018\u00010\n2\u0006\u0010&\u001a\u00020H2\u0006\u0010<\u001a\u00020\u00042\u0006\u0010I\u001a\u00020\b2\u0006\u0010c\u001a\u00020\bH\u0002J<\u0010b\u001a\u0004\u0018\u00010\u00012\b\u0010J\u001a\u0004\u0018\u00010\u00012\u0006\u0010K\u001a\u00020L2\u0006\u0010<\u001a\u00020\u00042\u0006\u0010I\u001a\u00020\b2\u0006\u0010c\u001a\u00020\b2\u0006\u0010Q\u001a\u00020\bH\u0002J\u0012\u0010d\u001a\u0004\u0018\u00010e2\u0006\u0010&\u001a\u00020fH\u0007J\u0012\u0010g\u001a\u0004\u0018\u00010h2\u0006\u0010&\u001a\u00020fH\u0007J(\u0010i\u001a\n\u0012\u0004\u0012\u00020j\u0018\u00010)2\u0006\u0010&\u001a\u00020k2\u0006\u0010l\u001a\u00020\b2\u0006\u0010I\u001a\u00020\bH\u0007J\u0012\u0010m\u001a\u00020\b2\b\u0010&\u001a\u0004\u0018\u00010-H\u0007J\u0018\u0010n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010o0)2\u0006\u0010&\u001a\u00020kH\u0007J \u0010p\u001a\n\u0012\u0004\u0012\u00020q\u0018\u00010)2\u0006\u0010&\u001a\u00020k2\u0006\u0010l\u001a\u00020\bH\u0007J\u0014\u0010r\u001a\u0004\u0018\u00010\u00042\b\u0010&\u001a\u0004\u0018\u00010-H\u0007J\u0018\u0010s\u001a\n\u0012\u0004\u0012\u00020t\u0018\u00010)2\u0006\u0010&\u001a\u000209H\u0007J \u0010u\u001a\n\u0012\u0004\u0012\u00020v\u0018\u00010)2\u0006\u0010&\u001a\u00020w2\u0006\u0010x\u001a\u00020\bH\u0007J\n\u0010y\u001a\u0004\u0018\u00010\u0004H\u0007J\u0012\u0010z\u001a\u0004\u0018\u00010\u00042\u0006\u0010&\u001a\u00020+H\u0007J\u0012\u0010{\u001a\u0004\u0018\u00010\u00042\u0006\u0010&\u001a\u00020+H\u0007J\u0010\u0010|\u001a\u00020\b2\u0006\u0010&\u001a\u00020+H\u0007J\u001a\u0010}\u001a\u0004\u0018\u00010\u00042\u0006\u0010~\u001a\u00020\u007f2\u0006\u0010\u000f\u001a\u00020\u0004H\u0007J\u0013\u0010\u0080\u0001\u001a\u0004\u0018\u00010\u00042\u0006\u0010&\u001a\u00020+H\u0007J\u001b\u0010\u0081\u0001\u001a\u0004\u0018\u00010\u00042\u0006\u0010~\u001a\u00020\u007f2\u0006\u0010\u000f\u001a\u00020\u0004H\u0007J\u0014\u0010\u0082\u0001\u001a\u0005\u0018\u00010\u0083\u00012\u0006\u0010&\u001a\u00020fH\u0007J\u0011\u0010\u0084\u0001\u001a\u00020\b2\u0006\u0010&\u001a\u000209H\u0007J\u0012\u0010\u0085\u0001\u001a\u00020\f2\u0007\u0010\u0086\u0001\u001a\u00020[H\u0002J\u0011\u0010\u0087\u0001\u001a\u00020\b2\u0006\u00100\u001a\u00020\u0004H\u0007J\t\u0010\u0088\u0001\u001a\u00020\fH\u0002J\u0011\u0010\u0089\u0001\u001a\u00020\f2\u0006\u0010&\u001a\u000209H\u0007J\u0012\u0010\u008a\u0001\u001a\u00020\u000e2\u0007\u0010\u008b\u0001\u001a\u00020\u0004H\u0002J\u0012\u0010\u008c\u0001\u001a\u00020\u000e2\u0007\u0010\u008b\u0001\u001a\u00020\u0004H\u0002J\u0012\u0010\u008d\u0001\u001a\u00020\u000e2\u0007\u0010\u008b\u0001\u001a\u00020\u0004H\u0002J\u000b\u0010\u008e\u0001\u001a\u0004\u0018\u00010\u0004H\u0007J\u001a\u0010\u008f\u0001\u001a\u00020\u000e2\u0006\u00100\u001a\u00020\u00042\u0007\u0010\u0086\u0001\u001a\u00020[H\u0002J\u000b\u0010\u0090\u0001\u001a\u0004\u0018\u00010\u0004H\u0007J&\u0010\u0091\u0001\u001a\u0002H/\"\u0004\b\u0000\u0010/2\u0006\u00100\u001a\u00020\u00042\u0007\u0010\u0092\u0001\u001a\u0002H/H\u0002¢\u0006\u0003\u0010\u0093\u0001J&\u0010\u0094\u0001\u001a\u0002H/\"\u0004\b\u0000\u0010/2\u0006\u00100\u001a\u00020\u00042\u0007\u0010\u0092\u0001\u001a\u0002H/H\u0002¢\u0006\u0003\u0010\u0093\u0001J+\u0010\u0095\u0001\u001a\u000b\u0012\u0007\u0012\u0005\u0018\u00010\u0096\u00010)2\u0006\u0010&\u001a\u00020H2\u0007\u0010\u0086\u0001\u001a\u00020[2\u0006\u0010I\u001a\u00020\bH\u0007JP\u0010\u0097\u0001\u001a\u000b\u0012\u0007\u0012\u0005\u0018\u00010\u0096\u00010)2\u0006\u0010&\u001a\u00020H2\n\u0010\u0098\u0001\u001a\u0005\u0018\u00010\u0099\u00012\u0011\u0010\u009a\u0001\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010[\u0018\u00010N2\u0007\u0010\u0086\u0001\u001a\u00020[2\u0006\u0010I\u001a\u00020\bH\u0007¢\u0006\u0003\u0010\u009b\u0001J+\u0010\u009c\u0001\u001a\u000b\u0012\u0007\u0012\u0005\u0018\u00010\u0096\u00010)2\u0006\u0010&\u001a\u00020H2\u0007\u0010\u0086\u0001\u001a\u00020[2\u0006\u0010I\u001a\u00020\bH\u0007J7\u0010\u009d\u0001\u001a\u00020\u000e2\u0006\u0010&\u001a\u00020X2\u0006\u0010Y\u001a\u00020\u00042\b\u0010\u009e\u0001\u001a\u00030\u009f\u00012\b\u0010 \u0001\u001a\u00030¡\u00012\b\u0010¢\u0001\u001a\u00030£\u0001H\u0007J%\u0010¤\u0001\u001a\u0005\u0018\u00010\u0096\u00012\u0006\u0010&\u001a\u00020H2\u0007\u0010\u0086\u0001\u001a\u00020[2\u0006\u0010I\u001a\u00020\bH\u0007J\u000b\u0010¥\u0001\u001a\u0004\u0018\u00010\u0004H\u0007J\u001a\u0010¦\u0001\u001a\u00020\u000e2\u0006\u0010&\u001a\u00020f2\u0007\u0010§\u0001\u001a\u00020eH\u0007J\u001b\u0010¨\u0001\u001a\u00020\u000e2\u0006\u0010&\u001a\u00020f2\b\u0010§\u0001\u001a\u00030\u0083\u0001H\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u001a\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\b0\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006©\u0001"}, d2 = {"Lcom/videogo/privacy/PrivacyMethod;", "", "()V", YLog.TAG_DEFAULT, "", "anyCache", "Ljava/util/concurrent/ConcurrentHashMap;", "countCache", "", "defaultPackageInfo", "Landroid/content/pm/PackageInfo;", "isSaveWifiInfo", "", "addCount", "", "name", Constants.PHONE_BRAND, "checkAgreePrivacy", "checkBackground", "checkAppListPrivacy", "checkBackgroundPrivacy", "checkBgCountPrivacy", "maxCount", "checkCount", ExceptionCode.CONNECT, Http2Codec.CONNECTION, "Lokhttp3/internal/connection/RealConnection;", "connectTimeout", "readTimeout", "writeTimeout", "pingIntervalMillis", "connectionRetryEnabled", "call", "Lokhttp3/Call;", "eventListener", "Lokhttp3/EventListener;", "fingerprint", "getAddress", "manager", "Landroid/bluetooth/BluetoothAdapter;", "getAllCellInfo", "", "Landroid/telephony/CellInfo;", "Landroid/telephony/TelephonyManager;", "getBSSID", "Landroid/net/wifi/WifiInfo;", "getCache", "T", "key", "(Ljava/lang/String;)Ljava/lang/Object;", "getCacheConnectionInfo", "getCacheInstalledApplications", "Landroid/content/pm/ApplicationInfo;", "getCacheInstalledPackages", "getCacheWifiState", "getConfiguredNetworks", "Landroid/net/wifi/WifiConfiguration;", "Landroid/net/wifi/WifiManager;", "getConnectionInfo", "getDefaultPackageInfo", Meizu.MEIZU_DEFAULT_EXTRA_PACKAGE, "getDeviceId", "index", "getDhcpInfo", "Landroid/net/DhcpInfo;", "getExternalStorageDirectory", "Ljava/io/File;", "getHardwareAddress", "", "Ljava/net/NetworkInterface;", "getImei", "getInstalledApplications", "Landroid/content/pm/PackageManager;", "flags", "oldPackageManager", "method", "Ljava/lang/reflect/Method;", WebViewEx.KEY_ARG_ARRAY, "", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", "getInstalledApplicationsAsUser", "userId", "getInstalledPackages", "getInstalledPackagesAsUser", "getInstallerPackageName", "getIpAddress", "getLastKnownLocation", "Landroid/location/Location;", "Landroid/location/LocationManager;", "provider", "getLaunchIntentForPackage", "Landroid/content/Intent;", "getLine1Number", "getListCache", "getLocalCache", "getMacAddress", "getMeid", "getNetworkOperatorName", "getPackageInfo", "allFlags", "getPrimaryClip", "Landroid/content/ClipData;", "Landroid/content/ClipboardManager;", "getPrimaryClipDescription", "Landroid/content/ClipDescription;", "getRecentTasks", "Landroid/app/ActivityManager$RecentTaskInfo;", "Landroid/app/ActivityManager;", "maxNum", "getRssi", "getRunningAppProcesses", "Landroid/app/ActivityManager$RunningAppProcessInfo;", "getRunningTasks", "Landroid/app/ActivityManager$RunningTaskInfo;", "getSSID", "getScanResults", "Landroid/net/wifi/ScanResult;", "getSensorList", "Landroid/hardware/Sensor;", "Landroid/hardware/SensorManager;", "type", "getSerial", "getSimOperator", "getSimSerialNumber", "getSimState", "getString", "resolver", "Landroid/content/ContentResolver;", "getSubscriberId", "getSystemString", "getText", "", "getWifiState", "isAndroidIntent", PreferenceInflater.INTENT_TAG_NAME, "isCacheWifiEnabled", "isUseCache", "isWifiEnabled", "logD", LogFragment.LOG, "logI", "logW", "model", "printIntent", "product", "putCache", "value", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", "putLocalCache", "queryIntentActivities", "Landroid/content/pm/ResolveInfo;", "queryIntentActivityOptions", "caller", "Landroid/content/ComponentName;", "specifics", "(Landroid/content/pm/PackageManager;Landroid/content/ComponentName;[Landroid/content/Intent;Landroid/content/Intent;I)Ljava/util/List;", "queryIntentServices", "requestLocationUpdates", "minTime", "", "minDistance", "", "listener", "Landroid/location/LocationListener;", "resolveService", "serial", "setPrimaryClip", "clip", "setText", "iot-push_release"}, k = 1, mv = {1, 4, 2})
/* loaded from: classes2.dex */
public final class PrivacyMethod {
//    public static final String TAG = "PrivacyMethod";
//    public static PackageInfo defaultPackageInfo;
//    public static boolean isSaveWifiInfo;
//    @NotNull
//    public static final PrivacyMethod INSTANCE = new PrivacyMethod();
//    public static ConcurrentHashMap<String, Object> anyCache = new ConcurrentHashMap<>();
//    public static final ConcurrentHashMap<String, Integer> countCache = new ConcurrentHashMap<>();
//
//    private final void addCount(String str) {
//        Integer num = countCache.get(str);
//        if (num == null) {
//            num = 0;
//        }
//        Intrinsics.checkNotNullExpressionValue(num, "countCache[name] ?: 0");
//        int intValue = num.intValue() + 1;
//        countCache.put(str, Integer.valueOf(intValue));
//        logD(str + ": addCount= " + intValue);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 178, oriClass = Build.class, oriDesc = "Ljava/lang/String;", oriMethod = "BRAND")
//    @Nullable
//    public static final String brand() {
//        String str = GlobalVariable.BUILD_BRAND.get();
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy(Constants.PHONE_BRAND, false)) {
//            GlobalVariable<Integer> globalVariable = GlobalVariable.GET_BRAND_TIMES;
//            globalVariable.set(Integer.valueOf(globalVariable.get().intValue() + 1));
//            String str2 = Build.BRAND;
//            GlobalVariable.BUILD_BRAND.set(str2);
//            return str2;
//        }
//        return "";
//    }
//
//    private final boolean checkBackgroundPrivacy(String str) {
//        return checkAgreePrivacy(str);
//    }
//
//    private final boolean checkBgCountPrivacy(String str, int i) {
//        return checkBackgroundPrivacy(str) && checkCount(str, i);
//    }
//
//    private final boolean checkCount(String str, int i) {
//        Integer num = countCache.get(str);
//        if (num == null) {
//            num = 0;
//        }
//        Intrinsics.checkNotNullExpressionValue(num, "countCache[name] ?: 0");
//        if (num.intValue() >= i) {
//            try {
//                Boolean bool = GlobalVariable.PRIVACY_COUNT_ENABLE.get();
//                Intrinsics.checkNotNullExpressionValue(bool, "GlobalVariable.PRIVACY_COUNT_ENABLE.get()");
//                if (bool.booleanValue()) {
//                    logD(str + ": count stack= " + Log.getStackTraceString(new Throwable()));
//                    return false;
//                }
//            } catch (Exception unused) {
//                logD(str + ": checkCount error= " + Log.getStackTraceString(new Throwable()));
//            }
//        }
//        return true;
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = RealConnection.class)
//    public static final void connect(@NotNull RealConnection connection, int i, int i2, int i3, int i4, boolean z, @NotNull Call call, @NotNull EventListener eventListener) {
//        Intrinsics.checkNotNullParameter(connection, "connection");
//        Intrinsics.checkNotNullParameter(call, "call");
//        Intrinsics.checkNotNullParameter(eventListener, "eventListener");
//        connection.connect(i, i2, i3, i4, z, call, eventListener);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 178, oriClass = Build.class, oriDesc = "Ljava/lang/String;", oriMethod = "FINGERPRINT")
//    @Nullable
//    public static final String fingerprint() {
//        String str = (String) INSTANCE.getCache("fingerprint");
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("fingerprint")) {
//            return (String) INSTANCE.putCache("fingerprint", Build.FINGERPRINT);
//        }
//        return "";
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = BluetoothAdapter.class)
//    @SuppressLint({"HardwareIds", "MissingPermission"})
//    @Nullable
//    public static final String getAddress(@NotNull BluetoothAdapter manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        if (!INSTANCE.checkAgreePrivacy("getAddress")) {
//            String str = (String) INSTANCE.getCache("getAddress");
//            return str != null ? str : "";
//        }
//        LocalInfo localInfo = LocalInfo.getInstance();
//        Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//        if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.BLUETOOTH") != 0) {
//            return "";
//        }
//        return (String) INSTANCE.putCache("getAddress", manager.getAddress());
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = TelephonyManager.class)
//    @SuppressLint({"MissingPermission"})
//    @Nullable
//    public static final List<CellInfo> getAllCellInfo(@NotNull TelephonyManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        List<CellInfo> listCache = INSTANCE.getListCache("getAllCellInfo");
//        if (listCache != null) {
//            return listCache;
//        }
//        if (!INSTANCE.checkAgreePrivacy("getAllCellInfo")) {
//            return CollectionsKt__CollectionsKt.emptyList();
//        }
//        LocalInfo localInfo = LocalInfo.getInstance();
//        Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//        if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.ACCESS_FINE_LOCATION") != 0) {
//            return CollectionsKt__CollectionsKt.emptyList();
//        }
//        return (List) INSTANCE.putCache("getAllCellInfo", manager.getAllCellInfo());
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = WifiInfo.class)
//    @Nullable
//    public static final String getBSSID(@Nullable WifiInfo wifiInfo) {
//        Boolean bool = GlobalVariable.CAN_GET_WIFI_INFO.get();
//        Intrinsics.checkNotNullExpressionValue(bool, "GlobalVariable.CAN_GET_WIFI_INFO.get()");
//        if (bool.booleanValue()) {
//            return (String) INSTANCE.putLocalCache("getBSSID", (wifiInfo == null || (r3 = wifiInfo.getBSSID()) == null) ? "" : "");
//        }
//        String str = (String) INSTANCE.getLocalCache("getBSSID");
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("getBSSID")) {
//            return (String) INSTANCE.putLocalCache("getBSSID", (wifiInfo == null || (r3 = wifiInfo.getBSSID()) == null) ? "" : "");
//        }
//        return "";
//    }
//
//    private final <T> T getCache(String str) {
//        if (isUseCache()) {
//            T t = (T) anyCache.get(str);
//            if (t != null) {
//                try {
//                    logI("getCache: key=" + str + ",value=" + t);
//                    return t;
//                } catch (Exception e) {
//                    logW("getCache: key=" + str + ",e=" + e.getMessage());
//                }
//            }
//            logD("getCache key=" + str + ",return null");
//            return null;
//        }
//        return null;
//    }
//
//    @JvmStatic
//    @Nullable
//    public static final WifiInfo getCacheConnectionInfo(@NotNull String key) {
//        WifiInfo wifiInfo;
//        Intrinsics.checkNotNullParameter(key, "key");
//        if (INSTANCE.checkAgreePrivacy(key) || (wifiInfo = (WifiInfo) INSTANCE.getCache(key)) == null) {
//            return null;
//        }
//        return wifiInfo;
//    }
//
//    private final List<ApplicationInfo> getCacheInstalledApplications(String str) {
//        if (checkAppListPrivacy(str)) {
//            return null;
//        }
//        List<ApplicationInfo> listCache = getListCache(str);
//        return listCache != null ? listCache : CollectionsKt__CollectionsKt.emptyList();
//    }
//
//    private final List<PackageInfo> getCacheInstalledPackages(String str) {
//        if (checkAppListPrivacy(str)) {
//            return null;
//        }
//        List<PackageInfo> listCache = getListCache(str);
//        return listCache != null ? listCache : CollectionsKt__CollectionsKt.emptyList();
//    }
//
//    @JvmStatic
//    public static final int getCacheWifiState(@NotNull String key) {
//        Intrinsics.checkNotNullParameter(key, "key");
//        if (INSTANCE.checkAgreePrivacy(key)) {
//            return -1;
//        }
//        Integer num = (Integer) INSTANCE.getCache(key);
//        if (num != null) {
//            return num.intValue();
//        }
//        return 4;
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = WifiManager.class)
//    @SuppressLint({"MissingPermission"})
//    @Nullable
//    public static final List<WifiConfiguration> getConfiguredNetworks(@NotNull WifiManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        if (!INSTANCE.checkAgreePrivacy("getConfiguredNetworks")) {
//            List<WifiConfiguration> listCache = INSTANCE.getListCache("getConfiguredNetworks");
//            return listCache != null ? listCache : new ArrayList();
//        }
//        LocalInfo localInfo = LocalInfo.getInstance();
//        Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//        if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.ACCESS_FINE_LOCATION") == 0) {
//            LocalInfo localInfo2 = LocalInfo.getInstance();
//            Intrinsics.checkNotNullExpressionValue(localInfo2, "LocalInfo.getInstance()");
//            if (ContextCompat.checkSelfPermission(localInfo2.getApplication(), "android.permission.ACCESS_WIFI_STATE") == 0) {
//                return (List) INSTANCE.putCache("getConfiguredNetworks", manager.getConfiguredNetworks());
//            }
//        }
//        List<WifiConfiguration> listCache2 = INSTANCE.getListCache("getConfiguredNetworks");
//        return listCache2 != null ? listCache2 : new ArrayList();
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = WifiManager.class)
//    @Nullable
//    public static final WifiInfo getConnectionInfo(@NotNull WifiManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        Boolean bool = GlobalVariable.CAN_GET_WIFI_INFO.get();
//        Intrinsics.checkNotNullExpressionValue(bool, "GlobalVariable.CAN_GET_WIFI_INFO.get()");
//        if (bool.booleanValue()) {
//            WifiInfo connectionInfo = manager.getConnectionInfo();
//            GlobalVariable.CONNECT_WIFI_INFO.set(connectionInfo);
//            return connectionInfo;
//        }
//        WifiInfo wifiInfo = GlobalVariable.CONNECT_WIFI_INFO.get();
//        if (wifiInfo != null) {
//            return wifiInfo;
//        }
//        return null;
//    }
//
//    private final PackageInfo getDefaultPackageInfo(String str) {
//        PackageInfo packageInfo = defaultPackageInfo;
//        if (packageInfo == null) {
//            PackageInfo packageInfo2 = new PackageInfo();
//            packageInfo2.packageName = str;
//            if (Build.VERSION.SDK_INT >= 22) {
//                packageInfo2.baseRevisionCode = -1;
//            }
//            if (Build.VERSION.SDK_INT >= 28) {
//                packageInfo2.setLongVersionCode(-1);
//            } else {
//                packageInfo2.versionCode = -1;
//            }
//            packageInfo2.versionName = "";
//            LocalInfo localInfo = LocalInfo.getInstance();
//            if (localInfo != null) {
//                Application application = localInfo.getApplication();
//                Intrinsics.checkNotNullExpressionValue(application, "instance.application");
//                packageInfo2.applicationInfo = application.getApplicationInfo();
//                packageInfo2.applicationInfo.metaData = new Bundle();
//            }
//            if (Build.VERSION.SDK_INT >= 28) {
//                packageInfo2.signingInfo = new SigningInfo();
//            }
//            Signature signature = new Signature("");
//            packageInfo2.signatures = new Signature[1];
//            packageInfo2.signatures[0] = signature;
//            packageInfo2.providers = new ProviderInfo[0];
//            packageInfo2.receivers = new ActivityInfo[0];
//            packageInfo2.activities = new ActivityInfo[0];
//            packageInfo2.services = new ServiceInfo[0];
//            packageInfo2.requestedPermissions = new String[]{"android.permission.INTERNET", DefaultConnectivityMonitorFactory.NETWORK_PERMISSION, "android.permission.WRITE_SETTINGS", "android.permission.VIBRATE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_WIFI_STATE", "android.permission.WAKE_LOCK"};
//            defaultPackageInfo = packageInfo2;
//        } else if (packageInfo != null) {
//            packageInfo.packageName = str;
//        }
//        return defaultPackageInfo;
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = TelephonyManager.class)
//    @SuppressLint({"HardwareIds,MissingPermission"})
//    @Nullable
//    public static final String getDeviceId(@NotNull TelephonyManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = (String) INSTANCE.getCache("getDeviceId");
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("getDeviceId")) {
//            LocalInfo localInfo = LocalInfo.getInstance();
//            Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//            if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.READ_PHONE_STATE") != 0) {
//                return "";
//            }
//            return (String) INSTANCE.putCache("getDeviceId", manager.getDeviceId());
//        }
//        return "";
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = WifiManager.class)
//    @Nullable
//    public static final DhcpInfo getDhcpInfo(@NotNull WifiManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        DhcpInfo dhcpInfo = (DhcpInfo) INSTANCE.getCache("getDhcpInfo");
//        if (dhcpInfo != null) {
//            return dhcpInfo;
//        }
//        if (!INSTANCE.checkAgreePrivacy("getDhcpInfo")) {
//            return new DhcpInfo();
//        }
//        return (DhcpInfo) INSTANCE.putCache("getDhcpInfo", manager.getDhcpInfo());
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 184, oriClass = Environment.class)
//    @Nullable
//    public static final File getExternalStorageDirectory() {
//        File file = (File) INSTANCE.getCache("getExternalStorageDirectory");
//        if (file != null) {
//            return file;
//        }
//        if (!INSTANCE.checkAgreePrivacy("getExternalStorageDirectory", false)) {
//            return new File("/");
//        }
//        return (File) INSTANCE.putCache("getExternalStorageDirectory", Environment.getExternalStorageDirectory());
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = NetworkInterface.class)
//    @SuppressLint({"HardwareIds"})
//    @Nullable
//    public static final byte[] getHardwareAddress(@NotNull NetworkInterface manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        byte[] bArr = (byte[]) INSTANCE.getCache("getHardwareAddress");
//        if (bArr != null) {
//            return bArr;
//        }
//        if (INSTANCE.checkAgreePrivacy("getHardwareAddress")) {
//            return (byte[]) INSTANCE.putCache("getHardwareAddress", manager.getHardwareAddress());
//        }
//        return new byte[1];
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = TelephonyManager.class)
//    @SuppressLint({"HardwareIds", "MissingPermission"})
//    @Nullable
//    @RequiresApi(26)
//    public static final String getImei(@NotNull TelephonyManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = (String) INSTANCE.getCache("getImei");
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("getImei")) {
//            LocalInfo localInfo = LocalInfo.getInstance();
//            Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//            if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.READ_PHONE_STATE") != 0) {
//                return "";
//            }
//            return (String) INSTANCE.putCache("getImei", manager.getImei());
//        }
//        return "";
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = PackageManager.class)
//    @Nullable
//    public static final List<ApplicationInfo> getInstalledApplications(@NotNull PackageManager manager, int i) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = "getInstalledApplications-" + i;
//        List<ApplicationInfo> cacheInstalledApplications = INSTANCE.getCacheInstalledApplications(str);
//        if (cacheInstalledApplications != null) {
//            return cacheInstalledApplications;
//        }
//        INSTANCE.logD(str + ": call stack= " + Log.getStackTraceString(new Throwable()));
//        List<ApplicationInfo> installedApplications = manager.getInstalledApplications(i);
//        Intrinsics.checkNotNullExpressionValue(installedApplications, "manager.getInstalledApplications(flags)");
//        return (List) INSTANCE.putCache(str, installedApplications);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = PackageManager.class)
//    @Nullable
//    public static final List<ApplicationInfo> getInstalledApplicationsAsUser(@NotNull PackageManager manager, int i, int i2) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        return getInstalledApplications(manager, i);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = PackageManager.class)
//    @Nullable
//    public static final List<PackageInfo> getInstalledPackages(@NotNull PackageManager manager, int i) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = "getInstalledPackages-" + i;
//        List<PackageInfo> cacheInstalledPackages = INSTANCE.getCacheInstalledPackages(str);
//        if (cacheInstalledPackages != null) {
//            return cacheInstalledPackages;
//        }
//        INSTANCE.logD(str + ": call stack= " + Log.getStackTraceString(new Throwable()));
//        List<PackageInfo> installedPackages = manager.getInstalledPackages(i);
//        Intrinsics.checkNotNullExpressionValue(installedPackages, "manager.getInstalledPackages(flags)");
//        return (List) INSTANCE.putCache(str, installedPackages);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = PackageManager.class)
//    @Nullable
//    public static final List<PackageInfo> getInstalledPackagesAsUser(@NotNull PackageManager manager, int i, int i2) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        return getInstalledPackages(manager, i);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = PackageManager.class)
//    @Nullable
//    public static final String getInstallerPackageName(@NotNull PackageManager manager, @NotNull String packageName) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        Intrinsics.checkNotNullParameter(packageName, "packageName");
//        String str = "getInstallerPackageName-" + packageName;
//        if (!INSTANCE.checkAgreePrivacy(str)) {
//            String str2 = (String) INSTANCE.getCache(str);
//            return str2 != null ? str2 : packageName;
//        }
//        INSTANCE.logD(str + ": call stack= " + Log.getStackTraceString(new Throwable()));
//        return (String) INSTANCE.putCache(str, manager.getInstallerPackageName(packageName));
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = WifiInfo.class)
//    public static final int getIpAddress(@Nullable WifiInfo wifiInfo) {
//        try {
//            Boolean bool = GlobalVariable.CAN_GET_WIFI_INFO.get();
//            Intrinsics.checkNotNullExpressionValue(bool, "GlobalVariable.CAN_GET_WIFI_INFO.get()");
//            if (bool.booleanValue()) {
//                return ((Number) INSTANCE.putLocalCache("getIpAddress", Integer.valueOf(wifiInfo != null ? wifiInfo.getIpAddress() : 0))).intValue();
//            }
//            Double d = (Double) INSTANCE.getLocalCache("getIpAddress");
//            Integer valueOf = d != null ? Integer.valueOf((int) d.doubleValue()) : null;
//            if (valueOf == null && !isSaveWifiInfo) {
//                isSaveWifiInfo = true;
//                GlobalVariable.GET_WIFI_TIMES.set(Integer.valueOf(GlobalVariable.GET_WIFI_TIMES.get().intValue() + 1));
//            }
//            if (valueOf != null) {
//                return valueOf.intValue();
//            }
//            if (INSTANCE.checkAgreePrivacy("getIpAddress")) {
//                return (int) ((Number) INSTANCE.putLocalCache("getIpAddress", Double.valueOf(wifiInfo != null ? wifiInfo.getIpAddress() : 0))).doubleValue();
//            }
//            return 0;
//        } catch (Exception unused) {
//            return 0;
//        }
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = LocationManager.class)
//    @SuppressLint({"MissingPermission"})
//    @Nullable
//    public static final Location getLastKnownLocation(@NotNull LocationManager manager, @NotNull String provider) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        Intrinsics.checkNotNullParameter(provider, "provider");
//        String str = "getLastKnownLocation-" + provider;
//        Location location = (Location) INSTANCE.getCache(str);
//        if (location != null) {
//            return location;
//        }
//        if (!INSTANCE.checkAgreePrivacy(str)) {
//            return new Location(provider);
//        }
//        LocalInfo localInfo = LocalInfo.getInstance();
//        Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//        if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.ACCESS_FINE_LOCATION") == 0) {
//            LocalInfo localInfo2 = LocalInfo.getInstance();
//            Intrinsics.checkNotNullExpressionValue(localInfo2, "LocalInfo.getInstance()");
//            if (ContextCompat.checkSelfPermission(localInfo2.getApplication(), "android.permission.ACCESS_COARSE_LOCATION") == 0) {
//                return (Location) INSTANCE.putCache(str, manager.getLastKnownLocation(provider));
//            }
//        }
//        return new Location(provider);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = PackageManager.class)
//    @Nullable
//    public static final Intent getLaunchIntentForPackage(@NotNull PackageManager manager, @NotNull String packageName) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        Intrinsics.checkNotNullParameter(packageName, "packageName");
//        String str = "getLaunchIntentForPackage-" + packageName;
//        Intent intent = (Intent) INSTANCE.getCache(str);
//        if (intent != null) {
//            return intent;
//        }
//        if (!INSTANCE.checkAgreePrivacy(str)) {
//            Intent intent2 = new Intent();
//            intent2.setPackage(packageName);
//            return intent2;
//        }
//        return (Intent) INSTANCE.putCache(str, manager.getLaunchIntentForPackage(packageName));
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = TelephonyManager.class)
//    @SuppressLint({"HardwareIds", "MissingPermission"})
//    @Nullable
//    public static final String getLine1Number(@NotNull TelephonyManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = (String) INSTANCE.getCache("getLine1Number");
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("getLine1Number")) {
//            LocalInfo localInfo = LocalInfo.getInstance();
//            Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//            if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.READ_PHONE_STATE") != 0) {
//                return "";
//            }
//            return (String) INSTANCE.putCache("getLine1Number", manager.getLine1Number());
//        }
//        return "";
//    }
//
//    private final <T> List<T> getListCache(String str) {
//        if (isUseCache()) {
//            Object obj = anyCache.get(str);
//            if (obj != null && (obj instanceof List)) {
//                try {
//                    return (List) obj;
//                } catch (Exception e) {
//                    logW("getListCache: key=" + str + ",e=" + e.getMessage());
//                }
//            }
//            logD("getListCache key=" + str + ",return null");
//            return null;
//        }
//        return null;
//    }
//
//    private final <T> T getLocalCache(String str) {
//        if (isUseCache()) {
//            T t = (T) GlobalVariable.PRIVACY_LOCAL_CACHE.get().get(str);
//            if (t != null) {
//                try {
//                    logI("getLocalCache: key=" + str + ",value=" + t);
//                    return t;
//                } catch (Exception e) {
//                    logW("getLocalCache: key=" + str + ",e=" + e.getMessage());
//                }
//            }
//            logD("getLocalCache key=" + str + ",return null");
//            return null;
//        }
//        return null;
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = WifiInfo.class)
//    @SuppressLint({"HardwareIds"})
//    @Nullable
//    public static final String getMacAddress(@Nullable WifiInfo wifiInfo) {
//        INSTANCE.logI("getMacAddress");
//        return "";
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = TelephonyManager.class)
//    @SuppressLint({"HardwareIds", "MissingPermission"})
//    @Nullable
//    @RequiresApi(26)
//    public static final String getMeid(@NotNull TelephonyManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = (String) INSTANCE.getCache("getMeid");
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("getMeid")) {
//            LocalInfo localInfo = LocalInfo.getInstance();
//            Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//            if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.READ_PHONE_STATE") != 0) {
//                return "";
//            }
//            return (String) INSTANCE.putCache("getMeid", manager.getMeid());
//        }
//        return "";
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = TelephonyManager.class)
//    @Nullable
//    public static final String getNetworkOperatorName(@NotNull TelephonyManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = (String) INSTANCE.getCache("getNetworkOperatorName");
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("getNetworkOperatorName")) {
//            return (String) INSTANCE.putCache("getNetworkOperatorName", manager.getNetworkOperatorName());
//        }
//        return "";
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = PackageManager.class)
//    @SuppressLint({"WrongConstant"})
//    @Nullable
//    public static final PackageInfo getPackageInfo(@NotNull PackageManager manager, @NotNull String packageName, int i) throws Throwable {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        Intrinsics.checkNotNullParameter(packageName, "packageName");
//        String str = "getPackageInfo-" + packageName;
//        INSTANCE.logD("XXGetPackageInfo-packageName-" + packageName + '-' + Integer.toHexString(i));
//        Boolean bool = GlobalVariable.CAN_GET_PACKAGE_INFO.get();
//        Intrinsics.checkNotNullExpressionValue(bool, "GlobalVariable.CAN_GET_PACKAGE_INFO.get()");
//        if (bool.booleanValue()) {
//            return manager.getPackageInfo(packageName, i);
//        }
//        PackageInfo packageInfo = (PackageInfo) INSTANCE.getCache(str + '-' + i);
//        if (packageInfo != null) {
//            return packageInfo;
//        }
//        INSTANCE.logD("XXGetPackageInfo-stack-" + Log.getStackTraceString(new Throwable()));
//        if (!INSTANCE.checkAgreePrivacy(str)) {
//            return INSTANCE.getDefaultPackageInfo(packageName);
//        }
//        ArrayList<String> arrayList = new ArrayList();
//        arrayList.addAll(PrivacyUtilsNew.INSTANCE.getPACKAGE_NAMES());
//        for (String str2 : arrayList) {
//            if (TextUtils.equals(packageName, str2)) {
//                return INSTANCE.getPackageInfo(manager, packageName, i, 134238415 | i);
//            }
//        }
//        if (!TextUtils.equals(packageName, "com.google.android.webview") && !TextUtils.equals(packageName, "com.huawei.webview") && !TextUtils.equals(packageName, "com.android.webview")) {
//            for (String str3 : CollectionsKt__CollectionsKt.mutableListOf("com.qq.e.comm.plugin.util")) {
//                if (HookUtils.isWhiteStack(str3)) {
//                    return INSTANCE.getPackageInfo(manager, packageName, i, 134238415 | i);
//                }
//            }
//            INSTANCE.logD("XXGetPackageInfo-packageName-" + packageName + " Throw Exception");
//            throw new PackageManager.NameNotFoundException();
//        }
//        return INSTANCE.getPackageInfo(manager, packageName, i, 268444864 | i);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = ClipboardManager.class)
//    @Nullable
//    public static final ClipData getPrimaryClip(@NotNull ClipboardManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        if (!INSTANCE.checkAgreePrivacy("getPrimaryClip")) {
//            return ClipData.newPlainText("Label", "");
//        }
//        return manager.getPrimaryClip();
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = ClipboardManager.class)
//    @Nullable
//    public static final ClipDescription getPrimaryClipDescription(@NotNull ClipboardManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        if (!INSTANCE.checkAgreePrivacy("getPrimaryClipDescription")) {
//            return new ClipDescription("", new String[]{"text/plain"});
//        }
//        return manager.getPrimaryClipDescription();
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = ActivityManager.class)
//    @Nullable
//    public static final List<ActivityManager.RecentTaskInfo> getRecentTasks(@NotNull ActivityManager manager, int i, int i2) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = "getRecentTasks-" + i + '-' + i2;
//        if (!INSTANCE.checkAgreePrivacy(str)) {
//            List<ActivityManager.RecentTaskInfo> listCache = INSTANCE.getListCache(str);
//            return listCache != null ? listCache : CollectionsKt__CollectionsKt.emptyList();
//        }
//        return (List) INSTANCE.putCache(str, manager.getRecentTasks(i, i2));
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = WifiInfo.class)
//    public static final int getRssi(@Nullable WifiInfo wifiInfo) {
//        try {
//            Boolean bool = GlobalVariable.CAN_GET_WIFI_INFO.get();
//            Intrinsics.checkNotNullExpressionValue(bool, "GlobalVariable.CAN_GET_WIFI_INFO.get()");
//            if (bool.booleanValue()) {
//                return ((Number) INSTANCE.putLocalCache("getRssi", Integer.valueOf(wifiInfo != null ? wifiInfo.getRssi() : 0))).intValue();
//            }
//            Double d = (Double) INSTANCE.getLocalCache("getRssi");
//            Integer valueOf = d != null ? Integer.valueOf((int) d.doubleValue()) : null;
//            if (valueOf != null) {
//                return valueOf.intValue();
//            }
//            if (INSTANCE.checkAgreePrivacy("getRssi")) {
//                return (int) ((Number) INSTANCE.putLocalCache("getRssi", Double.valueOf(wifiInfo != null ? wifiInfo.getRssi() : 0))).doubleValue();
//            }
//            return 0;
//        } catch (Exception unused) {
//            return 0;
//        }
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = ActivityManager.class)
//    @NotNull
//    public static final List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses(@NotNull ActivityManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        INSTANCE.logI("getRunningAppProcesses");
//        if (!INSTANCE.checkBgCountPrivacy("getRunningAppProcesses", 3)) {
//            List<ActivityManager.RunningAppProcessInfo> listCache = INSTANCE.getListCache("getRunningAppProcesses");
//            return listCache != null ? listCache : CollectionsKt__CollectionsKt.emptyList();
//        }
//        PrivacyMethod privacyMethod = INSTANCE;
//        privacyMethod.logD("getRunningAppProcesses: call stack= " + Log.getStackTraceString(new Throwable()));
//        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
//        INSTANCE.addCount("getRunningAppProcesses");
//        Object putCache = INSTANCE.putCache("getRunningAppProcesses", runningAppProcesses);
//        Intrinsics.checkNotNullExpressionValue(putCache, "putCache(key, value)");
//        return (List) putCache;
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = ActivityManager.class)
//    @Nullable
//    public static final List<ActivityManager.RunningTaskInfo> getRunningTasks(@NotNull ActivityManager manager, int i) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = "getRunningTasks-" + i;
//        if (!INSTANCE.checkAgreePrivacy(str)) {
//            List<ActivityManager.RunningTaskInfo> listCache = INSTANCE.getListCache(str);
//            return listCache != null ? listCache : CollectionsKt__CollectionsKt.emptyList();
//        }
//        return (List) INSTANCE.putCache(str, manager.getRunningTasks(i));
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = WifiInfo.class)
//    @Nullable
//    public static final String getSSID(@Nullable WifiInfo wifiInfo) {
//        Boolean bool = GlobalVariable.CAN_GET_WIFI_INFO.get();
//        Intrinsics.checkNotNullExpressionValue(bool, "GlobalVariable.CAN_GET_WIFI_INFO.get()");
//        if (bool.booleanValue()) {
//            return (String) INSTANCE.putLocalCache("getSSID", (wifiInfo == null || (r3 = wifiInfo.getSSID()) == null) ? "" : "");
//        }
//        String str = (String) INSTANCE.getLocalCache("getSSID");
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("getSSID")) {
//            return (String) INSTANCE.putLocalCache("getSSID", (wifiInfo == null || (r3 = wifiInfo.getSSID()) == null) ? "" : "");
//        }
//        return "";
//    }
//

//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = WifiManager.class)
//    @Nullable
//    public static final List<ScanResult> getScanResults(@NotNull WifiManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        if (!INSTANCE.checkAgreePrivacy("getScanResults")) {
//            List<ScanResult> listCache = INSTANCE.getListCache("getScanResults");
//            return listCache != null ? listCache : new ArrayList();
//        }
//        return (List) INSTANCE.putCache("getScanResults", manager.getScanResults());
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = SensorManager.class)
//    @Nullable
//    public static final List<Sensor> getSensorList(@NotNull SensorManager manager, int i) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        List<Sensor> listCache = INSTANCE.getListCache("getSensorList");
//        if (listCache != null) {
//            return listCache;
//        }
//        if (!INSTANCE.checkAgreePrivacy("getSensorList")) {
//            return new ArrayList();
//        }
//        return (List) INSTANCE.putCache("getSensorList", manager.getSensorList(i));
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 184, oriClass = Build.class)
//    @SuppressLint({"MissingPermission"})
//    @Nullable
//    @RequiresApi(26)
//    public static final String getSerial() {
//        String str = (String) INSTANCE.getCache("getSerial");
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("getSerial")) {
//            LocalInfo localInfo = LocalInfo.getInstance();
//            Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//            if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.READ_PHONE_STATE") != 0) {
//                return "";
//            }
//            return (String) INSTANCE.putCache("getSerial", Build.getSerial());
//        }
//        return "";
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = TelephonyManager.class)
//    @Nullable
//    public static final String getSimOperator(@NotNull TelephonyManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = (String) INSTANCE.getCache("getSimOperator");
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("getSimOperator")) {
//            return (String) INSTANCE.putCache("getSimOperator", manager.getSimOperator());
//        }
//        return "";
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = TelephonyManager.class)
//    @SuppressLint({"HardwareIds", "MissingPermission"})
//    @Nullable
//    public static final String getSimSerialNumber(@NotNull TelephonyManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = (String) INSTANCE.getCache("getSimSerialNumber");
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("getSimSerialNumber")) {
//            LocalInfo localInfo = LocalInfo.getInstance();
//            Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//            if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.READ_PHONE_STATE") != 0) {
//                return "";
//            }
//            return (String) INSTANCE.putCache("getSimSerialNumber", manager.getSimSerialNumber());
//        }
//        return "";
//    }
//
//    @JvmStatic
//    @RequiresApi(26)
//    @AsmMethodReplace(oriAccess = 182, oriClass = TelephonyManager.class)
//    public static final int getSimState(@NotNull TelephonyManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        Integer num = (Integer) INSTANCE.getCache("getSimState");
//        if (num != null) {
//            return num.intValue();
//        }
//        if (INSTANCE.checkAgreePrivacy("getSimState")) {
//            return ((Number) INSTANCE.putCache("getSimState", Integer.valueOf(manager.getSimState()))).intValue();
//        }
//        return 5;
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 184, oriClass = Settings.System.class)
//    @Nullable
//    public static final String getString(@NotNull ContentResolver resolver, @NotNull String name) {
//        Intrinsics.checkNotNullParameter(resolver, "resolver");
//        Intrinsics.checkNotNullParameter(name, "name");
//        INSTANCE.logI("getString Android Id");
//        if (Intrinsics.areEqual("android_id", name)) {
//            String str = GlobalVariable.ANDROID_ID.get();
//            if (str != null) {
//                return str;
//            }
//            if (INSTANCE.checkAgreePrivacy("ANDROID_ID")) {
//                GlobalVariable<Integer> globalVariable = GlobalVariable.GET_DEVICEID_TIMES;
//                globalVariable.set(Integer.valueOf(globalVariable.get().intValue() + 1));
//                String string = Settings.System.getString(resolver, name);
//                GlobalVariable.ANDROID_ID.set(string);
//                return string;
//            }
//            return "";
//        }
//        return Settings.System.getString(resolver, name);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = TelephonyManager.class)
//    @SuppressLint({"MissingPermission", "HardwareIds"})
//    @Nullable
//    public static final String getSubscriberId(@NotNull TelephonyManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = (String) INSTANCE.getCache("getSubscriberId");
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("getSubscriberId")) {
//            LocalInfo localInfo = LocalInfo.getInstance();
//            Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//            if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.READ_PHONE_STATE") != 0) {
//                return "";
//            }
//            return (String) INSTANCE.putCache("getSubscriberId", manager.getSubscriberId());
//        }
//        return "";
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 184, oriClass = Settings.System.class, oriDesc = "Ljava/lang/String;", oriMethod = "getString")
//    @Nullable
//    public static final String getSystemString(@NotNull ContentResolver resolver, @NotNull String name) {
//        Intrinsics.checkNotNullParameter(resolver, "resolver");
//        Intrinsics.checkNotNullParameter(name, "name");
//        return getString(resolver, name);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = ClipboardManager.class)
//    @Nullable
//    public static final CharSequence getText(@NotNull ClipboardManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        return !INSTANCE.checkAgreePrivacy("getText") ? "" : manager.getText();
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = WifiManager.class)
//    public static final int getWifiState(@NotNull WifiManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        int cacheWifiState = getCacheWifiState("getWifiState");
//        if (cacheWifiState != -1) {
//            return cacheWifiState;
//        }
//        return ((Number) INSTANCE.putCache("getWifiState", Integer.valueOf(manager.getWifiState()))).intValue();
//    }
//
//    private final boolean isAndroidIntent(Intent intent) {
//        String action = intent.getAction();
//        if (action == null) {
//            action = "";
//        }
//        Intrinsics.checkNotNullExpressionValue(action, "intent.action ?: \"\"");
//        return StringsKt__StringsKt.contains$default((CharSequence) action, (CharSequence) "android.", false, 2, (Object) null);
//    }
//
//    @JvmStatic
//    public static final int isCacheWifiEnabled(@NotNull String key) {
//        Intrinsics.checkNotNullParameter(key, "key");
//        if (INSTANCE.checkAgreePrivacy(key)) {
//            return -1;
//        }
//        Boolean bool = (Boolean) INSTANCE.getCache(key);
//        if (bool != null) {
//            return bool.booleanValue() ? 1 : 0;
//        }
//        return 0;
//    }
//
//    private final boolean isUseCache() {
//        Boolean bool = GlobalVariable.PRIVACY_USE_CACHE.get();
//        Intrinsics.checkNotNullExpressionValue(bool, "GlobalVariable.PRIVACY_USE_CACHE.get()");
//        return bool.booleanValue();
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = WifiManager.class)
//    public static final boolean isWifiEnabled(@NotNull WifiManager manager) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        int isCacheWifiEnabled = isCacheWifiEnabled("isWifiEnabled");
//        if (isCacheWifiEnabled != -1) {
//            return isCacheWifiEnabled == 1;
//        }
//        return ((Boolean) INSTANCE.putCache("isWifiEnabled", Boolean.valueOf(manager.isWifiEnabled()))).booleanValue();
//    }
//
//    private final void logD(String str) {
//        if (Config.LOGGING) {
//            Log.d(TAG, str);
//        }
//    }
//
//    private final void logI(String str) {
//        Log.i(TAG, str);
//    }
//
//    private final void logW(String str) {
//        Log.w(TAG, str);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 178, oriClass = Build.class, oriDesc = "Ljava/lang/String;", oriMethod = "MODEL")
//    @Nullable
//    public static final String model() {
//        String str = GlobalVariable.BUILD_MODEL.get();
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("model", false)) {
//            GlobalVariable<Integer> globalVariable = GlobalVariable.GET_MODEL_TIMES;
//            globalVariable.set(Integer.valueOf(globalVariable.get().intValue() + 1));
//            String str2 = Build.MODEL;
//            GlobalVariable.BUILD_MODEL.set(str2);
//            return str2;
//        }
//        return "";
//    }
//
//    private final void printIntent(String str, Intent intent) {
//        String packageName;
//        if (Config.LOGGING) {
//            StringBuilder sb = new StringBuilder();
//            Set<String> categories = intent.getCategories();
//            if (categories != null) {
//                sb.append("-categories:");
//                sb.append(categories.toString());
//                sb.append("\n");
//            }
//            String str2 = intent.getPackage();
//            if (str2 != null) {
//                sb.append("-packageName:");
//                sb.append(str2);
//                sb.append("\n");
//            }
//            Uri data = intent.getData();
//            if (data != null) {
//                sb.append("-data:");
//                sb.append(data.toString());
//                sb.append("\n");
//            }
//            ComponentName component = intent.getComponent();
//            if (component != null && (packageName = component.getPackageName()) != null) {
//                sb.append("-packageName:");
//                sb.append(packageName);
//                sb.append("\n");
//            }
//            boolean z = !(sb.length() == 0);
//            if (!StringsKt__StringsKt.contains$default((CharSequence) sb, (CharSequence) Meizu.MEIZU_DEFAULT_EXTRA_PACKAGE, false, 2, (Object) null)) {
//                z = false;
//            }
//            sb.append(str + "-legal:" + z);
//            sb.append("\n");
//            Log.d(TAG, sb.toString());
//        }
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 178, oriClass = Build.class, oriDesc = "Ljava/lang/String;", oriMethod = "PRODUCT")
//    @Nullable
//    public static final String product() {
//        String str = GlobalVariable.BUILD_PRODUCT.get();
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("product", false)) {
//            String str2 = Build.PRODUCT;
//            GlobalVariable.BUILD_PRODUCT.set(str2);
//            return str2;
//        }
//        return "";
//    }
//
//    private final <T> T putCache(String str, T t) {
//        if (isUseCache()) {
//            logI("putCache key=" + str + ",value=" + t);
//            if (t != null) {
//                anyCache.put(str, t);
//            }
//        }
//        return t;
//    }
//
//    private final <T> T putLocalCache(String str, T t) {
//        if (isUseCache()) {
//            logI("putLocalCache key=" + str + ",value=" + t);
//            if (t != null) {
//                HashMap<String, Object> cacheMap = GlobalVariable.PRIVACY_LOCAL_CACHE.get();
//                Intrinsics.checkNotNullExpressionValue(cacheMap, "cacheMap");
//                cacheMap.put(str, t);
//                GlobalVariable.PRIVACY_LOCAL_CACHE.set(cacheMap);
//            }
//        }
//        return t;
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = PackageManager.class)
//    @NotNull
//    public static final List<ResolveInfo> queryIntentActivities(@NotNull PackageManager manager, @NotNull Intent intent, int i) {
//        PrivacyMethod privacyMethod;
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        Intrinsics.checkNotNullParameter(intent, "intent");
//        String str = "queryIntentActivities-" + intent + '-' + i;
//        INSTANCE.printIntent(str, intent);
//        if (!INSTANCE.checkAgreePrivacy(str, !privacyMethod.isAndroidIntent(intent))) {
//            List<ResolveInfo> listCache = INSTANCE.getListCache(str);
//            return listCache != null ? listCache : CollectionsKt__CollectionsKt.emptyList();
//        }
//        List<ResolveInfo> queryIntentActivities = manager.queryIntentActivities(intent, i);
//        Intrinsics.checkNotNullExpressionValue(queryIntentActivities, "manager.queryIntentActivities(intent, flags)");
//        return (List) INSTANCE.putCache(str, queryIntentActivities);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = PackageManager.class)
//    @NotNull
//    public static final List<ResolveInfo> queryIntentActivityOptions(@NotNull PackageManager manager, @Nullable ComponentName componentName, @Nullable Intent[] intentArr, @NotNull Intent intent, int i) {
//        PrivacyMethod privacyMethod;
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        Intrinsics.checkNotNullParameter(intent, "intent");
//        String str = "queryIntentActivityOptions-" + intent + '-' + i;
//        INSTANCE.printIntent(str, intent);
//        if (!INSTANCE.checkAgreePrivacy(str, !privacyMethod.isAndroidIntent(intent))) {
//            List<ResolveInfo> listCache = INSTANCE.getListCache(str);
//            return listCache != null ? listCache : CollectionsKt__CollectionsKt.emptyList();
//        }
//        List<ResolveInfo> queryIntentActivityOptions = manager.queryIntentActivityOptions(componentName, intentArr, intent, i);
//        Intrinsics.checkNotNullExpressionValue(queryIntentActivityOptions, "manager.queryIntentActiv…specifics, intent, flags)");
//        return (List) INSTANCE.putCache(str, queryIntentActivityOptions);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = PackageManager.class)
//    @NotNull
//    public static final List<ResolveInfo> queryIntentServices(@NotNull PackageManager manager, @NotNull Intent intent, int i) {
//        PrivacyMethod privacyMethod;
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        Intrinsics.checkNotNullParameter(intent, "intent");
//        String str = "queryIntentServices-" + intent + '-' + i;
//        if (!INSTANCE.checkAgreePrivacy(str, !privacyMethod.isAndroidIntent(intent))) {
//            List<ResolveInfo> listCache = INSTANCE.getListCache(str);
//            return listCache != null ? listCache : CollectionsKt__CollectionsKt.emptyList();
//        }
//        List<ResolveInfo> queryIntentServices = manager.queryIntentServices(intent, i);
//        Intrinsics.checkNotNullExpressionValue(queryIntentServices, "manager.queryIntentServices(intent, flags)");
//        return (List) INSTANCE.putCache(str, queryIntentServices);
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = LocationManager.class)
//    @SuppressLint({"MissingPermission"})
//    public static final void requestLocationUpdates(@NotNull LocationManager manager, @NotNull String provider, long j, float f, @NotNull LocationListener listener) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        Intrinsics.checkNotNullParameter(provider, "provider");
//        Intrinsics.checkNotNullParameter(listener, "listener");
//        if (INSTANCE.checkAgreePrivacy("requestLocationUpdates")) {
//            LocalInfo localInfo = LocalInfo.getInstance();
//            Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//            if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.ACCESS_FINE_LOCATION") == 0) {
//                LocalInfo localInfo2 = LocalInfo.getInstance();
//                Intrinsics.checkNotNullExpressionValue(localInfo2, "LocalInfo.getInstance()");
//                if (ContextCompat.checkSelfPermission(localInfo2.getApplication(), "android.permission.ACCESS_COARSE_LOCATION") != 0) {
//                    return;
//                }
//                manager.requestLocationUpdates(provider, j, f, listener);
//            }
//        }
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = PackageManager.class)
//    @Nullable
//    public static final ResolveInfo resolveService(@NotNull PackageManager manager, @NotNull Intent intent, int i) {
//        PrivacyMethod privacyMethod;
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        Intrinsics.checkNotNullParameter(intent, "intent");
//        String str = "resolveService-" + intent + '-' + i;
//        ResolveInfo resolveInfo = (ResolveInfo) INSTANCE.getCache(str);
//        if (resolveInfo != null) {
//            return resolveInfo;
//        }
//        if (!INSTANCE.checkAgreePrivacy(str, !privacyMethod.isAndroidIntent(intent))) {
//            return new ResolveInfo();
//        }
//        return (ResolveInfo) INSTANCE.putCache(str, manager.resolveService(intent, i));
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 178, oriClass = Build.class, oriDesc = "Ljava/lang/String;", oriMethod = "SERIAL")
//    @SuppressLint({"HardwareIds"})
//    @Nullable
//    public static final String serial() {
//        String str = (String) INSTANCE.getCache("serial");
//        if (str != null) {
//            return str;
//        }
//        if (INSTANCE.checkAgreePrivacy("serial")) {
//            return (String) INSTANCE.putCache("serial", Build.SERIAL);
//        }
//        return "";
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = ClipboardManager.class)
//    public static final void setPrimaryClip(@NotNull ClipboardManager manager, @NotNull ClipData clip) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        Intrinsics.checkNotNullParameter(clip, "clip");
//        if (INSTANCE.checkAgreePrivacy("setPrimaryClip")) {
//            manager.setPrimaryClip(clip);
//        }
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = ClipboardManager.class)
//    public static final void setText(@NotNull ClipboardManager manager, @NotNull CharSequence clip) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        Intrinsics.checkNotNullParameter(clip, "clip");
//        if (INSTANCE.checkAgreePrivacy("setText")) {
//            manager.setText(clip);
//        }
//    }
//
//    public final boolean checkAgreePrivacy(@NotNull String name) {
//        Intrinsics.checkNotNullParameter(name, "name");
//        return checkAgreePrivacy(name, true);
//    }
//
//    public final boolean checkAppListPrivacy(@NotNull String name) {
//        Intrinsics.checkNotNullParameter(name, "name");
//        if (!GlobalVariable.AD_PERMISSION_APP_LIST.get().booleanValue()) {
//            logD(name + ": applist stack= " + Log.getStackTraceString(new Throwable()));
//            return false;
//        }
//        return checkAgreePrivacy(name);
//    }
//
//    public final boolean checkAgreePrivacy(@NotNull String name, boolean z) {
//        Intrinsics.checkNotNullParameter(name, "name");
//        if (!PrivacyUtils.checkPrivacyIsAgree()) {
//            logD(name + ": privacy stack= " + Log.getStackTraceString(new Throwable()));
//            return false;
//        } else if (z) {
//            Boolean bool = GlobalVariable.APP_IS_BACKGROUND.get();
//            Intrinsics.checkNotNullExpressionValue(bool, "GlobalVariable.APP_IS_BACKGROUND.get()");
//            if (bool.booleanValue()) {
//                logD(name + ": background stack= " + Log.getStackTraceString(new Throwable()));
//                return false;
//            }
//            return true;
//        } else {
//            return true;
//        }
//    }
//
//    @JvmStatic
//    @Nullable
//    public static final Object getInstalledApplications(@Nullable Object obj, @NotNull Method method, @Nullable Object[] objArr) throws Throwable {
//        Intrinsics.checkNotNullParameter(method, "method");
//        String key = HookUtils.generateCacheKey(method.getName(), objArr);
//        PrivacyMethod privacyMethod = INSTANCE;
//        Intrinsics.checkNotNullExpressionValue(key, "key");
//        List<ApplicationInfo> cacheInstalledApplications = privacyMethod.getCacheInstalledApplications(key);
//        if (cacheInstalledApplications != null) {
//            return cacheInstalledApplications;
//        }
//        PrivacyMethod privacyMethod2 = INSTANCE;
//        privacyMethod2.logD(key + ": call stack= " + Log.getStackTraceString(new Throwable()));
//        if (objArr == null) {
//            objArr = new Object[0];
//        }
//        return INSTANCE.putCache(key, method.invoke(obj, Arrays.copyOf(objArr, objArr.length)));
//    }
//
//    @JvmStatic
//    @Nullable
//    public static final Object getInstalledPackages(@Nullable Object obj, @NotNull Method method, @Nullable Object[] objArr) throws Throwable {
//        Intrinsics.checkNotNullParameter(method, "method");
//        String key = HookUtils.generateCacheKey(method.getName(), objArr);
//        PrivacyMethod privacyMethod = INSTANCE;
//        Intrinsics.checkNotNullExpressionValue(key, "key");
//        List<PackageInfo> cacheInstalledPackages = privacyMethod.getCacheInstalledPackages(key);
//        if (cacheInstalledPackages != null) {
//            return cacheInstalledPackages;
//        }
//        PrivacyMethod privacyMethod2 = INSTANCE;
//        privacyMethod2.logD(key + ": call stack= " + Log.getStackTraceString(new Throwable()));
//        if (objArr == null) {
//            objArr = new Object[0];
//        }
//        return INSTANCE.putCache(key, method.invoke(obj, Arrays.copyOf(objArr, objArr.length)));
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = TelephonyManager.class)
//    @SuppressLint({"HardwareIds,MissingPermission"})
//    @Nullable
//    @RequiresApi(23)
//    public static final String getDeviceId(@NotNull TelephonyManager manager, int i) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = "getDeviceId-" + i;
//        String str2 = (String) INSTANCE.getCache(str);
//        if (str2 != null) {
//            return str2;
//        }
//        if (INSTANCE.checkAgreePrivacy(str)) {
//            LocalInfo localInfo = LocalInfo.getInstance();
//            Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//            if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.READ_PHONE_STATE") != 0) {
//                return "";
//            }
//            return (String) INSTANCE.putCache(str, manager.getDeviceId(i));
//        }
//        return "";
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = TelephonyManager.class)
//    @SuppressLint({"HardwareIds", "MissingPermission"})
//    @Nullable
//    @RequiresApi(26)
//    public static final String getImei(@NotNull TelephonyManager manager, int i) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = "getImei-" + i;
//        String str2 = (String) INSTANCE.getCache(str);
//        if (str2 != null) {
//            return str2;
//        }
//        if (INSTANCE.checkAgreePrivacy(str)) {
//            LocalInfo localInfo = LocalInfo.getInstance();
//            Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//            if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.READ_PHONE_STATE") != 0) {
//                return "";
//            }
//            return (String) INSTANCE.putCache(str, manager.getImei(i));
//        }
//        return "";
//    }
//
//    @JvmStatic
//    @Nullable
//    public static final Object getInstallerPackageName(@Nullable Object obj, @NotNull Method method, @Nullable Object[] objArr) throws Throwable {
//        Intrinsics.checkNotNullParameter(method, "method");
//        String key = HookUtils.generateCacheKey(method.getName(), objArr);
//        PrivacyMethod privacyMethod = INSTANCE;
//        Intrinsics.checkNotNullExpressionValue(key, "key");
//        if (!privacyMethod.checkAgreePrivacy(key)) {
//            String str = (String) INSTANCE.getCache(key);
//            if (str != null) {
//                return str;
//            }
//            if (objArr == null || objArr.length <= 0 || !(objArr[0] instanceof String)) {
//                return Constant.PROCESS_NAME_MAIN;
//            }
//            Object obj2 = objArr[0];
//            if (obj2 != null) {
//                return (String) obj2;
//            }
//            throw new NullPointerException("null cannot be cast to non-null type kotlin.String");
//        }
//        PrivacyMethod privacyMethod2 = INSTANCE;
//        privacyMethod2.logD(key + ": call stack= " + Log.getStackTraceString(new Throwable()));
//        if (objArr == null) {
//            objArr = new Object[0];
//        }
//        return INSTANCE.putCache(key, method.invoke(obj, Arrays.copyOf(objArr, objArr.length)));
//    }
//
//    @JvmStatic
//    @AsmMethodReplace(oriAccess = 182, oriClass = TelephonyManager.class)
//    @SuppressLint({"HardwareIds", "MissingPermission"})
//    @Nullable
//    @RequiresApi(26)
//    public static final String getMeid(@NotNull TelephonyManager manager, int i) {
//        Intrinsics.checkNotNullParameter(manager, "manager");
//        String str = "getMeid-" + i;
//        String str2 = (String) INSTANCE.getCache(str);
//        if (str2 != null) {
//            return str2;
//        }
//        if (INSTANCE.checkAgreePrivacy(str)) {
//            LocalInfo localInfo = LocalInfo.getInstance();
//            Intrinsics.checkNotNullExpressionValue(localInfo, "LocalInfo.getInstance()");
//            if (ContextCompat.checkSelfPermission(localInfo.getApplication(), "android.permission.READ_PHONE_STATE") != 0) {
//                return "";
//            }
//            return (String) INSTANCE.putCache(str, manager.getMeid(i));
//        }
//        return "";
//    }
//
//    private final synchronized PackageInfo getPackageInfo(PackageManager packageManager, String str, int i, int i2) {
//        PackageInfo packageInfo;
//        try {
//            packageInfo = packageManager.getPackageInfo(str, i2);
//            putCache("getPackageInfo-" + str, packageInfo);
//        } catch (Throwable unused) {
//            return packageManager.getPackageInfo(str, i);
//        }
//        return packageInfo;
//    }
//
//    private final synchronized Object getPackageInfo(Object obj, Method method, String str, int i, int i2, int i3) {
//        Object invoke;
//        try {
//            String str2 = "getPackageInfo-" + str;
//            invoke = method.invoke(obj, str, Integer.valueOf(i2), Integer.valueOf(i3));
//            if (invoke instanceof PackageInfo) {
//                GlobalVariable.getSharedPreferences().encode(str2, (Parcelable) invoke);
//            }
//        } catch (Throwable unused) {
//            return method.invoke(obj, str, Integer.valueOf(i), Integer.valueOf(i3));
//        }
//        return invoke;
//    }
}