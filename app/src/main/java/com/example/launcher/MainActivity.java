package com.example.launcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.launcher.t9search.MapTrie;
import com.example.launcher.t9search.T9Trie;
import com.example.launcher.utils.AppInfo;
import com.example.launcher.utils.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    public static String favApps[]={"Phone","Google"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager mPager = (ViewPager) findViewById(R.id.mainFrame);
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);


    }




}