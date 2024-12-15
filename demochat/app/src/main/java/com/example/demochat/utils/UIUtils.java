package com.example.demochat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.demochat.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class UIUtils {

    // Cài đặt màu sắc cho BottomNavigationView
    public static void setupBottomNavigationView(Context context, BottomNavigationView bottomNavigationView) {
        // Lấy ColorStateList từ tài nguyên màu
        ColorStateList colorStateList = AppCompatResources.getColorStateList(context, R.color.bottom_nav_item_colors);

        // Áp dụng màu sắc cho icon và văn bản
        bottomNavigationView.setItemIconTintList(colorStateList);
        bottomNavigationView.setItemTextColor(colorStateList);

        // Hiển thị tên chỉ khi mục được chọn, không hiển thị khi nhấn giữ
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_SELECTED);
    }

    // Cài đặt màu thanh điều hướng
    public static void setNavigationBarColor(Activity activity, int color) {
        if (activity == null) return;

        // Lấy đối tượng Window của Activity để thay đổi màu thanh điều hướng
        Window window = activity.getWindow();
        window.setNavigationBarColor(color);
    }
    /**
     * Thiết lập giao diện để đè lên thanh điều hướng
     * @param activity Activity hiện tại
     */
    public static void setupFullScreenLayout(Activity activity) {
        if (activity == null) return;

        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ sử dụng WindowInsetsController
            window.setDecorFitsSystemWindows(false);
        } else {
            // Android 10 trở xuống sử dụng System UI Flags
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }

    /**
     * Điều chỉnh chiều cao cho các thành phần bên trên thanh điều hướng
     * @param view View cần dịch chuyển
     * @param activity Activity hiện tại
     */
    public static void adjustViewOverNavigationBar(View view, Activity activity) {
        if (view == null || activity == null) return;

        // Dịch chuyển view để đè lên thanh điều hướng
        view.setTranslationY(-getNavigationBarHeight(activity));
    }

    /**
     * Lấy chiều cao của thanh điều hướng
     * @param activity Activity hiện tại
     * @return Chiều cao thanh điều hướng (px)
     */
    private static int getNavigationBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resourceId > 0 ? resources.getDimensionPixelSize(resourceId) : 0;
    }


}
