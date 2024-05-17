package com.example.man;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MobileLoginActivity extends AppCompatActivity {
    private Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_login);

        // 设置返回按钮的ICON
        returnButton = findViewById(R.id.mobile_login_return_button);
        Drawable icon = getResources().getDrawable(R.drawable.return_icon);
        icon.setBounds(0, 0, 60, 60);
        returnButton.setCompoundDrawables(icon, null, null, null);
    }
}
