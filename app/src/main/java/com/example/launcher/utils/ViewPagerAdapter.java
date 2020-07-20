package com.example.launcher.utils;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.launcher.FragmentA;
import com.example.launcher.FragmentB;
import com.example.launcher.FragmentC;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    static final int NUM_PAGES=3;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new FragmentB();
            case 1:
                return new FragmentA();
            case 2:
                return new FragmentC();
        }
        return new FragmentA();
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}

