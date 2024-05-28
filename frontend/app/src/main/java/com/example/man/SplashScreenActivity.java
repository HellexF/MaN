package com.example.man;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.man.utils.SharedPreferencesManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检查用户是否已登录
        if (SharedPreferencesManager.getLoginStatus(this)) {
            // 如果用户已登录，跳转到笔记页面
            Intent intent = new Intent(SplashScreenActivity.this, InfoActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
        }

        // 关闭当前页面
        finish();
    }

}
