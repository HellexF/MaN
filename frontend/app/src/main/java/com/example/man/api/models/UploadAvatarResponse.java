package com.example.man.api.models;

public class UploadAvatarResponse {
    private String message;
    private String avatar_url;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getAvatarUrl() { return avatar_url; }
    public void setAvatarUrl(String avatar_url) { this.avatar_url = avatar_url; }
}
