package com.example.demochat.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public class ContextUtils {
    // Tên của SharedPreferences để lưu trữ thông tin ngôn ngữ
    private static final String PREF_NAME = "app_preferences";
    // Khóa để lưu trữ ngôn ngữ trong SharedPreferences
    private static final String LANGUAGE_KEY = "language";

    public static void setLanguage(Context context, String lang) {
        // Lưu ngôn ngữ vào SharedPreferences với khóa LANGUAGE_KEY
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putString(LANGUAGE_KEY, lang)
                .apply();// Lưu thay đổi vào SharedPreferences một cách bất đồng bộ
    }

    public static String getSavedLanguage(Context context) {
        // Lấy giá trị ngôn ngữ đã lưu từ SharedPreferences. Nếu không có thì mặc định là "vi".
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(LANGUAGE_KEY, "vi");
    }

    public static Context updateLocale(Context context) {
        // Tạo Locale mới dựa trên ngôn ngữ đã lưu (ví dụ: "vi" hoặc "en").
        Locale locale = new Locale(getSavedLanguage(context));
        Locale.setDefault(locale);// Đặt locale mặc định của ứng dụng

        // Lấy Configuration hiện tại của ứng dụng (bao gồm thông tin về ngôn ngữ, độ phân giải, ...)
        Configuration config = context.getResources().getConfiguration();
        // Nếu SDK >= API 24 (Android 7.0 trở lên), sử dụng LocaleList để hỗ trợ nhiều ngôn ngữ
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);  // Đặt locale cho Configuration
        } else {
            // Đối với các phiên bản thấp hơn, sử dụng phương thức cũ config.locale
            config.locale = locale;
        }
        // Trả về Context mới với cấu hình ngôn ngữ đã được thay đổi
        return context.createConfigurationContext(config);
    }
}