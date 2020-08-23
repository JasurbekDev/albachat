package com.example.simplechatapp;

public class Message {
    private String text;
    private String name;
    private String imageUrl;
    private String recipent;
    private String sender;
    private boolean isMine;
    private String time;

    public Message() {
    }

    public Message(String text, String name, String imageUrl, String recipent, String sender, boolean isMine, String time) {
        this.text = text;
        this.name = name;
        this.imageUrl = imageUrl;
        this.recipent = recipent;
        this.sender = sender;
        this.isMine = isMine;
        this.time = time;
    }

    public Message(String text, String name, boolean isMine) {
        this.text = text;
        this.name = name;
        this.isMine = isMine;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getRecipent() {
        return recipent;
    }

    public void setRecipent(String recipent) {
        this.recipent = recipent;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
