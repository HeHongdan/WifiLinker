package android.net.wifi.utils;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.blankj.utilcode.util.LogUtils;

/**
 * 类描述：通过系统自动初始化内容提供者获取上下文。
 *
 * @author HeHongdan
 * @date 2022/1/10
 * @since v2022/1/10
 */
public class ContextProvider extends ContentProvider {

    /** 上下文。 */
    public static Context CONTEXT;


    @Override
    public boolean onCreate() {
        CONTEXT = getContext().getApplicationContext();
        //LogUtils.d("内容提供者上下文", CONTEXT);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

