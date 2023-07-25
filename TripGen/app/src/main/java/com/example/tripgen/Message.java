package com.example.tripgen;

import android.graphics.Bitmap;

public class Message {
    public static final int TYPE_AI = 0;
    public static final int TYPE_USER = 1;
    public static final int TYPE_OPTIONS = 2;

    public static final int TYPE_LISTS = 3;


    private String content;

    private String description;
    private int type;

    private Bitmap imageBitmap; // New field for Bitmap

    public Message(String content,String description, Bitmap imageBitmap, int type) {
        this.content = content;
        this.description = description;
        this.imageBitmap = imageBitmap;
        this.type = type;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }


    public String getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }



    public int getType() {
        return type;
    }
}
