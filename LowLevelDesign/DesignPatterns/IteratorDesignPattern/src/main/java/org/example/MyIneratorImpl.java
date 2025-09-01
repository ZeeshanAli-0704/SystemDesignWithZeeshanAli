package org.example;

import java.util.ArrayList;
import java.util.List;

public class MyIneratorImpl implements MyIterator{
    private List<User> userList;
    private int lengthOfUserList = 0;
    private int position = 0;
    public MyIneratorImpl(List<User> userList) {
        this.userList = userList;
        this.lengthOfUserList = this.userList.size();
    }

    @Override
    public boolean hasNext() {
        if(position >= lengthOfUserList) {
            return false;
        }
        return true;
    }

    @Override
    public Object next() {
        User user =  userList.get(position);
        position++;
        return user;
    }
}
