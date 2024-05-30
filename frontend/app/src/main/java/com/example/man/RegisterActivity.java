package com.example.man;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.man.api.ApiClient;
import com.example.man.api.ApiService;
import com.example.man.api.models.RegisterInfoRequest;
import com.example.man.api.models.RegistrationResponse;
import com.example.man.utils.Regex;
import com.example.man.utils.SharedPreferencesManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private Button returnButton;
    private Button finishButton;
    private EditText extraEdit;
    private EditText usernameEdit;
    private EditText passwordEdit;
    private EditText passwordCheckEdit;
    private TextView extraErrorText;
    private TextView usernameErrorText;
    private TextView passwordErrorText;
    private TextView passwordCheckErrorText;
    private int registerType;
    private String registerValue;
    // 判断表单信息是否合法
    private Boolean infoValid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        infoValid = false;

        returnButton = findViewById(R.id.register_return_button);
        finishButton = findViewById(R.id.finish_button);

        // 获取ApiService实例
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // 判断是通过手机号注册/邮箱注册
        Intent intent = getIntent();
        registerType = intent.getIntExtra("type", -1);
        extraEdit = findViewById(R.id.extra_edit);
        extraErrorText = findViewById(R.id.extra_error_text);
        if (registerType == 0) {
            registerValue = intent.getStringExtra("phone_number");
            extraEdit.setHint(getString(R.string.email_edit_text));
            extraErrorText.setText(getString(R.string.invalid_email_text));
        }
        else {
            registerValue = intent.getStringExtra("email");
            extraEdit.setHint(getString(R.string.phone_number_edit_text));
            extraErrorText.setText(getString(R.string.invalid_phone_number_text));
        }

        // 验证表单内容是否合法
        usernameEdit = findViewById(R.id.username_edit);
        usernameErrorText = findViewById(R.id.username_error_text);
        usernameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    usernameErrorText.setVisibility(usernameEdit.getText().toString().isEmpty() ? TextView.VISIBLE : TextView.GONE);
                    setInfoValid();
                }
            }
        });


        passwordEdit = findViewById(R.id.password_edit);
        passwordErrorText = findViewById(R.id.password_error_text);
        passwordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    passwordErrorText.setVisibility(passwordEdit.getText().length() >= 8 ? TextView.GONE : TextView.VISIBLE);
                    setInfoValid();
                }
            }
        });

        passwordCheckEdit = findViewById(R.id.password_check_edit);
        passwordCheckErrorText = findViewById(R.id.password_check_error_text);
        passwordCheckEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    passwordCheckErrorText.setVisibility(passwordEdit.getText().toString().equals(passwordCheckEdit.getText().toString())
                            ? TextView.GONE : TextView.VISIBLE);
                    setInfoValid();
                }
            }
        });

        extraEdit = findViewById(R.id.extra_edit);
        extraEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    boolean isExtraValid = extraEdit.getText().toString().isEmpty() || (registerType == 0 ? Regex.isValidEmail(extraEdit.getText().toString()) :
                            Regex.isValidPhoneNumber(extraEdit.getText().toString()));
                    extraErrorText.setVisibility(isExtraValid ? TextView.GONE : TextView.VISIBLE);
                    setInfoValid();
                }
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(registerType == 0) {
                    intent = new Intent(RegisterActivity.this, MobileLoginActivity.class);
                }
                else {
                    intent = new Intent(RegisterActivity.this, EmailLoginActivity.class);
                }
                startActivity(intent);
            }
        });

        // 完成注册对应逻辑
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInfoValid();
                // 表单信息不合法
                if (!infoValid){
                    Toast.makeText(RegisterActivity.this, "请检查注册信息", Toast.LENGTH_LONG).show();
                    return;
                }

                // 构建请求体
                RegisterInfoRequest registerInfoRequest;
                if (registerType == 0){
                    registerInfoRequest = new RegisterInfoRequest(usernameEdit.getText().toString(), passwordEdit.getText().toString(),
                            registerValue, extraEdit.getText().toString());
                }
                else {
                    registerInfoRequest = new RegisterInfoRequest(usernameEdit.getText().toString(), passwordEdit.getText().toString(),
                            extraEdit.getText().toString(), registerValue);
                }

                Call<RegistrationResponse> call = apiService.register(registerInfoRequest);
                call.enqueue(new Callback<RegistrationResponse>() {
                    @Override
                    public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

                            // 将用户信息写入缓存
                            SharedPreferencesManager.saveLoginStatus(RegisterActivity.this, true);
                            SharedPreferencesManager.saveUserId(RegisterActivity.this, response.body().getId());
                            SharedPreferencesManager.saveUserName(RegisterActivity.this, usernameEdit.getText().toString());
                            SharedPreferencesManager.saveUserAvatar(RegisterActivity.this, "/media/images/avatars/default.png");

                            if (registerType == 0){
                                SharedPreferencesManager.saveUserPhone(RegisterActivity.this, registerValue);
                                SharedPreferencesManager.saveUserEmail(RegisterActivity.this, extraEdit.getText().toString());
                            }
                            else {
                                SharedPreferencesManager.saveUserPhone(RegisterActivity.this, extraEdit.getText().toString());
                                SharedPreferencesManager.saveUserEmail(RegisterActivity.this, registerValue);
                            }

                            Intent intent = new Intent(RegisterActivity.this, NoteActivity.class);
                            startActivity(intent);
                        } else {
                            // 判断注册失败的情况
                            try {
                                String errorBody = response.errorBody().string();
                                Gson gson = new Gson();
                                JsonElement jsonElement = gson.fromJson(errorBody, JsonElement.class);
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                // 用户名已存在
                                if (jsonObject.has("username")) {
                                    Toast.makeText(RegisterActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                                }
                                // 手机号已存在
                                else if (jsonObject.has("phone_number")) {
                                    Toast.makeText(RegisterActivity.this, "该手机号已被注册", Toast.LENGTH_SHORT).show();
                                }
                                // 邮箱
                                else if (jsonObject.has("email")) {
                                    Toast.makeText(RegisterActivity.this, "该邮箱已被注册", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private void setInfoValid() {
        infoValid = (!usernameEdit.getText().toString().isEmpty()) &&
                (passwordEdit.getText().toString().length() >= 8) &&
                (passwordEdit.getText().toString().equals(passwordCheckEdit.getText().toString())) &&
                (extraEdit.getText().toString().isEmpty() ||
                        (registerType == 0 ? Regex.isValidEmail(extraEdit.getText().toString()) :
                        Regex.isValidPhoneNumber(extraEdit.getText().toString())));
    }
}
