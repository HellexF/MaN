package com.example.man;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MobileLoginActivity extends AppCompatActivity {
    ConstraintLayout layout;
    private Button returnButton;
    private EditText mobileEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_login);

        // 设置返回按钮的ICON
        returnButton = findViewById(R.id.mobile_login_return_button);
        Drawable icon = getResources().getDrawable(R.drawable.return_icon);
        icon.setBounds(0, 0, 70, 70);
        returnButton.setCompoundDrawables(icon, null, null, null);
        // 设置返回按钮的跳转
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MobileLoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mobileEdit = findViewById(R.id.mobile_edit);
        // 用户聚焦时不显示提醒文字
        mobileEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mobileEdit.setHint("");
                } else {
                    if (TextUtils.isEmpty(mobileEdit.getText())) {
                        mobileEdit.setHint("请输入手机号");
                    }
                }
            }
        });
        // 设置回车键监听器
        mobileEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // 隐藏键盘
                    hideKeyboardAndClearFocus();
                    // 使 EditText 失去焦点
                    mobileEdit.clearFocus();
                    return true;
                }
                return false;
            }
        });
        // 设置焦点变化监听器
        mobileEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // 隐藏键盘
                    hideKeyboardAndClearFocus();
                    // 使 EditText 失去焦点
                    mobileEdit.clearFocus();
                }
            }
        });
    }

    private void hideKeyboardAndClearFocus() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 如果点击的是 EditText 以外的区域，隐藏键盘并使 EditText 失去焦点
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                // 隐藏键盘
                hideKeyboardAndClearFocus();
                // 使 EditText 失去焦点
                mobileEdit.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
