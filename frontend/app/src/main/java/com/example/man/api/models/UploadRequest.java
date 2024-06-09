package com.example.man.api.models;

import java.io.File;

public class UploadRequest {
    private byte[] file;
    private int id;
    private int type;

    public UploadRequest(byte[] file, int id, int type) {
        this.file = file;
        this.id = id;
        this.type = type;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
