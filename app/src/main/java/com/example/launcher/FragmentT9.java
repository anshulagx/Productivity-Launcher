package com.example.launcher;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.launcher.t9search.T9Trie;
import com.example.launcher.utils.AppInfo;
import com.example.launcher.utils.FragmentC_RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FragmentT9 extends Fragment implements View.OnClickListener {

    static String token;
    List<AppInfo> appList;
    Map<String,String> contactsInfoMap;
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
        ((Button)view.findViewById(R.id.b0)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.be3)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.be3)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                token="";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    updateListWith(token);
                }
                return false;
            }
        });

        appList=MainActivity.appData;
        contactsInfoMap=MainActivity.contactInfoMap;

        trie=initTrie(appList,contactsInfoMap);

        newList=new ArrayList<AppInfo>();

        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.search_recycler_view);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter=new FragmentC_RecyclerViewAdapter(newList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
            case R.id.b0:
                token+="0";
                break;
            case R.id.be3:
                token=(token.length()!=0)?(token.substring(0,token.length()-1)):"";
                break;

        }

        TextView t=((TextView)getView().findViewById(R.id.tokenDisplay));
        t.setText(token);

        if (token.length()==10)
        {

            makePhoneCallAt(token);
        }
        else
            updateListWith(token);



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void makePhoneCallAt(final String token) {
        ImageView b= getView().findViewById(R.id.be2);
        String defaultDialer=((TelecomManager)getActivity().getSystemService(Context.TELECOM_SERVICE)).getDefaultDialerPackage();
        try {
            b.setImageDrawable(getActivity().getPackageManager().getApplicationIcon(defaultDialer));
        }
        catch (Exception e)
        {}
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse("tel:" + token));
                getActivity().startActivity(call);
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateListWith(String token) {

        String searchPrefix=token;
        List<String> suggestions = trie.getT9ValueSuggestions(searchPrefix);

        //Generate a  list of AppData with string list
        newList.clear();
        for (String s:suggestions) {

            if(MainActivity.appMap.containsKey(s))
            {
                //implies that the string is an app
                String pkg=MainActivity.appMap.get(s);
                Drawable icon=null;
                try {
                    icon = getActivity().getPackageManager().getApplicationIcon(pkg);
                }catch (Exception e)
                {

                }
                newList.add(new AppInfo(s,pkg,icon));
            }
            else
            {
                //the string is a Contact
                String number=MainActivity.contactInfoMap.get(s);
                AppInfo info=new AppInfo();
                info.isContact();
                info.label=s;
                info.contactNo=number;

                newList.add(info);

            }


        }

        //update recycler view
        adapter.notifyDataSetChanged();

        //change the icon of be2 button
        ImageView b= getView().findViewById(R.id.be2);

        if(newList.get(0).isContact)
        {
            String defaultDialer=((TelecomManager)getActivity().getSystemService(Context.TELECOM_SERVICE)).getDefaultDialerPackage();
            try {
                b.setImageDrawable(getActivity().getPackageManager().getApplicationIcon(defaultDialer));

            }
            catch (Exception e){}
            //add onclick listener
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent call = new Intent(Intent.ACTION_CALL);
                    call.setData(Uri.parse("tel:" + newList.get(0).contactNo));
                    getActivity().startActivity(call);
                }
            });
        }
        else
        {
            b.setImageDrawable(newList.get(0).icon);
            //add onclick listener
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(newList.get(0).packageName);
                    startActivity(launchIntent);
                }
            });
        }


    }

    private T9Trie<String> initTrie(List<AppInfo> appList, Map<String, String> contactInfoMap){

        //String searchPrefix="42";

        final T9Trie<String> trie = new T9Trie<>();
        for (AppInfo app:appList)
        {
            LinkedList<String> l=new LinkedList<String>();
            l.add(app.label);
            trie.insert(app.label, l);
        }


        for(Map.Entry<String,String> ci:contactInfoMap.entrySet())
        {
            LinkedList<String> l=new LinkedList<String>();
            l.add(ci.getKey());
            trie.insert(ci.getKey(), l);
        }
        trie.print();
        return trie;
    }

    @Override
    public void onPause() {
        super.onPause();
        token="";
        TextView t=((TextView)getView().findViewById(R.id.tokenDisplay));
        t.setText("");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            updateListWith(token);
        }
    }
}
