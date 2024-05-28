package com.example.man.api.models;

public class UpdateSignatureRequest {
    private int id;
    private String signature;

    // 构造函数
    public UpdateSignatureRequest(int id, String username) {
        this.id = id;
        this.signature = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String username) {
        this.signature = username;
    }
}
