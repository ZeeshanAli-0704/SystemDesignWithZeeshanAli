// User.java
package org.example.models;

import org.example.enums.UserType;

public class User {
    private final int userId;
    private final String name;
    private final UserType userType;

    public User(int userId, String name, UserType userType) {
        this.userId = userId;
        this.name = name;
        this.userType = userType;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public UserType getUserType() { return userType; }
}