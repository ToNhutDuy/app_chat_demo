package com.example.demochat;


import android.content.Intent;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;


import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demochat.model.UserModel;
import com.example.demochat.utils.AndroidUtil;
import com.example.demochat.utils.FirebaseUtil;



public class ActivitySplash extends AppCompatActivity {
    private static final int SPLASH_DELAY = 700; // Thời gian delay trước khi chuyển màn hình
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        if(FirebaseUtil.isLoggedId() && getIntent().getExtras() != null){
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionReference().document(userId)
                    .get().addOnCompleteListener(task->{
                        if(task.isSuccessful()){
                            UserModel model = task.getResult().toObject(UserModel.class);
                            Intent mIntent = new Intent(ActivitySplash.this, MainActivity.class);
                            mIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mIntent);

                            Intent intent = new Intent(ActivitySplash.this, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

        }else{
            findViewById(android.R.id.content).postDelayed(this::navigateToNextScreen, SPLASH_DELAY);
        }

    }

    /**
     * Điều hướng đến màn hình tiếp theo dựa trên trạng thái đăng nhập.
     */
    private void navigateToNextScreen() {

        if(FirebaseUtil.isLoggedId()){
            startActivity(new Intent(ActivitySplash.this, MainActivity.class));
        }else{
            startActivity(new Intent(ActivitySplash.this, LoginPhoneNumberActivity.class));
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }


}
