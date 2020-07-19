package com.example.launcher.utils;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public String  label;
    public String packageName;
    public Drawable icon;
    public AppInfo()
    {}

    public AppInfo(String s, String pkg) {
        label=s;
        packageName=pkg;
    }
}
