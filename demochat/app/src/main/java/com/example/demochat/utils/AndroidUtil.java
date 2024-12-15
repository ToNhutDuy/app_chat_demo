package com.example.demochat.utils;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.example.demochat.model.UserModel;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;

public class AndroidUtil {
    public static void showToats(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void passUserModelAsIntent(Intent intent, UserModel userModel){
        intent.putExtra("userName", userModel.getUserName());
        intent.putExtra("userId", userModel.getUserId());
        intent.putExtra("phoneNumber", userModel.getPhoneNumber());
    }
    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setPhoneNumber(intent.getStringExtra("phoneNumber"));
        userModel.setUserName(intent.getStringExtra("userName"));
        return userModel;
    }

    public static void hideKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && context.getSystemService(Context.INPUT_METHOD_SERVICE) != null) {
            View view = ((AppCompatActivity) context).getCurrentFocus();
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }


    public static void setItemDecorationHoriAndVerti(RecyclerView recyclerView, Context context){
        // Thêm phân cách chiều dọc
        DividerItemDecoration verticalDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(verticalDecoration);

        // Thêm phân cách chiều ngang
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(horizontalDecoration);
    }
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }


}
