package com.example.launcher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.launcher.t9search.T9Trie;
import com.example.launcher.utils.AppInfo;
import com.example.launcher.utils.FragmentB_RecyclerViewAdapter;
import com.example.launcher.utils.FragmentC_RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FragmentC extends Fragment implements View.OnClickListener {

    static String token;
    List<AppInfo> appList;
    T9Trie<String> trie;
    RecyclerView.Adapter adapter;
    List<AppInfo> newList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c_layout, container, false);

        token="";

        ((Button)view.findViewById(R.id.b1)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.b2)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.b3)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.b4)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.b5)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.b6)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.b7)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.b8)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.b9)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.be1)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.be2)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.be3)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.be3)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                token="";
                updateListWith(token);
                return false;
            }
        });

        appList=MainActivity.appData;
        trie=initTrie(appList);

        newList=new ArrayList<AppInfo>();

        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.search_recycler_view);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter=new FragmentC_RecyclerViewAdapter(newList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id=((Button)view).getId();
        switch (id)
        {
            case R.id.b1:
                token+="1";
                break;
            case R.id.b2:
                token+="2";
                break;
            case R.id.b3:
                token+="3";
                break;
            case R.id.b4:
                token+="4";
                break;
            case R.id.b5:
                token+="5";
                break;
            case R.id.b6:
                token+="6";
                break;
            case R.id.b7:
                token+="7";
                break;
            case R.id.b8:
                token+="8";
                break;
            case R.id.b9:
                token+="9";
                break;
            case R.id.be3:
                token=(token.length()!=0)?(token.substring(0,token.length()-1)):"";
                break;

        }
        updateListWith(token);


    }

    private void updateListWith(String token) {

        String searchPrefix=token;
        List<String> suggestions = trie.getT9ValueSuggestions(searchPrefix);

        //Generate a  list of AppData with string list
        newList.clear();
        for (String s:suggestions) {

            String pkg=MainActivity.appMap.get(s);
            newList.add(new AppInfo(s,pkg));
        }

        //update recycler view
        adapter.notifyDataSetChanged();


    }

    private T9Trie<String> initTrie(List<AppInfo> appList){

        //String searchPrefix="42";

        final T9Trie<String> trie = new T9Trie<>();
        for (AppInfo app:appList)
        {
            LinkedList<String> l=new LinkedList<String>();
            l.add(app.label);
            trie.insert(app.label, l);
        }
        trie.print();
//        List<String> suggestions = trie.getT9ValueSuggestions(searchPrefix);
//        Log.d("TAG",suggestions.toString() );
        return trie;
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

    @Override
    public void onPause() {
        super.onPause();
        token="";
        updateListWith(token);
    }
}
