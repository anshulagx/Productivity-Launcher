package com.example.launcher.utils;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.launcher.FragmentHome;
import com.example.launcher.FragmentT9;
import com.example.launcher.FragmentWidget;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    static final int NUM_PAGES=2;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
//            case 0:
//                return new FragmentWidget();
            case 0:
                return new FragmentHome();
            case 1:
                return new FragmentT9();
        }
        return new FragmentHome();
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}

