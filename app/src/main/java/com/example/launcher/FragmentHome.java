package com.example.launcher;

import androidx.fragment.app.Fragment;
//import android.app.Fragment;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.launcher.utils.AppInfo;
import com.example.launcher.utils.CalendarManager;
import com.example.launcher.utils.TextParser;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.launcher.R.color.noteHighlight1;

public class FragmentHome extends Fragment implements View.OnClickListener {


    View view;
     int displayWidgetid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_a_layout, container, false);

        //List<AppInfo> appList=MainActivity.appData;
        //handelFavButtons(appList,MainActivity.favApps,view);

        handelNotes();

        //everything about widgets in Fragment A
        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(getContext());
        int widgetId = sharedPref.getInt("defaultWidget",-1);

        if(widgetId==-1)
            selectWidget();
        else {
            displayWidgetid=widgetId;
            setDefaultWidget(displayWidgetid);//38 is default for calendar app widget
        }
        return view;
    }

    private void handelNotes() {
        TextView note1=view.findViewById(R.id.note1);
        TextView note2=view.findViewById(R.id.note2);
        TextView note3=view.findViewById(R.id.note3);
        TextView note4=view.findViewById(R.id.note4);
        TextView note5=view.findViewById(R.id.note5);
        TextView note6=view.findViewById(R.id.note6);

        /*
        check if note 1 is available
        set text of note one
        repeat for others
        * */
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(getActivity());

                note1.setText(sp.getString(R.id.note1+"",""));
                //Log.d("Notes:",sp.getString(R.id.note1+"",""));
                note2.setText(sp.getString(R.id.note2+"",""));
                note3.setText(sp.getString(R.id.note3+"",""));
                note4.setText(sp.getString(R.id.note4+"",""));
                note5.setText(sp.getString(R.id.note5+"",""));
                note6.setText(sp.getString(R.id.note6+"",""));

                Log.d("notes1:", sp.getString(("c"+R.id.note1),""));

        note1.setBackgroundResource(Integer.parseInt((sp.getString(("c"+R.id.note1), "0"))));
        note2.setBackgroundResource(Integer.parseInt((sp.getString(("c"+R.id.note2), "0"))));
        note3.setBackgroundResource(Integer.parseInt((sp.getString(("c"+R.id.note3), "0"))));
        note4.setBackgroundResource(Integer.parseInt((sp.getString(("c"+R.id.note4), "0"))));
        note5.setBackgroundResource(Integer.parseInt((sp.getString(("c"+R.id.note5), "0"))));
        note6.setBackgroundResource(Integer.parseInt((sp.getString(("c"+R.id.note6), "0"))));



        note1.setOnClickListener(this);
        note2.setOnClickListener(this);
        note3.setOnClickListener(this);
        note4.setOnClickListener(this);
        note5.setOnClickListener(this);
        note6.setOnClickListener(this);


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
                            String options[]={"Settings","Uninstall"};
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    TextView txt = (TextView) view;

                                    String option = txt.getText().toString();


                                    switch (i) {
                                        case 2:
                                            //TODO
                                            //add to favourite

                                            break;
                                        case 0:
                                            //settings
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", MainActivity.appMap.get(option), null);
                                            intent.setData(uri);
                                            view.getContext().startActivity(intent);
                                            break;
                                        case 1:
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
    private int REQUEST_PICK_APPWIDGET = 2;
    private int REQUEST_CREATE_APPWIDGET = 3;
    private void initWidgetHost() {
        mAppWidgetManager = AppWidgetManager.getInstance(getContext().getApplicationContext());
        mAppWidgetHost = new AppWidgetHost(getContext().getApplicationContext(), APPWIDGET_HOST_ID);
    }
    private void setDefaultWidget(int appWidgetId) {
        initWidgetHost();
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        AppWidgetHostView hostView =
                mAppWidgetHost.createView(getContext().getApplicationContext(), appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        ((FrameLayout)view.findViewById(R.id.widgetFrame)).removeAllViews();
        ((FrameLayout)view.findViewById(R.id.widgetFrame)).addView(hostView);

//        Log.i("TAG", "The widget size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);

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

        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("defaultWidget", appWidgetId);
        editor.commit();


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


    @Override public void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }
    @Override public void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }

    @Override
    public void onClick(View view) {
        //called when a note is clicked

        TextView txt=(TextView)view;
//        switch (view.getId())
//        {
//            case R.id.note1:
//                showPopupWindow(R.id.note1,txt.getText().toString());
//                break;
//            case R.id.note2:
//                showPopupWindow(R.id.note2,txt.getText().toString());
//                break;
//            case R.id.note3:
//                showPopupWindow(R.id.note3,txt.getText().toString());
//                break;
//             case R.id.note4:
//                showPopupWindow(R.id.note4,txt.getText().toString());
//                 break;
//            case R.id.note5:
//                showPopupWindow(R.id.note5,txt.getText().toString());
//                break;
//            case R.id.note6:
//                showPopupWindow(R.id.note6,txt.getText().toString());
//                 break;
//        }
        showPopupWindow(view.getId(),txt.getText().toString());
    }

    public void showPopupWindow(final int noteId,String def) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.notes_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        final EditText txt=popupView.findViewById(R.id.popupTxt);
        txt.setText(def);

        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor editor = sp.edit();

        Button c1=popupView.findViewById(R.id.c1);
        Button c2=popupView.findViewById(R.id.c2);
        Button c3=popupView.findViewById(R.id.c3);
        Button c4=popupView.findViewById(R.id.c4);
        Button c5=popupView.findViewById(R.id.c5);
        Button c6=popupView.findViewById(R.id.c6);

        final TextView textView=(TextView)getActivity().findViewById(noteId);
        c1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                editor.putString("c"+noteId, String.valueOf((noteHighlight1)));
                Log.d("notes",String.valueOf((noteHighlight1)));
                editor.commit();

                textView.setBackgroundResource(noteHighlight1);
            }
        });
        c2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                editor.putString("c"+noteId, String.valueOf(R.color.noteHighlight2));
                editor.commit();

                textView.setBackgroundResource(R.color.noteHighlight2);
            }
        });
        c3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                editor.putString("c"+noteId, String.valueOf(R.color.noteHighlight3));
                editor.commit();

                textView.setBackgroundResource(R.color.noteHighlight3);
            }
        });
        c4.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                editor.putString("c"+noteId, String.valueOf(R.color.noteHighlight4));
                editor.commit();

                textView.setBackgroundResource(R.color.noteHighlight4);
            }
        });
        c5.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                editor.putString("c"+noteId, String.valueOf(R.color.noteHighlight5));
                editor.commit();

                textView.setBackgroundResource(R.color.noteHighlight5);
            }
        });
        c6.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                editor.putString("c"+noteId, String.valueOf(R.color.noteHighlight6));
                editor.commit();
                textView.setBackgroundResource(R.color.noteHighlight6);
            }
        });

        View saveBtn= popupView.findViewById(R.id.popupSaveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t=txt.getText().toString();

                editor.putString(noteId+"",t);
                editor.commit();
                textView.setText(t);
                popupWindow.dismiss();

                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(popupView.getWindowToken(), 0);
            }
        });

        View deleteBtn= popupView.findViewById(R.id.popupDeleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(noteId+"","");
                editor.commit();
                textView.setText("");
                editor.putString("c"+noteId, String.valueOf(R.color.colorPrimaryDark));
                editor.commit();
                popupWindow.dismiss();

                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(popupView.getWindowToken(), 0);
            }
        });

        View smartSaveBtn= popupView.findViewById(R.id.popupSmartSaveBtn);
        smartSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t=txt.getText().toString();
                popupWindow.dismiss();

                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(popupView.getWindowToken(), 0);
                textView.setText("");
                if (t.length() >0 && TextParser.isParsable(t))
                    new CalendarManager().parseAndAdd(t,getContext());
            }
        });
        smartSaveBtn.setVisibility(View.GONE);
        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }


            @Override
            public void afterTextChanged(Editable editable) {
                String t=txt.getText().toString();
                if (t.length() >0 && TextParser.isParsable(t))
                    smartSaveBtn.setVisibility(View.VISIBLE);
                else
                    smartSaveBtn.setVisibility(View.GONE);

            }
        });



        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //show keyboard
        txt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_NOT_ALWAYS);

    }
}