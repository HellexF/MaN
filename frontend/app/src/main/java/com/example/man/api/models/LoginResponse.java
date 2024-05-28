package com.example.man.api.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private String id;

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }
}
