package com.example.man.api.models;

public class SearchNoteRequest {
    private int user_id;
    private int category_id;
    private String key;

    public SearchNoteRequest(int user_id, int category_id, String key) {
        this.user_id = user_id;
        this.category_id = category_id;
        this.key = key;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}