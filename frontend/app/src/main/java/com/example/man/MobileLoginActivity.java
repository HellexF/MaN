package com.example.man;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.man.api.ApiClient;
import com.example.man.api.ApiService;
import com.example.man.api.models.CheckPhoneNumberAvailableResponse;
import com.example.man.api.models.PhoneNumber;
import com.example.man.utils.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileLoginActivity extends AppCompatActivity {
    ConstraintLayout layout;
    private Button returnButton;
    private EditText mobileEdit;

    // 获取ApiService实例
    ApiService apiService = ApiClient.getClient().create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_login);

        // 设置返回按钮的ICON
        returnButton = findViewById(R.id.mobile_login_return_button);
        // Drawable icon = getResources().getDrawable(R.drawable.return_icon);
        // icon.setBounds(0, 0, 70, 70);
        // returnButton.setCompoundDrawables(icon, null, null, null);
        // 设置返回按钮的跳转
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MobileLoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mobileEdit = findViewById(R.id.mobile_edit);
        // 监听输入框文字变化
        mobileEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入框中有文字时显示清除图标，没有文字时隐藏清除图标
                if (s.length() > 0) {
                    Drawable icon = getResources().getDrawable(R.drawable.clear_icon);
                    icon.setBounds(0, 0, 60, 60);
                    mobileEdit.setCompoundDrawables(null, null, icon, null);
                    mobileEdit.setCompoundDrawablePadding(14);
                    // 监听清除图标的点击事件
                    mobileEdit.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                int drawableRightStart = mobileEdit.getRight() - mobileEdit.getPaddingRight() - icon.getBounds().width();
                                if (event.getRawX() >= drawableRightStart) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(mobileEdit, InputMethodManager.SHOW_IMPLICIT);
                                    mobileEdit.setText("");
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                } else {
                    mobileEdit.setCompoundDrawables(null, null, null, null);
                    mobileEdit.setOnTouchListener(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // 设置回车键监听器
        mobileEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

                    // 匹配手机号
                    Boolean match = Regex.isValidPhoneNumber(mobileEdit.getText().toString());

                    // 隐藏键盘
                    hideKeyboardAndClearFocus();
                    // 使 EditText 失去焦点
                    mobileEdit.clearFocus();

                    if(!match) {
                        Toast.makeText(MobileLoginActivity.this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
                        return false;
                    }

                    Call<CheckPhoneNumberAvailableResponse> call = apiService.checkPhoneNumberAvailable(new PhoneNumber("1234567890"));
                    call.enqueue(new Callback<CheckPhoneNumberAvailableResponse>() {
                        @Override
                        public void onResponse(Call<CheckPhoneNumberAvailableResponse> call, Response<CheckPhoneNumberAvailableResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                CheckPhoneNumberAvailableResponse checkPhoneNumberResponse = response.body();
                                boolean isAvailable = checkPhoneNumberResponse.isAvailable();

                                // 进入注册页面
                                if (!isAvailable) {
                                    Intent intent = new Intent(MobileLoginActivity.this, RegisterActivity.class);
                                    intent.putExtra("phone_number", mobileEdit.getText().toString());
                                    intent.putExtra("type", 0);

                                    startActivity(intent);
                                }
                                // 进入登录页面
                                else{
                                    Intent intent = new Intent(MobileLoginActivity.this, LoginActivity.class);
                                    intent.putExtra("phone_number", mobileEdit.getText().toString());
                                    intent.putExtra("type", 0);

                                    startActivity(intent);
                                }

                            } else {
                                Toast.makeText(MobileLoginActivity.this, "请求错误", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<CheckPhoneNumberAvailableResponse> call, Throwable t) {
                            Toast.makeText(MobileLoginActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                        }
                    });

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
