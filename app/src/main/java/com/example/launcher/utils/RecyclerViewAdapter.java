package com.example.launcher.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
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
        holder.textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(view.getContext());
                builder.setTitle("Settings");
                String options[]={"Add to Favourite","Settings","Uninstall","More"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextView txt=(TextView)view;
                        String option=txt.getText().toString();

                        switch (i)
                        {
                            case 0:
                                //add to favourite

                            case 1:
                                //settings
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(option), null);
                                intent.setData(uri);
                                view.getContext().startActivity(intent);
                            case 3:
                                //uninstall
                            case 4:
                                //more

                        }
                    }

                    private String getPackageName(String name) {
                        PackageManager pm = view.getContext().getPackageManager();
                        List<ApplicationInfo> l = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                        String packName = "";
                        for (ApplicationInfo ai : l) {
                            String n = (String)pm.getApplicationLabel(ai);
                            if (n.contains(name) || name.contains(n)){
                                packName = ai.packageName;
                            }
                        }

                        return packName;

                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return appData.size();
    }
}
