package com.example.launcher;

import androidx.fragment.app.Fragment;
//import android.app.Fragment;
import android.app.AlertDialog;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.launcher.utils.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class FragmentA extends Fragment  {


    View view;
    final int displayWidgetid=38;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_a_layout, container, false);

        List<AppInfo> appList=MainActivity.appData;

        handelFavButtons(appList,MainActivity.favApps,view);

        //everything about widgets in Fragment A
        setCalenderWidget(displayWidgetid);//38 is default for calendar app widget

        return view;
    }

    private void handelFavButtons(List<AppInfo> appList, String[] favApps, View view) {

        final LinearLayout linearLayout=(LinearLayout)view.findViewById(R.id.favAppLayout);

        for (final AppInfo app:appList)
        {
            for (int i=0;i<favApps.length;i++) {

                if (app.label.equalsIgnoreCase(favApps[i]))
                {
                    TextView text = new TextView(getActivity());
                    text.setText(app.label);
                    //text.setId(R.id.fav1);
                    text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    text.setPadding(5, 20, 5, 20);
                    text.setTextSize(30);
                    text.setTextColor(getResources().getColor(R.color.colorPrimary));


                    linearLayout.addView(text);

                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(app.packageName);
                            startActivity(launchIntent);
                        }
                    });

                    text.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(final View view) {
                            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                            builder.setTitle("Settings");
                            String options[]={"Remove from Favourite","Settings","Uninstall"};
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    TextView txt = (TextView) view;

                                    String option = txt.getText().toString();


                                    switch (i) {
                                        case 0:
                                            //TODO
                                            //add to favourite
                                            break;
                                        case 1:
                                            //settings
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", MainActivity.appMap.get(option), null);
                                            intent.setData(uri);
                                            view.getContext().startActivity(intent);
                                            break;
                                        case 3:
                                            //uninstall
                                            Intent intent1=new Intent(Intent.ACTION_DELETE);
                                            intent1.setData(Uri.parse("package:"+MainActivity.appMap.get(option)));
                                            view.getContext().startActivity(intent1);
                                            break;

                                    }
                                }


                            });

                            AlertDialog dialog=builder.create();
                            dialog.show();

                            return false;
                        }
                    });
                }
            }
        }


    }



    AppWidgetManager mAppWidgetManager;
    AppWidgetHost mAppWidgetHost;
    private int APPWIDGET_HOST_ID = 1;
//    private int REQUEST_PICK_APPWIDGET = 2;
//    private int REQUEST_CREATE_APPWIDGET = 3;
    private void initWidgetHost() {
        mAppWidgetManager = AppWidgetManager.getInstance(getContext().getApplicationContext());
        mAppWidgetHost = new AppWidgetHost(getContext().getApplicationContext(), APPWIDGET_HOST_ID);
    }
    private void setCalenderWidget(int appWidgetId) {
        initWidgetHost();
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        AppWidgetHostView hostView =
                mAppWidgetHost.createView(getContext().getApplicationContext(), appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        ((FrameLayout)view.findViewById(R.id.widgetFrame)).removeAllViews();
        ((FrameLayout)view.findViewById(R.id.widgetFrame)).addView(hostView);

//        Log.i("TAG", "The widget size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);

    }

//    void selectWidget() {
//        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
//        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
//        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == getActivity().RESULT_OK) {
//            if (requestCode == REQUEST_PICK_APPWIDGET) {
//                configureWidget(data);
//            }
//            else if (requestCode == REQUEST_CREATE_APPWIDGET) {
//                createWidget(data);
//            }
//        }
//        else if (resultCode == getActivity().RESULT_CANCELED && data != null) {
//            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
//            if (appWidgetId != -1) {
//                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
//            }
//        }
//    }
//    private void configureWidget(Intent data) {
//        Bundle extras = data.getExtras();
//        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
//        Log.d("TAG", "configureWidget: "+appWidgetId);
//        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
//        if (appWidgetInfo.configure != null) {
//            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
//            intent.setComponent(appWidgetInfo.configure);
//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
//        } else {
//            createWidget(data);
//        }
//    }
//    public void createWidget(Intent data) {
//        Bundle extras = data.getExtras();
//        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
//
//        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
//
//        AppWidgetHostView hostView =
//                mAppWidgetHost.createView(getContext().getApplicationContext(), appWidgetId, appWidgetInfo);
//        hostView.setAppWidget(appWidgetId, appWidgetInfo);
//        ((FrameLayout)view.findViewById(R.id.widgetFrame)).removeAllViews();
//        ((FrameLayout)view.findViewById(R.id.widgetFrame)).addView(hostView);
//
//        Log.i("TAG", "The widget size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);
//    }


    @Override public void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }
    @Override public void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }

}