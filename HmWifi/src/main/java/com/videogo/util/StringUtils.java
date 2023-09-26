package com.videogo.util;

import android.net.Uri;
import android.text.TextUtils;
import java.util.regex.Pattern;

/* loaded from: classes5.dex */
public class StringUtils {
    public static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%;$";
    public static final int CHAR_TYPE_DIGIT = 1;
    public static final int CHAR_TYPE_LOWER_CASE = 2;
    public static final int CHAR_TYPE_OTHER = 8;
    public static final int CHAR_TYPE_UPPER_CASE = 4;

    public static String convertToNoQuotedString(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        int length = str.length() - 1;
        return (length > 2 && str.charAt(0) == '\"' && str.charAt(length) == '\"') ? str.substring(1, length) : str;
    }

    public static String convertToQuotedString(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        int length = str.length() - 1;
        if (length >= 0) {
            if (str.charAt(0) == '\"' && str.charAt(length) == '\"') {
                return str;
            }
            return "\"" + str + "\"";
        }
        return str;
    }

    public static int getCharType(CharSequence charSequence) {
        int i = 0;
        for (int i2 = 0; i2 < charSequence.length(); i2++) {
            char charAt = charSequence.charAt(i2);
            i = (charAt < '0' || charAt > '9') ? (charAt < 'a' || charAt > 'z') ? (charAt < 'A' || charAt > 'Z') ? i | 8 : i | 4 : i | 2 : i | 1;
        }
        return i;
    }

    public static String getSafeStringUrl(String str) {
        return Uri.encode(str, "@#&=*+-_.,:!?()/~'%;$");
    }

    public static boolean isDigitsOnly(CharSequence charSequence) {
        for (int i = 0; i < charSequence.length(); i++) {
            char charAt = charSequence.charAt(i);
            if (charAt < '0' || charAt > '9') {
                return false;
            }
        }
        return true;
    }

    public static boolean isLetterOnly(CharSequence charSequence) {
        for (int i = 0; i < charSequence.length(); i++) {
            char charAt = charSequence.charAt(i);
            if (charAt < 'A' || charAt > 'Z' || charAt < 'a' || charAt > 'z') {
                return false;
            }
        }
        return true;
    }

    public static boolean isLowerCaseOnly(CharSequence charSequence) {
        for (int i = 0; i < charSequence.length(); i++) {
            char charAt = charSequence.charAt(i);
            if (charAt < 'a' || charAt > 'z') {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    public static boolean isUpperCaseOnly(CharSequence charSequence) {
        for (int i = 0; i < charSequence.length(); i++) {
            char charAt = charSequence.charAt(i);
            if (charAt < 'A' || charAt > 'Z') {
                return false;
            }
        }
        return true;
    }
}