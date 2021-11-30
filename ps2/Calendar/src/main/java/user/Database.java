package user;

import calendar.MahmoudiCalendar;

import java.util.*;

public class Database {
    private final Map<String, User> users = new HashMap<>();
    private final Map<Integer, MahmoudiCalendar> calendars = new HashMap<>();
    private int createdCalendarCount = 0;

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public void removeUserByUsername(String username) {
        User user = users.get(username);
        for (int id : new ArrayList<>(user.getOwnedCalendarIdList()))
            removeCalendarById(id);
        users.remove(username);
    }

    public User getUserByUsername(String username) {
        return users.get(username);
    }

    public String[] getAlphabeticallySortedUsernames() {
        String[] usernames = users.keySet().toArray(new String[0]);
        Arrays.sort(usernames);
        return usernames;
    }

    public int addCalendar(String name) {
        int id = ++createdCalendarCount;
        MahmoudiCalendar calendar = new MahmoudiCalendar(id, name);
        calendars.put(id, calendar);
        return id;
    }

    public MahmoudiCalendar getCalendarById(int id) {
        return calendars.get(id);
    }

    public void removeCalendarById(int id) {
        for (User user : users.values()) {
            if (user.isCalendarOwned(id))
                user.removeOwnedCalenderId(id);
            else if (user.isCalendarShared(id))
                user.removeSharedCalenderId(id);
        }
        calendars.remove(id);
    }

    public MahmoudiCalendar[] getCalendarsByIds(List<Integer> ids) {
        MahmoudiCalendar[] calendars = new MahmoudiCalendar[ids.size()];

        for (int i = 0; i < ids.size(); i++)
            calendars[i] = getCalendarById(ids.get(i));
        return calendars;
    }
}
