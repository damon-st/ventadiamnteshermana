package com.damon.ventadiamante.models;

public class Users {

    String email;
    String name;
    String uid;
    String device_token;

    public Users() {
    }

    public Users(String email, String name, String uid, String device_token) {
        this.email = email;
        this.name = name;
        this.uid = uid;
        this.device_token = device_token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
