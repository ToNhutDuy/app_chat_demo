package com.example.demochat;

import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.demochat.adapter.SearchUserRecyclerAdapter;
import com.example.demochat.adapter.ViewPagerAdapter;

import com.example.demochat.utils.AndroidUtil;

import com.example.demochat.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView menuBottomNavigationView;
    ViewPager2 viewPager2;
    Toolbar mainToolbar;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter searchUserRecyclerAdapter;
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        init();
        //menuBottomNavigationView.setItemBackgroundResource(android.R.color.transparent);



        // Setup ViewPager2
        viewPager2.setAdapter(new ViewPagerAdapter(MainActivity.this));

        // Handle BottomNavigationView item selection
        menuBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menu_chat){
                    viewPager2.setCurrentItem(0);
                    return true;
                }else if(item.getItemId() == R.id.menu_profile){
                    viewPager2.setCurrentItem(1);
                    return true;
                }
                return false;
            }
        });


        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                menuBottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });
        mainToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    goToSearchActivity();
                    return true;
                } else if (item.getItemId() == R.id.action_function) {
                    return true;
                } else if (item.getItemId() == R.id.video_call) {
                    AndroidUtil.showToats(MainActivity.this, getString(R.string.video_call));
                    return true;
                } else if (item.getItemId() == R.id.logout) {
                    FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                AndroidUtil.showToats(MainActivity.this, getString(R.string.logout));
                                FirebaseUtil.logout();
                                Intent intent = new Intent(getApplication(), ActivitySplash.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                    });

                    return true;
                }
                return false;
            }
        });
        getFCToken();
    }

    private void getFCToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task ->{
           if(task.isSuccessful()){
               String token = task.getResult();
               FirebaseUtil.currentUserDetails().update("fcmToken", token);
           }
        });
    }

    private void goToSearchActivity() {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.function_menu_chat, menu);
        return true;
    }

    private void init() {
        viewPager2 = findViewById(R.id.view_pager2);
        menuBottomNavigationView = findViewById(R.id.bottom_navigation);
        mainToolbar = findViewById(R.id.main_toolbar);
    }




}