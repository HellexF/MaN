package com.example.man.api.models;

import com.example.man.NoteInfo;

import java.util.List;

public class SearchNoteResponse {
    private List<NoteInfo> noteInfo;

    public List<NoteInfo> getNoteInfo() {
        return noteInfo;
    }

    public void setNoteInfo(List<NoteInfo> noteInfo) {
        this.noteInfo = noteInfo;
    }
}
