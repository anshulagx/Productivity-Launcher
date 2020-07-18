package com.example.launcher.utils;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.launcher.FragmentB;
import com.example.launcher.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

    List<AppInfo> appData;
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public MyViewHolder(TextView v) {
            super(v);
            textView=v;
        }
    }

    public RecyclerViewAdapter(List<AppInfo> data)
    {
        appData=data;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView v=(TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_b_item_layout,parent,false);
        MyViewHolder vh=new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, final int position) {
        holder.textView.setText(appData.get(position).label);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = view.getContext().getPackageManager().getLaunchIntentForPackage(appData.get(position).packageName);
                view.getContext().startActivity(launchIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appData.size();
    }
}
