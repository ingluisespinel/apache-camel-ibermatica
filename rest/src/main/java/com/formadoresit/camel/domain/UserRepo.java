package com.formadoresit.camel.domain;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepo {
    private List<User> users;

    public UserRepo() {
        users = new ArrayList<>();
        var user = new User();
        user.setId(10);
        user.setName("Jose");
        user.setAge(30);
        users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }

    public User addUser(User user){
        user.setId(100);
        users.add(user);
        return user;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
