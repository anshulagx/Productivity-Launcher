package com.example.launcher.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarManager {
    private int calId=3;// google calender ID
    private Context context;

    boolean hasCalendarPermission(){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }
    void getPermissions(){
        Toast.makeText(context,"Please give calendar permission from settings",Toast.LENGTH_SHORT).show();
    }

    public void parseAndAdd(String t, Context context) {
        this.context=context;

        TextParser tp=new TextParser(t);
        addEvent(tp.getTitle(),tp.getDescription(),tp.getStartMill(),tp.getEndMill(), Color.YELLOW);

    }

    public void getCalendars() {

        if (!hasCalendarPermission()) {
            getPermissions();
            return;
        }

        // Projection array. Creating indices for this array instead of doing dynamic lookups improves performance.
        final String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
        final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;


        ContentResolver contentResolver = context.getContentResolver();
        Cursor cur = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION, null, null, null);

        ArrayList<String> calendarInfos = new ArrayList<>();
//        Log.d("CAL","1");
        while (cur.moveToNext()) {
//            Log.d("CAL","2");
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            String calendarInfo = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName);
            calendarInfos.add(calendarInfo);

        }

    }


    public void addEvent(String title, String desc, long startMill,long endMill,int color) {
        if (!hasCalendarPermission()) {
            getPermissions();
            return;
        }
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMill);
        values.put(CalendarContract.Events.DTEND, endMill);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, desc);
        values.put(CalendarContract.Events.CALENDAR_ID, calId);
        values.put(CalendarContract.Events.EVENT_COLOR, color);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "TimeZone.getDefault().getID()");
        values.put(CalendarContract.Events.ORGANIZER, "T9 Launcher");


            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            long eventID = Long.parseLong(uri.getLastPathSegment());
            Log.i("Calendar", "Event Created, the event id is: " + eventID+title);
            Toast.makeText(context,"Added to calendar",Toast.LENGTH_SHORT).show();


    }


}
