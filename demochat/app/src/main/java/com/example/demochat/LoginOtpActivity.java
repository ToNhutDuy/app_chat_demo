package com.example.demochat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demochat.utils.AndroidUtil;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LoginOtpActivity extends AppCompatActivity {

    private static final int OTP_LENGTH = 6;
    private EditText[] otpFields;
    private Button btnVerifyOtp;
    private TextView tvResendOtp, tvPhoneNumber;
    private String mVerificationId, mPhoneNumber;
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;
    private FirebaseAuth mAuth;
    public static final String TAG = LoginOtpActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_otp);
        init();
        getDataIntent();
        tvPhoneNumber.setText(mPhoneNumber);
        setupOtpFields();

        // Sự kiện xác nhận mã otp
        btnVerifyOtp.setOnClickListener(v -> verifyOtp());

        // Sự kiện gửi lại otp
        tvResendOtp.setOnClickListener(v -> resendOtp());
    }

    private void init() {
        // Khởi tạo các trường OTP
        otpFields = new EditText[]{
                findViewById(R.id.edt_otp1),
                findViewById(R.id.edt_otp2),
                findViewById(R.id.edt_otp3),
                findViewById(R.id.edt_otp4),
                findViewById(R.id.edt_otp5),
                findViewById(R.id.edt_otp6)
        };

        // Hiển thị số điện thoại
        tvPhoneNumber = findViewById(R.id.tv_phoneNumberShow);

        // Khởi tạo các nút và text
        btnVerifyOtp = findViewById(R.id.btn_verifyOTP);
        tvResendOtp = findViewById(R.id.tv_reSendOTP);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void getDataIntent() {
        // Lấy số điện thoại từ intent
        mPhoneNumber = getIntent().getStringExtra("phone");
        mVerificationId = getIntent().getStringExtra("verification_id");
    }

    private void setupOtpFields() {
        for (int i = 0; i < OTP_LENGTH; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < OTP_LENGTH - 1) {
                        otpFields[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        otpFields[index - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void resendOtp() {
        if (mVerificationId != null) {
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(mPhoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setForceResendingToken(mForceResendingToken)
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                            Log.d("LoginOtpActivity", "onVerificationCompleted: " + credential);
                            signInWithPhoneAuthCredential(credential);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Log.e(TAG, "onVerificationFailed: " + e.getMessage());
                            Toast.makeText(LoginOtpActivity.this,
                                    "Cannot resend OTP, please try again later",
                                    Toast.LENGTH_SHORT).show();
                            tvResendOtp.setEnabled(true);
                        }

                        @Override
                        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(verificationId, forceResendingToken);
                            Log.d("LoginOtpActivity", "onCodeSent: " + verificationId);
                            mVerificationId = verificationId;
                            mForceResendingToken = forceResendingToken;
                            startResendTimer();
                        }
                    })
                    .build();

            PhoneAuthProvider.verifyPhoneNumber(options);
            resetOtpFields();
            startResendTimer();
        } else {
            Toast.makeText(this, "Cannot resend OTP, please try again later", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyOtp() {
        btnVerifyOtp.setEnabled(false);
        String otpCode = collectOtpCode();
        if (otpCode == null) {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            btnVerifyOtp.setEnabled(true);
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpCode);
        signInWithPhoneAuthCredential(credential);
    }

    private String collectOtpCode() {
        StringBuilder otpCode = new StringBuilder();
        for (EditText field : otpFields) {
            String input = field.getText().toString().trim();
            if (input.isEmpty() || !input.matches("\\d")) {
                return null; // Invalid OTP
            }
            otpCode.append(input);
        }
        return otpCode.toString();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null)
                            goToProfile(user.getPhoneNumber());
                    } else {
                        // Xử lý lỗi đăng nhập
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(LoginOtpActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }
                        btnVerifyOtp.setEnabled(true);
                    }
                });
    }

    private void goToProfile(String phoneNumber) {
        AndroidUtil.showToats(LoginOtpActivity.this, "TRAin.......");
        Intent intent = new Intent(LoginOtpActivity.this, ProfileInformationActivity.class);
        intent.putExtra("phone", phoneNumber);
        startActivity(intent);
        //finish(); // Kết thúc activity hiện tại
    }

    private void startResendTimer() {
        tvResendOtp.setEnabled(false);

        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvResendOtp.setTextColor(Color.GRAY);
                tvResendOtp.setText(String.format(Locale.getDefault(),
                        "Resend OTP in %d seconds",
                        millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tvResendOtp.setText("Resend OTP");
                tvResendOtp.setEnabled(true);
            }
        }.start();
    }

    private void resetOtpFields() {
        for (EditText field : otpFields) {
            field.setText("");
        }
        otpFields[0].requestFocus();
    }
}