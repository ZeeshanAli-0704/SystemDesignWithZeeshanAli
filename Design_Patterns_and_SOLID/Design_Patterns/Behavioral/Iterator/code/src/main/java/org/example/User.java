package org.example;

public class User {
    private String name;
    private String emailID;

    public User(String name, String emailID) {
        this.name = name;
        this.emailID = emailID;
    }

    public String getName() {
        return name;
    }

    public String getEmailID() {
        return emailID;
    }
}
