package com.example.demochat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demochat.adapter.LanguageAdapter;
import com.example.demochat.utils.ContextUtils;
import com.google.firebase.FirebaseException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    private static final String TAG = LoginPhoneNumberActivity.class.getName();
    private FirebaseAuth mAuth;
    private CountryCodePicker countryCodePicker;
    private EditText phoneNumber;
    private Button nextButtonPhone;
    // Phương thức này được gọi khi Activity được tạo ra, nó giúp cập nhật ngôn ngữ cho toàn bộ ứng dụng
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ContextUtils.updateLocale(newBase)); // Cập nhật locale mới
    }

    // Phương thức onCreate được gọi khi Activity được khởi tạo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_phone_number); // Thiết lập layout cho Activity
        init();
        // Thiết lập Spinner chọn ngôn ngữ và xử lý thay đổi ngôn ngữ
        setupLanguageSelector();

        // Thiết lập chức năng xác nhận số điện thoại và chuyển sang màn hình OTP
        setupPhoneNumberAndSend();
    }

    private void setupLanguageSelector() {
        // Khởi tạo và thiết lập LanguageAdapter
        LanguageAdapter languageAdapter = new LanguageAdapter();
        languageAdapter.setupLanguageSelector(this, findViewById(android.R.id.content), new Runnable() {
            @Override
            public void run() {
                // Gọi recreate() sau khi ngôn ngữ thay đổi
                recreate();
            }
        });
    }

    private void init(){
        countryCodePicker = findViewById(R.id.countryCodePicker);
        phoneNumber = findViewById(R.id.phoneNumber);
        nextButtonPhone = findViewById(R.id.nextButtonPhone);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }
    private void setupPhoneNumberAndSend() {

        countryCodePicker.registerCarrierNumberEditText(phoneNumber);

        // Xử lý sự kiện khi người dùng nhấn nút "Next"
        nextButtonPhone.setOnClickListener(view -> {
            // Kiểm tra xem số điện thoại có hợp lệ không
            if (!countryCodePicker.isValidFullNumber()) {
                phoneNumber.setError(getString(R.string.invalid_phone_number)); // Hiển thị lỗi nếu không hợp lệ
                return; // Dừng hành động nếu số điện thoại không hợp lệ
            }
            String phone = countryCodePicker.getFullNumberWithPlus();
            sendOtp(phone);
        });
    }
    private void sendOtp(String phone) {
        PhoneAuthOptions options = PhoneAuthOptions
                .newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(LoginPhoneNumberActivity.this,
                                getString(R.string.verification_failed), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        goToLoginOtpActivity(phone, verificationId);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithCredential:success");

                        // Update UI
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(LoginPhoneNumberActivity.this, getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void goToLoginOtpActivity(String phone, String verificationId) {
        Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
        intent.putExtra("verification_id", verificationId);
        intent.putExtra("phone", phone);
        startActivity(intent);
    }

}
