package com.example.man.asyncTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.man.LoginActivity;
import com.example.man.MainActivity;
import com.example.man.utils.SharedPreferencesManager;

public class LogoutAsyncTask extends AsyncTask<Void, Void, Void> {

    private Context mContext;

    public LogoutAsyncTask(Context context) {
        mContext = context;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Intent intent = new Intent(mContext, MainActivity.class);
        SharedPreferencesManager.saveLoginStatus(mContext, false);
        mContext.startActivity(intent);
    }
}
