package com.example.man.api.models;

public class CreateNoteRequest {
    private int userId;
    private int categoryId;

    public CreateNoteRequest(int userId, int categoryId) {
        this.userId = userId;
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

}
