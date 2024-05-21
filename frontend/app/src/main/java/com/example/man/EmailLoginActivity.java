package com.example.man;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
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
import com.example.man.api.models.CheckEmailAvailableResponse;
import com.example.man.api.models.CheckPhoneNumberAvailableResponse;
import com.example.man.api.models.Email;
import com.example.man.api.models.PhoneNumber;
import com.example.man.utils.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailLoginActivity extends AppCompatActivity {
    private Button returnButton;
    private Button nextButton;
    private EditText emailEdit;
    private TextView emailErrorText;

    // 获取ApiService实例
    ApiService apiService = ApiClient.getClient().create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        emailErrorText = findViewById(R.id.email_error_text);

        returnButton = findViewById(R.id.email_login_return_button);
        // 设置返回按钮的跳转
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmailLoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        nextButton = findViewById(R.id.email_next_button);
        // 设置下一步按钮的跳转
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean match = Patterns.EMAIL_ADDRESS.matcher(emailEdit.getText().toString()).matches();
                if(!match) {
                    emailErrorText.setVisibility(TextView.VISIBLE);
                } else {
                    emailErrorText.setVisibility(TextView.GONE);
                    Call<CheckEmailAvailableResponse> call = apiService.checkEmailAvailable(new Email(emailEdit.getText().toString()));
                    call.enqueue(new Callback<CheckEmailAvailableResponse>() {
                        @Override
                        public void onResponse(Call<CheckEmailAvailableResponse> call, Response<CheckEmailAvailableResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                CheckEmailAvailableResponse checkEmailResponse = response.body();
                                boolean isAvailable = checkEmailResponse.isAvailable();

                                // 进入注册页面
                                if (!isAvailable) {
                                    Intent intent = new Intent(EmailLoginActivity.this, RegisterActivity.class);
                                    intent.putExtra("email", emailEdit.getText().toString());
                                    intent.putExtra("type", 1);

                                    startActivity(intent);
                                }
                                // 进入登录页面
                                else{
                                    Intent intent = new Intent(EmailLoginActivity.this, LoginActivity.class);
                                    intent.putExtra("email", emailEdit.getText().toString());
                                    intent.putExtra("type", 1);

                                    startActivity(intent);
                                }

                            } else {
                                Toast.makeText(EmailLoginActivity.this, "请求错误", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<CheckEmailAvailableResponse> call, Throwable t) {
                            Toast.makeText(EmailLoginActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        emailEdit = findViewById(R.id.email_edit);
        // 监听输入框文字变化
        emailEdit.addTextChangedListener(new TextWatcher() {
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
                    emailEdit.setCompoundDrawables(null, null, icon, null);
                    emailEdit.setCompoundDrawablePadding(14);
                    // 监听清除图标的点击事件
                    emailEdit.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                int drawableRightStart = emailEdit.getRight() - emailEdit.getPaddingRight() - icon.getBounds().width();
                                if (event.getRawX() >= drawableRightStart) {
                                    emailEdit.setText("");
                                    emailEdit.requestFocus();
                                    // 显示键盘
                                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(emailEdit, InputMethodManager.SHOW_IMPLICIT);
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                } else {
                    emailEdit.setCompoundDrawables(null, null, null, null);
                    emailEdit.setOnTouchListener(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 设置回车键监听器
        emailEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    // 隐藏键盘
                    hideKeyboardAndClearFocus();
                    // 使 EditText 失去焦点
                    emailEdit.clearFocus();
                    // 匹配邮箱
                    boolean match = Patterns.EMAIL_ADDRESS.matcher(emailEdit.getText().toString()).matches();
                    if(!match) {
                        emailErrorText.setVisibility(TextView.VISIBLE);
                        return false;
                    } else {
                        emailErrorText.setVisibility(TextView.GONE);
                    }
                    return true;
                }
                return false;
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
                emailEdit.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}