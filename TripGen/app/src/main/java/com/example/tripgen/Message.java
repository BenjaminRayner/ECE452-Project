package com.example.tripgen;

public class Message {
    public static final int TYPE_AI = 0;
    public static final int TYPE_USER = 1;
    public static final int TYPE_OPTIONS = 2;

    public static final int TYPE_LISTS = 3;


    private String content;

    private String description;
    private int type;
    private Integer imageId; // New field for image resource ID

    public Message(String content,String description,Integer imageId, int type) {
        this.content = content;
        this.description = description;
        this.imageId = imageId; // Set the image resource ID
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public Integer getImageId() {
        return imageId;
    }

    public int getType() {
        return type;
    }
}
