package com.example.man.api.models;

public class CreateNoteResponse {
    private String message;
    private int id;
    private String time;

    public CreateNoteResponse(int id, String time, String message) {
        this.id = id;
        this.time = time;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {return time;}

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time){ this.time = time;}
}
