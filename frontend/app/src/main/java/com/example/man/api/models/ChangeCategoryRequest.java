package com.example.man.api.models;

public class ChangeCategoryRequest {
    private int noteId;
    private String newCategoryName;
    private int userId;

    public ChangeCategoryRequest(int noteId, String newCategoryName, int userId) {
        this.noteId = noteId;
        this.newCategoryName = newCategoryName;
        this.userId = userId;
    }

    public int getNoteId() {
        return noteId;
    }

    public String getCategoryName() {
        return newCategoryName;
    }

    public int getUserId() {
        return userId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public void setCategoryName(String categoryName) {
        this.newCategoryName = categoryName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
