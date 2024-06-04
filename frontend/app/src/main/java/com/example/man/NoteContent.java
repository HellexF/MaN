package com.example.man;

public class NoteContent {
    public final static int TEXT_TYPE_CONTENT = 0;
    public final static int IMAGE_TYPE_CONTENT = 1;
    public final static int AUDIO_TYPE_CONTENT = 2;
    private int type;
    private String content;
    public NoteContent(int type, String content) {
        this.type = type;
        this.content = content;
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
}
