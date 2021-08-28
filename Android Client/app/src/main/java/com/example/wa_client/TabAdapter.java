package com.example.wa_client;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabAdapter extends FragmentStateAdapter {
    public TabAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment ;
        if(position == 0)
            fragment = new ChatList();
        else
            fragment = new Media();
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
