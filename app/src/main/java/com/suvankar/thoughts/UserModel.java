package com.suvankar.thoughts;

import android.hardware.usb.UsbRequest;

import java.util.List;

public class UserModel implements Cloneable {

    private String name, email, password;
    List<ThoughtModel> thoughts;

    public UserModel(String name, String email, String password, List<ThoughtModel> thoughts) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.thoughts = thoughts;
    }

    public UserModel(UserModel userModel) {
        this.name = userModel.name;
        this.email = userModel.email;
        this.password = userModel.password;
        this.thoughts = userModel.thoughts;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<ThoughtModel> getThoughts() {
        return thoughts;
    }

    public void setThoughts(List<ThoughtModel> thoughts) {
        this.thoughts = thoughts;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", thoughts=" + thoughts +
                '}';
    }
}
