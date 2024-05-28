package com.example.man.api.models;

public class UpdatePhoneNumberRequest {
    private int id;
    private String old_phone_number;
    private String new_phone_number;

    // 构造函数
    public UpdatePhoneNumberRequest(int id, String old_phone_number, String new_phone_number) {
        this.id = id;
        this.old_phone_number = old_phone_number;
        this.new_phone_number = new_phone_number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOldPhoneNumber() {
        return old_phone_number;
    }

    public void setOldPhoneNumber(String old_phone_number) {
        this.old_phone_number = old_phone_number;
    }

    public String getNewPhoneNumber() {
        return new_phone_number;
    }

    public void setNewPhoneNumber(String new_phone_number) {
        this.new_phone_number = new_phone_number;
    }
}
