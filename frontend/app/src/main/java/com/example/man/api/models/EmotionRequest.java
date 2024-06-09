package com.example.man.api.models;

public class EmotionRequest {
    private String prompt;

    public EmotionRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
