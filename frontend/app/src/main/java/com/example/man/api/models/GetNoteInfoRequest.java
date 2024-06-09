package com.example.man.api.models;

public class GetNoteInfoRequest {
    private int user_id;
    private int category_id;

    public GetNoteInfoRequest(int user_id, int category_id) {
        this.user_id = user_id;
        this.category_id = category_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int id) {
        this.user_id = id;
    }

    public int getCategoryId() {
        return category_id;
    }

    public void setCategoryId(int id) {
        this.category_id = id;
    }
}