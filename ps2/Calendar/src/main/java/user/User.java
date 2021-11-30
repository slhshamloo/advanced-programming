package user;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String username;
    private final List<Integer> ownedCalendarIdList = new ArrayList<>();
    private final List<Integer> sharedCalendarIdList = new ArrayList<>();
    private String password;
    private boolean isNeverLoggedIn = true;

    public User(String username, String password, Database database) {
        this.username = username;
        this.password = password;

        database.addUser(this);
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPasswordCorrect(String password) {
        return this.password.equals(password);
    }

    public void createFirstCalendarForFirstLogin(Database database) {
        if (isNeverLoggedIn) {
            int firstCalendarId = database.addCalendar(username);
            ownedCalendarIdList.add(firstCalendarId);
            System.out.println("calendar created successfully!");
            isNeverLoggedIn = false;
        }
    }

    public void addOwnedCalenderId(int id) {
        if (!isCalendarOwned(id))
            ownedCalendarIdList.add(id);
    }

    public void addSharedCalenderId(int id) {
        if (!isCalendarShared(id))
            sharedCalendarIdList.add(id);
    }

    public void removeOwnedCalenderId(int id) {
        ownedCalendarIdList.remove(Integer.valueOf(id));
    }

    public void removeSharedCalenderId(int id) {
        sharedCalendarIdList.remove(Integer.valueOf(id));
    }

    public boolean isCalendarOwned(int id) {
        return ownedCalendarIdList.contains(id);
    }

    public boolean isCalendarShared(int id) {
        return sharedCalendarIdList.contains(id);
    }

    public List<Integer> getOwnedCalendarIdList() {
        return ownedCalendarIdList;
    }

    public List<Integer> getSharedCalendarIdList() {
        return sharedCalendarIdList;
    }

    @Override
    public String toString() {
        return getUsername();
    }
}
