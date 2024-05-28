package com.example.man;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.content.ContentResolver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.a520wcf.yllistview.YLListView;
import com.bumptech.glide.Glide;
import com.example.man.adapters.UserInfoAdapter;
import com.example.man.api.ApiClient;
import com.example.man.api.ApiService;
import com.example.man.api.models.UploadAvatarResponse;
import com.example.man.api.models.UserInfoResponse;
import com.example.man.utils.SharedPreferencesManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoActivity extends AppCompatActivity {
    private ListView listView;
    private ImageView avatarImageView;
    private ApiService apiService;
    private static final int REQUEST_IMAGE_UPLOAD = 1;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // 获取ApiService实例
        apiService = ApiClient.getClient().create(ApiService.class);

        listView = (YLListView) findViewById(R.id.info_list_view);
        View topView=View.inflate(this,R.layout.user_info_top,null);
        listView.addHeaderView(topView);
        avatarImageView = topView.findViewById(R.id.avatar_image_view);

        ((YLListView)listView).setFinalTopHeight(500);

        String[] data = {"", "", "", "", "", "", ""};

        Call<UserInfoResponse> call = apiService.getUser(Integer.parseInt(SharedPreferencesManager.getUserId(this)));
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                parseInfo(response.body(), data);

                UserInfoAdapter adapter = new UserInfoAdapter(
                        InfoActivity.this,
                        R.layout.user_info_list_item,
                        Arrays.asList(data)
                );

                ImageView imageView = topView.findViewById(R.id.avatar_image_view);

                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        position=position-listView.getHeaderViewsCount();
                    }
                });

                listView.setVerticalScrollBarEnabled(false);

                // 设置头像上传逻辑
                ImageButton uploadButton = topView.findViewById(R.id.upload_button);
                uploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_IMAGE_UPLOAD);
                    }
                });

            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                Toast.makeText(InfoActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_UPLOAD && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            avatarImageView.setImageURI(selectedImage);

            uploadAvatar(selectedImage);
        }
    }

    void parseInfo(UserInfoResponse userInfoResponse, String[] data){
        // 解析用户名
        data[0] = "用户名：" + userInfoResponse.getUsername();

        // 解析密码
        data[1] = "密码：********";

        // 解析邮箱
        if(userInfoResponse.getEmail().isEmpty()){
            data[2] = "邮箱；未设置";
        }
        else {
            data[2] = "邮箱；" + userInfoResponse.getEmail();
        }

        // 解析手机号
        if(userInfoResponse.getPhoneNumber().isEmpty()){
            data[3] = "手机号；未设置";
        }
        else {
            data[3] = "手机号；" + userInfoResponse.getPhoneNumber();
        }

        // 解析签名
        if(userInfoResponse.getSignature().isEmpty()){
            data[4] = "签名；未设置";
        }
        else {
            data[4] = "签名；" + userInfoResponse.getSignature();
        }

        Glide.with(this)
                .load("http://10.0.2.2:8000" + userInfoResponse.getAvatar())
                .into(avatarImageView);
    }

    void uploadAvatar(Uri avatar){
        File avatarFile = uriToFile(avatar);
        if (avatarFile == null) {
            Toast.makeText(this, "文件转换错误", Toast.LENGTH_LONG).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), avatarFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", avatarFile.getName(), requestFile);

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SharedPreferencesManager.getUserId(InfoActivity.this)));

        Call<UploadAvatarResponse> call = apiService.uploadAvatar(body, userId);
        call.enqueue(new Callback<UploadAvatarResponse>() {
            @Override
            public void onResponse(Call<UploadAvatarResponse> call, Response<UploadAvatarResponse> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    Toast.makeText(InfoActivity.this, "上传成功" + statusCode, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(InfoActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UploadAvatarResponse> call, Throwable t) {
                Toast.makeText(InfoActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
            }
        });
    }

    private File uriToFile(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);
            File file = new File(getCacheDir(), "temp_avatar.jpg");
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
