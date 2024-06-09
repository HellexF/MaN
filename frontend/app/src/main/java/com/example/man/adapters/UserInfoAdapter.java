package com.example.man.adapters;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import static com.example.man.utils.Mask.maskEmail;
import static com.example.man.utils.Mask.maskPhoneNumber;
import static com.example.man.utils.Regex.isValidEmail;
import static com.example.man.utils.Regex.isValidPhoneNumber;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.man.InfoActivity;
import com.example.man.LoginActivity;
import com.example.man.MainActivity;
import com.example.man.R;
import com.example.man.RegisterActivity;
import com.example.man.api.ApiClient;
import com.example.man.api.ApiService;
import com.example.man.api.models.UpdateEmailRequest;
import com.example.man.api.models.UpdatePasswordRequest;
import com.example.man.api.models.UpdatePhoneNumberRequest;
import com.example.man.api.models.UpdateSignatureRequest;
import com.example.man.api.models.UpdateUserInfoResponse;
import com.example.man.api.models.UpdateUsernameRequest;
import com.example.man.api.models.UploadAvatarResponse;
import com.example.man.asyncTasks.LogoutAsyncTask;
import com.example.man.utils.SharedPreferencesManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoAdapter extends ArrayAdapter<String> {

    private int resourceLayout;
    private Context mContext;
    private ApiService apiService;
    private List<String> mData;

    private static final int VIEW_TYPE_INFO = 0;
    private static final int VIEW_TYPE_BTN = 1;
    private static final int VIEW_TYPE_SPACE = 2;
    private static final int MODIFY_INFO_USERNAME = 3;
    private static final int MODIFY_INFO_PASSWORD = 4;
    private static final int MODIFY_INFO_EMAIL = 5;
    private static final int MODIFY_INFO_PHONE = 6;
    private static final int MODIFY_INFO_SIGNATURE = 7;
    private static final int SET_INFO_EMAIL = 8;
    private static final int SET_INFO_PHONE = 9;

    private int convertPosToType (int position) {
        return position + 3 + (getItem(position).endsWith("未设置") && position != 4 ? 3 : 0);
    }
    private int convertTypeToPos (int type) { return type > 7 ? type - 6 : type - 3;}
    public UserInfoAdapter(Context context, int resource, ArrayList<String> data) {
        super(context, resource, data);
        this.resourceLayout = resource;
        this.mContext = context;
        this.mData = data;
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
        // 获取对应的视图
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
            } else if (viewType == VIEW_TYPE_SPACE) {
                return new View(parent.getContext());
            }
            else {
                convertView = inflater.inflate(R.layout.user_info_list_item, parent, false);

                String info = getItem(position);
                Button modfiyButton = convertView.findViewById(R.id.modify_button);

                if (info != null) {
                    TextView infoTextView = convertView.findViewById(R.id.info_text_view);

                    if (infoTextView != null) {
                        infoTextView.setText(info);
                        Button modifyButton = convertView.findViewById(R.id.modify_button);

                        if (info.endsWith("未设置") && position > 1 && position < 5) {
                            modifyButton.setText("设置");
                        }
                        else {
                            modifyButton.setText("修改");
                        }
                    }
                }

                modfiyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInputDialog(convertPosToType(position));
                    }
                });
            }
        }
        else {
            // 不为空时，不需要重新渲染view，但要更改已有内容
            String info = getItem(position);

            if (info != null) {
                TextView infoTextView = convertView.findViewById(R.id.info_text_view);

                if (infoTextView != null) {
                    infoTextView.setText(info);
                    Button modifyButton = convertView.findViewById(R.id.modify_button);

                    if (info.endsWith("未设置") && position > 1 && position < 5) {
                        modifyButton.setText("设置");
                    }
                    else {
                        modifyButton.setText("修改");
                    }
                }
            }
        }

        return convertView;
    }

    private void showInputDialog(int type) {
        apiService = ApiClient.getClient().create(ApiService.class);
        int pos = convertTypeToPos(type);

        // 创建对话框的布局视图
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView;
        if (type == MODIFY_INFO_USERNAME || type == MODIFY_INFO_SIGNATURE ||
        type == SET_INFO_EMAIL || type == SET_INFO_PHONE){
            dialogView = inflater.inflate(R.layout.dialog_input, null);
        }
        // 对于需验证的信息
        else if (type == MODIFY_INFO_EMAIL || type == MODIFY_INFO_PHONE){
            dialogView = inflater.inflate(R.layout.check_dialog_input, null);
        }
        // 对于密码
        else {
            dialogView = inflater.inflate(R.layout.recheck_dialog_input, null);
        }
        // 创建 AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        // 获取对话框布局中的视图
        TextView dialogTextView = dialogView.findViewById(R.id.dialog_text);
        TextView errorTextView = null;
        TextView recheckErrorTextView = null;
        final EditText editTextInput = dialogView.findViewById(R.id.dialog_input);
        EditText checkTextInput = null;
        EditText recheckTextInput = null;
        if (type != MODIFY_INFO_USERNAME && type != MODIFY_INFO_SIGNATURE){
            checkTextInput = dialogView.findViewById(R.id.check_input);
            errorTextView = dialogView.findViewById(R.id.dialog_error);
        }

        if (type == MODIFY_INFO_PASSWORD){
            recheckErrorTextView = dialogView.findViewById(R.id.recheck_error);
            recheckTextInput = dialogView.findViewById(R.id.recheck_input);
        }
        Button buttonOK = dialogView.findViewById(R.id.dialog_button_ok);
        Button buttonCancel = dialogView.findViewById(R.id.dialog_button_cancel);

        // 设置标题和占位符
        switch (type){
            case MODIFY_INFO_USERNAME:
                dialogTextView.setText("修改用户名");
                editTextInput.setHint("请输入用户名");
                break;
            case MODIFY_INFO_PASSWORD:
                dialogTextView.setText("修改密码");
                checkTextInput.setHint("请输入原密码");
                editTextInput.setHint("请输入新密码");
                recheckTextInput.setHint("请再次输入相同的新密码");
                break;
            case SET_INFO_EMAIL:
                dialogTextView.setText("设置邮箱");
                checkTextInput.setHint("请输入邮箱");
                break;
            case SET_INFO_PHONE:
                dialogTextView.setText("设置手机号");
                checkTextInput.setHint("请输入手机号");
                break;
            case MODIFY_INFO_EMAIL:
                dialogTextView.setText("修改邮箱");
                checkTextInput.setHint("请输入原邮箱");
                editTextInput.setHint("请输入新邮箱");
                break;
            case MODIFY_INFO_PHONE:
                dialogTextView.setText("修改手机号");
                checkTextInput.setHint("请输入原手机号");
                editTextInput.setHint("请输入新手机号");
                break;
            case MODIFY_INFO_SIGNATURE:
                dialogTextView.setText("设置/修改签名");
                editTextInput.setHint("请输入用户签名");
                break;
        }

        // 设置按钮点击事件，为对应的修改请求
        TextView finalErrorTextView = errorTextView;
        EditText finalCheckTextInput = checkTextInput;
        EditText finalRecheckTextInput = recheckTextInput;
        TextView finalRecheckErrorTextView = recheckErrorTextView;
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userId = Integer.parseInt(SharedPreferencesManager.getUserId(mContext));
                String content = editTextInput.getText().toString();

                // 清除输入框的焦点
                editTextInput.clearFocus();

                // 隐藏键盘
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(editTextInput.getWindowToken(), 0);
                switch (type){
                    case MODIFY_INFO_USERNAME:
                        Call<UpdateUserInfoResponse> usernameCall = apiService.updateUsername(new UpdateUsernameRequest(userId, content));
                        usernameCall.enqueue(new Callback<UpdateUserInfoResponse>() {
                            @Override
                            public void onResponse(Call<UpdateUserInfoResponse> call, Response<UpdateUserInfoResponse> response) {
                                int statusCode = response.code();
                                if (statusCode == 200) {
                                    dialog.dismiss();
                                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_LONG).show();
                                    // 写入缓存
                                    SharedPreferencesManager.saveUserName(mContext, content);
                                    mData.set(pos, "用户名：" + content);

                                    // 更新ListView
                                    notifyDataSetChanged();
                                } else {
                                    try {
                                        handleError(response.errorBody());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<UpdateUserInfoResponse> call, Throwable t) {
                                Toast.makeText(mContext, "网络连接错误", Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case MODIFY_INFO_PASSWORD:
                        Boolean isValidPassword = content.length() >= 8;
                        Boolean isSamePassword = content.equals(finalRecheckTextInput.getText().toString());

                        if (isValidPassword && isSamePassword){
                            finalErrorTextView.setVisibility(View.GONE);
                            Call<UpdateUserInfoResponse> passwordCall = apiService.updatePassword(new UpdatePasswordRequest(userId, finalCheckTextInput.getText().toString(), content));
                            passwordCall.enqueue(new Callback<UpdateUserInfoResponse>() {
                                @Override
                                public void onResponse(Call<UpdateUserInfoResponse> call, Response<UpdateUserInfoResponse> response) {
                                    int statusCode = response.code();
                                    if (statusCode == 200) {
                                        dialog.dismiss();
                                        Toast.makeText(mContext, "修改成功", Toast.LENGTH_LONG).show();
                                    } else {
                                        try {
                                            handleError(response.errorBody());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<UpdateUserInfoResponse> call, Throwable t) {
                                    Toast.makeText(mContext, "网络连接错误", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else {
                            finalRecheckErrorTextView.setText("两次输入的密码不相同");
                            finalErrorTextView.setVisibility(isValidPassword ? View.GONE : View.VISIBLE);
                            finalRecheckErrorTextView.setVisibility(isSamePassword ? View.GONE : View.VISIBLE);
                        }
                        break;
                    case SET_INFO_EMAIL:
                    case MODIFY_INFO_EMAIL:
                        if (isValidEmail(content)){
                            finalErrorTextView.setVisibility(View.GONE);
                            Call<UpdateUserInfoResponse> emailCall = apiService.updateEmail(
                                    new UpdateEmailRequest(userId, type == SET_INFO_EMAIL ? " " : finalCheckTextInput.getText().toString(), content));
                            emailCall.enqueue(new Callback<UpdateUserInfoResponse>() {
                                @Override
                                public void onResponse(Call<UpdateUserInfoResponse> call, Response<UpdateUserInfoResponse> response) {
                                    int statusCode = response.code();
                                    if (statusCode == 200) {
                                        dialog.dismiss();
                                        Toast.makeText(mContext, "修改成功", Toast.LENGTH_LONG).show();
                                        // 写入缓存
                                        SharedPreferencesManager.saveUserEmail(mContext, content);
                                        mData.set(pos, "邮箱：" + maskEmail(content));

                                        // 更新ListView
                                        notifyDataSetChanged();
                                    } else {
                                        try {
                                            handleError(response.errorBody());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<UpdateUserInfoResponse> call, Throwable t) {
                                    Toast.makeText(mContext, "网络连接错误", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else {
                            finalErrorTextView.setText("请输入合法邮箱");
                            finalErrorTextView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case SET_INFO_PHONE:
                    case MODIFY_INFO_PHONE:
                        if (isValidPhoneNumber(content)){
                            finalErrorTextView.setVisibility(View.GONE);
                            Call<UpdateUserInfoResponse> phoneCall = apiService.updatePhoneNumber(
                                    new UpdatePhoneNumberRequest(userId, type == SET_INFO_PHONE ? " " : finalCheckTextInput.getText().toString(), content));
                            phoneCall.enqueue(new Callback<UpdateUserInfoResponse>() {
                                @Override
                                public void onResponse(Call<UpdateUserInfoResponse> call, Response<UpdateUserInfoResponse> response) {
                                    int statusCode = response.code();
                                    if (statusCode == 200) {
                                        dialog.dismiss();
                                        Toast.makeText(mContext, "修改成功", Toast.LENGTH_LONG).show();
                                        // 写入缓存
                                        SharedPreferencesManager.saveUserPhone(mContext, content);
                                        mData.set(pos, "手机号：" + maskPhoneNumber(content));

                                        // 更新ListView
                                        notifyDataSetChanged();
                                    } else {
                                        try {
                                            handleError(response.errorBody());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<UpdateUserInfoResponse> call, Throwable t) {
                                    Toast.makeText(mContext, "网络连接错误", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else {
                            finalErrorTextView.setText("请输入合法手机号");
                            finalErrorTextView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MODIFY_INFO_SIGNATURE:
                        Call<UpdateUserInfoResponse> signatureCall = apiService.updateSignature(new UpdateSignatureRequest(userId, content));
                        signatureCall.enqueue(new Callback<UpdateUserInfoResponse>() {
                            @Override
                            public void onResponse(Call<UpdateUserInfoResponse> call, Response<UpdateUserInfoResponse> response) {
                                int statusCode = response.code();
                                if (statusCode == 200) {
                                    // 清除输入框的焦点
                                    editTextInput.clearFocus();

                                    // 隐藏键盘
                                    InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(editTextInput.getWindowToken(), 0);
                                    dialog.dismiss();
                                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_LONG).show();
                                    // 写入缓存
                                    SharedPreferencesManager.saveUserSignature(mContext, content);
                                    mData.set(pos, "签名：" + (content.equals("") ? "未设置" : content));

                                    // 更新ListView
                                    notifyDataSetChanged();
                                } else {
                                    try {
                                        handleError(response.errorBody());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<UpdateUserInfoResponse> call, Throwable t) {
                                Toast.makeText(mContext, "网络连接错误", Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                }
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 显示对话框
        dialog.show();

    }

    private void handleError(ResponseBody body) throws IOException {
        String errorBody = body.string();
        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(errorBody, JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        // 用户名已存在
        if (jsonObject.has("username")) {
            Toast.makeText(mContext, "用户名已存在", Toast.LENGTH_SHORT).show();
        }
        // 密码不匹配
        else if (jsonObject.has("wrong_password")) {
            Toast.makeText(mContext, "密码错误", Toast.LENGTH_LONG).show();
        }
        // 手机号已存在
        else if (jsonObject.has("phone_number")) {
            Toast.makeText(mContext, "该手机号已被注册", Toast.LENGTH_SHORT).show();
        }
        // 手机号不匹配
        else if (jsonObject.has("wrong_phone_number")) {
            Toast.makeText(mContext, "手机号错误", Toast.LENGTH_LONG).show();
        }
        // 邮箱已存在
        else if (jsonObject.has("email")) {
            Toast.makeText(mContext, "该邮箱已被注册", Toast.LENGTH_SHORT).show();
        }
        // 邮箱不匹配
        else if (jsonObject.has("wrong_email")) {
            Toast.makeText(mContext, "邮箱错误", Toast.LENGTH_LONG).show();
        }
    }
}