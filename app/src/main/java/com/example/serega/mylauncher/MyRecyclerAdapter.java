package com.example.serega.mylauncher;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private static ArrayList<AppInfo> apps;
    private Context context;

    public static ArrayList<AppInfo> getApps() {
        return apps;
    }

    public MyRecyclerAdapter(ArrayList<AppInfo> apps, Context context) {
        this.apps = apps;
        this.context = context;
    }

    @Override
    public MyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(context instanceof MainActivity){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item, parent, false);
        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item_with_checkbox, parent, false);
        }

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyRecyclerAdapter.ViewHolder holder, int position) {
            holder.appView.setText(apps.get(position).getLabel());
            holder.appIcon.setImageDrawable(apps.get(position).getIcon());
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView appView;
        public ImageView appIcon;
        public CheckBox checkBox;
        private PopupWindow popupWindow;

        public ViewHolder(View v) {
            super(v);
            appView = (TextView) v.findViewById(R.id.app_view);
            appIcon = (ImageView) v.findViewById(R.id.app_icon);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String name = getApps().get(position).getName().toString();
                    PackageManager manager = v.getContext().getPackageManager();
                    try {
                        Intent intent = manager.getLaunchIntentForPackage(name);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        v.getContext().startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    View inflatedView = view.inflate(view.getContext(), R.layout.popup_window, null);
                    popupWindow = new PopupWindow(
                            inflatedView,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.showAsDropDown(view, 0, 0);
                    TextView uninstall = (TextView) inflatedView.findViewById(R.id.uninstall);
                    TextView aboutApp = (TextView) inflatedView.findViewById(R.id.about_app);
                    aboutApp.setOnClickListener(onClickListener);
                    uninstall.setOnClickListener(onClickListener);

                    return true;
                }
            });
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                String name = getApps().get(position).getName().toString();
                switch (view.getId()) {
                    case R.id.uninstall:
                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        intent.setData(Uri.parse("package:" + name));
                        try {
                            view.getContext().startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.about_app:
                        Intent i = new Intent();
                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", name, null);
                        i.setData(uri);
                        try {
                            view.getContext().startActivity(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                popupWindow.dismiss();
            }
        };
    }
}
