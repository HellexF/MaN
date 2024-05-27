package com.example.man.api.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("username")
    private String username;

    @SerializedName("signature")
    private String signature;

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public String getSignature() {
        return signature;
    }
}
