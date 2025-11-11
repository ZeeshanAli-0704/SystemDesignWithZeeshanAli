package org.example.model.user;

import java.util.UUID;

public class User {
    private final String userId;
    private final UserType userType;
    private final String userName;
    private final String email;
    private final String city;
    private final GenderType gender;


    public User(UserType userType, String userName, String email, String city, GenderType gender){
        this.userId = UUID.randomUUID().toString();;
        this.userType=userType;
        this.userName=userName;
        this.email=email;
        this.city=city;
        this.gender = gender;
    };

    public String getUserId() {
        return userId;
    };

    public UserType getUserType() {
        return userType;
    };

    public String getUserName() {
        return userName;
    };

    public String getEmail() {
        return email;
    };

    public String getCity() {
        return city;
    };

    public GenderType getGender() {
        return gender;
    };
}
