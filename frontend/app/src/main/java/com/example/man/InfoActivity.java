package com.example.man;

import static com.example.man.utils.Mask.maskEmail;
import static com.example.man.utils.Mask.maskPhoneNumber;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentResolver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.a520wcf.yllistview.YLListView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.man.adapters.UserInfoAdapter;
import com.example.man.api.ApiClient;
import com.example.man.api.ApiService;
import com.example.man.api.models.UploadAvatarResponse;
import com.example.man.api.models.UserInfoResponse;
import com.example.man.utils.SharedPreferencesManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
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
    private UserInfoAdapter userInfoAdapter;
    private static final int REQUEST_IMAGE_UPLOAD = 1;
    private ArrayList<String> data;
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

        data = new ArrayList<>(Arrays.asList("", "", "", "", "", "", ""));

        parseInfo(data);

        userInfoAdapter = new UserInfoAdapter(
                InfoActivity.this,
                R.layout.user_info_list_item,
                data
        );

        listView.setAdapter(userInfoAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position=position-listView.getHeaderViewsCount();
            }
        });

        listView.setVerticalScrollBarEnabled(false);

        // 设置拉动时刷新信息
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当滚动停止时，且滚动到列表顶部时触发
                if (scrollState == SCROLL_STATE_IDLE && listView.getFirstVisiblePosition() == 0) {
                    Call<UserInfoResponse> infoCall = apiService.getUser(Integer.parseInt(SharedPreferencesManager.getUserId(InfoActivity.this)));
                    infoCall.enqueue(new Callback<UserInfoResponse>() {
                        @Override
                        public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> infoResponse) {
                            SharedPreferencesManager.saveUserName(InfoActivity.this, infoResponse.body().getUsername());
                            SharedPreferencesManager.saveUserAvatar(InfoActivity.this, infoResponse.body().getAvatar());
                            SharedPreferencesManager.saveUserEmail(InfoActivity.this, infoResponse.body().getEmail());
                            SharedPreferencesManager.saveUserPhone(InfoActivity.this, infoResponse.body().getPhoneNumber());
                            SharedPreferencesManager.saveUserSignature(InfoActivity.this, infoResponse.body().getSignature());

                            ArrayList<String> newData = new ArrayList<>(Arrays.asList("", "", "", "", "", "", ""));
                            parseInfo(newData);
                            data.clear();
                            data.addAll(newData);
                            userInfoAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                            Toast.makeText(InfoActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        // 设置头像上传逻辑
        ImageButton uploadButton = topView.findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_UPLOAD);
            }
        });

        // 设置返回按钮
        ImageButton returnButton = findViewById(R.id.info_return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoActivity.this, NoteActivity.class);
                startActivity(intent);
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

    void parseInfo(ArrayList<String> data){
        // 解析用户名
        data.set(0, "用户名：" + SharedPreferencesManager.getUserName(this));

        // 解析密码
        data.set(1, "密码：********");

        // 解析邮箱
        if(SharedPreferencesManager.getUserEmail(this).isEmpty()){
            data.set(2, "邮箱：未设置");
        }
        else {
            String email = SharedPreferencesManager.getUserEmail(this);;
            data.set(2, "邮箱：" + maskEmail(email));
        }

        // 解析手机号
        if(SharedPreferencesManager.getUserPhone(this).isEmpty()){
            data.set(3, "手机号：未设置");
        }
        else {
            String phoneNumber = SharedPreferencesManager.getUserPhone(this);
            data.set(3, "手机号；" + maskPhoneNumber(phoneNumber));
        }

        // 解析签名
        if(SharedPreferencesManager.getUserSignature(this).isEmpty()){
            data.set(4, "签名：未设置");
        }
        else {
            data.set(4, "签名：" + SharedPreferencesManager.getUserSignature(this));
        }

        Glide.with(this)
                .load("http://10.0.2.2:8000" + SharedPreferencesManager.getUserAvatar(this))
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
                    // 更新本地头像缓存
                    SharedPreferencesManager.saveUserAvatar(InfoActivity.this, response.body().getAvatarUrl());
                    Glide.with(InfoActivity.this)
                            .load("http://10.0.2.2:8000" + SharedPreferencesManager.getUserAvatar(InfoActivity.this))
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.user_avatar)
                                    .error(R.drawable.user_avatar))
                            .into(avatarImageView);

                    Toast.makeText(InfoActivity.this, "上传成功", Toast.LENGTH_LONG).show();
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
