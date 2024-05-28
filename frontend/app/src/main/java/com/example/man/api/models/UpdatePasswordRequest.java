package com.example.man.api.models;

public class UpdatePasswordRequest {
    private int id;
    private String old_password;
    private String new_password;

    // 构造函数
    public UpdatePasswordRequest(int id, String old_password, String new_password) {
        this.id = id;
        this.old_password = old_password;
        this.new_password = new_password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOldPassword() {
        return old_password;
    }

    public void setOldPassword(String old_password) {
        this.old_password = old_password;
    }

    public String getNewPassword() {
        return new_password;
    }

    public void setNewPassword(String new_password) {
        this.new_password = new_password;
    }
}
