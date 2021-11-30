package user;

import java.util.*;

public class Userbase {
    private final Map<String, User> users = new HashMap<>();

    public void addUser(String username, String password) {
        addUser(new User(username, password));
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public User getUserByUsername(String username) {
        return users.get(username);
    }
}
