package com.example.launcher.utils;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.launcher.FragmentA;
import com.example.launcher.FragmentB;
import com.example.launcher.FragmentC;
import com.example.launcher.FragmentD;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    static final int NUM_PAGES=4;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new FragmentD();
            case 1:
                return new FragmentA();
            case 2:
                return new FragmentC();
            case 3:
                return new FragmentB();

        }
        return new FragmentA();
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}

