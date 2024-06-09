package com.example.man.models;

public class NoteContent {
    public final static int TEXT_TYPE_CONTENT = 0;
    public final static int IMAGE_TYPE_CONTENT = 1;
    public final static int AUDIO_TYPE_CONTENT = 2;
    private int type;
    private String content;
    private int note_id;
    private int user_id;
    private int category_id;
    public NoteContent(int type, String content, int note_id, int user_id, int category_id) {
        this.type = type;
        this.content = content;
        this.note_id = note_id;
        this.user_id = user_id;
        this.category_id = category_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNoteId() {
        return note_id;
    }

    public void setNoteId(int note_id) {
        this.note_id = note_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public int getCategoryId() {
        return category_id;
    }

    public void setCategoryId(int category_id) {
        this.category_id = category_id;
    }
}
