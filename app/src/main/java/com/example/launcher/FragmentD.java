package com.example.launcher;

import androidx.fragment.app.Fragment;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class FragmentD extends Fragment {

    View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_d_layout, container, false);

        FrameLayout f1=((FrameLayout)view.findViewById(R.id.widgetFrame1));
        FrameLayout f2=((FrameLayout)view.findViewById(R.id.widgetFrame2));
        FrameLayout f3=((FrameLayout)view.findViewById(R.id.widgetFrame3));
        FrameLayout f4=((FrameLayout)view.findViewById(R.id.widgetFrame4));

        setCalenderWidget(f1,38);
        return view;
    }


    private int APPWIDGET_HOST_ID = 1;

        private int REQUEST_PICK_APPWIDGET = 2;
        private int REQUEST_CREATE_APPWIDGET = 3;

    AppWidgetManager mAppWidgetManager;
    AppWidgetHost mAppWidgetHost;
    private void initWidgetHost() {
        mAppWidgetManager = AppWidgetManager.getInstance(getContext().getApplicationContext());
        mAppWidgetHost = new AppWidgetHost(getContext().getApplicationContext(), APPWIDGET_HOST_ID);
    }
    private void setCalenderWidget(FrameLayout frame,int appWidgetId) {
        initWidgetHost();
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        AppWidgetHostView hostView =
                mAppWidgetHost.createView(getContext().getApplicationContext(), appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        frame.removeAllViews();
        frame.addView(hostView);

        Log.i("TAG", "The widget size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);

    }

    void selectWidget() {
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_PICK_APPWIDGET) {
                configureWidget(data);
            }
            else if (requestCode == REQUEST_CREATE_APPWIDGET) {
                createWidget(data);
            }
        }
        else if (resultCode == getActivity().RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }
    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        Log.d("TAG", "configureWidget: "+appWidgetId);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            createWidget(data);
        }
    }

    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        AppWidgetHostView hostView =
                mAppWidgetHost.createView(getContext().getApplicationContext(), appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        ((FrameLayout)view.findViewById(R.id.widgetFrame)).removeAllViews();
        ((FrameLayout)view.findViewById(R.id.widgetFrame)).addView(hostView);


        Log.i("TAG", "The widget size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);
    }

}