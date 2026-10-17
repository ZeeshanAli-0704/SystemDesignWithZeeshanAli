package org.example.exceptions;
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int id) { super("User not found: " + id); }
}
