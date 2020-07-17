package com.example.launcher;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.launcher.utils.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<AppInfo> appList=generateInstalledAppData();

        final String favApps[]={"Youtube","Chrome","Mentor","Gmail","Google"};
        handelFavButtons(appList,favApps);




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
        }

        return appsList;

    }

    private void handelFavButtons(List<AppInfo> appList, String[] favApps) {

        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.favAppLayout);

        for (final AppInfo app:appList)
        {
            for (int i=0;i<favApps.length;i++) {

                if (app.label.equalsIgnoreCase(favApps[i]))
                {
                    TextView text = new TextView(this);
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
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(app.packageName);
                            startActivity(launchIntent);
                        }
                    });
                }
            }
        }


    }
}