package com.example.launcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.launcher.utils.AppInfo;
import com.example.launcher.utils.ViewPagerAdapter;
import com.google.android.material.snackbar.Snackbar;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity  {

    public static String favApps[]={"Reminder"};

    public static List<AppInfo> appData;
    public static HashMap<String, String> appMap;

    public static Context context;
    public static HashMap<String, String> contactInfoMap;

    boolean hasPermission;
    ViewPager mPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //permission stuff
        hasPermission=false;
        //also has calendar permissions
        checkPermission(Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS,30,20);


    }
    private void doStuff()
    {

        //to be used elsewhere
        Log.d("Perf", "appData collection async class call");
        new MyTask1().execute();
        //appData=generateInstalledAppData();//appMAp is also initaialize here
        Log.d("Perf", "contactData collection async class call");
        new MyTask2().execute();
        //contactInfoMap=generateContactsData();
        Log.d("Perf", "contactData next line");

        mPager = (ViewPager) findViewById(R.id.mainFrame);
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mPager.setAdapter(mPagerAdapter);

        mPager.setCurrentItem(0);


        // get the gesture detector for pull down notification
        mDetector = new GestureDetector(this, new MyGestureListener());
        context=getApplicationContext();
        mPager.setOnTouchListener(touchListener);

    }
    GestureDetector mDetector;
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mDetector.onTouchEvent(event);

        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        if(hasPermission) {
            mPager.setCurrentItem(0);
        }
    }

    private class MyTask1 extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            Log.d("Perf", "ASYNC appData collection from android started");
            appData=generateInstalledAppData();//appMAp is also initaialize here
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Perf", "ASYNC appData collection from android ended");
            super.onPostExecute(result);
            // do something with result
        }
    }
    private class MyTask2 extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            Log.d("Perf", "ASYNC contactData collection from android started");
            contactInfoMap=generateContactsData();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Perf", "ASYNC contactData collection from android ended");
            super.onPostExecute(result);
            // do something with result
        }
    }

    private List<AppInfo> generateInstalledAppData() {
        appMap=new HashMap<String,String>();
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

    private HashMap<String, String> generateContactsData(){
        HashMap<String,String> contactInfoMap =new HashMap<String, String>();
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
                    String phoneNumber = "";
                    if (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    }

                    phoneCursor.close();

                    //contactsInfoList.add(contactsInfo);
                    contactInfoMap.put(displayName, phoneNumber);
                }
            }
        }
        cursor.close();
    return contactInfoMap;
    }
    // Function to check and request permission.
    private void checkPermission(String permission1,String permission2, int requestCode1,int requestCode2)
    {
        //calendar permission
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR}, 5631);

        if (ContextCompat.checkSelfPermission(MainActivity.this, permission1)
                == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission1 },
                    requestCode1);
        }
        else if( ContextCompat.checkSelfPermission(MainActivity.this, permission2)
            == PackageManager.PERMISSION_DENIED){
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission2 },
                    requestCode2);
        }

        else if(ContextCompat.checkSelfPermission(MainActivity.this, permission2)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, permission2)
                == PackageManager.PERMISSION_GRANTED)
            {
            doStuff();
            hasPermission=true;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if (requestCode == 30 || requestCode==20) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermission(Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS,30,20);
                //hasPermission=true;
                //doStuff();

            }
            else {
                hasPermission=false;
                Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }



}

class MyGestureListener extends GestureDetector.SimpleOnGestureListener {



    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        //Log.d("TAG", "onFling: ");
        //Log.d("vx", "onFling: "+velocityX);
        //Log.d("vy", "onFling: "+velocityY);
        if(velocityY>8000)
        {
            //open notification
            Log.d("TAG", "Open notification shade");

            try {
                @SuppressLint("WrongConstant") Object sbservice = MainActivity.context.getSystemService( "statusbar" );
                Class<?> statusbarManager = Class.forName( "android.app.StatusBarManager" );
                Method showsb;
                if (Build.VERSION.SDK_INT >= 17) {
                    showsb = statusbarManager.getMethod("expandNotificationsPanel");
                }
                else {
                    showsb = statusbarManager.getMethod("expand");
                }
                showsb.invoke( sbservice );
            }catch (Exception e){
                Log.e("TAG", e.getMessage());
            }
        }
        return false;
    }
}

