package controllers;

import user.User;
import user.Database;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginMenu extends AbstractController {
    private final Database database = new Database();

    public LoginMenu(Scanner inputStream) {
        super(inputStream, "Exit");
    }

    public static boolean isUsernameFormatInvalid(String username) {
        return !username.matches("\\w+");
    }

    private static boolean isPasswordFormatInvalid(String password) {
        return !password.matches("\\w+");
    }

    private static boolean isPasswordWeak(String password) {
        return !(password.length() >= 5
                && hasRegex(password, "[a-z]")
                && hasRegex(password, "[A-Z]")
                && hasRegex(password, "\\d"));
    }

    private static boolean hasRegex(String string, String regex) {
        return string.matches(".*" + regex + ".*");
    }

    @Override
    protected Map<Pattern, Consumer<Matcher>> createCommandMap() {
        Map<Pattern, Consumer<Matcher>> commandMap = new HashMap<>();

        commandMap.put(Pattern.compile("Register (\\S+) (\\S+)"), this::register);
        commandMap.put(Pattern.compile("Login (\\S+) (\\S+)"), this::login);
        commandMap.put(Pattern.compile("Remove (\\S+) (\\S+)"), this::removeUser);
        commandMap.put(Pattern.compile("Change Password (\\S+) (\\S+) (\\S+)"),
                this::changePassword);

        return commandMap;
    }

    @Override
    protected Map<String, Runnable> createNoArgumentCommandMap() {
        Map<String, Runnable> noArgumentCommandMap = new HashMap<>();

        noArgumentCommandMap.put("Show All Usernames", this::printSortedUsernames);

        return noArgumentCommandMap;
    }

    @Override
    protected void escape() {
    }

    private void register(Matcher matcher) {
        String username = matcher.group(1);
        String password = matcher.group(2);

        if (isUsernameFormatInvalid(username))
            System.out.println("invalid username!");
        else if (database.getUserByUsername(username) != null)
            System.out.println("a user exists with this username");
        else if (isPasswordFormatInvalid(password))
            System.out.println("invalid password!");
        else if (isPasswordWeak(password))
            System.out.println("password is weak!");
        else {
            new User(username, password, database);
            System.out.println("register successful!");
        }
    }

    private boolean hasNoErrorInCredentials(String username, String password) {
        if (isUsernameFormatInvalid(username)) {
            System.out.println("invalid username!");
            return false;
        } else {
            User user = database.getUserByUsername(username);

            if (user == null) {
                System.out.println("no user exists with this username");
                return false;
            } else if (isPasswordFormatInvalid(password)) {
                System.out.println("invalid password!");
                return false;
            } else if (!user.isPasswordCorrect(password)) {
                System.out.println("password is wrong!");
                return false;
            }
        }
        return true;
    }

    private void login(Matcher matcher) {
        String username = matcher.group(1);
        String password = matcher.group(2);

        if (hasNoErrorInCredentials(username, password)) {
            System.out.println("login successful!");
            User user = database.getUserByUsername(username);
            Controller mainMenu = new MainMenu(inputStream, user, database);
            mainMenu.run();
        }
    }

    private void removeUser(Matcher matcher) {
        String username = matcher.group(1);
        String password = matcher.group(2);

        if (hasNoErrorInCredentials(username, password)) {
            database.removeUserByUsername(username);
            System.out.println("removed successfully!");
        }
    }

    private void changePassword(Matcher matcher) {
        String username = matcher.group(1);
        String oldPassword = matcher.group(2);
        String newPassword = matcher.group(3);

        if (isUsernameFormatInvalid(username))
            System.out.println("invalid username!");
        else {
            User user = database.getUserByUsername(username);

            if (user == null)
                System.out.println("no user exists with this username");
            else if (isPasswordFormatInvalid(oldPassword))
                System.out.println("invalid old password!");
            else if (!user.isPasswordCorrect(oldPassword))
                System.out.println("password is wrong!");
            else if (isPasswordFormatInvalid(newPassword))
                System.out.println("invalid new password!");
            else if (isPasswordWeak(newPassword))
                System.out.println("new password is weak!");
            else {
                user.setPassword(newPassword);
                System.out.println("password changed successfully!");
            }
        }
    }

    private void printSortedUsernames() {
        printLineBreakSeparatedArray(database.getAlphabeticallySortedUsernames());
    }
}
