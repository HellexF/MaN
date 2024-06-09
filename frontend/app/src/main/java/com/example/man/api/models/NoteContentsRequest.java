package com.example.man.api.models;

public class NoteContentsRequest {
    private int user_id;
    private int note_id;

    public NoteContentsRequest(int userId, int noteId) {
        this.user_id = userId;
        this.note_id = noteId;
    }

    public int getUserId() {
        return user_id;
    }

    public int getNoteId() {
        return note_id;
    }

    public void setUserId(int userId) {
        this.user_id = userId;
    }

    public void setNoteId(int noteId) {
        this.note_id = noteId;
    }
}
