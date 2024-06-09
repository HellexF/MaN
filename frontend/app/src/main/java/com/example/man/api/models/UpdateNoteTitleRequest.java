package com.example.man.api.models;

public class UpdateNoteTitleRequest {
    private String title;

    public UpdateNoteTitleRequest(String title) {
        this.title = title;
    }

    public String getTitle() {return title;}

    public void setTitle(String title){ this.title = title; }
}
