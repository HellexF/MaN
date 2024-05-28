package com.example.man.api.models;

public class UpdateUsernameRequest {
    private int id;
    private String username;

    // 构造函数
    public UpdateUsernameRequest(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
