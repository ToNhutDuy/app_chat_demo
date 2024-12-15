package com.example.demochat;

import android.content.Intent;

import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demochat.model.UserModel;

import com.example.demochat.utils.FirebaseUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;


import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileInformationActivity extends AppCompatActivity {

    private CircleImageView userProfilePic;
    private EditText edtYourName;
    private String phoneNumber;
    private Button btnConfirm;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_information);
        init();

        // Lấy số điện thoại từ Intent
        phoneNumber = getIntent().getStringExtra("phone");



        // Lấy thông tin người dùng từ Firebase
        getProfile();


        // Xử lý khi nhấn nút xác nhận
        btnConfirm.setOnClickListener(v -> setProfile());
    }


    // Cập nhật thông tin người dùng và ảnh đại diện
    private void setProfile() {
        String userName = edtYourName.getText().toString();

        if (userName.isEmpty() || userName.length() < 3) {
            edtYourName.setError(getString(R.string.username_must_be_at_least_3_characters));
            return;
        }

        if (userModel != null) {
                userModel.setUserName(userName);
        } else {
                // Nếu chưa có userModel, tạo mới và lưu vào Firebase
                userModel = new UserModel(FirebaseUtil.currentUserId(),phoneNumber, userName, "", Timestamp.now());
        }
        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    goToMainActivity();
                }
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(ProfileInformationActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void init() {
        userProfilePic = findViewById(R.id.user_pro_file_pic);
        edtYourName = findViewById(R.id.edt_your_name);
        btnConfirm = findViewById(R.id.btn_confirm);
    }

    private void getProfile() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if(userModel != null){
                        edtYourName.setText(userModel.getUserName());
                    }
                }
            }
        });
    }
}
