package com.example.demochat.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.demochat.ChatFragment;
import com.example.demochat.ProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter{
    public static final int NUMBER_PAGE = 2;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ChatFragment();
        } else if (position == 1) {
            return new ProfileFragment();
        }
        return new ChatFragment();
    }
    @Override
    public int getItemCount() {
        return NUMBER_PAGE;
    }
}
