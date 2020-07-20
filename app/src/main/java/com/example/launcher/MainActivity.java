package com.example.launcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.example.launcher.utils.AppInfo;
import com.example.launcher.utils.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String favApps[]={"Phone","Google"};

    public static List<AppInfo> appData;
    public static HashMap<String, String> appMap;


    public static HashMap<String, String> contactInfoMap;

    ViewPager mPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPager = (ViewPager) findViewById(R.id.mainFrame);
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);

        //to be used elsewhere
        appMap = new HashMap<String, String>();
        appData=generateInstalledAppData();

        contactInfoMap=new HashMap<String, String>();

        getContacts();

    }
    @Override
    protected void onStart() {
        super.onStart();
        mPager.setCurrentItem(1);
    }
    private List<AppInfo> generateInstalledAppData() {
        PackageManager pm = getPackageManager();
        List<AppInfo> appsList = new ArrayList<AppInfo>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> allApps = pm.queryIntentActivities(i, 0);
        for(ResolveInfo ri:allApps) {
            AppInfo app = new AppInfo();
            app.label = ri.loadLabel(pm).toString();
            app.packageName = ri.activityInfo.packageName.toString();
            app.icon = ri.activityInfo.loadIcon(pm);
            appsList.add(app);

            appMap.put(app.label,app.packageName);
        }

        return appsList;

    }

    private void getContacts(){
        ContentResolver contentResolver = getContentResolver();
        String contactId = null;
        String displayName = null;
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    //ContactsInfo contactsInfo = new ContactsInfo();
                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);
                    String phoneNumber="";
                    if (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    }

                    phoneCursor.close();

                    //contactsInfoList.add(contactsInfo);
                    contactInfoMap.put(displayName,phoneNumber);
                }
            }
        }
        cursor.close();

    }




}