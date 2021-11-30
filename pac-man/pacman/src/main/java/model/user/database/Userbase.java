package model.user.database;

import model.user.User;

import java.util.ArrayList;
import java.util.List;

public class Userbase {
    private final List<User> users = new ArrayList<>();

    public void addUser(String username, String password) {
        User user = new User(username, password);
        users.add(user);
    }

    public User getUserByUsername(String username) {
        return users.stream().filter(user -> username.equals(user.getUsername()))
                .findFirst().orElse(null);
    }
}