package com.example.launcher;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.launcher.utils.AppInfo;
import com.example.launcher.utils.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FragmentB extends Fragment {

    View view;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_b_layout, container, false);

        List<AppInfo> appList=generateInstalledAppData();
        appList.sort(new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo appInfo, AppInfo t1) {
                return appInfo.label.compareToIgnoreCase(t1.label);
            }
        });
        handelRecyclerViewStuff(view,appList);

        return view;
    }

    private void handelRecyclerViewStuff(View view, List<AppInfo> data) {
        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter=new RecyclerViewAdapter(data);
        recyclerView.setAdapter(adapter);

    }

    private List<AppInfo> generateInstalledAppData() {
        PackageManager pm = getActivity().getPackageManager();
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

}