package com.example.man.api.models;

public class CreateCategoryResponse {
    private int id;
    private String name;

    public CreateCategoryResponse(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public CreateCategoryResponse() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}