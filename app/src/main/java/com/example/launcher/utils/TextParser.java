package com.example.launcher.utils;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class TextParser {

    private static final String TAG = "51258a";
    String text;

    long startMill,endMill;
    Calendar now;

    private int buffer = 2 ; // default hrs between start and end

    static String words[]={"later","tomorrow","morning","noon","afternoon","evening","after"};

    private int MORNING_HOUR=8;
    private int NOON_HOUR=12;
    private int AFTERNOON_HOUR=12+4;
    private int EVENING_HOUR=12+6;
    private int NIGHT_HOUR=12+9;

    private int LATER_DELAY=5;

    public TextParser(){

    }
    public TextParser(String text){

        this.text = text.toLowerCase();

        now = Calendar.getInstance();


        startMill= now.getTimeInMillis();
        endMill= startMill + TimeUnit.HOURS.toMillis(5);
        processTime();

    }
    public static boolean isParsable(String s){
        s=s.toLowerCase();

        for (String str:words){
            if (s.contains(str))
                    return true;
        }
        return false;
    }

    public String getTitle() {
        String title = text.split("\n")[0];
        Log.d(TAG,"Title:"+title);

        return text;
    }

    public String getDescription() {
        //String title = text.substring(text.indexOf('\n')+1,text.length());
        //Log.d(TAG,"Title:"+title);
        return "Desc";
    }
    void processTime(){
        Calendar temp = now;

        if(text.contains("later")){
            temp.add(Calendar.HOUR_OF_DAY,LATER_DELAY);

        }
        else if(text.contains("tomorrow")){
            temp.set(Calendar.HOUR_OF_DAY,MORNING_HOUR);
            temp.add(Calendar.DATE,1);
        }
        else if(text.contains("morning")){
            int hr = temp.get(Calendar.HOUR_OF_DAY);
            temp.set(Calendar.HOUR_OF_DAY,MORNING_HOUR);
            if(hr > MORNING_HOUR)
                temp.add(Calendar.DATE,1);

        }
        else if(text.contains("noon")){
            int hr = temp.get(Calendar.HOUR_OF_DAY);
            temp.set(Calendar.HOUR_OF_DAY,NOON_HOUR);
            if(hr > NOON_HOUR)
                temp.add(Calendar.DATE,1);
        }
        else if(text.contains("afternoon")){
            int hr = temp.get(Calendar.HOUR_OF_DAY);
            temp.set(Calendar.HOUR_OF_DAY,AFTERNOON_HOUR);
            if(hr > AFTERNOON_HOUR)
                temp.add(Calendar.DATE,1);
        }
        else if(text.contains("evening")){
            int hr = temp.get(Calendar.HOUR_OF_DAY);
            temp.set(Calendar.HOUR_OF_DAY,EVENING_HOUR);
            if(hr > EVENING_HOUR)
                temp.add(Calendar.DATE,1);
        }
        else if(text.contains("night")){
            int hr = temp.get(Calendar.HOUR_OF_DAY);
            temp.set(Calendar.HOUR_OF_DAY,NIGHT_HOUR);
            if(hr > NIGHT_HOUR)
                temp.add(Calendar.DATE,1);
        }
        else if(text.contains("after")){

        }

        startMill = temp.getTimeInMillis();
        temp.add(Calendar.HOUR_OF_DAY,buffer);
        endMill = temp.getTimeInMillis();

    }
    public long getStartMill() {
        return startMill;
    }

    public long getEndMill() {
        return endMill;
    }


    private long Time(int min, int hr, int date, int month, int year){
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month-1, date, hr, min);
        return beginTime.getTimeInMillis();
    }
}
