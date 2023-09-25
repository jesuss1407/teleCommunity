package com.example.telecommunity.entity;

public class NotificationItem {
    private int photoResId; // ID de recurso de la foto
    private String title;
    private String timestamp;
    private String content;

    public NotificationItem(int photoResId, String title, String timestamp, String content) {
        this.photoResId = photoResId;
        this.title = title;
        this.timestamp = timestamp;
        this.content = content;
    }

    public int getPhotoResId() {
        return photoResId;
    }

    public String getTitle() {
        return title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }
}

