package com.example.launcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.launcher.utils.AppInfo;
import com.example.launcher.utils.ViewPagerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    public static String favApps[]={"Phone","Google"};

    public static List<AppInfo> appData;
    public static HashMap<String, String> appMap;

    public static Context context;
    public static HashMap<String, String> contactInfoMap;

    public boolean hasPermission;

    ViewPager mPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //permission stuff
        hasPermission=false;
        checkPermission(Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS,30,20);

        //doStuff();

    }
    void doStuff()
    {

        mPager = (ViewPager) findViewById(R.id.mainFrame);
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);

        //to be used elsewhere
        appMap = new HashMap<String, String>();
        appData=generateInstalledAppData();

        contactInfoMap=new HashMap<String, String>();
        getContacts();

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
            mPager.setCurrentItem(1);
        }
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
    // Function to check and request permission.
    public void checkPermission(String permission1,String permission2, int requestCode1,int requestCode2)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission1)
                == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission1 },
                    requestCode1);
        }
        if( ContextCompat.checkSelfPermission(MainActivity.this, permission2)
            == PackageManager.PERMISSION_DENIED){
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission2 },
                    requestCode2);
        }

        if(ContextCompat.checkSelfPermission(MainActivity.this, permission2)
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
                hasPermission=true;
                doStuff();

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
        if(velocityY>10000)
        {
            //open notification
            Log.d("TAG", "Open notification shade");

            try {Object sbservice = MainActivity.context.getSystemService( "statusbar" );
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

