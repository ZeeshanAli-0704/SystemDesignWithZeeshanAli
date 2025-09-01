package org.example.repository;

import org.example.models.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    private final Map<Integer, User> users = new ConcurrentHashMap<>();

    public void save(User user) {
        users.put(user.getUserId(), user);
    }

    public User findById(int userId) {
        return users.get(userId);
    }

    public boolean exists(int userId) {
        return users.containsKey(userId);
    }
}
