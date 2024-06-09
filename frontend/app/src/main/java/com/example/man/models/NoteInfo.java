package com.example.man.models;

public class NoteInfo {
    public int id;
    public String image;
    public String title;
    public String date;
    public String time;
    public String emotion;

    public NoteInfo(int id, String image, String title, String date, String time, String emotion) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.date = date;
        this.time = time;
        this.emotion = emotion;
    }
}
