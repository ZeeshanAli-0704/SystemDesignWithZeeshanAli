package org.example;

public class User {

    private String name;
    private  String email;
    private String address;

    public User(UserBuilder builder){
        this.name = builder.name;
        this.email = builder.email;
        this.address = builder.address;
    }

    @Override
    public String toString() {
        return "User : " +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' + "";
    }

    public static class UserBuilder{
        private String name;
        private String email;
        private String address;

        public UserBuilder setEmail(String email) {
            this.email = email;
            return this;
        };

        public UserBuilder setName(String name) {
            this.name = name;
            return this;
        };

        public UserBuilder setAddress(String address) {
            this.address = address;
            return this;
        };

        public User build() {
            return  new User(this);
        }
    }
}
