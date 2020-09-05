package com.example.launcher;

import androidx.fragment.app.Fragment;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class FragmentD extends Fragment {

    View view;

    SharedPreferences sharedPref;
    FrameLayout frame;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        sharedPref= PreferenceManager.getDefaultSharedPreferences(getContext());

        view = inflater.inflate(R.layout.fragment_d_layout, container, false);
        LinearLayout widgetHolderLayout=(LinearLayout)view.findViewById(R.id.widgetHolderLayout);

        Button addWidgetBtn=view.findViewById(R.id.addWidgetBtn);

        addWidgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectWidget();
            }
        });

        int widgetTotal = sharedPref.getInt("widgetTotal",0);

        for(int i=1;i<=widgetTotal;i++)
        {
            int widgetId = sharedPref.getInt("widget"+i,0);
            Log.d("TAG", "setting widget id: "+widgetId);
            frame=new FrameLayout(view.getContext());
            frame.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 650));

            setWidgetWithID(frame,widgetId);
            widgetHolderLayout.addView(frame);
        }

         return view;
    }


    private int APPWIDGET_HOST_ID = 1;

    private int REQUEST_PICK_APPWIDGET = 2;
    private int REQUEST_CREATE_APPWIDGET = 3;

    AppWidgetManager mAppWidgetManager;
    AppWidgetHost mAppWidgetHost;


    private void setWidgetWithID(FrameLayout frame, int appWidgetId) {
        initWidgetHost();
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        AppWidgetHostView hostView =
                mAppWidgetHost.createView(getContext().getApplicationContext(), appWidgetId, appWidgetInfo);


                
        try {
            frame.removeAllViews();
        } catch (Exception e) {
        }

        Log.i("TAG", "The widget size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);
        frame.addView(hostView);

    }
       private void initWidgetHost() {
        mAppWidgetManager = AppWidgetManager.getInstance(getContext().getApplicationContext());
        mAppWidgetHost = new AppWidgetHost(getContext().getApplicationContext(), APPWIDGET_HOST_ID);
    }

    void selectWidget() {
        initWidgetHost();
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_PICK_APPWIDGET ) {
                Bundle extras = data.getExtras();
                int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

                saveWidget(appWidgetId);
                configureWidget(frame,appWidgetId);

            }
            if( requestCode == REQUEST_CREATE_APPWIDGET)
            {
                Bundle extras = data.getExtras();
                int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                setWidgetWithID(frame,appWidgetId);
            }
        }
        else if (resultCode == getActivity().RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    private void saveWidget(int appWidgetId) {
        int widgetTotal = sharedPref.getInt("widgetTotal",0);
        widgetTotal++;

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("widgetTotal", widgetTotal);
        editor.putInt("widget"+(widgetTotal), appWidgetId);
        editor.commit();
    }

    private void configureWidget(FrameLayout frame,int appWidgetId) {
        Log.d("TAG", "configureWidget: "+appWidgetId);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        //if widget need any config
        if (appWidgetInfo.configure != null) {
             Log.d("TAG", "Configuring widget");
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        }
        //after configuation this is called
        else {
            setWidgetWithID(frame,appWidgetId);
        }
    }

}