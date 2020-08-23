package com.example.simplechatapp;

public class User {
    private String name;
    private String gender;
    private String age;
    private String email;
    private String id;
    private int avatarMockUpResource;
    private boolean isOnline = true;

    public User() {
    }

    public User(String name, String gender, String age, String email, String id, int avatarMockUpResource) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.email = email;
        this.id = id;
        this.avatarMockUpResource = avatarMockUpResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAvatarMockUpResource() {
        return avatarMockUpResource;
    }

    public void setAvatarMockUpResource(int avatarMockUpResource) {
        this.avatarMockUpResource = avatarMockUpResource;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
