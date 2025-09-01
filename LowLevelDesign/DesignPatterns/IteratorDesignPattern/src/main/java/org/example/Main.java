package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        UserManagement userManagement = new UserManagement();

        User user1 = new User("User1", "user1@gmail.com");
        User user2 = new User("User2", "user2@gmail.com");

        userManagement.addUser(user1);
        userManagement.addUser(user2);


        MyIterator iterator = userManagement.getIterator();

        while (iterator.hasNext()){
            User user = (User) iterator.next();
            System.out.println(user.getName() + " " +  user.getEmailID());
        }

    }
}