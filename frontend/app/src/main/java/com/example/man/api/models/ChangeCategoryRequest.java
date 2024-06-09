package com.example.man.api.models;

public class ChangeCategoryRequest {
    private int noteId;
    private String newCategoryName;

    public ChangeCategoryRequest(int noteId, String newCategoryName) {
        this.noteId = noteId;
        this.newCategoryName = newCategoryName;
    }

    public int getNoteId() {
        return noteId;
    }

    public String getCategoryName() {
        return newCategoryName;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public void setCategoryName(String categoryName) {
        this.newCategoryName = categoryName;
    }

}
