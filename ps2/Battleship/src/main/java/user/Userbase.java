package user;

import java.util.*;

public class Userbase {
    private final Map<String, User> users = new HashMap<>();

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public User getUserByUsername(String username) {
        return users.get(username);
    }

    public void removeUserByUsername(String username) {
        users.remove(username);
    }

    public String[] getAlphabeticallySortedUsernames() {
        String[] usernames = users.keySet().toArray(new String[0]);
        Arrays.sort(usernames);
        return usernames;
    }

    public User[] getUsersSortedByScoreboard() {
        User[] sortedUsers = users.values().toArray(new User[0]);

        Arrays.sort(sortedUsers, Comparator.comparing(User::getUsername));
        Arrays.sort(sortedUsers, Comparator.comparing(User::getLosses));
        Arrays.sort(sortedUsers, Collections.reverseOrder(Comparator.comparing(User::getDraws)));
        Arrays.sort(sortedUsers, Collections.reverseOrder(Comparator.comparing(User::getWins)));
        Arrays.sort(sortedUsers, Collections.reverseOrder(Comparator.comparing(User::getScore)));

        return sortedUsers;
    }
}
