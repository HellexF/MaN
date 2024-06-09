package com.example.man.api.models;

public class UpdateNoteEmotionRequest {
    private String emotion;

    public UpdateNoteEmotionRequest(String emotion) {
        this.emotion = emotion;
    }

    public String getEmotion() {return emotion;}

    public void setEmotion(String emotion){ this.emotion = emotion; }
}
