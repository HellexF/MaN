package com.example.man.api;

import com.example.man.api.models.CheckPhoneNumberAvailableResponse;
import com.example.man.api.models.PhoneNumber;
import com.example.man.api.models.RegisterInfo;
import com.example.man.api.models.RegistrationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/user/check_phone_number_available")
    Call<CheckPhoneNumberAvailableResponse> checkPhoneNumberAvailable(@Body PhoneNumber phoneNumber);
    @POST("/user/register")
    Call<RegistrationResponse> register(@Body RegisterInfo registerInfo);
}