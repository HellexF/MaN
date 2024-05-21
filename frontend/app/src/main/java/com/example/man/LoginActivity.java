package com.example.man;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.example.man.api.ApiClient;
import com.example.man.api.ApiService;
import com.example.man.api.models.CheckPhoneNumberAvailableResponse;
import com.example.man.api.models.LoginInfo;
import com.example.man.api.models.LoginResponse;
import com.example.man.api.models.PhoneNumber;
import com.example.man.utils.Regex;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private int loginType;
    private String loginValue;
    private EditText passwordEdit;
    private TextView passwordErrorText;
    private Button loginButton;
    private Button returnButton;
    ApiService apiService = ApiClient.getClient().create(ApiService.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 判断是通过手机号登录/邮箱登录
        Intent intent = getIntent();
        loginType = intent.getIntExtra("type", -1);
        if (loginType == 0) {
            loginValue = intent.getStringExtra("phone_number");
        }
        else {
            loginValue = intent.getStringExtra("email");
        }

        passwordEdit = findViewById(R.id.login_password_edit);
        passwordErrorText = findViewById(R.id.login_password_error_text);
        passwordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    passwordErrorText.setVisibility(passwordEdit.getText().length() >= 8 ? TextView.GONE : TextView.VISIBLE);
                }
            }
        });
        // 监听输入框文字变化
        passwordEdit.addTextChangedListener(new TextWatcher() {
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
                    passwordEdit.setCompoundDrawables(null, null, icon, null);
                    passwordEdit.setCompoundDrawablePadding(14);
                    // 监听清除图标的点击事件
                    passwordEdit.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                int drawableRightStart = passwordEdit.getRight() - passwordEdit.getPaddingRight() - icon.getBounds().width();
                                if (event.getRawX() >= drawableRightStart) {
                                    passwordEdit.setText("");
                                    passwordEdit.requestFocus();
                                    // 显示键盘
                                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(passwordEdit, InputMethodManager.SHOW_IMPLICIT);
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                } else {
                    passwordEdit.setCompoundDrawables(null, null, null, null);
                    passwordEdit.setOnTouchListener(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // 设置回车键监听器
        passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    // 隐藏键盘
                    hideKeyboardAndClearFocus();
                    // 使 EditText 失去焦点
                    passwordEdit.clearFocus();
                    return true;
                }
                return false;
            }
        });

        loginButton = findViewById(R.id.login_button);
        // 设置登录按钮的跳转
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean match = passwordEdit.getText().length() >= 8;
                if(!match) {
                    passwordErrorText.setVisibility(TextView.VISIBLE);
                } else {
                    passwordErrorText.setVisibility(TextView.GONE);
                    Call<LoginResponse> call;
                    if (loginType == 0) {
                        call = apiService.login(new LoginInfo(loginType, passwordEdit.getText().toString(), loginValue, ""));
                    } else {
                        call = apiService.login(new LoginInfo(loginType, passwordEdit.getText().toString(), "", loginValue));
                    }
                    call.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            if (response.isSuccessful()) {
                                // 进入笔记页面
                                Intent intent = new Intent(LoginActivity.this, NoteActivity.class);
                                startActivity(intent);
                            } else {
                                // 登录失败，获取错误信息
                                if (response.errorBody() != null) {
                                    try {
                                        String errorBody = response.errorBody().string();
                                        JSONObject jsonObject = new JSONObject(errorBody);
                                        String message = jsonObject.optString("message");
                                        if (message.equals("Invalid credentials")) {
                                            Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "请求错误", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (IOException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "请求错误", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        returnButton = findViewById(R.id.login_return_button);
        // 设置返回按钮的跳转
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginType == 0) {
                    Intent intent = new Intent(LoginActivity.this, MobileLoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(LoginActivity.this, EmailLoginActivity.class);
                    startActivity(intent);
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
                passwordEdit.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
