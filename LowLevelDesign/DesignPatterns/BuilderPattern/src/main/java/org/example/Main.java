package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        User userObject = new User.UserBuilder().setName("Zeeshan").setEmail("abc@gmail.com").setAddress("India").build();
        System.out.println(userObject.toString());
    }
}