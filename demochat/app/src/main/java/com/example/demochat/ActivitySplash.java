package com.example.demochat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ActivitySplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int delayMillis = 600;

        // Tạo độ trễ trước khi chuyển màn hình
        new Handler().postDelayed(() -> {
            startActivity(new Intent(ActivitySplash.this, LoginPhoneNumberActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, delayMillis);
    }
}
