package com.example.launcher.utils;

import android.graphics.drawable.Drawable;

import com.example.launcher.MainActivity;

public class AppInfo {
    public String  label;
    public String packageName;
    public Drawable icon;

    public boolean isContact;
    public String contactNo;
    public AppInfo()
    {
        isContact=false;
    }

    public AppInfo(String s, String pkg,Drawable icon) {
        label=s;
        packageName=pkg;
        this.icon=icon;
        isContact=false;
    }

    public void isContact() {
        this.isContact=true;
    }
}
