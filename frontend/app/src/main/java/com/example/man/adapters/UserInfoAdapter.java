package com.example.man.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.man.InfoActivity;
import com.example.man.LoginActivity;
import com.example.man.MainActivity;
import com.example.man.R;
import com.example.man.asyncTasks.LogoutAsyncTask;
import com.example.man.utils.SharedPreferencesManager;

import java.util.List;

public class UserInfoAdapter extends ArrayAdapter<String> {

    private int resourceLayout;
    private Context mContext;
    private static final int VIEW_TYPE_INFO = 0;
    private static final int VIEW_TYPE_BTN = 1;
    private static final int VIEW_TYPE_SPACE = 2;

    public UserInfoAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        // 返回当前位置的视图类型
        if (position == 6) {
            return VIEW_TYPE_BTN;
        } else if (position == 5){
            return VIEW_TYPE_SPACE;
        }
        else {
            return VIEW_TYPE_INFO;
        }
    }

    @Override
    public int getViewTypeCount() {
        // 返回视图类型总数
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取对应的视图雷影
        int viewType = getItemViewType(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == VIEW_TYPE_BTN) {
                convertView = inflater.inflate(R.layout.user_info_btn, parent, false);

                // 设置登出事件监听器
                TextView logoutButton = convertView.findViewById(R.id.logout_btn);
                logoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new LogoutAsyncTask(mContext).execute();
                    }
                });
            } else if (viewType == VIEW_TYPE_SPACE){
                return new View(parent.getContext());
            }
            else {
                convertView = inflater.inflate(R.layout.user_info_list_item, parent, false);

                String info = getItem(position);

                if (info != null) {
                    TextView infoTextView = convertView.findViewById(R.id.info_text_view);

                    if (infoTextView != null) {
                        infoTextView.setText(info);
                    }
                }
            }
        }

        return convertView;
    }
}