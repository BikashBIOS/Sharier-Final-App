package com.example.shareer.User;

public class ModelUser {

    String id, Name, Email,Password;

    public ModelUser() {
    }

    public ModelUser(String id, String name, String email, String password) {
        this.id = id;
        Name = name;
        Email = email;
        Password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
