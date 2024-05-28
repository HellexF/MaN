package com.example.man.api;

import com.example.man.api.models.CheckEmailAvailableResponse;
import com.example.man.api.models.CheckPhoneNumberAvailableResponse;
import com.example.man.api.models.EmailRequest;
import com.example.man.api.models.LoginInfoRequest;
import com.example.man.api.models.LoginResponse;
import com.example.man.api.models.PhoneNumberRequest;
import com.example.man.api.models.RegisterInfoRequest;
import com.example.man.api.models.RegistrationResponse;
import com.example.man.api.models.UploadAvatarResponse;
import com.example.man.api.models.UserInfoResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/user/check_phone_number_available")
    Call<CheckPhoneNumberAvailableResponse> checkPhoneNumberAvailable(@Body PhoneNumberRequest phoneNumberRequest);
    @POST("/user/check_email_available")
    Call<CheckEmailAvailableResponse> checkEmailAvailable(@Body EmailRequest emailRequest);
    @POST("/user/register")
    Call<RegistrationResponse> register(@Body RegisterInfoRequest registerInfoRequest);
    @POST("/user/login")
    Call<LoginResponse> login(@Body LoginInfoRequest loginInfoRequest);
    @GET("/user/user_info/{user_id}/")
    Call<UserInfoResponse> getUser(@Path("user_id") int Id);
    @Multipart
    @POST("/user/upload_avatar")
    Call<UploadAvatarResponse> uploadAvatar(
            @Part MultipartBody.Part avatar,
            @Part("id") RequestBody userId
    );
}