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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.launcher.R;

import java.util.List;

public class FragmentB_RecyclerViewAdapter extends RecyclerView.Adapter<FragmentB_RecyclerViewAdapter.MyViewHolder>{

    List<AppInfo> appData;
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public LinearLayout linearLayout;
        public MyViewHolder(LinearLayout v) {
            super(v);
            linearLayout=v;
            textView=v.findViewById(R.id.itemText);
        }
    }

    public FragmentB_RecyclerViewAdapter(List<AppInfo> data)
    {
        appData=data;
    }

    @NonNull
    @Override
    public FragmentB_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v=(LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_b_item_layout,parent,false);
        MyViewHolder vh=new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final FragmentB_RecyclerViewAdapter.MyViewHolder holder, final int position) {
        holder.textView.setText(appData.get(position).label);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = view.getContext().getPackageManager().getLaunchIntentForPackage(appData.get(position).packageName);
                view.getContext().startActivity(launchIntent);
            }
        });
        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(view.getContext());
                builder.setTitle("Settings");
                String options[]={"Add to Favourite","Settings","Uninstall","More"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextView txt=(TextView)view.findViewById(R.id.itemText);
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
