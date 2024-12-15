package com.example.demochat.adapter;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.demochat.R;
import com.example.demochat.utils.ContextUtils;

public class LanguageAdapter {

    // Phương thức thiết lập Spinner chọn ngôn ngữ
    public void setupLanguageSelector(Context context, View rootView, Runnable onLanguageChanged) {
        // Lấy Spinner từ layout và thiết lập dữ liệu ngôn ngữ
        Spinner languageSpinner = rootView.findViewById(R.id.languageSpinner);
        String[] languages = {context.getString(R.string.Vietnamese), context.getString(R.string.English)};

        // Thiết lập ArrayAdapter cho Spinner để hiển thị các lựa chọn ngôn ngữ
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, languages);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Cài đặt giao diện dropdown
        languageSpinner.setAdapter(arrayAdapter); // Gắn adapter vào Spinner

        // Lấy ngôn ngữ đã lưu từ Preferences, mặc định là Tiếng Việt
        String savedLanguage = ContextUtils.getSavedLanguage(context);
        languageSpinner.setSelection("vi".equals(savedLanguage) ? 0 : 1); // Chọn ngôn ngữ mặc định

        // Lắng nghe sự kiện khi người dùng chọn ngôn ngữ mới
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                String selectedLanguage = (position == 0) ? "vi" : "en"; // Xác định ngôn ngữ đã chọn
                if (!selectedLanguage.equals(savedLanguage)) { // Nếu ngôn ngữ thay đổi
                    // Lưu ngôn ngữ mới vào SharedPreferences
                    ContextUtils.setLanguage(context, selectedLanguage);
                    // Gọi lại phương thức được truyền vào để thực hiện hành động như gọi recreate()
                    if (onLanguageChanged != null) {
                        onLanguageChanged.run();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Không làm gì khi không có sự lựa chọn
            }
        });
    }
}
