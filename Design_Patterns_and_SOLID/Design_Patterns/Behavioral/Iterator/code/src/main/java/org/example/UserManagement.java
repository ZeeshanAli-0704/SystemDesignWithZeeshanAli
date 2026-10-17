package org.example;

import java.util.ArrayList;
import java.util.List;

public class UserManagement {
    private ArrayList<User> list = new ArrayList<>();

    public User getUser(int index) {
        return list.get(index);
    }

    public void addUser(User user) {
        list.add(user);
    }

    public MyIterator getIterator(){
        return new MyIneratorImpl(list);
    }
}
