package com.example.man.api.models;

public class UpdateEmailRequest {
    private int id;
    private String old_email;
    private String new_email;

    // 构造函数
    public UpdateEmailRequest(int id, String old_email, String new_email) {
        this.id = id;
        this.old_email = old_email;
        this.new_email = new_email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOldEmail() {
        return old_email;
    }

    public void setOldEmail(String old_email) {
        this.old_email = old_email;
    }

    public String getNewEmail() {
        return new_email;
    }

    public void setNewEmail(String new_email) {
        this.new_email = new_email;
    }
}
